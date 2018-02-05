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
package uk.ac.standrews.cs.digitising_scotland.linkage.experiments.compareMtreeandLSHandMIFile;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Family;
import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.FamilyLinkageUtils;
import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.GFNGLNBFNBMNDistanceOverBirth;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordRepository;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.utilities.lsh.MinHash;
import uk.ac.standrews.cs.utilities.m_tree.Distance;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

/**
 * Attempt to compare blocking and M-trees
 * Created by al on 18/9/17
 */
public class GroupFamiliesLSH {

    private static final String[] ARG_NAMES = {"store_path", "source_repo_name"};

    private Store store;
    private String store_path;
    private String source_repo_name;
    private IRepository results_repo;

    protected RecordRepository record_repository;
    protected MinHash<Birth> mh;

    private Distance<Birth> DistanceMetric = new GFNGLNBFNBMNDistanceOverBirth();
    public static final float DISTANCE_THRESHOLD = 8.0F; // same threshold used in BirthParentsMarriageThresholdNN
    private FamilyLinkageUtils lu;
    private Map<Long, Family> id_to_family_map;


    public GroupFamiliesLSH(String store_path, String source_repo_name ) throws Exception {

        System.out.println("Initialising");
        record_repository = new RecordRepository(store_path, source_repo_name);
        initialise(store_path, source_repo_name);

        linkSiblings();

        System.out.println("Finished");
    }

    private void linkSiblings() throws Exception {
        compute();
        lu.printFamilies();
        lu.printLinkageStats();
    }

    private void compute() throws Exception { // Checked.

        put_in_minhash();
        formFamilies();
    }

    /**
     * Try and form families from LSH blocking buckets
     */
    private void formFamilies() throws BucketException {
        for (Birth to_match : record_repository.births.getInputStream()) {

            String key = to_match.getFathersForename() + to_match.getFathersSurname() + to_match.getMothersForename() + to_match.getMothersMaidenSurname();
            Set<Birth> people_in_same_block = mh.getClosest(key);

            Family f = new Family(to_match);
            id_to_family_map.put(to_match.getId(),f);

            for( Birth matched : people_in_same_block ) {

                float distance = DistanceMetric.distance(to_match, matched);

                if (distance < DISTANCE_THRESHOLD && matched != to_match) {
                    // addBirthsToMap(to_match, matched);
                    f.addSibling(matched);
                    id_to_family_map.put(matched.getId(),f);
                }

            }
        }
    }

    private void put_in_minhash() throws BucketException {
        for (Birth next : record_repository.births.getInputStream()) {
            String key = next.getFathersForename() + next.getFathersSurname() + next.getMothersForename() + next.getMothersMaidenSurname();
            mh.put(key, next);
        }
    }


    private void initialise(String store_path, String repo_name) throws Exception {

        this.store_path = store_path;
        this.source_repo_name = repo_name;

        lu = new FamilyLinkageUtils(store_path,repo_name);
        id_to_family_map = lu.getId_to_family_map();

        store = new Store( Paths.get(store_path) );
        mh = new MinHash<Birth>(2,20,5); // TODO WHAT WERE THE DEFAULTS BEFORE CHANGE???

    }

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];

            new GroupFamiliesLSH(store_path, repo_name);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
        }
    }
}
