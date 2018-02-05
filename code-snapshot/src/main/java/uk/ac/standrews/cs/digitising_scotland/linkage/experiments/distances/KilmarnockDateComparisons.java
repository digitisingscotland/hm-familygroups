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
package uk.ac.standrews.cs.digitising_scotland.linkage.experiments.distances;

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.FamilyLinkageUtils;
import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.BirthMarriageDateDistance;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.utilities.m_tree.Distance;

import java.lang.invoke.MethodHandles;
import java.util.Set;

public class KilmarnockDateComparisons extends FamilyLinkageUtils {

    private static final String[] ARG_NAMES = {"store_path","repo_name"};
    private static final int NUMBER_OF_BIRTHS_TO_PROCESS = 50000;

    public KilmarnockDateComparisons(String store_path_string, String repo_name) throws Exception {
        super(store_path_string, repo_name);
    }

    private void evaluateDateComparison() throws Exception {

        System.out.println("LEVENSHTEIN_DATE_DISTANCE:");
        evaluateDateComparison(BirthMarriageDateDistance.LEVENSHTEIN_DATE_DISTANCE);

        System.out.println("LEVENSHTEIN_DATE_DISTANCE_WITH_NULL_FILTERING:");
        evaluateDateComparison(BirthMarriageDateDistance.LEVENSHTEIN_DATE_DISTANCE_WITH_NULL_FILTERING);

        System.out.println("LEVENSHTEIN_DATE_DISTANCE_WITH_DIFFERENTIAL_NULL_FILTERING:");
        evaluateDateComparison(BirthMarriageDateDistance.LEVENSHTEIN_DATE_DISTANCE_WITH_DIFFERENTIAL_NULL_FILTERING);

        System.out.println("NUMERICAL_DATE_DISTANCE:");
        evaluateDateComparison(BirthMarriageDateDistance.NUMERICAL_DATE_DISTANCE);

        System.out.println("NUMERICAL_DATE_DISTANCE_WITH_NULL_FILTERING:");
        evaluateDateComparison(BirthMarriageDateDistance.COMPOSITE_DATE_DISTANCE_WITH_NULL_FILTERING);

        System.out.println("NUMERICAL_DATE_DISTANCE:");
        evaluateDateComparison(BirthMarriageDateDistance.NUMERICAL_DATE_DISTANCE);

        System.out.println("NUMERICAL_DATE_DISTANCE_THRESHOLD:");
        evaluateDateComparison(BirthMarriageDateDistance.NUMERICAL_DATE_DISTANCE_THRESHOLD);

        System.out.println("NUMERICAL_YEAR_THRESHOLD1:");
        evaluateDateComparison(BirthMarriageDateDistance.NUMERICAL_YEAR_THRESHOLD1);

        System.out.println("NUMERICAL_YEAR_THRESHOLD2:");
        evaluateDateComparison(BirthMarriageDateDistance.NUMERICAL_YEAR_THRESHOLD2);

        System.out.println("NUMERICAL_YEAR_THRESHOLD3:");
        evaluateDateComparison(BirthMarriageDateDistance.NUMERICAL_YEAR_THRESHOLD3);

        System.out.println("NUMERICAL_YEAR_THRESHOLD4:");
        evaluateDateComparison(BirthMarriageDateDistance.NUMERICAL_YEAR_THRESHOLD4);

        System.out.println("NUMERICAL_YEAR_THRESHOLD5:");
        evaluateDateComparison(BirthMarriageDateDistance.NUMERICAL_YEAR_THRESHOLD5);

        System.out.println("NUMERICAL_YEAR_THRESHOLD6:");
        evaluateDateComparison(BirthMarriageDateDistance.NUMERICAL_YEAR_THRESHOLD6);
    }

    private void evaluateDateComparison(Distance<Birth> distance_metric) throws Exception {

        BirthMarriageDateDistance.resetCache();

        Set<Birth> birth_set = loadBirths(NUMBER_OF_BIRTHS_TO_PROCESS);

        float total_distance_within_families = 0;
        float total_distance_between_families = 0;

        float max_distance_within_families = 0;
        float min_distance_between_families = Float.MAX_VALUE;

        long number_of_links = 0;
        long number_of_non_links = 0;

        for (Birth birth1 : birth_set) {
            for (Birth birth2 : birth_set) {
                if (birth1 != birth2) {

                    if (birth1.get(Birth.FAMILY).equals(birth2.get(Birth.FAMILY))) {

                        float date_distance = distance_metric.distance(birth1, birth2);

                        total_distance_within_families += date_distance;
                        number_of_links++;

                        if (date_distance > max_distance_within_families) {
                            max_distance_within_families = date_distance;
                        }
                    }
                }
            }
        }

        for (Birth birth1 : birth_set) {
            for (Birth birth2 : birth_set) {
                if (birth1 != birth2) {

                    if (!birth1.get(Birth.FAMILY).equals(birth2.get(Birth.FAMILY))) {

                        float date_distance = distance_metric.distance(birth1, birth2);

                        total_distance_between_families += date_distance;
                        number_of_non_links++;

                        if (date_distance < min_distance_between_families) {
                            min_distance_between_families = date_distance;
                        }
                    }
                }
            }
        }

        System.out.println(String.format("Number of people considered:    %d", birth_set.size()));
        System.out.println(String.format("Number of links considered:     %d", number_of_links));
        System.out.println(String.format("Number of non-links considered: %d", number_of_links));
        System.out.println(String.format("Average distance for links:     %.1f", total_distance_within_families / number_of_links));
        System.out.println(String.format("Average distance for non-links: %.1f", total_distance_between_families / number_of_non_links));
        System.out.println();
    }

    private void compute() throws Exception {

        timedRun("Evaluating date comparison", () -> {
            evaluateDateComparison();
            return null;
        });
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        Experiment experiment = new Experiment(ARG_NAMES, args, MethodHandles.lookup().lookupClass());

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];

            KilmarnockDateComparisons matcher = new KilmarnockDateComparisons(store_path,repo_name);

            experiment.printDescription();

            matcher.compute();

        } else {
            experiment.usage();
        }
    }
}
