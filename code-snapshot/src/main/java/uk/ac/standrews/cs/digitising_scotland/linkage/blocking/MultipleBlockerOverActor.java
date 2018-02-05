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
package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.*;
import java.util.*;

/**
 * This class blocks on streams of Person records.
 * The categories of blocking are:
 * <p/>
 * 1.  FNLN     First name, Last name
 * 2.	FNLNMF   First name, Last name, Mothers First name
 * 3.	FNLNFF   First name, Last name, Fathers First name
 * 4.	FNLNMFFF First name, Last name, Mothers First name, Fathers First name
 * 5. 	FNMF     First name, Mothers First name
 * 6.  FNFF     First name, Fathers First name
 * 7.  FNFL     First name, Fathers Last name
 * 8.  MFMMFF   Mothers Fist name, Mothers Maiden name, Fathers First name  (not marriage)
 * <p/>
 * Created by al on 01/08/2014.
 */
public class MultipleBlockerOverActor extends AbstractBlocker<Role> {

    public MultipleBlockerOverActor(final IBucket<Role> roleBucket, final IRepository output_repo, Class<Role> clazz) throws BucketException, RepositoryException, IOException {

        super(roleBucket.getInputStream(), output_repo, clazz);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for FATHER
     */
    public String[] determineBlockedBucketNamesForRecord(final Role record) {

        // Only operates over role records

        String FN = normaliseName(record.getForename());
        String LN = normaliseName(record.getSurname());
        String FF = normaliseName(record.getFathersForename());
        String FL = normaliseName(record.getFathersSurname());
        String MF = normaliseName(record.getMothersForename());
        String MM = normaliseName(record.getMothersMaidenSurname());

        String FNLN = concatenate(FN, LN);
        String FNLNMF = concatenate(FNLN, MF);
        String FNLNFF = concatenate(FNLN, FF);
        String FNLNMFFF = concatenate(FNLN, MF, FF);
        String FNMF = concatenate(FN, MF);
        String FNFF = concatenate(FN, FF);
        String FNFL = concatenate(FN, FL);
        String MFMMFF = concatenate(MF, MM, FF);

        String[] blocked_names = new String[]{FNLN, FNLNMF, FNLNFF, FNLNMFFF, FNMF, FNFF, FNFL, MFMMFF};
        return deduplicate(blocked_names);
    }

    private String[] deduplicate(String[] blocked_names) {

        Set<String> deduped = new HashSet<String>();
        deduped.addAll(Arrays.asList(blocked_names));

        return deduped.toArray(new String[0]);
    }
}

