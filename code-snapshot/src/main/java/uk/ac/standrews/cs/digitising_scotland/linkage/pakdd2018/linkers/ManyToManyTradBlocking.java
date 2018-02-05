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

import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.lxp_records.Cora;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;

import java.util.*;

/**
 * Group arbitrary records using LSH
 * Created by al on 23/10/2017
 */
public class ManyToManyTradBlocking extends GenericAbstractLinker {

    private static List<Integer> select_fields;

    private HashMap<Integer, HashMap<String, List<IStoreReference<LXP>>>> maps; // map from field slot number to a map from key to list of matches.

    private String blocking_method_name;

    public ManyToManyTradBlocking(long starttime, String store_path, String repo_name,
                                  String source_bucketname, String source_matchfield_name,
                                  String sink_bucketname, String sink_matchfield_name,
                                  String blocking_method_name,
                                  String distance_method_name, float distance_threshold) throws Exception {

        super(starttime, store_path, repo_name, source_bucketname, source_matchfield_name, sink_bucketname, sink_matchfield_name, distance_method_name, distance_threshold);

        this.blocking_method_name = blocking_method_name;
        select_fields = Cora.getRecordLinkingFields();
        maps = new HashMap<>();

        report(link());
    }

    public void printStats() {

        System.err.println("Traditional Linking report: ");

        System.err.println("Map distribution:");
        int count_hash_keys = 0;
        int count_hash_rhs = 0;
        for (Map.Entry<Integer, HashMap<String, List<IStoreReference<LXP>>>> entry1 : maps.entrySet()) {

            for (Map.Entry<String, List<IStoreReference<LXP>>> entry2 : entry1.getValue().entrySet()) {
                int size = entry2.getValue().size();
                count_hash_keys++;
                count_hash_rhs += size;
                System.err.println("\tkey: >" + entry2.getKey() + "< size: " + size);
            }

        }
        System.err.println("Average number of rhs in hash map : " + (float) count_hash_rhs / count_hash_keys);
    }

    @Override
    protected LinkedHashMap<String, String> describeAlgorithm() {
        LinkedHashMap<String, String> algorithmDescription = new LinkedHashMap<>();
        algorithmDescription.put("Data Set (Source)", source_bucket_name);
        algorithmDescription.put("Data Set (Sink)", sink_bucket_name);
        algorithmDescription.put("Linker", "TradBlocking");
        algorithmDescription.put("Blocking Method", blocking_method_name);
        algorithmDescription.put("Distance Method", distance_method_name);
        algorithmDescription.put("Distance Threshold", String.format("%16.2f", distance_threshold));
        return algorithmDescription;
    }

    @Override
    protected void addSourceRecord(IStoreReference source_record) throws Exception {
        for (int slotnumber : select_fields) {

            HashMap<String, List<IStoreReference<LXP>>> selected_field_map = maps.get(slotnumber);
            if (selected_field_map == null) {
                selected_field_map = new HashMap<>();
                maps.put(slotnumber, selected_field_map);
            }

            String key = makeKey((LXP) source_record.getReferend(), slotnumber);
            add_to_map(selected_field_map, key, source_record);
        }

    }

    @Override
    protected List<DataDistance<IStoreReference<LXP>>> getCandidateMatches(IStoreReference thisRef) throws BucketException {

        // blocking_method_name can be either the name of one of the blocking fields
        // or the String "all", which means to union them all.

        Set<IStoreReference<LXP>> added = new HashSet<>();
        ArrayList<DataDistance<IStoreReference<LXP>>> results = new ArrayList<>();

        for (Map.Entry<Integer, HashMap<String, List<IStoreReference<LXP>>>> entry : maps.entrySet()) { // iterate through the hashmaps by fieldname

            Integer field = entry.getKey();

            if (blocking_method_name.equals("all") || blocking_method_name.equals(field)) {

                HashMap<String, List<IStoreReference<LXP>>> map = entry.getValue();

                String key = makeKey((LXP) thisRef.getReferend(), field);

                List<IStoreReference<LXP>> matching_records = map.get(key);

                for (IStoreReference<LXP> matched_record : matching_records) {
                    if (!added.contains(matched_record)) {
                        float distance = distance_method.distance(thisRef, matched_record);
                        results.add(new DataDistance<IStoreReference<LXP>>(matched_record, distance));
                        added.add(matched_record);
                    }
                }
            }
        }
        return results;
    }

    private void add_to_map(HashMap<String, List<IStoreReference<LXP>>> selected_field_map, String key, IStoreReference thisRef) {

        List<IStoreReference<LXP>> matches = selected_field_map.get(key);
        if (matches == null) {
            matches = new ArrayList<>();
            matches.add(thisRef);
            selected_field_map.put(key, matches);
        } else {
            matches.add(thisRef);
        }
    }

    private String makeKey(LXP record, int selector) {

        return record.getString(selector);
    }

    private static final String[] ARG_NAMES = {
            "store_path", "repo_name",
            "source_bucketname", "source_matchfield",
            "sink_bucketname", "sink_matchfield",
            "blocking_method_name",
            "distance_method_name", "distance_threshold"};

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];

            String source_bucket_name = args[2];
            String source_matchfield_name = args[3];

            String sink_bucket_name = args[4];
            String sink_matchfield_name = args[5];

            String blocking_method = args[6];
            String distance_method = args[7];
            float distance_threshold = Float.parseFloat(args[8]);

            new ManyToManyTradBlocking(System.currentTimeMillis(), store_path, repo_name, source_bucket_name, source_matchfield_name, sink_bucket_name, sink_matchfield_name, blocking_method, distance_method, distance_threshold);
        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
        }
    }


}
