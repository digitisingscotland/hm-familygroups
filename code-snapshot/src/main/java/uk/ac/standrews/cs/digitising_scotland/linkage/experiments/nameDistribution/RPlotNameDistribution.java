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
package uk.ac.standrews.cs.digitising_scotland.linkage.experiments.nameDistribution;

import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Family;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Deaths;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriages;
import uk.ac.standrews.cs.digitising_scotland.linkage.rUtilities.Rcaller;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IIdtoLXPMap;

import java.io.PrintStream;
import java.nio.file.Paths;

/**
 * Group families using M-Trees
 * Created by al on 19/9/2017
 */
public class RPlotNameDistribution {

    private static final String[] ARG_NAMES = {"store_path", "source_repo_name", "birth_fieldname", "output_data_filename", "image_path"};

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

    private Levenshtein levenshtein = new Levenshtein();

    private static String script_path = "src/main/scripts/R/render.R";


    public RPlotNameDistribution(String store_path, String source_repo_name, int field, String data_path, String image_path ) throws Exception {

        record_repository = new RecordRepository(store_path, source_repo_name);
        initialise(store_path, source_repo_name );

        examineBirths( field, data_path );
        System.out.println( "Calling: " + "Rcaller.callR( " + script_path + "," + data_path + "," + image_path +  "," +  "\"" + field + "(" + source_repo_name + ") distribution\" )" );
        int return_val = Rcaller.callR( script_path, data_path, image_path, field + " (" + source_repo_name + ") distribution" );
        System.out.println( "R call exited with value " + return_val );
    }

    private void examineBirths( int field, String file_name ) throws Exception { // Checked.

        try ( PrintStream stream = new PrintStream(Paths.get(file_name).toFile()); ) {

            try {
                for (Birth birth1 : record_repository.births.getInputStream()) {

                    for (Birth birth2 : record_repository.births.getInputStream()) {

                        String field1 = birth1.getString(field).split(" ")[0]; // first field up to separator
                        String field2 = birth2.getString(field).split(" ")[0]; // first field up to separator;

                        if (!birth1.equals(birth2) && !field1.equals("") && !field2.equals("")) {
                            stream.println(field1 + "\t" + field2 + "\t" + levenshtein.distance(birth1.getString(field), birth2.getString(field)));
                        }
                    }
                }
            } catch (KeyNotFoundException e) {
                System.err.println("Error in field name: " + field);
                return;
            }
        }
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
            int field = Integer.parseInt(args[2]);
            String data_path = args[3];
            String image_path = args[4];

            new RPlotNameDistribution(store_path, repo_name, field, data_path, image_path);

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
        }
    }

}
