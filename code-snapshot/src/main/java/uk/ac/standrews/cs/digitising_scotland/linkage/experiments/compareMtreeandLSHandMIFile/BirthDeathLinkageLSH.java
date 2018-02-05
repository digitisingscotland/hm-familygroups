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
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.utilities.lsh.MinHash;
import uk.ac.standrews.cs.utilities.m_tree.Distance;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

/**
 * Attempt to compare blocking and M-trees
 * Created by al on 20/9/17
 */
public class BirthDeathLinkageLSH {

    private static final String[] ARG_NAMES = {"store_path", "source_repo_name", "distance_threshold", "shingle_size", "number_of_bands", "band_size"};

    private Store store;
    private String store_path;
    private String source_repo_name;
    private IRepository results_repo;

    protected RecordRepository record_repository;
    protected MinHash<Death> mh;

    private Distance<Death> DistanceMetric = new FNLNSFFNFLNMFNMMSDistanceOverDeath();
    private BirthDeathLinkageUtils lu;
    private Map<Birth, Death> birthDeathtable;

    public float distance_threshold;
    private int shingle_size;
    private int number_of_bands;
    private int band_size;

    public BirthDeathLinkageLSH(long starttime, String store_path, String source_repo_name, float distance_threshold, int shingle_size, int number_of_bands, int band_size) throws Exception {

//        System.out.println("Initialising BirthDeathLinkageLSH: store path: " + store_path + " source repo: " + source_repo_name +
//                " distance threshold: " + distance_threshold + " shingle size: " +  shingle_size + " num bands: " +  number_of_bands + " band size: " +  band_size);
        record_repository = new RecordRepository(store_path, source_repo_name);
        initialise(store_path, source_repo_name, distance_threshold, shingle_size, number_of_bands, band_size);

        compute();
        lu.printLSHCSVLinkageStats( this.getClass().getName(), store_path,  source_repo_name,  distance_threshold,  shingle_size,  number_of_bands,  band_size, starttime);

    }

    private void compute() throws Exception { // Checked.

        put_deaths_in_minhash();
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

//            System.out.println( "Looking for " + birth_record.getForename() + birth_record.getSurname() );
            Set<Death> deceased_in_same_block = mh.getClosest(key);

            Death closest_death = null;
            float closest_distance = Float.MAX_VALUE;

            for( Death matched_death : deceased_in_same_block ) {

                float distance = DistanceMetric.distance(inferred_record, matched_death);

                if (distance < distance_threshold && distance < closest_distance) {

                    closest_death = matched_death;
                    closest_distance = distance;

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

    private void put_deaths_in_minhash() throws BucketException {
        for (Death record : record_repository.deaths.getInputStream()) {
            String key = makeKey( record );
            mh.put(key, record);
        }
    }


    private void initialise(String store_path, String repo_name, float distance_threshold, int shingle_size, int number_of_bands, int band_size) throws Exception {

        this.store_path = store_path;
        this.source_repo_name = repo_name;
        this.distance_threshold = distance_threshold;
        this.shingle_size = shingle_size;
        this.number_of_bands = number_of_bands;
        this.band_size = band_size;

        lu = new BirthDeathLinkageUtils(store_path,repo_name);
        birthDeathtable = lu.getBirthDeathMap();

        store = new Store( Paths.get(store_path) );
        mh = new MinHash<>(shingle_size,number_of_bands,band_size);

    }

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];
            float distance_threshold = Float.parseFloat(args[2]);
            int shingle_size = Integer.parseInt(args[3]);
            int number_of_bands = Integer.parseInt(args[4]);
            int band_size = Integer.parseInt(args[5]);

            new BirthDeathLinkageLSH(System.currentTimeMillis(),store_path, repo_name, distance_threshold, shingle_size, number_of_bands, band_size);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
            System.err.print("Outputs: classname\tstore_path\tsource_repo_name\tdistance_threshold\tshingle_size\tnumber_of_bands\tband_size\ttimetaken\ttrue_positives\tfalse_positives\t");
            System.err.println("false_negatives\ttotal\tprecision\trecall\tfmeasure");
        }
    }
}
