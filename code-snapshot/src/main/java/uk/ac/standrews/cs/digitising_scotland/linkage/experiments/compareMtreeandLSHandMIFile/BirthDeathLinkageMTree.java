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

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.BirthDeathThresholdNN;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Deaths;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Family;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriages;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IIdtoLXPMap;

import java.nio.file.Paths;

/**
 * Group families using M-Trees
 * Created by al on 19/9/2017
 */
public class BirthDeathLinkageMTree {

    private static final String[] ARG_NAMES = {"store_path", "source_repo_name", "distance_threshold"};

    private Store store;
    private String store_path;
    private String source_repo_name;
    private final float distance_threshold;

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

    public BirthDeathLinkageMTree(long starttime, String store_path, String source_repo_name, float distance_threshold ) throws Exception {

//        System.out.println("Initialising BirthDeathLinkageMTree: store path: " + store_path + " source repo: " + source_repo_name + " source repo: " +
//                " distance threshold: " + distance_threshold );
        record_repository = new RecordRepository(store_path, source_repo_name);
        this.store_path = store_path;
        this.source_repo_name = source_repo_name;
        this.distance_threshold = distance_threshold;

        store = new Store( Paths.get(store_path) );

        linkBirthDeath(starttime,distance_threshold);

//        System.out.println("Finished in " +  ( System.currentTimeMillis() - starttime) );
    }

    private void linkBirthDeath( long starttime, float distance_threshold ) throws Exception { // Checked.

        BirthDeathThresholdNN matcher = new BirthDeathThresholdNN(store_path, source_repo_name, distance_threshold);
        matcher.compute( false );
        matcher.printMTreeCSVLinkageStats( this.getClass().getName(), store_path, source_repo_name, distance_threshold, starttime );
    }


    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];
            float distance_threshold = Float.parseFloat(args[2]);

            new BirthDeathLinkageMTree(System.currentTimeMillis(), store_path, repo_name, distance_threshold);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
            System.err.print("Outputs: classname\tstore_path\tsource_repo_name\tdistance_threshold\ttimetaken\ttrue_positives\tfalse_positives\tfalse_negatives\ttotal\tprecision\trecall\tfmeasure");
        }
    }


}
