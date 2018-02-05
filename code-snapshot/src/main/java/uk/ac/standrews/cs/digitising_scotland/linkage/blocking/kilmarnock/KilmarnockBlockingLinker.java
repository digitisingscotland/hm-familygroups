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
package uk.ac.standrews.cs.digitising_scotland.linkage.blocking.kilmarnock;

import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.BlockingLinker;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.DataSetImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.kilmarnock.KilmarnockDataSetImporter;

/**
 * Attempt to create a linking framework
 * Created by al on 6/8/2014.
 */
public  class KilmarnockBlockingLinker extends BlockingLinker {

    public KilmarnockBlockingLinker(String births_source_path, String deaths_source_path, String marriages_source_path) throws Exception {
        super(births_source_path, deaths_source_path, marriages_source_path);
    }

    @Override
    protected DataSetImporter getDataSetImporter(String store_path, String repo_name, String birth_records_path, String death_records_path, String marriage_records_path) throws Exception {
        return new KilmarnockDataSetImporter(store_path, repo_name, birth_records_path, death_records_path, marriage_records_path);
    }

    public static void main(String[] args) throws Exception {

        String births_source_path = "/Digitising Scotland/KilmarnockBDM/births.csv";
        String deaths_source_path = "/Digitising Scotland/KilmarnockBDM/deaths.csv";
        String marriages_source_path = "/Digitising Scotland/KilmarnockBDM/marriages.csv";

        new KilmarnockBlockingLinker(births_source_path, deaths_source_path, marriages_source_path);
    }
}
