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

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.storr.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.storr.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

/**
 * This class blocks based on persons' first name, last name and first name of parents over streams of Birth records
 * Created by al on 02/05/2014. x
 */
public class FNLFFMFOverBirths extends AbstractBlocker<Birth> {

    public FNLFFMFOverBirths(final IBucket<Birth> birthsBucket, final IRepository output_repo) throws Exception {

        super(birthsBucket.getInputStream(), output_repo, Birth.class);
    }

    @Override
    public String[] determineBlockedBucketNamesForRecord(final Birth record) {

        // Note will concat nulls into key if any fields are null - working hypothesis - this doesn't matter.

        try {
            final String normalised_forename = normaliseName(record.getString(Birth.FORENAME));
            final String normalised_surname = normaliseName(record.getString(Birth.SURNAME));
            final String normalised_father_forename = normaliseName(record.getString(Birth.FATHERS_FORENAME));
            final String normalised_mother_forename = normaliseName(record.getString(Birth.MOTHERS_FORENAME));

            String bucket_name = concatenate(normalised_forename, normalised_surname, normalised_father_forename, normalised_mother_forename);
            return new String[]{bucket_name};
        } catch (KeyNotFoundException | TypeMismatchFoundException e) {
            return new String[]{};
        }
    }
}
