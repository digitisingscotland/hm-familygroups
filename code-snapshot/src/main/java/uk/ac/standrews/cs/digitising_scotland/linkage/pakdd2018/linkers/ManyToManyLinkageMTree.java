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
import uk.ac.standrews.cs.utilities.m_tree.MTree;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Group arbitrary records using M-Trees
 * Created by al on 23/10/2017
 */
public class ManyToManyLinkageMTree extends GenericAbstractLinker {

    private final int level_size;
    private MTree<IStoreReference<LXP>> lxpMTree;

    public ManyToManyLinkageMTree(long starttime, String store_path, String repo_name,
                                  String source_bucketname, String source_matchfield_name,
                                  String sink_bucketname, String sink_matchfield_name,
                                  String distance_method_name, float distance_threshold, int level_size) throws Exception {

        super(starttime, store_path, repo_name, source_bucketname, source_matchfield_name, sink_bucketname, sink_matchfield_name, distance_method_name, distance_threshold);

        this.level_size = level_size;

        lxpMTree = new MTree<>(distance_method, level_size);

        report(link());
    }

    @Override
    protected void addSourceRecord(IStoreReference source_record) {
        lxpMTree.add(source_record);
    }

    @Override
    protected List<DataDistance<IStoreReference<LXP>>> getCandidateMatches(IStoreReference thisRef) {
        return lxpMTree.rangeSearch(thisRef, distance_threshold);
    }


    @Override
    protected void printStats() {
        lxpMTree.showStructure().printStats();

    }

    @Override
    protected LinkedHashMap<String, String> describeAlgorithm() {
        LinkedHashMap<String, String> algorithmDescription = new LinkedHashMap<>();
        algorithmDescription.put("Data Set (Source)", source_bucket_name);
        algorithmDescription.put("Data Set (Sink)", sink_bucket_name);
        algorithmDescription.put("Linker", "MTree");
        algorithmDescription.put("Distance Method", distance_method_name);
        algorithmDescription.put("Distance Threshold", String.format("%16.2f", distance_threshold));
        algorithmDescription.put("Branching factor", String.valueOf(level_size));

        return algorithmDescription;
    }

    private static final String[] ARG_NAMES = {
            "store_path", "repo_name",
            "source_bucketname", "source_matchfield",
            "sink_bucketname", "sink_matchfield",
            "distance_method_name", "distance_threshold", "level_size"
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
            int level_size = Integer.parseInt(args[8]);

            new ManyToManyLinkageMTree(System.currentTimeMillis(), store_path, repo_name, source_bucketname, source_matchfield, sink_bucketname, sink_matchfield, distance_method, distance_threshold, level_size);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
            System.err.print("Outputs: classname\tstore_path\tsource_repo_name\tdistance_threshold\ttimetaken\ttrue_positives\tfalse_positives\tfalse_negatives\ttotal\tprecision\trecall\tfmeasure");
        }
    }
}
