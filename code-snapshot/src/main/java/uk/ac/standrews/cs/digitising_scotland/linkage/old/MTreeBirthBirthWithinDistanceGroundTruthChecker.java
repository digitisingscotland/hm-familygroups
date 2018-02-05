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
package uk.ac.standrews.cs.digitising_scotland.linkage.old;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from BlockingLinker.
 * Created by al on 17/2/1017
 */
public class MTreeBirthBirthWithinDistanceGroundTruthChecker { //extends KilmarnockExperiment {

//    private static final String[] ARG_NAMES = {"births_source_path", "deaths_source_path", "marriages_source_path"};
//    private MTree<Birth> birthMTree;
//
//    private BirthBirthWithinDistanceGroundTruthChecker() throws StoreException, IOException, RepositoryException {
//    }
//
//    private void compute() throws Exception {
//
//        timedRun("Creating Birth MTree", () -> {
//            createBirthMTreeOverGFNGLNBFNBMNPOMDOM();
//            return null;
//        });
//
//        timedRun("Forming families from Birth-Birth links", () -> {
//            formFamilies();
//            printFamilies();
//            return null;
//        });
//
//        timedRun("Calculating linkage stats", () -> {
//            printLinkageStats();
//            return null;
//        });
//    }
//
//    private void createBirthMTreeOverGFNGLNBFNBMNPOMDOM() throws RepositoryException, BucketException, IOException {
//
//        System.out.println("Creating M Tree of births by AverageSimilarityOverSelectedFields...");
//
//        birthMTree = new MTree<>(new AverageSimilarityOverSelectedFields());
//
//        IInputStream<Birth> stream = record_repository.births.getInputStream();
//
//        for (Birth birth : stream) {
//
//            birthMTree.add(birth);
//        }
//    }
//
//    /**
//     * Try and form families from Birth M Tree data_array
//     */
//    private void formFamilies() {
//
//        IInputStream<Birth> stream;
//        try {
//            stream = record_repository.births.getInputStream();
//        } catch (BucketException e) {
//            System.out.println("Exception whilst getting births");
//            return;
//        }
//
//        for (Birth b : stream) {
//
//            // Calculate the neighbours of b, including b which is found in the rangeSearch
//            List<DataDistance<Birth>> bsNeighbours = birthMTree.rangeSearch(b, 10);  // pronounced b's neighbours.
//
//            // bs_neighbours_families is the set of families of neighbours that are different from bsFamily
//            Set<Family> bs_neighbours_families = new TreeSet<Family>();
//
//            Family bsFamily = id_to_family_map.get(b.getId()); // maybe null - is this right????
//
//            // Add all of the families from bsNeighbours to bs_neighbours_families
//            for (DataDistance<Birth> dd_to_bs_neighbour : bsNeighbours) {
//                Birth bsNeighbour = dd_to_bs_neighbour.value;
//                Family bs_neighbours_family = id_to_family_map.get(bsNeighbour.getId());
//                if (bs_neighbours_family != null && bs_neighbours_family != bsFamily) {
//                    bs_neighbours_families.add(bs_neighbours_family);
//                }
//            }
//
//            Family thisFamily;
//
//            if (bs_neighbours_families.size() == 1) { // just bsFamily in the set?
//                // there are no "competing" family ids for this group of people
//                // their id can remain the same
//
//                thisFamily = bs_neighbours_families.iterator().next();
//
//            } else {
//                // there are (zero or) multiple "competing" family ids for this group of people
//                // let's merge them
//                thisFamily = new Family(b);
//            }
//
//            //  make all of bsNeighbours be in thisFamily
//            for (DataDistance<Birth> dd : bsNeighbours) {
//                Birth person = dd.value;
//                id_to_family_map.put(person.getId(), thisFamily);
//            }
//
//            // if a person was previously in a different family, we merge them into thisFamily
//            for (Family bs_neighbours_familiy : bs_neighbours_families) {
//                for (Birth sibling : bs_neighbours_familiy.siblings) {
//
//                    if (id_to_family_map.containsKey(sibling.getId())) {
//                        id_to_family_map.put(sibling.getId(), thisFamily); //  replace person's family with the new one.
//                    }
//                }
//            }
//        }
//    }
//
//    //***********************************************************************************
//
//    public static void main(String[] args) throws Exception {
//
//        Experiment experiment = new Experiment(ARG_NAMES, args, MethodHandles.lookup().lookupClass());
//
//        if (args.length >= ARG_NAMES.length) {
//
//            String births_source_path = args[0];
//            String deaths_source_path = args[1];
//            String marriages_source_path = args[2];
//
//            BirthBirthWithinDistanceGroundTruthChecker matcher = new BirthBirthWithinDistanceGroundTruthChecker();
//
//            experiment.printDescription();
//
//            matcher.importRecords(births_source_path, deaths_source_path, marriages_source_path);
//            matcher.compute();
//
//        } else {
//            experiment.usage();
//        }
//    }
}
