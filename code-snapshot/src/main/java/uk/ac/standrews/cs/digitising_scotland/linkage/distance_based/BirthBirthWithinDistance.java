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
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;
import uk.ac.standrews.cs.utilities.m_tree.MTree;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from BlockingLinker.
 * Created by al on 27/2/1017
 */
public class BirthBirthWithinDistance extends FamilyLinkageUtils {

    private MTree<Birth> birthMTree;

    public BirthBirthWithinDistance(String store_path, String repo_name) throws Exception {
        super(store_path,repo_name);
    }

    public void compute(boolean show_progress) throws Exception {

        if( show_progress ) {
            timedRun("Creating Birth MTree", () -> {
                createBirthMTreeOverGFNGLNBFNBMNPOMDOM();
                return null;
            });

            timedRun("Forming families from Birth-Birth links", () -> {
                formFamilies();
                return null;
            });

        } else {
            createBirthMTreeOverGFNGLNBFNBMNPOMDOM();
            formFamilies();
        }
    }

    private void createBirthMTreeOverGFNGLNBFNBMNPOMDOM() throws RepositoryException, BucketException, IOException {

        System.out.println("Creating M Tree of births by AverageSimilarityOverSelectedFields...");

        birthMTree = new MTree<Birth>(new GFNGLNBFNBMNPOMDOMDistanceOverBirth());

        IInputStream<Birth> stream = record_repository.births.getInputStream();

        for (Birth birth : stream) {

            birthMTree.add(birth);
        }
    }

    /**
     * Try and form families from Birth M Tree data_array
     */
    private void formFamilies() {

        IInputStream<Birth> stream;
        try {
            stream = record_repository.births.getInputStream();
        } catch (BucketException e) {
            System.out.println("Exception whilst getting births");
            return;
        }

        for (Birth b : stream) {

            // Calculate the neighbours of b, including b which is found in the rangeSearch
            List<DataDistance<Birth>> bsNeighbours = birthMTree.rangeSearch(b, 10);  // pronounced b's neighbours.

            // bs_neighbours_families is the set of families of neighbours that are different from bsFamily
            Set<Family> bs_neighbours_families = new TreeSet<Family>();

            Family bsFamily = id_to_family_map.get(b.getId()); // maybe null - is this right????

            // Add all of the families from bsNeighbours to bs_neighbours_families
            for (DataDistance<Birth> dd_to_bs_neighbour : bsNeighbours) {
                Birth bsNeighbour = dd_to_bs_neighbour.value;
                Family bs_neighbours_family = id_to_family_map.get(bsNeighbour.getId());
                if (bs_neighbours_family != null && bs_neighbours_family != bsFamily) {
                    bs_neighbours_families.add(bs_neighbours_family);
                }
            }

            Family thisFamily;

            if (bs_neighbours_families.size() == 1) { // just bsFamily in the set?
                // there are no "competing" family ids for this group of people
                // their id can remain the same

                thisFamily = bs_neighbours_families.iterator().next();

            } else {
                // there are (zero or) multiple "competing" family ids for this group of people
                // let's merge them
                thisFamily = new Family(b);
            }

            //  make all of bsNeighbours be in thisFamily
            for (DataDistance<Birth> dd : bsNeighbours) {
                Birth person = dd.value;
                id_to_family_map.put(person.getId(), thisFamily);
            }

            // if a person was previously in a different family, we merge them into thisFamily
            for (Family bs_neighbours_familiy : bs_neighbours_families) {
                for (Birth sibling : bs_neighbours_familiy.getSiblings()) {

                    if (id_to_family_map.containsKey(sibling.getId())) {
                        id_to_family_map.put(sibling.getId(), thisFamily); //  replace person's family with the new one.
                    }
                }
            }
        }
    }
}
