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
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Family;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.utilities.PercentageProgressIndicator;
import uk.ac.standrews.cs.utilities.ProgressIndicator;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;
import uk.ac.standrews.cs.utilities.m_tree.Distance;
import uk.ac.standrews.cs.utilities.m_tree.MTree;

import java.io.IOException;
import java.util.Map;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from BlockingLinker.
 * Created by al on 27/2/1017
 */
public class BirthBirthThresholdNN extends FamilyLinkageUtils {

    private MTree<Birth> birth_MTree;
    private float match_family_distance_threshold;

    //protected RecordRepository record_repository;

    public Map<Long, Family> getId_to_family_map() {
        return id_to_family_map;
    }


    public BirthBirthThresholdNN(String store_path, String repo_name, float match_family_distance_threshold) throws Exception {

        super(store_path, repo_name);
        this.match_family_distance_threshold = match_family_distance_threshold;
    }

    public void compute( boolean show_progress ) throws Exception {

        if( show_progress ) {
            timedRun("Creating Birth MTree", () -> {
                createBirthMTree(new GFNGLNBFNBMNPOMDOMDistanceOverBirth(), true);
                return null;
            });

            timedRun("Forming families from Birth-Birth links", () -> {
                formFamilies(true);
                return null;
            });

        } else {
            createBirthMTree(new GFNGLNBFNBMNPOMDOMDistanceOverBirth(), false);
            formFamilies(false);
        }
    }

    protected void createBirthMTree(Distance<Birth> distanceMethod, boolean show_progress) throws RepositoryException, BucketException, IOException {

        ProgressIndicator indicator = new PercentageProgressIndicator(10);
        indicator.setTotalSteps(getBirthsCount());

        birth_MTree = new MTree<>(distanceMethod);

        for (Birth birth : record_repository.births.getInputStream()) {

            birth_MTree.add(birth);
            if( show_progress ) { indicator.progressStep(); }
        }
    }

    /**
     * Try and form families from Birth M Tree data_array
     */
    protected void formFamilies( boolean show_progress ) throws BucketException {

        ProgressIndicator indicator = new PercentageProgressIndicator(10);
        indicator.setTotalSteps(getBirthsCount());

        for (Birth to_match : record_repository.births.getInputStream()) {

            DataDistance<Birth> matched = birth_MTree.nearestNeighbour(to_match);

            if (matched.distance < match_family_distance_threshold && matched.value != to_match) {
                addBirthsToMap(to_match, matched);
            }
            if( show_progress ) { indicator.progressStep(); }
        }
    }

    /**
     * Adds a birth record to a family map.
     *
     * @param searched the record that was used to search for a match
     * @param found_dd the data distance that was matched in the search
     */
    private void addBirthsToMap(Birth searched, DataDistance<Birth> found_dd) {

        Birth found = found_dd.value;

        long searched_key = searched.getId();
        long found_key = found.getId();

        if (!id_to_family_map.containsKey(searched_key) && !id_to_family_map.containsKey(found_key)) {

            // Not seen either birth before.
            // Create a new Family and add to map under both keys.
            Family new_family = new Family(searched);
            new_family.addSibling(found);
            id_to_family_map.put(searched_key, new_family);
            id_to_family_map.put(found_key, new_family);
            return;
        }

        // Don't bother with whether these are the same family or not, or if the added values are already in the set
        // Set implementation should deal with this.
        if (id_to_family_map.containsKey(searched_key) && !id_to_family_map.containsKey(found_key)) {

            // Already seen the searched birth => been found already
            Family f = id_to_family_map.get(searched_key);
            f.addSibling(found);
        }

        if (id_to_family_map.containsKey(found_key) && !id_to_family_map.containsKey(searched_key)) {

            // Already seen the found birth => been searched for earlier
            Family f = id_to_family_map.get(found_key);
            f.addSibling(searched);
        }
    }
}
