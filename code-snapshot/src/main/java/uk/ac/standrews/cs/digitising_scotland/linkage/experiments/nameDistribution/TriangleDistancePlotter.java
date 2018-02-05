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

import org.simmetrics.StringMetric;
import org.simmetrics.metrics.Jaro;
import org.simmetrics.metrics.JaroWinkler;
import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;

import static uk.ac.standrews.cs.digitising_scotland.linkage.experiments.nameDistribution.CompareMetics.surnameSelector;
import static uk.ac.standrews.cs.utilities.FileManipulation.createFileIfDoesNotExist;

/**
 * Outputs a CSV file of distances from supplied points to all other points in the dataset.
 */
public class TriangleDistancePlotter {

    private Store store;
    private String store_path;
    private String source_repo_name;
    private IRepository source_repo;
    private static Metric js = new JensenShannon();
    private static Metric js2 = new JensenShannon2(255);
    private static Metric sed = new SED(255);
    private static Metric cosine = new Cosine();
    private static StringMetric lev = new Levenshtein();
    private static StringMetric jw = new JaroWinkler();
    private static StringMetric jaro = new Jaro();

    private static final String[] ARG_NAMES = {"store_path", "source_repo_name", "results_repo_name"};

    private IBucket<Birth> births;
    private HashMap<String, Integer> name_counts = new HashMap<>();
    private NameCache name_cache = new NameCache();

    public TriangleDistancePlotter(String store_path, String source_repo_name) throws Exception {

        System.out.println("Examining surnames of births");

        initialise(store_path, source_repo_name);
    }


    /**
     * Fisher-Yates algorithm with O(n) time complexity
     * Permutes the given array
     *
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


    private void plotDistances(Pair<Birth> sample, Function2args<Birth,Birth,Double> distance, Function<Birth, String> selector) throws BucketException, IOException {

        Birth record1 = sample.first;
        Birth record2 = sample.second;

        String fname = selector.apply(record1) + selector.apply(record2);
        String results_path = "/Users/al/Desktop/distances/" + fname + ".csv";

        Path path = Paths.get(results_path);

        DataSet data = new DataSet(new ArrayList<>(Arrays.asList(new String[]{"name", "Dp1", "Dp2", "x_coord", "y_coord"})));

        double distance_between_pivots = distance.apply(record1, record2);

        addRow(data, selector.apply( record1 ), 0, distance_between_pivots, 0, 0);                 // name, distance to p1, distance to p2, coordinate
        addRow(data, selector.apply( record2 ), distance_between_pivots, 0, distance_between_pivots, 0);      // name, distance to p1, distance to p2, coordinate

        for (Birth birth : births.getInputStream()) {

            String birth_name = clean(selector.apply(birth));

            if ((!clean(selector.apply(record1)).equals("") && !clean(selector.apply(record2)).equals("") && !clean(selector.apply(birth)).equals(""))) {
                double distance1 = distance.apply(record1, birth);
                double distance2 = distance.apply(record2, birth);

                double x = calculateXCoord(distance_between_pivots, distance1, distance2);
                double y = calculateYCoord(distance1, x);

                addRow(data, selector.apply(birth), distance1, distance2, x, y);
            }
        }
        createFileIfDoesNotExist(path);
        data.print(path);
    }

    /**
     * Calculates the Y coordinate of the third point of a triangle with vertices (0,0), (0,AB)
     *
     * @param AB
     * @param AC
     * @param BC
     * @return
     */
    private static double calculateXCoord(double AB, double AC, double BC) {
        return (square(AB) + square(AC) - square(BC)) / (2 * AB);
    }

    /**
     * Calculates the X coordinate of the third point of a triangle with vertices (0,0), (0,AB)
     *
     * @param AC
     * @return
     */
    private static double calculateYCoord(double AC, double yccord) {
        return Math.sqrt(square(AC) - square(yccord));
    }

    private static double square(double x) {
        return x * x;
    }

    private void addRow(DataSet data, String rep, double JS_distance1, double JS_distance2, double xcoord, double ycoord) {

        data.addRow(rep, Double.toString(JS_distance1), Double.toString(JS_distance2), Double.toString(xcoord), Double.toString(ycoord));
    }

    private String clean(String name) {
        return name.replace(" ", "");  // get rid of white space
    }


    private void initialise(String store_path, String source_repo_name) throws Exception {

        this.store_path = store_path;
        this.source_repo_name = source_repo_name;

        store = new Store(Paths.get(store_path));
        source_repo = store.getRepository(source_repo_name);
        births = source_repo.getBucket("birth_records", Birth.class);
    }

    private Birth makeBirth( String fathers_surname ) {
        Birth b = new Birth();
        b.put(Birth.FATHERS_FORENAME,fathers_surname);
        return b;
    }

    private Birth makeBirth( String fathers_firstname, String fathers_surname, String mothers_firstname, String mothers_surname ) {
        Birth b = new Birth();
        b.put(Birth.FATHERS_FORENAME,fathers_firstname);
        b.put(Birth.FATHERS_SURNAME,fathers_surname);
        b.put(Birth.MOTHERS_FORENAME,mothers_firstname);
        b.put(Birth.MOTHERS_MAIDEN_SURNAME,mothers_surname);
        return b;
    }

    public void doCalculations(Function2args<Birth, Birth, Double> distance,Function<Birth, String> selector ) throws BucketException, IOException {
        Pair<Birth>[] fathers_surnames = new Pair[]{
                new Pair(makeBirth("Tolmie"), makeBirth("Kircaldy")),
                new Pair(makeBirth("Mcgaw"), makeBirth("Boyce")),
                new Pair(makeBirth("MacDougald"), makeBirth("MacDugald")),
                new Pair(makeBirth("MacQueen"), makeBirth("McQueen"))

        };

        Pair[] father_mother_surnames = new Pair[]{

                // Pairs of names separated by unique separators = *!+

                new Pair(makeBirth("alexander", "mcleannan", "marion", "mccaskill"),
                        makeBirth("alexander", "mclennan", "marion", "mccaskill")), // close
                new Pair(makeBirth("colin", "murchison", "margaret", "mcqueen"),
                        makeBirth("colin", "murchison", "margaret", "macqueen")), //  close
                new Pair(makeBirth("duncan", "mckinnon", "peggy", "mckinnon"),
                        makeBirth("niel", "mcgillivray", "marion", "anderson")), // far away
                new Pair(makeBirth("malcolm", "macdonald", "catherine", "maclean"),
                        makeBirth("niel", "macleod", "marion", "macleod")) // far away - close to max distance
        };


        for (int i = 0; i < 4; i++) {

            Pair<Birth> pivots = father_mother_surnames[i];
            String fname = selector.apply( pivots.first ) + selector.apply( pivots.second );
            plotDistances(pivots, distance, selector);
        }
    }

    @FunctionalInterface
    interface Function2args<A, B, C> {
        public C apply (A a, B b);
    }

    private static double checkDistance( String s1, String s2 ) {
        if( s1 == null || s2 == null || s1.equals("") || s2.equals("") ) {
            return 0;
        } else {
            return js2.distance( s1,s2 );
        }
    }

    public static Function2args<Birth, Birth, Double> fathers_surname_distance = (b1,b2) -> {

        return checkDistance( surnameSelector( b1 ).toLowerCase(), surnameSelector( b2 ).toLowerCase() );
    };

    public static Function2args<Birth, Birth, Double> all_parents_surname_distance = (b1,b2) -> {

        return ( checkDistance( b1.getString(Birth.FATHERS_FORENAME).toLowerCase(), b2.getString(Birth.FATHERS_FORENAME).toLowerCase() ) +
                 checkDistance( b1.getString(Birth.FATHERS_SURNAME).toLowerCase(), b2.getString(Birth.FATHERS_SURNAME).toLowerCase() ) +
                 checkDistance( b1.getString(Birth.MOTHERS_FORENAME).toLowerCase(), b2.getString(Birth.MOTHERS_FORENAME).toLowerCase() ) +
                 checkDistance( b1.getString(Birth.MOTHERS_MAIDEN_SURNAME).toLowerCase(), b2.getString(Birth.MOTHERS_MAIDEN_SURNAME).toLowerCase() ) ) / 4;
    };


    public static void main(String[] args) throws Exception {

        TriangleDistancePlotter dc = new TriangleDistancePlotter("/DigitisingScotland/Storr-root", "Skye");
        //dc.doCalculations(            fathers_surname_distance,CompareMetics::surnameSelector);
        dc.doCalculations(            all_parents_surname_distance,CompareMetics::parentsnamesSelector);
    }

}
