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

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.io.IOException;

/**
 * This class blocks on streams of Role records.
 * The categories of blocking are first name of parents along with place of marriage and date of marriage over streams of Role records
 * This should form family groups
 * This pattern was suggested at Raasay Colloquium by Eilidh.
 * These are unique tags for all vital event records.
 * Created by al on 30/8/16
 */
public class FFNFLNMFNMMNPOMDOMOverMarriage extends AbstractBlocker<Marriage> {

    public FFNFLNMFNMMNPOMDOMOverMarriage(final IBucket<Marriage> birthsBucket, final IRepository output_repo, Class<Marriage> clazz) throws BucketException, RepositoryException, IOException {

        super(birthsBucket.getInputStream(), output_repo, clazz);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys based on Father's first name, last name, Mother's first name, last name, place of marriage
     */
    public String[] determineBlockedBucketNamesForRecord(final Marriage record) throws NoSuitableBucketException {

        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        try {
            final String normalised_groom_forename = normaliseName(record.getGroomsForename());
            final String normalised_groom_surname = normaliseName(record.getGroomsSurname());
            final String normalised_bride_forename = normaliseName(record.getBridesForename());
            final String normalised_bride_surname = normaliseName(record.getBridesSurname());

            final String normalised_place_of_marriage = normalisePlace(record.getPlaceOfMarriage());
            final String date_of_marriage = record.getDateOfMarriage();

            String bucket_name = concatenate(normalised_groom_forename, normalised_groom_surname, normalised_bride_forename, normalised_bride_surname, normalised_place_of_marriage, date_of_marriage);
            return new String[]{bucket_name};
        } catch (KeyNotFoundException | TypeMismatchFoundException e) {
            throw new NoSuitableBucketException(e);
        }
    }
}
