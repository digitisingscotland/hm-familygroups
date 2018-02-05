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
package uk.ac.standrews.cs.digitising_scotland.linkage.importers.skye;

import uk.ac.standrews.cs.digitising_scotland.linkage.importers.BirthRecordImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.DeathRecordImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.MarriageRecordImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.DataSetImporter;

/**
 * Module to ingest BDM records into an initialised store.
 * Created by al on 25/3/2017.
 *
 * @author al@st-andrews.ac.uk
 */
public class SkyeDataSetImporter extends DataSetImporter {

    public SkyeDataSetImporter(String[] args) throws Exception {

        super(args);
    }

    @Override
    protected BirthRecordImporter getBirthImporter() {
        return new SkyeBirthRecordImporter();
    }

    @Override
    protected DeathRecordImporter getDeathImporter() {
        return new SkyeDeathRecordImporter();
    }

    @Override
    protected MarriageRecordImporter getMarriageImporter() {
        return new SkyeMarriageRecordImporter();
    }

    @Override
    protected String getDataSetName() {
        return "Skye";
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        new SkyeDataSetImporter(args).importRecords();
    }
}
