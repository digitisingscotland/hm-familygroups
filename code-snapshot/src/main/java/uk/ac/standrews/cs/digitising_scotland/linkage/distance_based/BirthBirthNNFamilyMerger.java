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

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Family;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;
import uk.ac.standrews.cs.utilities.m_tree.Distance;
import uk.ac.standrews.cs.utilities.m_tree.MTree;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by al on 10/03/2017.
 */
public class BirthBirthNNFamilyMerger extends BirthBirthThresholdNN {

    private static final int NUMBER_OF_NEARBY_FAMILIES_TO_CONSIDER_FOR_MERGING = 15;

    private String distanceMethodStr;
    private int max_family_size;
    private float family_merge_distance_threshold;

    public BirthBirthNNFamilyMerger(String store_path, String repo_name, String distanceMethodStr, float match_family_distance_threshold, int max_family_size, float family_merge_distance_threshold) throws Exception {

        super(store_path, repo_name, match_family_distance_threshold);

        this.distanceMethodStr = distanceMethodStr;
        this.max_family_size = max_family_size;
        this.family_merge_distance_threshold = family_merge_distance_threshold;
    }

    private Distance<Birth> getBirthDistanceMethod() {
        // Values are: NamesOriginal, NamesClean, NamesMarriageOriginal, NamesMarriageClean
        if (distanceMethodStr.equals("NamesOriginal")) {
            return new GFNGLNBFNBMNDistanceOverBirth();
        }
        if (distanceMethodStr.equals("NamesClean")) {
            return new GFNGLNBFNBMNDistanceOverBirthClean();
        }
        if (distanceMethodStr.equals("NamesMarriageOriginal")) {
            return new GFNGLNBFNBMNPOMDOMDistanceOverBirth();
        }
        if (distanceMethodStr.equals("NamesMarriageClean")) {
            return new GFNGLNBFNBMNPOMDOMDistanceOverBirthClean();
        }
        return null;
    }

    private Distance<Family> getFamilyDistanceMethod() {
        // Values are: NamesOriginal, NamesClean, NamesMarriageOriginal, NamesMarriageClean
        if (distanceMethodStr.equals("NamesOriginal")) {
            return new GFNGLNBFNBMNDistanceOverFamily();
        }
        if (distanceMethodStr.equals("NamesClean")) {
            return new GFNGLNBFNBMNDistanceOverFamilyClean();
        }
        if (distanceMethodStr.equals("NamesMarriageOriginal")) {
            return new GFNGLNBFNBMNPOMDOMDistanceOverFamily();
        }
        if (distanceMethodStr.equals("NamesMarriageClean")) {
            return new GFNGLNBFNBMNPOMDOMDistanceOverFamilyClean();
        }
        return null;
    }

    public void compute( boolean show_progress ) throws Exception {

        createBirthMTree(getBirthDistanceMethod(),show_progress);
        formFamilies(show_progress);
        mergeFamilies();
    }

    private void mergeFamilies() {

        Map<Long, Family> family_id_to_families = new HashMap<>(); // Maps from family id to family.
        MTree<Family> familyMTree = new MTree<>(getFamilyDistanceMethod());

        // add families to family distance MTree
        for (Family f : id_to_family_map.values()) {
            familyMTree.add(f);
        }

        // Merge the families and put merged families into family_id_tofamilies
        for (Family f : id_to_family_map.values()) {

            for (DataDistance<Family> dd : familyMTree.nearestN(f, NUMBER_OF_NEARBY_FAMILIES_TO_CONSIDER_FOR_MERGING)) {

                if (dd.distance < family_merge_distance_threshold && dd.value.getSiblings().size() + f.getSiblings().size() < max_family_size ) {  // f.getSiblings().size() < max_family_size) {

                    Family other_family = dd.value;
//                    System.out.println("Merged family:" + f.getFathersForename() + " " + f.getFathersSurname() + " " + f.getMothersForename() + " " + f.getMothersMaidenSurname());
//                    System.out.println("         with:" + other_family.getFathersForename() + " " + other_family.getFathersSurname() + " " + other_family.getMothersForename() + " " + other_family.getMothersMaidenSurname());

                    for (Birth child : other_family.getSiblings()) { // merge the families.
                        f.addSibling(child);
                    }
                } else {
                    family_id_to_families.put(f.getId(), f); // put the merged (or otherwise family into the new map
                    break;
                }
            }
        }

        // finally create a new families hash map
        id_to_family_map = new HashMap<>(); // Maps from person id to family.

        // and insert all the people from family_id_tofamilies into families
        for (Family f : family_id_to_families.values()) {
            for (Birth child : f.getSiblings()) {
                id_to_family_map.put(child.getId(), f);
            }
        }
    }
}
