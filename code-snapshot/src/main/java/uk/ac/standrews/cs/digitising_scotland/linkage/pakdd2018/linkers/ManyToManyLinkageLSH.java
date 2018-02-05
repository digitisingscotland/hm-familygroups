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

import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;
import uk.ac.standrews.cs.utilities.lsh.MinHash;
import uk.ac.standrews.cs.utilities.lsh.MinHashStructure;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;

import java.util.*;

/**
 * Group arbitrary records using LSH
 * Created by al on 23/10/2017
 */
public class ManyToManyLinkageLSH extends GenericAbstractLinker {

    private final int shingle_size;
    private final int number_of_bands;
    private final int band_size;

    private MinHash<IStoreReference<LXP>> mh;


    public ManyToManyLinkageLSH(long starttime, String store_path, String repo_name,
                                String source_bucketname, String source_matchfield,
                                String sink_bucketname, String sink_matchfield,
                                String distance_method_name, float distance_threshold,
                                int shingle_size, int number_of_bands, int band_size) throws Exception {

        super(starttime, store_path, repo_name, source_bucketname, source_matchfield, sink_bucketname, sink_matchfield, distance_method_name, distance_threshold);

        this.shingle_size = shingle_size;
        this.number_of_bands = number_of_bands;
        this.band_size = band_size;

        mh = new MinHash<>(shingle_size, number_of_bands, band_size);

        report(link());
    }

    public void printStats(MinHashStructure mhs) {

        System.err.println("LSH report: ");
        System.err.println("shingle_size: " + mhs.shingle_size);
        System.err.println("signature_size: " + mhs.signature_size);
        System.err.println("band_size: " + mhs.band_size);
        System.err.println("number_of_keys: " + mhs.number_of_keys);

        System.err.println("Map distribution:");
        int counts = 0;
        int products = 0;
        for (Map.Entry<Integer, Integer> entry : mhs.list_distribution.entrySet()) {
            int size = entry.getKey();
            int count = entry.getValue();
            counts += count;
            products += size * count;
            System.err.println("\tsize: " + size + " count: " + count);
        }
        System.err.println("Average number of rhs in hash map : " + (float) products / counts);

    }

    @Override
    protected void addSourceRecord(IStoreReference source_record) throws Exception {
        String key = makeKey((LXP) source_record.getReferend());
        mh.put(key, source_record);
    }

    @Override
    protected List<DataDistance<IStoreReference<LXP>>> getCandidateMatches(IStoreReference thisRef) throws Exception {
        String key = makeKey((LXP)thisRef.getReferend());
        Set<IStoreReference<LXP>> closest_set = mh.getClosest(key);

        List<DataDistance<IStoreReference<LXP>>> results = new ArrayList<>();

        for (IStoreReference<LXP> matched_record : closest_set) {

            float distance = distance_method.distance(thisRef, matched_record);
            results.add(new DataDistance<>(matched_record, distance));
        }

        return results;
    }

    @Override
    protected void printStats() {
        printStats(mh.showStructure());
    }

    @Override
    protected LinkedHashMap<String, String> describeAlgorithm() {
        LinkedHashMap<String, String> algorithmDescription = new LinkedHashMap<>();
        algorithmDescription.put("Data Set (Source)", source_bucket_name);
        algorithmDescription.put("Data Set (Sink)", sink_bucket_name);
        algorithmDescription.put("Linker", "LSH");
        algorithmDescription.put("Distance Method", distance_method_name);
        algorithmDescription.put("Distance Threshold", String.format("%16.2f", distance_threshold));
        algorithmDescription.put("Shingle size", String.valueOf(shingle_size));
        algorithmDescription.put("Number of bands", String.valueOf(number_of_bands));
        algorithmDescription.put("Band size", String.valueOf(band_size));
        return algorithmDescription;
    }

    private String makeKey(LXP record) throws Exception {

        String result = "";

        List<Integer> excludedFields = getExcludedFields(source_bucket_name);
        for (int i : record.getMetaData().getSlots()) {
            if (!excludedFields.contains(i)) { // do not match the excluded fields
                String value = record.getString(i);
                if (!value.equals(null)) { // don't match empty fields
                    result += value;
                }
            }
        }

        return result;
    }

    private static final String[] ARG_NAMES = {
            "store_path", "repo_name",
            "source_bucketname", "source_matchfield",
            "sink_bucketname", "sink_matchfield",
            "distance_method_name", "distance_threshold",
            "shingle_size", "number_of_bands", "band_size"};

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];

            String source_bucket_name = args[2];
            String source_matchfield = args[3];

            String sink_bucket_name = args[4];
            String sink_matchfield = args[5];

            String distance_method = args[6];
            float distance_threshold = Float.parseFloat(args[7]);

            int shingle_size = Integer.parseInt(args[8]);
            int number_of_bands = Integer.parseInt(args[9]);
            int band_size = Integer.parseInt(args[10]);

            new ManyToManyLinkageLSH(System.currentTimeMillis(), store_path, repo_name, source_bucket_name, source_matchfield, sink_bucket_name, sink_matchfield, distance_method, distance_threshold, shingle_size, number_of_bands, band_size);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
            System.err.print("Outputs: classname\tstore_path\tsource_repo_name\tdistance_threshold\ttimetaken\ttrue_positives\tfalse_positives\tfalse_negatives\ttotal\tprecision\trecall\tfmeasure");
        }
    }


}
