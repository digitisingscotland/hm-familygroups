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
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.normalisation.DateNormalisation;
import uk.ac.standrews.cs.utilities.dataset.DataSet;

import java.util.HashMap;
import java.util.List;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth.*;

/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 21/3/2017.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class SkyeBirthRecordImporter extends BirthRecordImporter {

    private static final HashMap<Integer,String> RECORD_LABEL_MAP = new HashMap<>();
    static {

        RECORD_LABEL_MAP.put(ORIGINAL_ID, "ID");

        RECORD_LABEL_MAP.put(YEAR_OF_REGISTRATION, "year of reg");

        RECORD_LABEL_MAP.put(REGISTRATION_DISTRICT_SUFFIX, "RD Identifier");

        RECORD_LABEL_MAP.put(REGISTRATION_DISTRICT_SUFFIX, "register identifier");

        RECORD_LABEL_MAP.put(ENTRY, "IOS_Entry no");

        // *********************************

        RECORD_LABEL_MAP.put(FORENAME, "child's forname(s)");
        RECORD_LABEL_MAP.put(SURNAME, "child's surname");

        RECORD_LABEL_MAP.put(SEX, "sex");

        // *********************************

        RECORD_LABEL_MAP.put(MOTHERS_FORENAME, "mother's forename");
        RECORD_LABEL_MAP.put(MOTHERS_MAIDEN_SURNAME, "mother's maiden surname");

        // *********************************

        RECORD_LABEL_MAP.put(FATHERS_FORENAME, "father's forename");
        RECORD_LABEL_MAP.put(FATHERS_SURNAME, "father's surname");

        // *********************************

        RECORD_LABEL_MAP.put(PARENTS_DAY_OF_MARRIAGE, "day of parents' marriage");

        RECORD_LABEL_MAP.put(PARENTS_MONTH_OF_MARRIAGE, "month of parents' marriage");

        RECORD_LABEL_MAP.put(PARENTS_YEAR_OF_MARRIAGE, "year of parents' marriage");

        RECORD_LABEL_MAP.put(PARENTS_PLACE_OF_MARRIAGE, "place of parent's marriage 1");

        RECORD_LABEL_MAP.put(FATHERS_OCCUPATION, "father's occupation");

        RECORD_LABEL_MAP.put(INFORMANT_DID_NOT_SIGN, "did inform sign?");

        RECORD_LABEL_MAP.put(FAMILY, "family");

        RECORD_LABEL_MAP.put(DEATH, "Death");

        // *********************************

        RECORD_LABEL_MAP.put(FORENAME_CLEAN, "sxchild");
        RECORD_LABEL_MAP.put(SURNAME_CLEAN, "sschild");

        RECORD_LABEL_MAP.put(MOTHERS_FORENAME_CLEAN, "sxmother");
        RECORD_LABEL_MAP.put(MOTHERS_SURNAME_CLEAN, "ssmother");

        RECORD_LABEL_MAP.put(FATHERS_FORENAME_CLEAN, "sxfather");
        RECORD_LABEL_MAP.put(FATHERS_SURNAME_CLEAN, "ssfather");

    }

    private static final int[] UNAVAILABLE_RECORD_LABELS = {

            // Fields not present in Skye dataset.

            REGISTRATION_DISTRICT_NUMBER, ILLEGITIMATE_INDICATOR, CHANGED_FORENAME, CHANGED_SURNAME, MOTHERS_SURNAME, CHANGED_MOTHERS_MAIDEN_SURNAME, CORRECTED_ENTRY, IMAGE_QUALITY, BIRTH_ADDRESS, ADOPTION, ILLEGITIMATE_INDICATOR, BIRTH_YEAR, BIRTH_DAY
    };

    @Override
    public HashMap<Integer, String> getRecordMap() {
        return RECORD_LABEL_MAP;
    }

    @Override
    public int[] getUnavailableRecords() {
        return UNAVAILABLE_RECORD_LABELS;
    }

    @Override
    public void addAvailableCompoundFields(DataSet data, List<String> record, Birth birth) {

        birth.put(BIRTH_ADDRESS, combineFields(data, record, "address 1", "address 2"));
        birth.put(INFORMANT, combineFields(data, record, "forename of informant", "surname of informant"));
    }

    @Override
    public void addAvailableNormalisedFields(DataSet data, List<String> record, Birth birth) {

        String dob = data.getValue(record, "birth date"); // These are of the form 7/4/1861, 25/4/1861 etc.
        String[] dob_parts = dob.split("/");

        if (dob_parts.length > 0) {
            birth.put(BIRTH_DAY, dob_parts[0]);
        }
        if (dob_parts.length > 1) {
            birth.put(BIRTH_MONTH, DateNormalisation.normaliseMonth(dob_parts[1]));
        }
        if (dob_parts.length > 2) {
            birth.put(BIRTH_YEAR, dob_parts[2]);
        }
    }
}
