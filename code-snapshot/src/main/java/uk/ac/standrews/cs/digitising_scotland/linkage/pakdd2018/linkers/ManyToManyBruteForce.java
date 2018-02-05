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
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Group arbitrary records using Brute Force (N2 approach)
 * Created by al on 23/10/2017
 */
public class ManyToManyBruteForce extends GenericAbstractLinker {

    List<IStoreReference> sources = new ArrayList<>();

    public ManyToManyBruteForce(long starttime, String store_path, String repo_name,
                                String source_bucketname, String source_matchfield,
                                String sink_bucketname, String sink_matchfield,
                                String distance_method_name, float distance_threshold) throws Exception {

        super( starttime, store_path, repo_name, source_bucketname,  source_matchfield, sink_bucketname,  sink_matchfield,  distance_method_name, distance_threshold );

        report( link() );
    }

    @Override
    protected void addSourceRecord(IStoreReference source_record) throws Exception {
        sources.add( source_record );
    }

    @Override
    protected List<DataDistance<IStoreReference<LXP>>> getCandidateMatches(IStoreReference thisRef) throws Exception {

        List<DataDistance<IStoreReference<LXP>>> results = new ArrayList<>();

        for (IStoreReference<LXP> candidate_record : sources) {

            float distance = distance_method.distance(thisRef, candidate_record);
            results.add(new DataDistance<>(candidate_record, distance));
        }

        return results;
    }

    @Override
    protected void printStats() {

    }

    @Override
    protected LinkedHashMap<String, String> describeAlgorithm() {
        LinkedHashMap<String, String> algorithmDescription = new LinkedHashMap<>();
        algorithmDescription.put("Data Set (Source)", source_bucket_name);
        algorithmDescription.put("Data Set (Sink)", sink_bucket_name);
        algorithmDescription.put("Linker", "Brute Force");
        algorithmDescription.put("Distance Method", distance_method_name);
        algorithmDescription.put("Distance Threshold", String.format("%16.2f", distance_threshold));
        return algorithmDescription;
    }

    private static final String[] ARG_NAMES = {
            "store_path", "repo_name",
            "source_bucketname", "source_matchfield",
            "sink_bucketname", "sink_matchfield",
            "distance_method_name", "distance_threshold"
    };

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];

            String source_bucketname = args[2];
            String source_matchfield = args[3];

            String sink_bucketname = args[4];
            String sink_matchfield = args[5];

            String distance_method = args[6];
            float distance_threshold = Float.parseFloat(args[7]);

            new ManyToManyBruteForce(System.currentTimeMillis(), store_path, repo_name, source_bucketname, source_matchfield, sink_bucketname, sink_matchfield, distance_method, distance_threshold);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
        }
    }


}
