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

/**
 * This class blocks on streams of Role records.
 * The categories of blocking are: firstname, lastname
 * <p/>
 *
 * Created by al on 17/08/16.
 */
public class FNLNOverActor extends AbstractBlocker<Role> {

    public FNLNOverActor(final IBucket<Role> roleBucket, final IRepository output_repo, final Class<Role> clazz) throws BucketException, RepositoryException, IOException {

        super(roleBucket.getInputStream(), output_repo, clazz);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for FATHER
     */
    public String[] determineBlockedBucketNamesForRecord(final Role record) {

        // Only operates over role records.

        final String normalised_forename = normaliseName(record.getForename());
        final String normalised_surname = normaliseName(record.getSurname());

        String bucket_name = concatenate(normalised_forename, normalised_surname);
        return new String[]{bucket_name};
    }
}

