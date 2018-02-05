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
import uk.ac.standrews.cs.utilities.metrics.Cosine;
import uk.ac.standrews.cs.utilities.metrics.JensenShannon;
import uk.ac.standrews.cs.utilities.metrics.JensenShannon2;
import uk.ac.standrews.cs.utilities.metrics.SED;
import uk.ac.standrews.cs.utilities.metrics.coreConcepts.Metric;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Attempt to create an end to end linkage framework
 * Created by al on 24/4/2017
 */
public class AnalyseMetrics {

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

    private static final String[] ARG_NAMES = {"store_path", "source_repo_name"};

    private IBucket<Birth> births;
    private HashMap<String, Integer> name_counts = new HashMap<>();

    public AnalyseMetrics(String store_path, String source_repo_name ) throws Exception {

        System.out.println("Examining metrics over births");

        initialise(store_path, source_repo_name);
    }

    private void initialise(String store_path, String source_repo_name) throws Exception {

        this.store_path = store_path;
        this.source_repo_name = source_repo_name;

        store = new Store(Paths.get(store_path));
        source_repo = store.getRepository(source_repo_name);
        births = source_repo.getBucket("birth_records", Birth.class);
    }

    private String clean(String name) {
        return name.replace( " ","");  // get rid of white space
    }

    private void time() throws RepositoryException, BucketException, IOException {

        // load all elements into an array.
        // Going to do this so the births can be permuted and choices of non links will be truly random

        Birth[] all_births = new Birth[births.size()];
        int index = 0;
        for (Birth birth : births.getInputStream()  ) {
            all_births[index++] = birth;
        }

        for( int i = 0; i < all_births.length; i++ ) {

            Birth birth1 = all_births[i];


        }

        Function<Birth,String> selector = CompareMetics::surnameSelector;

        doComparisons( all_births, js_metric2, js_metric2.getMetricName(), selector );
//        doComparisons( all_births, sed, sed.getMetricName(), selector );
//        doComparisons( all_births, cosine, cosine.getMetricName(), selector  );
//        doComparisons( all_births, new MetricWrapper( "levenshtein", lev ), "levenshtein", selector );
//        doComparisons( all_births, new MetricWrapper( "JaroWinkler", jw ), "JaroWinkler", selector );
//        doComparisons( all_births, new MetricWrapper( "Jaro", jaro ), "Jaro", selector  );
    }

    private class MetricWrapper implements Metric<String> {

        private final String name;
        private final StringMetric sm;

        public MetricWrapper(String name, StringMetric sm ) {
            this.name = name;
            this.sm = sm;
        }

        @Override
        public double distance(String x, String y) {
            return 1 - sm.compare( x,y );
        }

        @Override
        public String getMetricName() {
            return name;
        }
    }

    private void doComparisons(Birth[] all_births, Metric m, String name, Function<Birth,String> selector ) {

        boolean unused = true;

        AggregateSummaryStatistics ss = new AggregateSummaryStatistics();
        SummaryStatistics js_contrib = ss.createContributingStatistics();

        NameCache name_cache = new NameCache();

        ArrayList<Double> distances = new ArrayList<>(); // used for median caclulation

        long start_time = System.currentTimeMillis();

        for( int i = 0; i < all_births.length; i++ ) {

            Birth birth1 = all_births[i];

            for (int j = i+1; j < all_births.length; j++ ) {

                Birth birth2 = all_births[j];

                String surname1 = clean(selector.apply( birth1));
                String surname2 = clean(selector.apply( birth2));
                if (!(surname1.equals("") || surname2.equals(""))) { // ignore empties and near empties (via clean).

                    if ( name_cache.isUnseenPair(surname1, surname2, unused)) {
                        double distance = m.distance(surname1, surname2);
                        distances.add(distance);
                        js_contrib.addValue(distance);
                    }

                }
            }
        }
        long elapsed_time = System.currentTimeMillis() - start_time;

        print_data( name, ss, distances, elapsed_time );
    }

    private double median( AggregateSummaryStatistics ss, ArrayList<Double> distances ) {

        Double[] array_distances = new Double[distances.size()];
        distances.toArray(array_distances);
        Arrays.sort( array_distances );

        double pos1 = Math.floor((array_distances.length - 1.0) / 2.0);

         return array_distances[(int)pos1];
    }

    private double IDim( double std_dev, double median ) {

        return ( median * median ) / ( 2 * std_dev * std_dev );
    }

    private void print_data(String name, AggregateSummaryStatistics ss, ArrayList<Double> distances, long elapsed_time) {
        long count = ss.getN();
        double std_dev = ss.getStandardDeviation();
        double median = median( ss, distances);

        System.out.println( "Times for " + name );
        System.out.println( "Time taken for " + count + " distance calculations = " + elapsed_time + " ms" );
        System.out.println( "Time per calculation = " + elapsed_time / ((float)count) + " ms" );
        System.out.println();
        System.out.println( "Mean distance between names " + ss.getMean() );
        System.out.println( "Max distance between names " + ss.getMax() );
        System.out.println( "Min distance between names " + ss.getMin() );
        System.out.println( "Median distance between names " + median );
        System.out.println( "Std Dev of distance between names = " + ss.getStandardDeviation() );

        System.out.println( "Intrinsic dimensionality = " + IDim( std_dev, median ) );
    }


    public static void main( String[] args ) throws Exception {
        AnalyseMetrics nc = new AnalyseMetrics( "/DigitisingScotland/Storr-root", "Skye");
        nc.time();
    }



}
