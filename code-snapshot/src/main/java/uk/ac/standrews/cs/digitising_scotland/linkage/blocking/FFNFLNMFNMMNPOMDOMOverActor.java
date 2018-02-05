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
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role;
import uk.ac.standrews.cs.storr.impl.LXP;
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

public class FFNFLNMFNMMNPOMDOMOverActor extends AbstractBlocker<Role> {

    public FFNFLNMFNMMNPOMDOMOverActor(final IBucket<Role> roleBucket, final IRepository output_repo, Class<Role> clazz) throws BucketException, RepositoryException, IOException {

        super(roleBucket.getInputStream(), output_repo, clazz);
    }

    /**
     * @param record - a Person record to be blocked
     * @return the blocking keys - one for baby and one for FATHER
     */
    public String[] determineBlockedBucketNamesForRecord(final Role record) throws NoSuitableBucketException {

        switch (record.getRole()) {
            case PRINCIPAL:
                return determineBlockedBucketNamesForPrincipal(record);
            case FATHER:
                return determineBlockedBucketNamesForFather(record);
            case MOTHER:
                return determineBlockedBucketNamesForMother(record);
            case BRIDE:
                return determineBlockedBucketNamesForBrideOrGroom(record);
            case GROOM:
                return determineBlockedBucketNamesForBrideOrGroom(record);

            default:
                throw new NoSuitableBucketException("No match");
        }
    }

    /*
     * @param record - a Person record to be blocked who is in the role of PRINCIPAL
     * @return the blocking key based on FNLN of mother and FATHER and their place and date of marriage
     */
    private String[] determineBlockedBucketNamesForPrincipal(final Role record) throws NoSuitableBucketException {

        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        try {
            final String normalised_father_forename = normaliseName(record.getFathersForename());
            final String normalised_father_surname = normaliseName(record.getFathersSurname());
            final String normalised_mother_forename = normaliseName(record.getMothersForename());
            final String normalised_mother_maiden_surname = normaliseName(record.getMothersMaidenSurname());

            String bucket_name = concatenate(normalised_father_forename, normalised_father_surname, normalised_mother_forename, normalised_mother_maiden_surname, record.getPlaceOfMarriage(), record.getDateOfMarriage());
            return new String[]{bucket_name};
        } catch (KeyNotFoundException | TypeMismatchFoundException e) {
            throw new NoSuitableBucketException(e);
        }
    }

    /*
     * @param record - a Person record to be blocked who is in the role of FATHER
     * @return the blocking key based on FNLN of mother and FATHER and their place and date of marriage
     */
    private String[] determineBlockedBucketNamesForFather(Role record) throws NoSuitableBucketException {

        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        try {
            final String normalised_father_forename = normaliseName(record.getForename());
            final String normalised_father_surname = normaliseName(record.getSurname());

            // Get the mother's names from the original record.
            LXP primary = record.getOriginalRecord();

            final String normalised_mother_forename = normaliseName((String) primary.get(Birth.MOTHERS_FORENAME));
            final String normalised_mother_maiden_surname = normaliseName((String) primary.get(Birth.MOTHERS_MAIDEN_SURNAME));

            String bucket_name = concatenate(normalised_father_forename, normalised_father_surname, normalised_mother_forename, normalised_mother_maiden_surname, record.getPlaceOfMarriage(), record.getDateOfMarriage());
            return new String[]{bucket_name};
        } catch (KeyNotFoundException | TypeMismatchFoundException e) {
            throw new NoSuitableBucketException(e);
        }
    }

    /*
     * @param record - a Person record to be blocked who is in the role of mother
     * @return the blocking key based on FNLN of mother and FATHER and their place and date of marriage
     */
    private String[] determineBlockedBucketNamesForMother(Role record) throws NoSuitableBucketException {

        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        StringBuilder builder = new StringBuilder();

        try {
            // Get the father's names from the original record.
            LXP primary = record.getOriginalRecord();

            final String normalised_father_forename = normaliseName((String) primary.get(Birth.FATHERS_FORENAME));
            final String normalised_father_surname = normaliseName((String) primary.get(Birth.FATHERS_SURNAME));

            final String normalised_mother_forename = normaliseName(record.getForename());
            final String normalised_mother_surname = normaliseName(record.getSurname());

            String bucket_name = concatenate(normalised_father_forename, normalised_father_surname, normalised_mother_forename, normalised_mother_surname, record.getPlaceOfMarriage(), record.getDateOfMarriage());
            return new String[]{bucket_name};
        } catch (KeyNotFoundException | TypeMismatchFoundException e) {
            throw new NoSuitableBucketException(e);
        }
    }

    /*
     * @param record - a Person record to be blocked who is in the role of bride or groom
     * @return the blocking key based on FNLN of bride and groom and their place and date of marriage
     */
    private String[] determineBlockedBucketNamesForBrideOrGroom(Role record) throws NoSuitableBucketException {

        // Note will concat null strings into key if any fields are null - working hypothesis - this doesn't matter.

        try {
            // Get the spouses' names from the original record.
            LXP primary = record.getOriginalRecord();

            final String normalised_groom_forename = normaliseName((String) primary.get(Marriage.GROOM_FORENAME));
            final String normlaised_groom_surname = normaliseName((String) primary.get(Marriage.GROOM_SURNAME));

            final String normalised_bride_forename = normaliseName((String) primary.get(Marriage.BRIDE_FORENAME));
            final String normalised_bride_surname = normaliseName((String) primary.get(Marriage.BRIDE_SURNAME));

            String bucket_name = concatenate(normalised_groom_forename, normlaised_groom_surname, normalised_bride_forename, normalised_bride_surname, record.getPlaceOfMarriage(), record.getDateOfMarriage());
            return new String[]{bucket_name};
        } catch (KeyNotFoundException | TypeMismatchFoundException e) {
            throw new NoSuitableBucketException(e);
        }
    }
}

