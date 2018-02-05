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

package uk.ac.standrews.cs.digitising_scotland.linkage.distance_based;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.utilities.ClassificationMetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by al on 21/09/2017.
 */
public class BirthDeathLinkageUtils extends LinkageUtils {


    Map<Birth,Death> birthdeathmap = new HashMap<Birth,Death>();

    public BirthDeathLinkageUtils(String store_path_string, String repo_name) throws Exception {
        super(store_path_string, repo_name);
    }

    private static boolean present(String id) {

        return id != null && id.length() > 0;
    }

    public void printLinkageStats() throws BucketException {
        LinkageStats linkage_stats = createLinkageStats(loadLinkageResults(record_repository.births, birthdeathmap));

        System.out.println("True Positives  : " + linkage_stats.getTruePositives());
        System.out.println("False Positives : " + linkage_stats.getFalsePositives());
        System.out.println("False Negatives : " + linkage_stats.getFalseNegatives());
        System.out.println("Total :           " + linkage_stats.getTotal());

        if ((linkage_stats.getTruePositives() + linkage_stats.getFalsePositives()) == 0 || (linkage_stats.getTruePositives() + linkage_stats.getFalseNegatives()) == 0) {
            System.out.println("Cannot calculate precision and recall.");

        } else {

            System.out.println(String.format("Precision       : %.2f", ClassificationMetrics.precision(linkage_stats.getTruePositives(), linkage_stats.getFalsePositives())));
            System.out.println(String.format("Recall          : %.2f", ClassificationMetrics.recall(linkage_stats.getTruePositives(), linkage_stats.getFalseNegatives())));
            System.out.println(String.format("F1 Measure      : %.2f", ClassificationMetrics.F1(linkage_stats.getTruePositives(), linkage_stats.getFalsePositives(), linkage_stats.getFalseNegatives())));
        }

    }

    public void printMTreeCSVLinkageStats(String name, String store_path, String source_repo_name, float distance_threshold,long starttime) throws BucketException {
        long timetaken = System.currentTimeMillis() - starttime;

        LinkageStats linkage_stats = createLinkageStats(loadLinkageResults(record_repository.births, birthdeathmap));

        int true_positive = linkage_stats.getTruePositives();
        int false_positive = linkage_stats.getFalsePositives();
        int false_negative = linkage_stats.getFalseNegatives();
        int total = linkage_stats.getTotal();

        double precision = 0.0F;
        double recall = 0.0F;
        double f = 0.0F;

        if (!((true_positive + false_positive) == 0 || (true_positive + false_negative) == 0)) {


            precision =  ClassificationMetrics.precision(true_positive, false_positive);
            recall = ClassificationMetrics.recall(true_positive, false_negative);
            f = ClassificationMetrics.F1(true_positive, false_positive, false_negative);
        }

        System.out.print( name + "\t" );
        System.out.print(store_path + "\t");
        System.out.print(source_repo_name + "\t");
        System.out.print(distance_threshold + "\t");
        System.out.print(timetaken + "\t");
        System.out.print(true_positive + "\t");
        System.out.print(false_positive + "\t");
        System.out.print(false_negative + "\t");
        System.out.print(total + "\t");
        System.out.printf("%.3f\t",precision);
        System.out.printf("%.3f\t",recall);
        System.out.printf("%.3f",f);
        System.out.println();

    }

    public void printMIFileLinkageStats(String name, String store_path, String source_repo_name, float distance_threshold, int ki, int ks, long starttime) throws BucketException {
        long timetaken = System.currentTimeMillis() - starttime;

        LinkageStats linkage_stats = createLinkageStats(loadLinkageResults(record_repository.births, birthdeathmap));

        int true_positive = linkage_stats.getTruePositives();
        int false_positive = linkage_stats.getFalsePositives();
        int false_negative = linkage_stats.getFalseNegatives();
        int total = linkage_stats.getTotal();

        double precision = 0.0F;
        double recall = 0.0F;
        double f = 0.0F;

        if (!((true_positive + false_positive) == 0 || (true_positive + false_negative) == 0)) {


            precision =  ClassificationMetrics.precision(true_positive, false_positive);
            recall = ClassificationMetrics.recall(true_positive, false_negative);
            f = ClassificationMetrics.F1(true_positive, false_positive, false_negative);
        }

        System.out.print( name + "\t" );
        System.out.print(store_path + "\t");
        System.out.print(source_repo_name + "\t");
        System.out.print(distance_threshold + "\t");
        System.out.print(timetaken + "\t");
        System.out.print(true_positive + "\t");
        System.out.print(false_positive + "\t");
        System.out.print(false_negative + "\t");
        System.out.print(total + "\t");
        System.out.printf("%.3f\t",precision);
        System.out.printf("%.3f\t",recall);
        System.out.printf("%.3f",f);
        System.out.println();

    }


    public void printLSHCSVLinkageStats(String name, String store_path, String source_repo_name, float distance_threshold, int shingle_size, int number_of_bands, int band_size, long starttime) throws BucketException {
        long timetaken = System.currentTimeMillis() - starttime;

        LinkageStats linkage_stats = createLinkageStats(loadLinkageResults(record_repository.births, birthdeathmap));

        int true_positive = linkage_stats.getTruePositives();
        int false_positive = linkage_stats.getFalsePositives();
        int false_negative = linkage_stats.getFalseNegatives();
        int total = linkage_stats.getTotal();

        double precision = 0.0F;
        double recall = 0.0F;
        double f = 0.0F;

        if (!((true_positive + false_positive) == 0 || (true_positive + false_negative) == 0)) {


            precision =  ClassificationMetrics.precision(true_positive, false_positive);
            recall = ClassificationMetrics.recall(true_positive, false_negative);
            f = ClassificationMetrics.F1(true_positive, false_positive, false_negative);
        }

        System.out.print( name + "\t" );
        System.out.print(store_path + "\t");
        System.out.print(source_repo_name + "\t");
        System.out.print(distance_threshold + "\t");
        System.out.print(shingle_size + "\t");
        System.out.print(number_of_bands + "\t");
        System.out.print(band_size + "\t");
        System.out.print(timetaken + "\t");
        System.out.print(true_positive + "\t");
        System.out.print(false_positive + "\t");
        System.out.print(false_negative + "\t");
        System.out.print(total + "\t");
        System.out.printf("%.3f\t",precision);
        System.out.printf("%.3f\t",recall);
        System.out.printf("%.3f",f);
        System.out.println();
    }


    private static LinkageStats createLinkageStats(List<BirthDeathLinkResult>  link_results) throws BucketException {

        // Number of deaths for which the demographer birth-deaths match assigned.
        int true_positives = 0;

        // Number of deaths that we matched and the demographers did not.
        int false_positives = 0;

        // Number of deaths for we did not match but demographers did match
        int false_negatives = 0;

        // Number of deaths for nobody made a match
        int true_negatives = 0;

//        System.out.println( "Found " + link_results.size() + " births");

        for (BirthDeathLinkResult result : link_results) {

//            System.out.println( "b: " + result.birth_record_id + " d: " + result.real_death_id + " found d: " + result.assigned_death_id );

            if (present(result.assigned_death_id)) { // we have found a match
                if( result.assigned_death_id.equals( result.real_death_id ) ) {
                    true_positives++;
                } else {
                    false_positives++;
                }
            } else { // we did not find a match
                if( present( result.real_death_id ) ) { // demographers found a match
                    false_negatives++;
                } else {
                    true_negatives++;
                }

            }

        }

        return new LinkageStats(true_positives, false_positives, false_negatives, true_negatives);
    }


    private static List<BirthDeathLinkResult> loadLinkageResults(IBucket<Birth> births, Map<Birth, Death> birthdeathmap) throws BucketException {

        List<BirthDeathLinkResult> results = new ArrayList<>();

        for (Birth birth_record : births.getInputStream()) {

            final String original_birth_id = birth_record.getString(Birth.ORIGINAL_ID);
            Death death = birthdeathmap.get(birth_record);
            final String assigned_death_id = death == null ? null : death.getString(Death.ORIGINAL_ID);

            results.add(new BirthDeathLinkResult(original_birth_id, assigned_death_id, birth_record.getString(Birth.DEATH)));
        }
        return results;
    }

    public Map<Birth,Death> getBirthDeathMap() {
        return birthdeathmap;
    }

    public Map<Birth,Death> getBirth_to_death_map() {
        return birthdeathmap;
    }


    private static class LinkageStats {

        private final int true_positives;
        private final int false_positives;
        private final int false_negatives;
        private final int true_negatives;
        private final int total;

        private LinkageStats(int true_positives, int false_positives, int false_negatives, int true_negatives) {

            this.true_positives = true_positives;
            this.false_positives = false_positives;
            this.false_negatives = false_negatives;
            this.true_negatives = true_negatives;
            this.total = true_positives + false_positives + false_negatives + true_negatives;
        }

        int getTruePositives() {
            return true_positives;
        }

        int getFalsePositives() {
            return false_positives;
        }

        int getFalseNegatives() {
            return false_negatives;
        }

        int getTrueNegatives() {
            return true_negatives;
        }

        int getTotal() { return total; }
    }

    private static class BirthDeathLinkResult {

        final String birth_record_id;
        final String assigned_death_id;
        final String real_death_id;

        BirthDeathLinkResult(String birth_record_id, String assigned_death_id, String real_death_id) {

            this.birth_record_id = birth_record_id;
            this.assigned_death_id = assigned_death_id;
            this.real_death_id = real_death_id;
        }
    }



}
