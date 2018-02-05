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

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.BirthDeathLinkageUtils;
import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.FNLNSFFNFLNMFNMMSDistanceOverDeath;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;
import uk.ac.standrews.cs.utilities.m_tree.Distance;
import uk.ac.standrews.cs.utilities.mi_file.MIFile;

import java.nio.file.Paths;
import java.util.*;

/**
 * Attempt to compare blocking and M-trees
 * Created by al on 20/9/17
 */
public class BirthDeathLinkageMIFile {

    private static final String[] ARG_NAMES = {"store_path", "source_repo_name", "distance_threshold", "ki", "ks"};

    private Store store;
    private String store_path;
    private String source_repo_name;
    private IRepository results_repo;

    protected RecordRepository record_repository;
    protected MIFile<Death> mi_file;

    private Distance<Death> distanceMetric = new FNLNSFFNFLNMFNMMSDistanceOverDeath();
    private BirthDeathLinkageUtils lu;
    private Map<Birth, Death> birthDeathtable;

    public float distance_threshold;


    public BirthDeathLinkageMIFile(long starttime, String store_path, String source_repo_name, float distance_threshold, int ki, int ks) throws Exception {

//        System.out.println("Initialising BirthDeathLinkageLSH: store path: " + store_path + " source repo: " + source_repo_name +
//                " distance threshold: " + distance_threshold + " shingle size: " +  shingle_size + " num bands: " +  number_of_bands + " band size: " +  band_size);
        record_repository = new RecordRepository(store_path, source_repo_name);
        initialise(store_path, source_repo_name, distance_threshold, ki, ks);

        compute();
        lu.printMIFileLinkageStats( this.getClass().getName(), store_path,  source_repo_name,  distance_threshold, ki, ks , starttime);

    }

    private void compute() throws Exception { // Checked.

        put_deaths_in_file();
        search_deaths_using_births_in_minhash();
    }

    /**
     * Try to find births using deaths in the LSH blocking buckets
     */
    private void search_deaths_using_births_in_minhash() throws BucketException {
        for (Birth birth_record : record_repository.births.getInputStream()) {

            String key = makeKey( birth_record );

            Death inferred_record = new Death();
            inferred_record.put( Death.FORENAME, birth_record.getForename() );
            inferred_record.put(Death.SURNAME, birth_record.getSurname());
            inferred_record.put( Death.SEX, birth_record.getSex());
            inferred_record.put(Death.FATHERS_FORENAME, birth_record.getFathersForename());
            inferred_record.put(Death.FATHERS_SURNAME, birth_record.getFathersSurname());
            inferred_record.put(Death.MOTHERS_FORENAME, birth_record.getMothersForename());
            inferred_record.put(Death.MOTHERS_MAIDEN_SURNAME, birth_record.getMothersMaidenSurname());

            List<DataDistance<Death>> neighbours = mi_file.nearestN(inferred_record, 5); // TODO This should not be necessary but we don't have the functions for now.

            Death closest_death = null;
            float closest_distance = Float.MAX_VALUE;

            for( DataDistance<Death> distance : neighbours ) {

                float d = distance.distance;

                if (d < distance_threshold && d < closest_distance) {

                    closest_death = distance.value;
                    closest_distance = d;

                }
            }

            if( closest_death != null ) {
//                System.out.println( "Found: " + closest_death.getForename() + closest_death.getSurname() );
                birthDeathtable.put(birth_record, closest_death);
            }
        }
    }

    private String makeKey(LXP record) {
        return record.getString( Death.FORENAME ) +
        record.getString(Death.SURNAME ) +
        record.getString( Death.SEX ) +
        record.getString(Death.FATHERS_FORENAME ) +
        record.getString(Death.FATHERS_SURNAME ) +
        record.getString(Death.MOTHERS_FORENAME ) +
        record.getString(Death.MOTHERS_MAIDEN_SURNAME );
    }

    private void put_deaths_in_file() throws Exception {
        for (Death record : record_repository.deaths.getInputStream()) {
            String key = makeKey( record );
            mi_file.add(record);
        }
    }


    private void initialise(String store_path, String repo_name, float distance_threshold, int ki, int ks) throws Exception {

        this.store_path = store_path;
        this.source_repo_name = repo_name;
        this.distance_threshold = distance_threshold;

        lu = new BirthDeathLinkageUtils(store_path,repo_name);
        birthDeathtable = lu.getBirthDeathMap();

        store = new Store( Paths.get(store_path) );

        Set<Death> reference_objects = pickPivots();

        mi_file = new MIFile<>( distanceMetric, reference_objects, ki, ks );

    }

    /**
     * Picks 2 * sqrt( num entries) of reference objects from the deaths.
     * @return the set of pivots
     */
    private Set<Death> pickPivots() throws BucketException {

        IBucket<Death> deaths = record_repository.deaths;
        int count_deaths = deaths.size();
        long required = Math.round( 2 * Math.sqrt(  count_deaths ) );
        // Now pick required from the set.
        List<Long> oids = deaths.getOids();       // oids are randomly allocated.
        Collections.shuffle(oids); // mix up the list

        Iterator<Long> iter = oids.iterator();

        Set<Death> pivots = new HashSet<>();
        for( int i = 0; i < required; i++ ) {
            pivots.add( deaths.getObjectById(iter.next()));
        }

        return pivots;
    }

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];
            float distance_threshold = Float.parseFloat(args[2]);
            int ki = Integer.parseInt(args[3]);
            int ks = Integer.parseInt(args[4]);

            new BirthDeathLinkageMIFile(System.currentTimeMillis(),store_path, repo_name, distance_threshold, ki, ks );

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
            System.err.print("Outputs: classname\tstore_path\tsource_repo_name\tdistance_threshold\ttimetaken\ttrue_positives\tfalse_positives\t");
            System.err.println("false_negatives\ttotal\tprecision\trecall\tfmeasure");
        }
    }
}
