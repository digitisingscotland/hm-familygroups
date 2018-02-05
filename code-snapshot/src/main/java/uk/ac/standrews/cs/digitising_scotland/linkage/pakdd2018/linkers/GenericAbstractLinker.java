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
import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.lxp_records.BirthDeath;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.lxp_records.Cora;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.lxp_records.NCVR;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.distances.baseDistances.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.distances.generators.AverageOverSelectedFields;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.distances.generators.CountingDistanceOverDistance;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.distances.generators.DistanceOverIDFromDistanceOverLXP;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.distances.generators.SigmaOverSelectedFields;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.distances.interfaces.CountingDistance;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.utils.ILinkageResult;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.utils.LinkageResult;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;
import uk.ac.standrews.cs.utilities.PercentageProgressIndicatorStderr;
import uk.ac.standrews.cs.utilities.ProgressIndicator;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;
import uk.ac.standrews.cs.utilities.m_tree.Distance;

import java.nio.file.Paths;
import java.util.*;

/**
 * Super class for generic linkers for PAKDD experiments
 * Created by al on 23/10/2017
 */
public abstract class GenericAbstractLinker {

    private static final String[] ARG_NAMES = {
            "store_path", "repo_name",
            "source_bucketname", "source_matchfield",
            "sink_bucketname", "sink_matchfield",
            "distance_method_name", "distance_threshold"
    };

    final long starttime;

    final String source_bucket_name;
    final IBucket<LXP> source_bucket;
    final int source_matchfield;

    final String sink_bucket_name;
    final IBucket<LXP> sink_bucket;
    final int sink_matchfield;

    final String distance_method_name;
    final CountingDistance<IStoreReference<LXP>> distance_method;
    final float distance_threshold;


    public GenericAbstractLinker(long starttime, String store_path, String repo_name,
                                 String source_bucket_name, String source_matchfield_name,
                                 String sink_bucket_name, String sink_matchfield_name,
                                 String distance_method_name, float distance_threshold) throws Exception {

        Store store = new Store(Paths.get(store_path));
        IRepository repo = store.getRepository(repo_name);

        this.starttime = starttime;

        this.source_bucket_name = source_bucket_name;
        this.source_bucket = repo.getBucket(source_bucket_name);
        this.source_matchfield = getSlotNumber( source_bucket_name,source_matchfield_name );

        this.sink_bucket_name = sink_bucket_name;
        this.sink_bucket = repo.getBucket(sink_bucket_name);
        this.sink_matchfield = getSlotNumber( source_bucket_name,sink_matchfield_name );

        this.distance_method_name = distance_method_name;
        this.distance_threshold = distance_threshold;

        this.distance_method = getDistanceMethod(distance_method_name, source_bucket_name);

    }

    private int getSlotNumber(String source_bucket_name, String field_name) throws Exception {
        if (source_bucket_name.startsWith("Cora")) {
            return new Cora().getMetaData().getSlot(field_name);
        } else if (source_bucket_name.startsWith("Skye") || source_bucket_name.startsWith("Kilmarnock") ) {
            return new BirthDeath().getMetaData().getSlot(field_name);
        } else if (source_bucket_name.startsWith("NCVR")) {
            return new NCVR().getMetaData().getSlot(field_name);
        } else {
            throw new Exception("Unrecognised source_bucket_name: " + source_bucket_name);
        }
    }


    public static List<Integer> getExcludedFields(String source_bucket_name) throws Exception {
        if (source_bucket_name.startsWith("Cora")) {
            return Cora.getExcludedFields();
        } else if (source_bucket_name.startsWith("Skye") || source_bucket_name.startsWith("Kilmarnock") ) {
            return BirthDeath.getExcludedFields();
        } else if (source_bucket_name.startsWith("NCVR")) {
                return NCVR.getExcludedFields();
        } else {
            throw new Exception("Unrecognised source_bucket_name: " + source_bucket_name);
        }
    }

    public static StringDistance getStringDistanceMethod(String distance_method) throws Exception {
        String[] parts = distance_method.split("-");
        StringDistance fieldDistance = null;

        if (parts.length == 3) {
//            String dataset = parts[0];
//            String recordLevel = parts[1];
            String fieldLevel = parts[2];

            if (fieldLevel.equals("Levenshtein")) {
                fieldDistance = new Levenshtein();
            } else if (fieldLevel.equals("GLD")) {
                fieldDistance = new GLD();
            } else if (fieldLevel.equals("NED2")) {
                fieldDistance = new NED2();
            } else if (fieldLevel.equals("Jaccard")) {
                fieldDistance = new JaccardOver2Grams();
            }
        }

        if (fieldDistance == null) {
            throw new Exception("Unrecognised sim method: " + distance_method + "\nparts: " + Arrays.toString(parts));
        } else {
            return fieldDistance;
        }
    }

    public static CountingDistance<IStoreReference<LXP>> getDistanceMethod(String distance_method, String source_bucket_name) throws Exception {

        String[] parts = distance_method.split("-");
        Distance<LXP> baseDistance = null;

        if (parts.length == 3) {
            String dataset = parts[0];
            String recordLevel = parts[1];
//            String fieldLevel = parts[2];

            StringDistance fieldDistance = getStringDistanceMethod(distance_method);

            if (dataset.equals("BirthDeath")) {
                if (recordLevel.equals("Sigma")) {
                    baseDistance = new BirthDeathSigma(fieldDistance);
                } else if (recordLevel.equals("Average")) {
                    baseDistance = new BirthDeathAverage(fieldDistance);
                }
            } else if (dataset.equals("Generic")) {
                if (recordLevel.equals("Sigma")) {
                    baseDistance = SigmaOverSelectedFields.wrapDistance(getExcludedFields(source_bucket_name), fieldDistance);
                } else if (recordLevel.equals("Average")) {
                    baseDistance = AverageOverSelectedFields.wrapDistance(getExcludedFields(source_bucket_name), fieldDistance);
                }
            }
        }

        if (baseDistance == null) {
            throw new Exception("Unrecognised sim method: " + distance_method + "\nparts: " + Arrays.toString(parts));
        } else {
//            return DistanceOverIDFromDistanceOverLXP.wrapCountingDistanceOverLXP(
//                    CachingDistanceOverDistance.wrapDistance(
//                            CountingDistanceOverDistance.wrapDistance(baseDistance)));
            return DistanceOverIDFromDistanceOverLXP.wrapCountingDistanceOverLXP(
                    CountingDistanceOverDistance.wrapDistance(baseDistance));
        }
    }


    ArrayList<Long> times = new ArrayList<>();

    void saveLookupTime(long time) {
        times.add(time);
    }

    void printTimes() {
        System.err.println("Times for lookup:");
        System.err.println("Min lookuptime: " + Collections.min(times));
        System.err.println("Max lookuptime: " + Collections.max(times));

        long sum = 0;
        for (long time : times) {
            sum += time;
        }
        System.err.println("Average lookuptime: " + sum / times.size());
    }

    public void report(ILinkageResult linkageResult) {
        // Print stats
        linkageResult.printLinkageStatsHeader();
        linkageResult.printLinkageStats();
//        linkageResult.printLinkageStatsVerbose();
    }

    public LinkageResult link() throws Exception {

        IInputStream<LXP> source_stream = source_bucket.getInputStream();
        IInputStream<LXP> sink_stream = sink_bucket.getInputStream();

        LinkageResult linkageResult = new LinkageResult(starttime, source_matchfield, sink_matchfield, describeAlgorithm(), source_bucket, sink_bucket);

        linkageResult.populateMatches(source_stream, sink_stream);

        ProgressIndicator loadIndicator = new PercentageProgressIndicatorStderr(10);
        loadIndicator.setTotalSteps(source_bucket.size());

        for (LXP record : source_stream) {
            addSourceRecord(record.getThisRef());
            loadIndicator.progressStep();
        }

        printStats();

        linkageResult.addSetupComparisonCount(distance_method.getComparisonCount());

        ProgressIndicator linkingIndicator = new PercentageProgressIndicatorStderr(100);
        linkingIndicator.setTotalSteps(sink_bucket.size());

        for (LXP sink_record : sink_stream) {

            List<DataDistance<IStoreReference<LXP>>> closest_set = getCandidateMatches(sink_record.getThisRef());

            int link_count = 0;
            int match_count = 0;

            if (closest_set != null) { // it could be ...
                for (DataDistance<IStoreReference<LXP>> match : closest_set) {      // These should be in order but to be safe...
                    if (match.distance <= distance_threshold) {
                        link_count++;
                        linkageResult.addLink(match.value, sink_record.getThisRef(), match.distance); // add the record to results;
                    }
                    if (linkageResult.isMatch(match.value, sink_record.getThisRef())) {
                        match_count++;
                    }
                }
            }

            if (closest_set.size() > 0) {
                int non_match_count = linkageResult.countMissingMatches(sink_record, closest_set);
                if (match_count + non_match_count > 0) {
                    linkageResult.addCandidates(closest_set.size(), link_count, match_count, non_match_count);
                }
            }
            linkingIndicator.progressStep();
        }

        linkageResult.addMemoryUsage(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        linkageResult.addOverallComparisonCount(distance_method.getComparisonCount());

        return linkageResult;
    }

    protected abstract void addSourceRecord(IStoreReference source_record) throws Exception;

    protected abstract List<DataDistance<IStoreReference<LXP>>> getCandidateMatches(IStoreReference thisRef) throws Exception;

    protected abstract void printStats();

    protected abstract LinkedHashMap<String, String> describeAlgorithm();

}

