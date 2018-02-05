/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module linkage-java.
 *
 * linkage-java is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * linkage-java is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with linkage-java. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.linkers;

import org.simmetrics.StringDistance;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.lxp_records.BirthDeath;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.lxp_records.Cora;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.distances.interfaces.CountingDistance;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Group arbitrary records using M-Trees
 * Created by al on 23/10/2017
 */
public class MetricChecker {

    final long starttime;
    private final String source_bucket_name;
    private final IBucket<LXP> source_bucket;
    private final String sink_bucket_name;
    private final IBucket<LXP> sink_bucket;
    private final String distance_method_name;

    private CountingDistance<IStoreReference<LXP>> recordDistance;
    private StringDistance stringDistance;

    public MetricChecker(long starttime, String store_path, String repo_name,
                         String source_bucket_name, String sink_bucket_name,
                         String distance_method_name) throws Exception {

        Store store = new Store(Paths.get(store_path));
        IRepository repo = store.getRepository(repo_name);

        this.starttime = starttime;

        this.source_bucket_name = source_bucket_name;
        this.source_bucket = repo.getBucket(source_bucket_name);

        this.sink_bucket_name = sink_bucket_name;
        this.sink_bucket = repo.getBucket(sink_bucket_name);

        this.distance_method_name = distance_method_name;

        metricDistanceSanityCheck();

    }

    protected void metricDistanceSanityCheck() throws Exception {
        HashSet<Long> allRecordIDs = new HashSet<>();
        ArrayList<IStoreReference<LXP>> allRecords = new ArrayList<>();
        IInputStream<LXP> source_stream = source_bucket.getInputStream();
        for (LXP record : source_stream) {
            if (!allRecordIDs.contains(record.getId())) {
                allRecords.add(record.getThisRef());
                allRecordIDs.add(record.getId());
            }
        }
        System.err.println("Added all source records to the allRecords set, size: " + allRecords.size());
        IInputStream<LXP> sink_stream = sink_bucket.getInputStream();
        for (LXP record : sink_stream) {
            if (!allRecordIDs.contains(record.getId())) {
                allRecords.add(record.getThisRef());
                allRecordIDs.add(record.getId());
            }
        }
        System.err.println("Added all sink records to the allRecords set, size: " + allRecords.size());

        recordDistance = GenericAbstractLinker.getDistanceMethod(distance_method_name, source_bucket_name);
        stringDistance = GenericAbstractLinker.getStringDistanceMethod(distance_method_name);
        if (recordDistance != null) {
            tick("Progress %d out of %d", 0, allRecords.size());
            for (int i = 0; i < allRecords.size(); i++) {
                for (int j = i + 1; j < allRecords.size(); j++) {
                    for (int k = j + 1; k < allRecords.size(); k++) {
                        checkMetricProperties(allRecords.get(i), allRecords.get(j), allRecords.get(k));
                    }
                }
                tick("Progress %d out of %d", i + 1, allRecords.size());
            }
            System.err.println("Done");
        } else {
            System.err.println("Does not implement CheckMetricProperties");
        }

    }

    // Print a timestamped message (at most) once every minute.
    // The first call within the minute is printed, following calls are ignored.
    private void tick(String message, Object... args) {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH.mm").format(new Date());
        if (!timeStamp.equals(lastTimeStamp)) {
            System.err.printf("[%s] %s\n", timeStamp, String.format(message, args));
            lastTimeStamp = timeStamp;
        }
    }

    private String lastTimeStamp = "";

    public float distanceCheckSymmetric(IStoreReference<LXP> a, IStoreReference<LXP> b) throws PersistentObjectException, BucketException {
        float dist_ab = recordDistance.distance(a, b);
        float dist_ba = recordDistance.distance(b, a);
        if (dist_ab != dist_ba) {
            System.err.println("Distance is not symmetric.");
            System.err.println("\ta: " + a.getReferend());
            System.err.println("\tb: " + b.getReferend());
            System.err.println("\tdist(a,b): " + dist_ab);
            System.err.println("\tdist(b,a): " + dist_ba);
            System.err.println();
        }
        return dist_ab;
    }

    public void checkMetricProperties(IStoreReference<LXP> i, IStoreReference<LXP> j, IStoreReference<LXP> k) throws Exception {

        float EPSILON = 0.00001f;


        // 1. Check the record level property

        float record_level_ij = distanceCheckSymmetric(i, j);
        float record_level_jk = distanceCheckSymmetric(j, k);
        float record_level_ik = distanceCheckSymmetric(i, k);


        boolean record_level_failed = false;

        // slack cannot be positive
        float slack = record_level_ik - record_level_ij - record_level_jk;
        if (slack >= EPSILON) {
            System.err.printf("Not Metric! ij + jk < ik -- slack: %.6f\n", slack);
            record_level_failed = true;
        }

        slack = record_level_jk - record_level_ij - record_level_ik;
        if (slack >= EPSILON) {
            System.err.printf("Not Metric! ij + ik < jk -- slack: %.6f\n", slack);
            record_level_failed = true;
        }

        slack = record_level_ij - record_level_ik - record_level_jk;
        if (slack >= EPSILON) {
            System.err.printf("Not Metric! ik + jk < ij -- slack: %.6f\n", slack);
            record_level_failed = true;
        }

        if (record_level_failed) {
            System.err.println("i: " + i);
            System.err.println("j: " + j);
            System.err.println("k: " + k);
            System.err.printf("ij: %.6f\n", record_level_ij);
            System.err.printf("jk: %.6f\n", record_level_jk);
            System.err.printf("ik: %.6f\n", record_level_ik);
        }

        if (record_level_failed) {

            // 2. Now check for each field

            Integer[] fields_i = getKeyFields(i.getReferend());
            Integer[] fields_j = getKeyFields(j.getReferend());
            Integer[] fields_k = getKeyFields(k.getReferend());

            // fields_ij and fields_ik should contain the same fields in them
            boolean sameFields = true;

            if (fields_i.length != fields_j.length || fields_i.length != fields_k.length) {
                sameFields = false;
            }

            for (int f = 0; f < fields_i.length; f++) {
                if (!fields_i[f].equals(fields_k[f])) {
                    sameFields = false;
                }
                if (!fields_i[f].equals(fields_k[f])) {
                    sameFields = false;
                }
            }

            if (!sameFields) {
                System.err.println("Different key fields.");
                System.err.println("fields_i: " + Arrays.toString(fields_i));
                System.err.println("fields_j: " + Arrays.toString(fields_j));
                System.err.println("fields_k: " + Arrays.toString(fields_k));
                System.err.println("When checking i: " + i);
                System.err.println("              j: " + j);
                System.err.println("              k: " + k);
            }


            float total_ij = 0;
            float total_jk = 0;
            float total_ik = 0;

            for (int f = 0; f < fields_i.length; f++) {
                float ij = stringDistance.distance(i.getReferend().getString(fields_i[f]), j.getReferend().getString(fields_i[f]));
                float jk = stringDistance.distance(j.getReferend().getString(fields_j[f]), k.getReferend().getString(fields_j[f]));
                float ik = stringDistance.distance(i.getReferend().getString(fields_k[f]), k.getReferend().getString(fields_k[f]));

                total_ij += ij;
                total_jk += jk;
                total_ik += ik;

                boolean failed = false;

                // slack cannot be positive
                slack = ik - ij - jk;
                if (slack >= EPSILON) {
                    System.err.printf("Field Not Metric! ij + jk < ik -- slack: %.6f\n", slack);
                    failed = true;
                }

                slack = jk - ij - ik;
                if (slack >= EPSILON) {
                    System.err.printf("Field Not Metric! ij + ik < jk -- slack: %.6f\n", slack);
                    failed = true;
                }

                slack = ij - ik - jk;
                if (slack >= EPSILON) {
                    System.err.printf("Field Not Metric! ik + jk < ij -- slack: %.6f\n", slack);
                    failed = true;
                }


                System.err.println("failed: " + failed);
                System.err.printf("ij: (%s: %s) -- (%s: %s) -- %.6f\n", fields_i[f], i.getReferend().getString(fields_i[f]), fields_j[f], j.getReferend().getString(fields_j[f]), ij);
                System.err.printf("jk: (%s: %s) -- (%s: %s) -- %.6f\n", fields_j[f], j.getReferend().getString(fields_j[f]), fields_k[f], k.getReferend().getString(fields_k[f]), jk);
                System.err.printf("ik: (%s: %s) -- (%s: %s) -- %.6f\n", fields_k[f], i.getReferend().getString(fields_k[f]), fields_k[f], k.getReferend().getString(fields_k[f]), ik);

            }

            System.err.printf("total ij: %.6f vs %.6f\n", record_level_ij, total_ij);
            System.err.printf("total jk: %.6f vs %.6f\n", record_level_jk, total_jk);
            System.err.printf("total ik: %.6f vs %.6f\n", record_level_ik, total_ik);
            System.err.println();

        }
    }

    private Integer[] getKeyFields(LXP lxp) throws Exception {
        if (distance_method_name.startsWith("BirthDeath")) {
            List<Integer> fields = BirthDeath.getRecordLinkingFields();
            return fields.toArray( new Integer[fields.size()] );
        } else if (source_bucket_name.startsWith("Cora") || source_bucket_name.startsWith("NCVR")) {
            List<Integer> fields = Cora.getRecordLinkingFields();
            return fields.toArray( new Integer[fields.size()] );
        } else {
            return null;
        }

    }

    private static final String[] ARG_NAMES = {
            "store_path", "repo_name",
            "source_bucket_name",
            "sink_bucket_name",
            "distance_method_name"
    };

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];
            String source_bucket_name = args[2];
            String sink_bucket_name = args[3];
            String distance_method = args[4];

            new MetricChecker(System.currentTimeMillis(), store_path, repo_name, source_bucket_name, sink_bucket_name, distance_method);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
        }
    }

}
