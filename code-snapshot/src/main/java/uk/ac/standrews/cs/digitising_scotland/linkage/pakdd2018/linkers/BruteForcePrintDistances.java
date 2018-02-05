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

import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.utils.LinkageResult;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class for printing distances
 * Created by al on 23/10/2017
 */
public class BruteForcePrintDistances extends GenericAbstractLinker {


    private final LinkageResult linkageResults;

    public BruteForcePrintDistances(String store_path, String repo_name,
                                    String source_bucketname,
                                    String sink_bucketname,
                                    String distance_method_name ) throws Exception {

        super( System.currentTimeMillis(), store_path, repo_name, source_bucketname,  "CITATION_KEY", sink_bucketname,  "CITATION_KEY",  distance_method_name, 0.0f );

        linkageResults = link();
        print_distances();
    }

    public void print_distances() throws Exception {

        IInputStream<LXP> source_stream = source_bucket.getInputStream();
        IInputStream<LXP> sink_stream = sink_bucket.getInputStream();

        System.out.println( "Source_id" + "\t" + "Sink_id" + "\t" + "Distance" + "\t" + "is_match");

        for (LXP source_record : source_stream) {

            for (LXP sink_record : sink_stream) {

                if (!source_record.equals(sink_record)) {

                    float d = distance_method.distance(source_record.getThisRef(), sink_record.getThisRef());

                    boolean is_match = linkageResults.isMatch(source_record.getThisRef(), sink_record.getThisRef());

                    System.out.println( source_record.getThisRef() + "\t" + sink_record.getThisRef() + "\t" +  d + "\t" + is_match );
                }
            }
        }

    }

    private static final String[] ARG_NAMES = {
            "store_path", "repo_name",
            "source_bucketname",
            "sink_bucketname",
            "distance_method_name" };

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];

            String source_bucket_name = args[2];

            String sink_bucket_name = args[3];

            String similarity_method_name = args[4];

            new BruteForcePrintDistances(store_path, repo_name, source_bucket_name, sink_bucket_name, similarity_method_name);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
            System.err.print("Outputs: classname\tstore_path\tsource_repo_name\tdistance_threshold\ttimetaken\ttrue_positives\tfalse_positives\tfalse_negatives\ttotal\tprecision\trecall\tfmeasure");
        }
    }

    @Override
    protected void addSourceRecord(IStoreReference source_record) throws Exception {
        // don't do anything
    }

    @Override
    protected List<DataDistance<IStoreReference<LXP>>> getCandidateMatches(IStoreReference thisRef) throws Exception {
        // don't do anything
        return new ArrayList<DataDistance<IStoreReference<LXP>>>();
    }

    @Override
    protected void printStats() {
        // don't do anything
    }

    @Override
    protected LinkedHashMap<String, String> describeAlgorithm() {
        LinkedHashMap<String, String> algorithmDescription = new LinkedHashMap<>();
        algorithmDescription.put("Linker", "BruteForceDistances");
        return algorithmDescription;
    }
}
