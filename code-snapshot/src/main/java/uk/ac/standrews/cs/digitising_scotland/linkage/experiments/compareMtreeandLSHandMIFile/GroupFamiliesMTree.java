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

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.BirthBirthNNFamilyMerger;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Family;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Deaths;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriages;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IIdtoLXPMap;

import java.nio.file.Paths;

/**
 * Group families using M-Trees
 * Created by al on 19/9/2017
 */
public class GroupFamiliesMTree {

    private static final String[] ARG_NAMES = {"store_path", "source_repo_name"};

    private Store store;
    private String store_path;
    private String source_repo_name;

    private IBucket<Family> linked_siblings_bucket;
    private IBucket<Marriages> marriages_bucket;
    private IBucket<Deaths> deaths_list_bucket;

    private IIdtoLXPMap<Marriages> pbirth_to_marriage_map;
    private IIdtoLXPMap<Family> pbirth_to_family_map;
    private IIdtoLXPMap<Death> pmarriage_to_death_map;
    private IIdtoLXPMap<Deaths> pbirth_to_death_map;
    private IIdtoLXPMap<Marriages> pchild_id_to_parents_marriages_map;
    private IIdtoLXPMap<Marriages> pprimary_on_marriage_to_parents_marriage;

    protected RecordRepository record_repository;


    public GroupFamiliesMTree(String store_path, String source_repo_name ) throws Exception {

        System.out.println("Initialising");
        record_repository = new RecordRepository(store_path, source_repo_name);
        initialise(store_path, source_repo_name );

        linkSiblings();                         // form simple families - sets of siblings - no parent ids

        System.out.println("Finished");
    }

    private void linkSiblings() throws Exception { // Checked.

        BirthBirthNNFamilyMerger matcher = new BirthBirthNNFamilyMerger(store_path, source_repo_name, "NamesOriginal", 9, 20, 9);
        matcher.compute( false );
        matcher.printFamilies();
        matcher.printLinkageStats();
    }


    private void initialise(String store_path, String repo_name ) throws RepositoryException {

        this.store_path = store_path;
        this.source_repo_name = repo_name;

        store = new Store( Paths.get(store_path) );

    }

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];

            new GroupFamiliesMTree(store_path, repo_name);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
        }
    }


}
