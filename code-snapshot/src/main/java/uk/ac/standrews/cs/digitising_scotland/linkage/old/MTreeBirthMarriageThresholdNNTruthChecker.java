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
public class MTreeBirthMarriageThresholdNNTruthChecker { // extends KilmarnockExperiment {

//    private static final String[] ARG_NAMES = {"births_source_path", "deaths_source_path", "marriages_source_path"};
//    public static final float distance_threshold = 8.0F;
//
//    private MTree<Marriage> marriageMtree;
//
//    private BirthSiblingsUsingMarriageThresholdNN() throws StoreException, RepositoryException, IOException {
//    }
//
//    private void compute() throws Exception {
//
//        timedRun("Creating Marriage MTree", () -> {
//            createMarriageMTreeOverGFNGLNBFNBMNPOMDOM();
//            return null;
//        });
//
//        timedRun("Forming families from Marriage-Birth links", () -> {
//            formFamilies();
//            showFamilies();
//            return null;
//        });
//    }
//
//    private void showFamilies() throws BucketException {
//
//        System.out.println("Number of families formed:" + new HashSet<>(person_to_family_map.values()).size());
//        printFamilies();
//    }
//
//    private void createMarriageMTreeOverGFNGLNBFNBMNPOMDOM() throws RepositoryException, BucketException, IOException {
//
//        marriageMtree = new MTree<>(new GFNGLNBFNBMNPOMDOMDistanceOverMarriage());
//
//        for (Marriage marriage : record_repository.marriages.getInputStream()) {
//            marriageMtree.add(marriage);
//        }
//    }
//
//    /**
//     * Try and form families from Marriage M Tree data_array
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
//            Marriage marriage_query = new Marriage();
//            marriage_query.put(Marriage.GROOM_FORENAME, b.getFathersForename());
//            marriage_query.put(Marriage.GROOM_SURNAME, b.getFathersSurname());
//            marriage_query.put(Marriage.BRIDE_FORENAME, b.getMothersForename());
//            marriage_query.put(Marriage.BRIDE_SURNAME, b.getMothersMaidenSurname());
//            marriage_query.put(Marriage.PLACE_OF_MARRIAGE, b.getPlaceOfMarriage());
//
//            marriage_query.put(Marriage.MARRIAGE_DAY, b.getString(Birth.PARENTS_DAY_OF_MARRIAGE));
//            marriage_query.put(Marriage.MARRIAGE_MONTH, b.getString(Birth.PARENTS_MONTH_OF_MARRIAGE));
//            marriage_query.put(Marriage.MARRIAGE_YEAR, b.getString(Birth.PARENTS_YEAR_OF_MARRIAGE));
//
//            DataDistance<Marriage> result = marriageMtree.nearestNeighbour(marriage_query);
//
//            if (result.distance < distance_threshold) {
//                addBirthToMap(person_to_family_map, result.value.getId(), b); // used the marriage id as a unique identifier.
//            }
//        }
//    }
//
//    /**
//     * Adds a birth record to a family map.
//     *
//     * @param map          the map to which the record should be added
//     * @param birth_record the record to add to the map
//     */
//    private void addBirthToMap(Map<Long, Family> map, Long key, Birth birth_record) {
//
//        if (map.containsKey(key)) { // have already seen a member of this family - so just add the birth to the family map
//            // could check here to ensure parents are the same etc.
//            Family f = map.get(key);
//            f.siblings.add(birth_record);
//        } else { // a new family we have not seen before
//            Family new_family = new Family(birth_record);
//            map.put(key, new_family);
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
//            BirthSiblingsUsingMarriageThresholdNN matcher = new BirthSiblingsUsingMarriageThresholdNN();
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
