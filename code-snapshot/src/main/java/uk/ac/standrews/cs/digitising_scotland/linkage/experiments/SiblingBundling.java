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
package uk.ac.standrews.cs.digitising_scotland.linkage.experiments;

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.ParentNamesFamily;
import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.GFNGLNBFNBMNDistanceOverParentNames;
import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.GFNGLNBFNBMNDistanceOverParentNamesFamilyClosest;
import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.GFNGLNBFNBMNDistanceOverParentNamesFamilyFurthest;
import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.GFNGLNBFNBMNDistanceOverParentNamesFamilyMean;
import uk.ac.standrews.cs.digitising_scotland.linkage.experiments.distances.Experiment;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;
import uk.ac.standrews.cs.utilities.m_tree.Distance;
import uk.ac.standrews.cs.utilities.m_tree.MTree;

import java.lang.invoke.MethodHandles;
import java.util.*;


public class SiblingBundling {

    public static final String[] ARG_NAMES = {"store_path", "repo_name",
            "family_forming_threshold", "family_merging_threshold", "maximum_family_size",
            "source_of_siblings", "source_of_families", "family_distance_method"};

    // command line options
    static String store_path;
    static String repo_name;
    static int family_forming_threshold;
    static int family_merging_threshold;
    static int maximum_family_size;
    static String source_of_siblings;
    static String source_of_families;
    static String family_distance_method;

    static RecordRepository record_repository;


    static List<ParentNames> allPotentialSiblings;          // all potential siblings. comes from source_of_siblings (either Birth/Death)

    static MTree<ParentNames> formingMTree;                 // this will be used when we initially form families
                                                            // comes from source_of_families (either Birth/Death/Marriage)

    static MTree<ParentNamesFamily> mergingMTree;           // this will be used when we merge families
                                                            // families are those formed in the first step

    // the families
    static Map<Long, ParentNamesFamily> families;
    static long nextFamilyId;


    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        Experiment experiment = new Experiment(ARG_NAMES, args, MethodHandles.lookup().lookupClass());

        if (args.length < ARG_NAMES.length) {
            experiment.usage();
        }

        for (int i = 0; i < ARG_NAMES.length; i++) {
            System.out.println(
                    String.format("Argument %d -- %-30s -- %s",
                            i, ARG_NAMES[i], args[i]));
        }

        store_path = args[0];
        repo_name = args[1];
        family_forming_threshold = Integer.parseInt(args[2]);
        family_merging_threshold = Integer.parseInt(args[3]);
        maximum_family_size = Integer.parseInt(args[4]);
        source_of_siblings = args[5];
        source_of_families = args[6];
        family_distance_method = args[7];

        record_repository = new RecordRepository(store_path, repo_name);

        families = new HashMap<>();
        nextFamilyId = 1;

        createMTree();
        formFamilies();
        mergeFamilies();
        reportResults();

        System.out.println("Finished");
    }

    private static void createMTree() throws BucketException, PersistentObjectException {

        if (source_of_siblings.equals("Birth")) {
            allPotentialSiblings = new ArrayList<>();
            for (Birth rec : record_repository.births.getInputStream()) {
                allPotentialSiblings.add(rec.toParentNames());
            }
        }
        if (source_of_siblings.equals("Death")) {
            allPotentialSiblings = new ArrayList<>();
            for (Death rec : record_repository.deaths.getInputStream()) {
                allPotentialSiblings.add(rec.toParentNames());
            }
        }

        Distance<ParentNames> distanceMethod = new GFNGLNBFNBMNDistanceOverParentNames();

        // We create the MTree depending on the value of source_of_families
        if (source_of_families.equals("Birth")) {
            formingMTree = new MTree<>(distanceMethod);
            for (Birth rec : record_repository.births.getInputStream()) {
                formingMTree.add(rec.toParentNames());
            }
        }
        if (source_of_families.equals("Death")) {
            formingMTree = new MTree<>(distanceMethod);
            for (Death rec : record_repository.deaths.getInputStream()) {
                formingMTree.add(rec.toParentNames());
            }
        }
        if (source_of_families.equals("Marriage")) {
            formingMTree = new MTree<>(distanceMethod);
            for (Marriage rec : record_repository.marriages.getInputStream()) {
                formingMTree.add(rec.toParentNames());
            }
        }

        if (family_distance_method.equals("Closest")) {
            mergingMTree = new MTree<>(new GFNGLNBFNBMNDistanceOverParentNamesFamilyClosest());
        }
        if (family_distance_method.equals("Furthest")) {
            mergingMTree = new MTree<>(new GFNGLNBFNBMNDistanceOverParentNamesFamilyFurthest());
        }
        if (family_distance_method.equals("Mean")) {
            mergingMTree = new MTree<>(new GFNGLNBFNBMNDistanceOverParentNamesFamilyMean());
        }

    }

    private static void formFamilies() throws BucketException, PersistentObjectException {
        for (ParentNames to_match : allPotentialSiblings) {
            Set<ParentNames> siblings = new HashSet<>();
            siblings.add(to_match);
//            List<DataDistance<ParentNames>> matchedList = formingMTree.rangeSearch(to_match, family_forming_threshold);
            List<DataDistance<ParentNames>> matchedList = formingMTree.nearestN(to_match, 1);
            for (DataDistance<ParentNames> matched : matchedList) {
                if (matched.value.getId() != to_match.getId() &&
                        matched.distance < family_forming_threshold &&
                        matched.value.getId() != to_match.getId()) {
                    System.out.println("[NOTICE] Forming family: " + to_match.getId() + " and " + matched.value.getId());
                    siblings.add(matched.value);
                }
            }
            if (siblings.size() > 1) {
                sameFamily(siblings);
            }
        }
    }

    private static void mergeFamilies() {

        // add families to family distance MTree
        for (ParentNamesFamily f : families.values()) {
            mergingMTree.add(f);
        }

        // Merge the families and put merged families into family_id_tofamilies
        for (ParentNamesFamily f : families.values()) {

            Set<ParentNames> siblings = new HashSet<>();
            siblings.addAll(f.getSiblings());

            for (DataDistance<ParentNamesFamily> dd : mergingMTree.rangeSearch(f, family_merging_threshold)) {
                if (dd.value.id != f.id &&
                        dd.distance < family_merging_threshold) {

                    ParentNamesFamily other_family = dd.value;
                    System.out.println("[NOTICE] Merging family: " + f.id + " and " + dd.value.id);

                    siblings.addAll(dd.value.getSiblings());

                }
            }
            sameFamily(siblings);
        }
    }

    private static void reportResults() {
        for (ParentNames p : allPotentialSiblings) {
            ParentNamesFamily pFamily = families.get(p.getId());
            String pFamilyId = "?";
            if (pFamily != null) {
                pFamilyId = String.valueOf(pFamily.id);
            }
            String demographerId = p.getDemographerFamilyId();
            if (demographerId == null) {
                demographerId = "?";
            } else if (demographerId.length() == 0) {
                demographerId = "?";
            }
            System.out.println("families\t" + p.getId() + "\t" + pFamilyId + "\t" + demographerId);
        }
    }

    private static void sameFamily(Set<ParentNames> people) {

        Set<ParentNames> allSiblings = new HashSet<>();
        Set<Long> allFamilyIds = new HashSet<>();

        for (ParentNames p : people) {
            if (p.getId() != -1) {
                allSiblings.add(p);
                ParentNamesFamily pFamily = families.get(p.getId());
                if (pFamily != null) {
                    for (ParentNames sibling : pFamily.getSiblings()) {
                        allSiblings.add(sibling);
                    }
                }
            }
        }

        if (allSiblings.size() <= maximum_family_size) {
            long thisFamilyId;
            if (allFamilyIds.size() == 1) {
                thisFamilyId = (long) allFamilyIds.toArray()[0];
            } else {
                thisFamilyId = nextFamilyId++;
            }

            ParentNamesFamily newFamily = new ParentNamesFamily(thisFamilyId, allSiblings);

            for (ParentNames sibling : allSiblings) {
                families.put(sibling.getId(), newFamily);
            }
        }
    }
}
