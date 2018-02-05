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
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;
import uk.ac.standrews.cs.utilities.mi_file.MIFile;
import uk.ac.standrews.cs.utilities.mi_file.MIFileStructure;

import java.util.*;

/**
 * Group arbitrary records using MI-File
 * Created by al on 23/10/2017
 */
public class ManyToManyLinkageMIFile extends GenericAbstractLinker {

    private final int ki;
    private final int ks;
    private final int nearest_n;

    protected MIFile<IStoreReference<LXP>> mi_file;

    private static final List<Integer> exclude_fields = Cora.getExcludedFields();


    public ManyToManyLinkageMIFile(long starttime, String store_path, String repo_name,
                                   String source_bucketname, String source_matchfield,
                                   String sink_bucketname, String sink_matchfield,
                                   String distance_method_name, float distance_threshold,
                                   int ki, int ks, int nearest_n ) throws Exception {

        super( starttime, store_path, repo_name, source_bucketname,  source_matchfield, sink_bucketname,  sink_matchfield,  distance_method_name, distance_threshold );

        this.ki = ki;
        this.ks = ks;
        this.nearest_n = nearest_n;
        Set<IStoreReference<LXP>> reference_objects = pickPivots();

        mi_file = new MIFile<IStoreReference<LXP>>(distance_method, reference_objects, ki, ks );

        report( link() );
    }

    private void printStats(MIFileStructure mifs) {

        System.err.println("MIFile report: ");
        System.err.println("No Reference Objects: " + mifs.n_ro);
        System.err.println("ks: " + mifs.ks);
        System.err.println("ki: " + mifs.ki);
        System.err.println("max_pos_diff: " + mifs.max_pos_diff);
        System.err.println("number_of_items " + mifs.number_of_items);
        System.err.println("number_of_keys: " + mifs.number_of_keys);

        System.err.println("Map distribution:");
        int counts = 0;
        int products = 0;
        for (Map.Entry<Integer, Integer> entry : mifs.list_distribution.entrySet()) {
            int size = entry.getKey();
            int count = entry.getValue();
            counts += count;
            products += size * count;
            System.err.println("\tsize: " + size + " count: " + count);
        }
        System.err.println( "Average number of rhs in hash map : " + (float) products / counts );
    }

    private String makeKey(LXP record) {

        String result = "";

        for( int s : record.getMetaData().getSlots() ) {
            if (!exclude_fields.contains(s)) { // do not match the excluded fields
                String value = record.getString(s);
                if (! value.equals(null) ) { // don't match empty fields
                    result += value;
                }
            }
        }

        return result;
    }

    /**
     * Picks 2 * sqrt( num entries) of reference objects from the deaths.
     * @return the set of pivots
     */
    private Set<IStoreReference<LXP>> pickPivots() throws BucketException, PersistentObjectException {

        int count_records = source_bucket.size();
        long required = Math.round( 2 * Math.sqrt(  count_records ) );
        // Now pick required from the set.
        List<Long> oids = source_bucket.getOids();       // oids are randomly allocated.
        Collections.shuffle(oids); // mix up the list

        Iterator<Long> iter = oids.iterator();

        Set<IStoreReference<LXP>> pivots = new HashSet<>();

        for( int i = 0; i < required; i++ ) {
            pivots.add( source_bucket.getObjectById(oids.get(i)).getThisRef());
        }

        return pivots;
    }

    // Describe the algorithm (and its options)
    public LinkedHashMap<String,String> describeAlgorithm() {
        LinkedHashMap<String, String> algorithmDescription = new LinkedHashMap<>();
        algorithmDescription.put("Data Set (Source)", source_bucket_name);
        algorithmDescription.put("Data Set (Sink)", sink_bucket_name);
        algorithmDescription.put("Linker", "MiFile");
        algorithmDescription.put("Distance Method", distance_method_name);
        algorithmDescription.put("Distance Threshold", String.format("%16.2f", distance_threshold));
        algorithmDescription.put("ki", String.valueOf(ki));
        algorithmDescription.put("ks", String.valueOf(ks));

        return algorithmDescription;
    }


    @Override
    protected void addSourceRecord(IStoreReference source_record) throws Exception {
        mi_file.add(source_record);

    }

    @Override
    protected List<DataDistance<IStoreReference<LXP>>> getCandidateMatches(IStoreReference thisRef) {
        return mi_file.nearestN(thisRef, nearest_n);
    }

    public void printStats() {
        printStats( mi_file.showStructure() );
    }

    private static final String[] ARG_NAMES = {
            "store_path", "repo_name",
            "source_bucketname", "source_matchfield",
            "sink_bucketname", "sink_matchfield",
            "distance_method_name", "distance_threshold",
            "ki", "ks", "nearest_n" };

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

            int ki = Integer.parseInt(args[8]);
            int ks = Integer.parseInt(args[9]);
            int nearest_n = Integer.parseInt(args[10]);

            new ManyToManyLinkageMIFile( System.currentTimeMillis(), store_path, repo_name, source_bucketname, source_matchfield, sink_bucketname, sink_matchfield, distance_method, distance_threshold, ki, ks, nearest_n);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
            System.err.print("Outputs: classname\tstore_path\tsource_repo_name\tdistance_threshold\ttimetaken\ttrue_positives\tfalse_positives\tfalse_negatives\ttotal\tprecision\trecall\tfmeasure");
        }
    }


}
