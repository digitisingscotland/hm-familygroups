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

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IIdtoLXPMap;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Attempt to create an end to end linkage framework
 * Created by al on 24/4/2017
 */
public class EndToEndLinker {

    private static final String[] ARG_NAMES = {"store_path", "source_repo_name", "results_repo_name"};
    private static final float DEFAULT_DISTANCE_THRESHOLD = 8.0F;

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
    private IIdtoLXPMap<Marriages> pchild_id_to_parents_marriages_map;
    private IIdtoLXPMap<Marriages> pprimary_on_marriage_to_parents_marriage;

    private float distance_threshold;

    public EndToEndLinker(String store_path, String source_repo_name, String results_repo_name ) throws Exception {

        System.out.println("Initialising");
        initialise(store_path, source_repo_name, results_repo_name);
        linkSiblings();                         // form simple families - sets of siblings - no parent ids
        linkChildOnBirthToParentsMarriage();    // find corresponding marriage records from births - links parents to children
        linkBirthToOwnMarriage();           // link from birth certificate of a baby to own marriage certificate
        linkPrimaryOnMarriageToOwnDeath();      // find death records corresponding to marriage records
        linkChildOnBirthToOwnDeath();           // link in death records.
        linkPrimaryOnMarriageToParentsMarriage(); // link marriage of children with marriage of parents.
        System.out.println("Finished");
    }

    private void initialiseBuckets() throws RepositoryException {

        linked_siblings_bucket = results_repo.getBucket( "families",Family.class );
        marriages_bucket = results_repo.getBucket( "marriages",Marriages.class );
        deaths_list_bucket = results_repo.getBucket( "deaths",Deaths.class );

        pbirth_to_family_map = results_repo.getIdtoLXPMap("birth_to_family_map", Family.class);
        pmarriage_to_death_map = results_repo.getIdtoLXPMap("marriage_to_death_map", Deaths.class);
        pbirth_to_death_map = results_repo.getIdtoLXPMap("birth_to_death_map", Deaths.class);
        pbirth_to_marriage_map = results_repo.getIdtoLXPMap("birth_to_marriage_map", Marriages.class);
        pchild_id_to_parents_marriages_map = results_repo.getIdtoLXPMap("child_id_to_parents_marriages_map", Marriages.class);
        pprimary_on_marriage_to_parents_marriage = results_repo.getIdtoLXPMap("primary_on_marriage_to_parents_marriage", Marriages.class);
    }


    private void initialise(String store_path, String source_repo_name, String results_repo_name) throws Exception {

        this.store_path = store_path;
        this.source_repo_name = source_repo_name;
        this.distance_threshold = DEFAULT_DISTANCE_THRESHOLD;

        store = new Store( Paths.get(store_path) );
        results_repo = store.getRepository(results_repo_name);

        initialiseBuckets();
    }


    private void linkSiblings() throws Exception { // Checked.

        BirthBirthNNFamilyMerger matcher = new BirthBirthNNFamilyMerger(store_path, source_repo_name, "NamesMarriageOriginal", 9, 3, 9);
        matcher.compute( false );
        Map<Long, Family> birth_to_family_map = matcher.getId_to_family_map();

        // put all the families into a linked siblings results bucket

        for( Family family : birth_to_family_map.values() ) {
            try {
                linked_siblings_bucket.makePersistent(family);
            } catch( BucketException e ) {
                ErrorHandling.error("Attempt to commit duplicate family id: " + family.getId() );
            }
        }

        pbirth_to_family_map.injestMap( birth_to_family_map );
    }

    private void linkChildOnBirthToParentsMarriage() throws Exception { // Checked.

        BirthParentsMarriageThresholdNN matcher = new BirthParentsMarriageThresholdNN(store_path, source_repo_name);
        matcher.compute( false );
        Map<Long, List<Marriage>> baby_to_parents_marriage_map = matcher.getBirth_to_marriage_map();

        Set<Map.Entry<Long, List<Marriage>>> entry_set = baby_to_parents_marriage_map.entrySet();
        if( entry_set.size() > 0 ) { // loop below fails if set is empty!
            for (Map.Entry<Long, List<Marriage>> entry : baby_to_parents_marriage_map.entrySet()) {

                Marriages m = new Marriages(entry.getValue());
                marriages_bucket.makePersistent(m);
                pchild_id_to_parents_marriages_map.put(entry.getKey(), m.getThisRef());
            }
        }
    }

    private void linkBirthToOwnMarriage() throws Exception {    // Checked.
        BirthOwnMarriageThresholdNN matcher = new BirthOwnMarriageThresholdNN(store_path, source_repo_name);
        matcher.compute(false);
        Map<Long, List<Marriage>> birth_to_marriage_map = matcher.getBirth_to_marriage_map();

        for (Map.Entry<Long, List<Marriage>> entry : birth_to_marriage_map.entrySet()) {

            Marriages m = new Marriages( entry.getValue() );
            marriages_bucket.makePersistent(m);
            pbirth_to_marriage_map.put( entry.getKey(),m.getThisRef() );
        }
    }

    private void linkPrimaryOnMarriageToOwnDeath() throws Exception {    // Checked.
        MarriageDeathThresholdNN matcher = new MarriageDeathThresholdNN(store_path, source_repo_name, deaths_list_bucket);
        matcher.compute( false );
        Map<Long, Deaths> marriage_to_death_map = matcher.getMarriageToDeathMap();

        pmarriage_to_death_map.injestMap( marriage_to_death_map );
    }

    private void linkChildOnBirthToOwnDeath() throws Exception {
        BirthDeathThresholdNN matcher = new BirthDeathThresholdNN(store_path, source_repo_name, distance_threshold);
        matcher.compute( false );
        Map<Birth, Death> birth_to_death_map = matcher.getBirth_to_death_map();

        for (Map.Entry<Birth, Death> entry : birth_to_death_map.entrySet()) {

            Death d = entry.getValue();
            pbirth_to_death_map.put(entry.getKey().getId(), d.getThisRef());
        }
    }

    private void linkPrimaryOnMarriageToParentsMarriage() throws Exception {
        MarriageMarriageThresholdNN matcher = new MarriageMarriageThresholdNN(store_path, source_repo_name);
        matcher.compute( false );
        Map<Long, List<Marriage>> marriage_to_marriage_map = matcher.getMarriageToMarriageMap();

        for (Map.Entry<Long, List<Marriage>> entry : marriage_to_marriage_map.entrySet()) {
            Marriages m = new Marriages( entry.getValue() );
            marriages_bucket.makePersistent(m);
            pprimary_on_marriage_to_parents_marriage.put( entry.getKey(),m.getThisRef() );
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];
            String results_repo_name = args[2];

            new EndToEndLinker(store_path, repo_name, results_repo_name);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
        }
    }
}
