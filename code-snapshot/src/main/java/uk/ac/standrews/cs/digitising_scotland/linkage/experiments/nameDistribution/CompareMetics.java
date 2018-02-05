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
package uk.ac.standrews.cs.digitising_scotland.linkage.experiments.nameDistribution;

import org.apache.commons.math3.stat.descriptive.AggregateSummaryStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.Jaro;
import org.simmetrics.metrics.JaroWinkler;
import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.utilities.dataset.DataSet;
import uk.ac.standrews.cs.utilities.metrics.Cosine;
import uk.ac.standrews.cs.utilities.metrics.JensenShannon;
import uk.ac.standrews.cs.utilities.metrics.JensenShannon2;
import uk.ac.standrews.cs.utilities.metrics.SED;
import uk.ac.standrews.cs.utilities.metrics.coreConcepts.Metric;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

import static uk.ac.standrews.cs.utilities.FileManipulation.createFileIfDoesNotExist;

/**
 * Outputs a CSV file containing distances between records labelled as links and non links.
 */
public class CompareMetics {

    private Store store;
    private String store_path;
    private String source_repo_name;
    private IRepository source_repo;
    private Metric js_metric = new JensenShannon();
    private Metric js_metric2 = new JensenShannon2(255);
    private Metric sed = new SED(255);
    private Metric cosine = new Cosine();
    private StringMetric lev = new Levenshtein();
    private StringMetric jw = new JaroWinkler();
    private StringMetric jaro = new Jaro();

    private static final String[] ARG_NAMES = {"store_path", "source_repo_name", "results_repo_name"};
    private final String results_path;

    private IBucket<Birth> births;
    private HashMap<String, Integer> name_counts = new HashMap<>();
    HashSet<String> families = new HashSet<>();
    private NameCache name_cache = new NameCache();
    private int match_count = 0;


    public CompareMetics(String store_path, String source_repo_name, String results_path  ) throws Exception {

        System.out.println("Examining surnames of births");
        this.results_path = results_path;

        initialise(store_path, source_repo_name);
    }

    private void describeData() throws BucketException {

        AggregateSummaryStatistics ss = new AggregateSummaryStatistics();
        SummaryStatistics contrib = ss.createContributingStatistics();

        for(Map.Entry<String,Integer> entry : name_counts.entrySet() ) {
            contrib.addValue( entry.getValue() );
        }

        System.out.println( "Number of records in cohort = " + births.size() );
        System.out.println( "Number of unique names  = " + ss.getN() );
        System.out.println( "Number of unique matching pairs (demographer labelled same family) =  " + match_count );
        System.out.println( "Number of unique families (demographer labelled same family) =  " + families.size() );
        System.out.println( "Number of families (demographer labelled) = " + name_cache.getFamilyCount() );
        System.out.println( "Max number of occurences of a name = " +  ss.getMax() );
        System.out.println( "Mean number of occurences of a name = " + ss.getMean() );
        System.out.println( "Std Dev = " + ss.getStandardDeviation() );
    }

    private void enumerateBirths() throws RepositoryException, BucketException {

        for (Birth birth : births.getInputStream()  ) {

            String name = birth.getString( Birth.FATHERS_SURNAME );
            Integer count = name_counts.get(name);
            if( count == null ) {
                name_counts.put( name, 1 );
            } else {
                name_counts.put( name, count + 1 );
            }
        }
    }

    /**
     * Fisher-Yates algorithm with O(n) time complexity
     * Permutes the given array
     * @param array array to be shuffled
     */
    public static void shuffle(Birth[] array) {
        Random r = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = r.nextInt(i);
            // swap
            Birth tmp = array[index];
            array[index] = array[i];
            array[i] = tmp;
        }
    }


    private void distancesMatchesAndNonMatches(Function<Birth,String> selector) throws BucketException, IOException {

        Path path = Paths.get(results_path);

        DataSet data = new DataSet(new ArrayList<>( Arrays.asList( new String[] {"Name1","Name2","Link","JS","JS2", "SED","cos","Lev","JW","Jaro" } ) ));

        // load all elements into an array.
        // Going to do this so the births can be permuted and choices of non links will be truly random

        Birth[] all_births = new Birth[births.size()];
        int index = 0;
        for (Birth birth : births.getInputStream()  ) {
            all_births[index++] = birth;
        }
        // now shuffle all of the births to ensure randomness.
        shuffle( all_births );

        int unmatched = 0;


        for( int i = 0; i < all_births.length; i++ ) {

            Birth birth1 = all_births[i];

            families.add(birth1.getString(Birth.FAMILY));

            for (int j = i+1; j < all_births.length; j++ ) {

                Birth birth2 = all_births[j];

                String name1 = clean( selector.apply( birth1 ) ) ; // birth1.getString( Birth.FATHERS_SURNAME ) );
                String name2 = clean( selector.apply( birth2 ) ) ; // .getString( Birth.FATHERS_SURNAME ) );
                if( ! ( name1.equals("") || name2.equals("")) ) { // ignore empties and near empties (via clean).

                    boolean same_family = birth1.getString(Birth.FAMILY).equals(birth2.getString(Birth.FAMILY));

                    if (name_cache.isUnseenPair(name1, name2,same_family)) {

                        double JS_distance = js_metric.distance(name1, name2);
                        double JS_distance2 = js_metric2.distance(name1, name2);
                        double sed_distance = sed.distance(name1, name2);
                        double cosine_distance = cosine.distance(name1, name2);
                        double lev_distance = 1 - lev.compare(name1, name2);
                        double jw_distance = 1 - jw.compare(name1, name2);
                        double jaro_distance = 1 - jaro.compare(name1, name2);

                        if (same_family) { // same family so put out a positive match
                            addRow(data, name1, name2, JS_distance, JS_distance2, sed_distance, cosine_distance, lev_distance, jw_distance, jaro_distance, "1");
                            match_count++;
                            unmatched++; // remember we have generated one more match - try and keep sam number of  matches and non-matches.
                        } else if (unmatched > 0 && !name1.equals(name2)) {
                            // need to add some non matches to the output.
                            addRow(data, name1, name2, JS_distance, JS_distance2, sed_distance, cosine_distance, lev_distance, jw_distance, jaro_distance, "0");
                            unmatched--;
                        }
                    }
                }
            }
        }
        createFileIfDoesNotExist( path );
        data.print(path);
    }

    private void addRow(DataSet data, String name1, String name2, double JS_distance, double JS_distance2, double sed_distance, double cosine_distance, double lev_distance, double jw_distance, double jaro_distance, String s) {

        data.addRow(name1, name2, s, Double.toString(JS_distance), Double.toString(JS_distance2), Double.toString(sed_distance), Double.toString(cosine_distance), Double.toString(lev_distance), Double.toString(jw_distance), Double.toString(jaro_distance));
    }


    public static String surnameSelector(Birth b) {
        return b.getString(Birth.FATHERS_SURNAME);
    }

    public static String parentsnamesSelector(Birth b) {
        return b.getString(Birth.FATHERS_FORENAME) + "*" + b.getString(Birth.FATHERS_SURNAME)  + "!" +
                b.getString(Birth.MOTHERS_FORENAME)  + "+" +  b.getString(Birth.MOTHERS_MAIDEN_SURNAME);
    }

    public static String parentsnamesPOMDOMSelector(Birth b) {
        return parentsnamesSelector(b) + "?" + b.getDateOfMarriage() + "%" + b.getPlaceOfMarriage();
    }

    private static String clean(String name) {
        return name.replace( " ","");  // get rid of white space
    }

    private void initialise(String store_path, String source_repo_name) throws Exception {

        this.store_path = store_path;
        this.source_repo_name = source_repo_name;

        store = new Store( Paths.get(store_path) );
        source_repo = store.getRepository(source_repo_name);
        births = source_repo.getBucket( "birth_records",Birth.class );

        enumerateBirths();
    }

    public static void main( String[] args ) throws Exception {
        CompareMetics nc = new CompareMetics( "/DigitisingScotland/Storr-root", "Skye", "/Users/al/Desktop/skye-distances.csv");

        //nc.distancesMatchesAndNonMatches(CompareMetics::surnameSelector);
        nc.distancesMatchesAndNonMatches(CompareMetics::parentsnamesSelector);
        //nc.distancesMatchesAndNonMatches(CompareMetics::parentsnamesPOMDOMSelector);

        nc.describeData();

    }

}
