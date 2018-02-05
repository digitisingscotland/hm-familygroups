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
package uk.ac.standrews.cs.digitising_scotland.linkage.importers.census;

import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.CensusPair;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.utilities.dataset.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.CensusPair.*;

/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 24/1/2018
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class CensusExtractImporter extends RecordImporter {

    /**
     * @param census_pairs the bucket into which to import
     * @param pairs_source_path containing the source records
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public int importCensusPairs(IBucket<CensusPair> census_pairs, String pairs_source_path) throws IOException, RecordFormatException, BucketException {

        DataSet data = new DataSet(Paths.get(pairs_source_path));
        int count = 0;

        for (List<String> record : data.getRecords()) {

            CensusPair census_pair = importCensusPair(data, record);
            census_pairs.makePersistent(census_pair);
            count++;
        }

        return count;
    }

    /**
     * Fills in a record.
     */
    private CensusPair importCensusPair(DataSet data, List<String> record) throws IOException, RecordFormatException, IllegalKeyException {

        CensusPair census_pair = new CensusPair();

        addAvailableSingleFields(data, record, census_pair, getRecordMap());

        return census_pair;
    }


    public HashMap<Integer, String> getRecordMap() {
        return RECORD_LABEL_MAP;
    }

    // unused in this code but abstract in RecordImporter
    public int[] getUnavailableRecords() {
        return new int[0];
    }

//    census61.year	census61.sch	census61.ID
//    census61.fname	census61.sname	census61.stanxname	census61.stansname
//    census71.year	census71.sch	census71.ID
//    census71.fname	census71.sname	census71.stanxname	census71.stansname

    private static final HashMap<Integer,String> RECORD_LABEL_MAP = new HashMap<>();
    static {

        RECORD_LABEL_MAP.put( CENSUS_YEAR_1, "year1" );

        RECORD_LABEL_MAP.put( SCHEDULE_ID1, "schedule1" );

        RECORD_LABEL_MAP.put( PERSON_ID1, "id1" );

        RECORD_LABEL_MAP.put( FIRSTNAME1, "fname1" );

        RECORD_LABEL_MAP.put( SURNAME1, "sname1" );

        RECORD_LABEL_MAP.put( FIRSTNAME_CLEAN1, "stanxname1" );

        RECORD_LABEL_MAP.put( SURNAME_CLEAN1, "stansname1" );

        // *********************************

        RECORD_LABEL_MAP.put( CENSUS_YEAR_2, "year2" );

        RECORD_LABEL_MAP.put( SCHEDULE_ID2, "schedule2" );

        RECORD_LABEL_MAP.put( PERSON_ID2, "id2" );

        RECORD_LABEL_MAP.put( FIRSTNAME2, "fname2" );

        RECORD_LABEL_MAP.put( SURNAME2, "sname2" );

        RECORD_LABEL_MAP.put( FIRSTNAME_CLEAN2, "stanxname2" );

        RECORD_LABEL_MAP.put( SURNAME_CLEAN2, "stansname2" );
    }

}
