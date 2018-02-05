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
package uk.ac.standrews.cs.digitising_scotland.linkage.importers;


public abstract class DataSetImporter {

    private static final String[] ARG_NAMES = {"store_path", "repo_name", "births_path", "deaths_path", "marriages_path"};

    private String store_path;
    private String repo_name;
    private String birth_records_path;
    private String death_records_path;
    private String marriage_records_path;

    protected abstract BirthRecordImporter getBirthImporter();

    protected abstract DeathRecordImporter getDeathImporter();

    protected abstract MarriageRecordImporter getMarriageImporter();

    protected abstract String getDataSetName();

    public DataSetImporter(String store_path, String repo_name, String birth_records_path,String death_records_path, String marriage_records_path) throws Exception {

        init( store_path, repo_name, birth_records_path, death_records_path, marriage_records_path);
    }

    public DataSetImporter(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            init(args[0], args[1], args[2], args[3], args[4]);

        } else {

            usage();
        }
    }

    private void init(String store_path, String repo_name, String birth_records_path,String death_records_path, String marriage_records_path) throws Exception {

        this.store_path = store_path;
        this.repo_name = repo_name;
        this.birth_records_path = birth_records_path;
        this.death_records_path = death_records_path;
        this.marriage_records_path = marriage_records_path;
    }

    public void importRecords() throws Exception {

        System.out.println("Importing " + getDataSetName() + " records into repository: " + repo_name);
        System.out.println();

        RecordRepository record_repository = new RecordRepository(store_path, repo_name);

        int births_count = getBirthImporter().importBirthRecords(record_repository.births, birth_records_path);
        System.out.println("Imported " + births_count + " birth records");

        int deaths_count = getDeathImporter().importDeathRecords(record_repository.deaths, death_records_path);
        System.out.println("Imported " + deaths_count + " death records");

        int marriages_count = getMarriageImporter().importMarriageRecords(record_repository.marriages, marriage_records_path);
        System.out.println("Imported " + marriages_count + " marriage records");

        System.out.println();
        System.out.println("Complete");
    }

    private static void usage() {

        System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
    }
}
