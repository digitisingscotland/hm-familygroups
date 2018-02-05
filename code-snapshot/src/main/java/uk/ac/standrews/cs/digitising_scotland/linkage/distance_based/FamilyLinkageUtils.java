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
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Family;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.utilities.ClassificationMetrics;

import java.util.*;

public class FamilyLinkageUtils extends LinkageUtils {

    public Map<Long, Family> getId_to_family_map() {
        return id_to_family_map;
    }

    public Map<Long, Family> id_to_family_map = new HashMap<>();     // Maps from an id to family: id used for different purposes in different contexts

    public FamilyLinkageUtils(String store_path_string, String repo_name) throws Exception {

            super(store_path_string, repo_name);

    }

    public void printFamilies() throws BucketException {

        printFamilies(record_repository.births, id_to_family_map);
    }

    private static LinkageStats createLinkageStats(List<FamilyLinkResult> family_link_results) throws BucketException {

        // Record the number of members in each assigned family.
        Map<String, Integer> assigned_family_member_counts = new HashMap<>();

        // Record the number of members in each real family (i.e. determined by demographers).
        Map<String, Integer> real_family_member_counts = new HashMap<>();

        // Count the individuals for which we failed to assign a family id.
        int number_of_people_missing_from_assigned_families = 0;

        // Count the individuals for which the demographers failed to assign a family id.
        int number_of_people_missing_from_real_families = 0;

        LinkageCounts linkage_counts = new LinkageCounts();

        for (FamilyLinkResult result1 : family_link_results) {

            if (present(result1.assigned_family_id)) {
                incrementFamilyCount(assigned_family_member_counts, result1.assigned_family_id);

            } else {
                number_of_people_missing_from_assigned_families++;
            }

            if (present(result1.real_family_id)) {
                incrementFamilyCount(real_family_member_counts, result1.real_family_id);

            } else {
                number_of_people_missing_from_real_families++;
            }

            for (FamilyLinkResult result2 : family_link_results) {
                if (result1 != result2) {
                    updateLinkageCounts(linkage_counts, result1, result2);
                }
            }
        }

        return new LinkageStats(linkage_counts.true_positives, linkage_counts.false_positives, linkage_counts.false_negatives, number_of_people_missing_from_assigned_families, number_of_people_missing_from_real_families, assigned_family_member_counts.values(), real_family_member_counts.values());
    }

    private static boolean present(String id) {

        return id != null && id.length() > 0;
    }

    private static void incrementFamilyCount(Map<String, Integer> family_member_counts, String id) {

        if (family_member_counts.containsKey(id)) {
            family_member_counts.put(id, family_member_counts.get(id) + 1);

        } else {
            family_member_counts.put(id, 1);
        }
    }

    private static void printFamilyStats(Collection<Integer> family_sizes, int missing) {

        try {
            System.out.println(String.format("Number of families                : %d", family_sizes.size()));
            System.out.println(String.format("Max-size of families              : %d", Collections.max(family_sizes)));
            System.out.println(String.format("Min-size of families              : %d", Collections.min(family_sizes)));
            System.out.println(String.format("Mean-size of families             : %.1f", mean(family_sizes)));
            System.out.println(String.format("Individuals with missing families : %d", missing));
            System.out.println();

        } catch (Exception e) {
            System.out.println("No families");
        }
    }

    private static void updateLinkageCounts(LinkageCounts linkage_counts, FamilyLinkResult result1, FamilyLinkResult result2) {

        boolean real_families_same = present(result1.real_family_id)
                && present(result2.real_family_id)
                && result1.real_family_id.equals(result2.real_family_id);
        boolean assigned_families_same = present(result1.assigned_family_id)
                && present(result2.assigned_family_id)
                && result1.assigned_family_id.equals(result2.assigned_family_id);

        if (assigned_families_same) {

            if (real_families_same) {
                linkage_counts.true_positives++;

            } else {
                linkage_counts.false_positives++;
            }
        } else {

            if (real_families_same) {
                linkage_counts.false_negatives++;
            }
        }
    }


    public void printLinkageStats() throws BucketException {

        LinkageStats linkage_stats = createLinkageStats(loadFamilyLinkResults(record_repository.births, id_to_family_map));
        System.out.println("Assigned family stats");
        printFamilyStats(linkage_stats.getSizesOfAssignedFamilies(), linkage_stats.getNumberOfPeopleMissingFromAssignedFamilies());

        System.out.println("Real family stats");
        printFamilyStats(linkage_stats.getSizesOfRealFamilies(), linkage_stats.getNumberOfPeopleMissingFromRealFamilies());

        System.out.println("True Positives  : " + linkage_stats.getTruePositives());
        System.out.println("False Positives : " + linkage_stats.getFalsePositives());
        System.out.println("False Negatives : " + linkage_stats.getFalseNegatives());

        if ((linkage_stats.getTruePositives() + linkage_stats.getFalsePositives()) == 0 || (linkage_stats.getTruePositives() + linkage_stats.getFalseNegatives()) == 0) {
            System.out.println("Cannot calculate precision and recall.");

        } else {

            System.out.println(String.format("Precision       : %.2f", ClassificationMetrics.precision(linkage_stats.getTruePositives(), linkage_stats.getFalsePositives())));
            System.out.println(String.format("Recall          : %.2f", ClassificationMetrics.recall(linkage_stats.getTruePositives(), linkage_stats.getFalseNegatives())));
            System.out.println(String.format("F1 Measure      : %.2f", ClassificationMetrics.F1(linkage_stats.getTruePositives(), linkage_stats.getFalsePositives(), linkage_stats.getFalseNegatives())));
        }
    }
    /**
     * Display the  families in CSV format
     * All generated family tags are empty for unmatched families
     * Tests that families do not appear in map more than once, which can occur in some experiments.
     */
    private static void printFamilies(IBucket<Birth> births, Map<Long, Family> person_to_family_map) throws BucketException {

        System.out.println("Generated family id\tDemographer family id\tRecord id\tForname\tSurname\tDOB\tPOM\tDOM\tFather's forename\tFather's surname\tMother's forename\tMother's maidenname");

        for (Birth birth_record : births.getInputStream()) {

            Family family = person_to_family_map.get(birth_record.getId());

            System.out.println((family != null ? family.getId() : "") + "\t" + birth_record.getString(Birth.FAMILY) + "\t" + birth_record.getString(Birth.ORIGINAL_ID) + "\t" + birth_record.getString(Birth.FORENAME) + "\t" + birth_record.getString(Birth.SURNAME) + "\t" + birth_record.getDOB() + "\t" + birth_record.getPlaceOfMarriage() + "\t" + birth_record.getDateOfMarriage() + "\t" + birth_record.getFathersForename() + "\t" + birth_record.getFathersSurname() + "\t" + birth_record.getMothersForename() + "\t" + birth_record.getMothersMaidenSurname());
        }
    }


    private static List<FamilyLinkResult> loadFamilyLinkResults(IBucket<Birth> births, Map<Long, Family> person_to_family_map) throws BucketException {

        List<FamilyLinkResult> results = new ArrayList<>();

        for (Birth birth_record : births.getInputStream()) {

            final long birth_record_id = birth_record.getId();
            final String assigned_family_id = person_to_family_map.containsKey(birth_record_id) ? String.valueOf(person_to_family_map.get(birth_record_id).getId()) : null;

            results.add(new FamilyLinkResult(String.valueOf(birth_record_id), assigned_family_id, birth_record.getString(Birth.FAMILY)));
        }
        return results;
    }

    private static class LinkageStats {

        private final int true_positives;
        private final int false_positives;
        private final int false_negatives;
        private final int number_of_people_missing_from_assigned_families;
        private final int number_of_people_missing_from_real_families;

        private final Collection<Integer> sizes_of_assigned_families;
        private final Collection<Integer> sizes_of_real_families;

        private LinkageStats(int true_positives, int false_positives, int false_negatives, int number_of_people_missing_from_assigned_families, int number_of_people_missing_from_real_families, Collection<Integer> sizes_of_assigned_families, Collection<Integer> sizes_of_real_families) {

            this.true_positives = true_positives;
            this.false_positives = false_positives;
            this.false_negatives = false_negatives;
            this.number_of_people_missing_from_assigned_families = number_of_people_missing_from_assigned_families;
            this.number_of_people_missing_from_real_families = number_of_people_missing_from_real_families;
            this.sizes_of_assigned_families = sizes_of_assigned_families;
            this.sizes_of_real_families = sizes_of_real_families;
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

        int getNumberOfPeopleMissingFromAssignedFamilies() {
            return number_of_people_missing_from_assigned_families;
        }

        int getNumberOfPeopleMissingFromRealFamilies() {
            return number_of_people_missing_from_real_families;
        }

        Collection<Integer> getSizesOfAssignedFamilies() {
            return sizes_of_assigned_families;
        }

        Collection<Integer> getSizesOfRealFamilies() {
            return sizes_of_real_families;
        }
    }

    private static class LinkageCounts {

        int true_positives;
        int false_positives;
        int false_negatives;
    }

    private static class FamilyLinkResult {

        final String birth_record_id;
        final String assigned_family_id;
        final String real_family_id;

        FamilyLinkResult(String birth_record_id, String assigned_family_id, String real_family_id) {

            this.birth_record_id = birth_record_id;
            this.assigned_family_id = assigned_family_id;
            this.real_family_id = real_family_id;
        }
    }
}
