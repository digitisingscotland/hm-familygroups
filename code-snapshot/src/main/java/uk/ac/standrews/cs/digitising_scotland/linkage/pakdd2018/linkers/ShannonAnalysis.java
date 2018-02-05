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
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;
import uk.ac.standrews.cs.utilities.PercentageProgressIndicatorStderr;
import uk.ac.standrews.cs.utilities.ProgressIndicator;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Try and work out Shannon entropy of a dataset
 * This code is a hack - sorry
 * It is not really a linker!
 *
 * Created by al on 10/11/2017
 */
public class ShannonAnalysis extends GenericAbstractLinker {

    public ShannonAnalysis(long starttime, String store_path, String repo_name,
                           String source_bucketname ) throws Exception {

        super( starttime, store_path, repo_name, source_bucketname,  "0", source_bucketname, "0", "CoraNormalisedLevenshteinCaching", 0.0f );

        analyse();
    }

    public void analyse() throws Exception {

        IInputStream<LXP> source_stream = source_bucket.getInputStream();
        IInputStream<LXP> sink_stream = sink_bucket.getInputStream();

        // Describe the algorithm (and its options)
        LinkedHashMap<String, String> algorithmDescription = new LinkedHashMap<>();
        algorithmDescription.put("Data Set (Source)", source_bucket_name);
        algorithmDescription.put("Linker", "Shannon complexity");

        ProgressIndicator linkingIndicator = new PercentageProgressIndicatorStderr(10);
        linkingIndicator.setTotalSteps(source_bucket.size());

        // Perform linkage
        for (LXP source_record : source_stream) {

            addToFieldDistributions( source_record );

        }
        float complexity = caclulateShannonComplexity();

        System.out.println( "Complexity of " + source_bucket_name + " dataset is " + complexity);
    }

    /******************************** Shannon calculations ********************************/

    private HashMap<Integer, HashMap<String, Integer>> distribution_of_field_values = new HashMap<Integer, HashMap<String, Integer>>(); // maps from fieldname to fieldvalue X count.


    /**
     * Calculate the Shannon complexity for the dataset
     * Assumes that each field is independent og the others (which may not be true but hey ho).
     * For each field:
     *     Calculate the probability of each different values in the column by the taking the count / total number of values
     *     Sum the p(value) * log(p(value) and multiply by -1 to get H for that column
     * Then Sum all the H(x) for all the fields to get the value of H for the dataset.
     * This should return the number of bits necessary to encode the dataset.
     */
    private float caclulateShannonComplexity() {

        ArrayList<Float> shannon_of_fields = new ArrayList<>();

        for( HashMap<String,Integer> fieldMap : distribution_of_field_values.values() ) { // for each fieldname in the dataset
            shannon_of_fields.add( calculate_shannon_of_field( fieldMap ) );
        }
        return overall_complexity_over_all_complexities( shannon_of_fields );

    }

    private float overall_complexity_over_all_complexities(ArrayList<Float> shannon_of_fields) {
        float result = 0.0f;
        for( float field_complexity : shannon_of_fields) {
            result += field_complexity;
        }
        return result;
    }

    /**
     * Calculates the shannon complexity for a single 'column' in the dataset.
     * @param fieldMap
     * @return
     */
    private float calculate_shannon_of_field(HashMap<String, Integer> fieldMap) {
        float shannon_of_field = 0.0f;
        int total_number_of_values = 0;


        for( int count : fieldMap.values() ) { total_number_of_values += count; }
        for( int count : fieldMap.values() ) {

            float p_entry = count / (float) total_number_of_values; // probability of this entry occurring
            float h = (float) ( -1 * p_entry * log2(p_entry) ); // shannon entropy of this field

            shannon_of_field += h;
        }
        return shannon_of_field;
    }


    public static double log2(double num) {
        return (Math.log(num)/Math.log(2));
    }

    private void addToFieldDistributions(LXP source_record) {
        for( int slot : source_record.getMetaData().getSlots() ) { // for all the records in a record
            String value = source_record.getString(slot);
                add_to_distribution( slot, value );
        }
    }

    private void add_to_distribution(int slot, String value) {

        HashMap<String,Integer> fieldMap = distribution_of_field_values.get( slot );

        if( fieldMap == null ) {       // we have not seen this fieldname before - so add to the associated map
            fieldMap = new HashMap<>();
            distribution_of_field_values.put(slot,fieldMap);
        }
        // now we have a fieldmap into which to insert the value and count.
        Integer count = fieldMap.get(value);
        if( count == null ) { // not seen this value before.
            fieldMap.put(value, 1);
        } else { // seen it already so increment the count
            fieldMap.put(value, count + 1);
        }
    }

    /******************************** end of Shannon calculations ********************************/


    @Override
    protected void addSourceRecord(IStoreReference source_record) throws Exception {

    }

    @Override
    protected List<DataDistance<IStoreReference<LXP>>> getCandidateMatches(IStoreReference thisRef) {
        return null;
    }

    @Override
    protected void printStats() {

    }

    @Override
    protected LinkedHashMap<String, String> describeAlgorithm() {
        return null;
    }

    /******************************** end of Hacked unused methods ********************************/



    private static final String[] ARG_NAMES = {
            "store_path", "repo_name", "source_bucketname",
    };

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];

            String source_bucketname = args[2];

            new ShannonAnalysis(System.currentTimeMillis(), store_path, repo_name, source_bucketname );

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
        }
    }
}
