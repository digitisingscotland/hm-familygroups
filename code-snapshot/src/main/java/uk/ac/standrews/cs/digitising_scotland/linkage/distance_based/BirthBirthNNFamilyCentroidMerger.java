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

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.GFNGLNBFNBMNPOMDOMDistanceOverBirth;
import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.GFNGLNBFNBMNPOMDOMDistanceOverFamily;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Family;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;
import uk.ac.standrews.cs.utilities.m_tree.MTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by al on 10/03/2017.
 */
public class BirthBirthNNFamilyCentroidMerger extends BirthBirthThresholdNN {

    private static final String[] ARG_NAMES = {"store_path", "repo_name", "family_distance_threshold", "max_family_size", "family_merge_distance_threshold"};

    private int max_family_size;
    private float family_merge_distance_threshold;

    public BirthBirthNNFamilyCentroidMerger(String store_path, String repo_name, float match_family_distance_threshold, int max_family_size, float family_merge_distance_threshold) throws Exception {

        super(store_path, repo_name, match_family_distance_threshold);

        this.max_family_size = max_family_size;
        this.family_merge_distance_threshold = family_merge_distance_threshold;
    }

    public void compute( boolean show_progress ) throws Exception {

        if( show_progress ) {
            timedRun("Creating Birth MTree", () -> {
                createBirthMTree(new GFNGLNBFNBMNPOMDOMDistanceOverBirth(), true);
                return null;
            });

            timedRun("Forming families from Birth-Birth links", () -> {
                formFamilies(true);
                mergeFamilies();
                return null;
            });

        } else {
            createBirthMTree(new GFNGLNBFNBMNPOMDOMDistanceOverBirth(), false);
            formFamilies(false);
            mergeFamilies();

        }
    }

    private void mergeFamilies() {

        Map<Long, Family> family_id_to_families = new HashMap<>(); // Maps from family id to family.

        MTree<Family> familyMTree = new MTree<>(new GFNGLNBFNBMNPOMDOMDistanceOverFamily());
        for (Family f : id_to_family_map.values()) {
            familyMTree.add(f);
        }

        for (Family f : id_to_family_map.values()) {

            int pool_size = 1; // the size of the pool in which we are examining families to merge.

            // These 2 lines initialise the search and are repeated in the if at the end of the while
            // Might be able to make this cleaner but at least I think I understand it! - al
            List<DataDistance<Family>> dds = familyMTree.nearestN(f, pool_size);
            int index = 0; // index in dds - the next family to look at in the search

            // This code sweeps out (in pools of 5) in circles looking for families to merge - we stop when the families we are finding are outside the threshold

            while (dds.get(index).distance < family_merge_distance_threshold && f.getSiblings().size() < max_family_size) {

                Family other = dds.get(index).value; // next family to compare with the current.

                System.out.println("Merged family:" + f.getFathersForename() + " " + f.getFathersSurname() + " " + f.getMothersForename() + " " + f.getMothersMaidenSurname());
                System.out.println("         with:" + other.getFathersForename() + " " + other.getFathersSurname() + " " + other.getMothersForename() + " " + other.getMothersMaidenSurname());

                // merge the families.
                f.addSiblings(other.getSiblings());

                index++;
                if (index == dds.size()) { // we are at the end of the list - get the next circle of Families.
                    pool_size += 5;
                    dds = familyMTree.nearestN(f, pool_size); // get the next circle of families (including the ones we have already seen).
                    dds = dds.subList(pool_size - 1, dds.size()); // chop off the families we have already looked at.
                    index = 0;
                }
            }
            family_id_to_families.put(f.getId(), f); // put the merged (or otherwise) family into the new map - slightly inefficient but easier to code
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
