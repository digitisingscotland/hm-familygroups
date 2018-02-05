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
package uk.ac.standrews.cs.digitising_scotland.linkage.end_to_end;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Deaths;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Family;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriages;
import uk.ac.standrews.cs.storr.impl.BucketKind;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IIdtoLXPMap;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.nio.file.Paths;

/**
 * Initialisation of end to end linkage framework
 * Created by al on 19/7/2017
 */
public class InitialiseEndToEndLinker {

    private static final String[] ARG_NAMES = {"store_path", "source_repo_name", "results_repo_name"};

    private Store store;
    private String store_path;
    private String source_repo_name;
    private IRepository results_repo;

    private IBucket<Family> linked_siblings_bucket;
    private IBucket<Marriages> marriages_bucket;
    private IBucket<Deaths> deaths_list_bucket;

    private IIdtoLXPMap<Marriages> pbirth_to_marriage_map;
    private IIdtoLXPMap<Family> pbirth_to_family_map;
    private IIdtoLXPMap<Deaths> pmarriage_to_death_map;
    private IIdtoLXPMap<Deaths> pbirth_to_death_map;
    IIdtoLXPMap<Marriages> pchild_id_to_parents_marriages_map;
    IIdtoLXPMap<Marriages> pprimary_on_marriage_to_parents_marriage;

    public InitialiseEndToEndLinker(String store_path, String source_repo_name, String results_repo_name ) throws Exception {

        System.out.println("Initialising environment");
        initialise(store_path, source_repo_name, results_repo_name);

        System.out.println("Finished initialisation");
    }

    private void createBuckets() throws RepositoryException {

        linked_siblings_bucket = results_repo.makeBucket( "families", BucketKind.DIRECTORYBACKED, Family.class );
        marriages_bucket = results_repo.makeBucket( "marriages", BucketKind.DIRECTORYBACKED, Marriages.class );
        deaths_list_bucket = results_repo.makeBucket( "deaths", BucketKind.DIRECTORYBACKED, Deaths.class );

        pbirth_to_family_map = results_repo.makeIdtoLXPMap("birth_to_family_map", Family.class);
        pmarriage_to_death_map = results_repo.makeIdtoLXPMap("marriage_to_death_map", Deaths.class);
        pbirth_to_death_map = results_repo.makeIdtoLXPMap("birth_to_death_map", Deaths.class);
        pbirth_to_marriage_map = results_repo.makeIdtoLXPMap("birth_to_marriage_map", Marriages.class);
        pchild_id_to_parents_marriages_map = results_repo.makeIdtoLXPMap("child_id_to_parents_marriages_map", Marriages.class);
        pprimary_on_marriage_to_parents_marriage = results_repo.makeIdtoLXPMap("primary_on_marriage_to_parents_marriage", Marriages.class);
    }


    private void initialise(String store_path, String repo_name, String results_repo_name) throws Exception {

        this.store_path = store_path;
        this.source_repo_name = repo_name;

        store = new Store( Paths.get(store_path) );
        results_repo = store.makeRepository(results_repo_name);

        createBuckets();
    }


    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];
            String results_repo_name = args[2];

            new InitialiseEndToEndLinker(store_path, repo_name, results_repo_name);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
        }
    }
}
