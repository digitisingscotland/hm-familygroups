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
public class MTreeBirthBirthWithinDistanceBFTGenerator { //extends KilmarnockExperiment {

//    public static final String[] ARG_NAMES = {"births_source_path", "deaths_source_path", "marriages_source_path"};
//    private  MTree<Birth> birthMTree;
//
//    public BirthBirthWithinDistanceBFTGenerator() throws StoreException, IOException, RepositoryException {
//    }
//
//    private void compute() throws Exception {
//
//        timedRun("Creating Birth MTree", () -> {
//            createBirthMTreeOverGFNGLNBFNBMNPOMDOM();
//            return null;
//        });
//
//        timedRun("Dumping bft.json", () -> {
//            dumpBFT();
//            return null;
//        });
//    }
//
//    private void dumpBFT() throws FileNotFoundException, UnsupportedEncodingException {
//
//        IInputStream<Birth> stream;
//        try {
//            stream = record_repository.births.getInputStream();
//        } catch (BucketException e) {
//            System.out.println("Exception whilst getting births");
//            return;
//        }
//
//        boolean first = true;
//
//        PrintWriter writer = new PrintWriter("bft.json", "UTF-8");
//        writer.print("{");
//
//        for (Birth b : stream) {
//            if (!first) {
//                writer.println(",");
//            }
//            else {
//                first = false;
//            }
//            // Calculate the neighbours of b, including b which is found in the rangeSearch
//            List<DataDistance<Birth>> bsNeighbours = birthMTree.rangeSearch(b, 3);  // pronounced b's neighbours.
//
//            writer.print("\"" + b.getId() + "\" : [");
//            for (int i = 0; i < bsNeighbours.size(); i++) {
//                writer.print("[" + bsNeighbours.get(i).value.getId() + ", " + Math.round(bsNeighbours.get(i).distance)+ "]");
//                if (i != bsNeighbours.size() - 1) {
//                    writer.print(",");
//                }
//            }
//            writer.print(']');
//        }
//        writer.println("}");
//        writer.close();
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
//            birthMTree.add( birth );
//        }
//    }
//
//    //***********************************************************************************
//
//    public static void main(String[] args) throws Exception {
//
//        Experiment experiment = new Experiment(ARG_NAMES, args, MethodHandles.lookup().lookupClass());
//
//        BirthBirthWithinDistanceBFTGenerator matcher = new BirthBirthWithinDistanceBFTGenerator();
//
//        if (args.length >= ARG_NAMES.length) {
//
//            String births_source_path = args[0];
//            String deaths_source_path = args[1];
//            String marriages_source_path = args[2];
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
