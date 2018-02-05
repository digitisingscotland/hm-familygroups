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
 * Output on std out matches between births and marriages in a csv format
 * The output is the number of matches between births and marriages at edit distances 0,1,2.. RANGE_MAX
 * File is derived from BlockingLinker.
 * Created by al on 17/2/1017
 */
public class MTreeBirthMarriageRangeCSVGenerator { //extends KilmarnockExperiment {

//    private static final String[] ARG_NAMES = {"births_source_path", "deaths_source_path", "marriages_source_path"};
//
//    final static int RANGE_MAX = 15;
//
//    private MTree<Marriage> marriageMtree;
//
//    private BirthMarriageRangeCSVGenerator() throws StoreException, IOException, RepositoryException {
//    }
//
//    private void compute() throws Exception {
//
//        timedRun("Creating Marriage MTree", () -> {
//            createMarriageMTreeOverGFNGLNBFNBMNPOMDOM();
//            return null;
//        });
//
//        timedRun("Outputting matches", () -> {
//            outputRangeSearchMatchesBetweenBirthsAndMarriages();
//            return null;
//        });
//    }
//
//    private void createMarriageMTreeOverGFNGLNBFNBMNPOMDOM() throws RepositoryException, BucketException, IOException {
//
//        marriageMtree = new MTree<>(new GFNGLNBFNBMNPOMDOMDistanceOverMarriage());
//
//        IInputStream<Marriage> stream = record_repository.marriages.getInputStream();
//
//        for (Marriage marriage : stream) {
//
//            marriageMtree.add(marriage);
//        }
//    }
//
//    /**
//     * Output number of matches between births and marriages in csv format.
//     */
//    private void outputRangeSearchMatchesBetweenBirthsAndMarriages() {
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
//            for (int range = 0; range < RANGE_MAX; ) {
//
//                List<DataDistance<Marriage>> results = marriageMtree.rangeSearch(marriage_query, range++);
//                System.out.print(results.size());
//                if (range != RANGE_MAX) {
//                    System.out.print(",");
//                }
//            }
//            System.out.println();
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
//            BirthMarriageRangeCSVGenerator matcher = new BirthMarriageRangeCSVGenerator();
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
