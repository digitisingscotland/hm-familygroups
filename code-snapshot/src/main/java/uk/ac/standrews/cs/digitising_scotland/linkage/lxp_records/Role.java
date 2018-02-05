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
package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.Metadata;
import uk.ac.standrews.cs.storr.impl.StaticLXP;
import uk.ac.standrews.cs.storr.impl.StoreReference;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.types.LXP_REF;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;
import uk.ac.standrews.cs.utilities.JSONReader;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import static uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Role.RolePlayed.*;
import static uk.ac.standrews.cs.storr.types.LXPBaseType.LONG;
import static uk.ac.standrews.cs.storr.types.LXPBaseType.STRING;

/**
 * Created by al on 03/10/2014.
 */
public class Role extends StaticLXP {

    private static Metadata static_md;
    static {

        try {
            static_md = new Metadata( Role.class,"Role" );

        } catch (Exception e) {
            ErrorHandling.exceptionError( e );
        }
    }

    private static final String DATE_SEPARATOR = "-";
    private static final String MALE = "M";
    private static final String FEMALE = "F";

    public enum RolePlayed {PRINCIPAL, FATHER, MOTHER, BRIDE, GROOM, GROOMS_FATHER, GROOMS_MOTHER, BRIDES_FATHER, BRIDES_MOTHER}

    // Person labels

    @LXP_SCALAR(type = STRING)
    public static int SURNAME ;

    @LXP_SCALAR(type = STRING)
    public static int FORENAME;

    @LXP_SCALAR(type = STRING)
    public static int SEX;

    @LXP_REF(type = "lxp")
    public static int ORIGINAL_RECORD;

    @LXP_SCALAR(type = LONG)
    public static int ORIGINAL_RECORD_TYPE;

    @LXP_SCALAR(type = STRING)
    public static int ROLE;

    public Role() {

        super();
    }

    public Role(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException, IllegalKeyException {

        super(persistent_object_id, reader, bucket);
    }

    public Role(String surname, String forename, String sex, RolePlayed role, StoreReference original_record_ref, long original_record_type) throws StoreException {

        this();

        try {
            put(SURNAME, surname);
            put(FORENAME, forename);
            put(SEX, sex);
            put(ROLE, role.name());
            put(ORIGINAL_RECORD, original_record_ref.toString());
            put(ORIGINAL_RECORD_TYPE, original_record_type);

        } catch (IllegalKeyException e) {
            throw new StoreException("Illegal key in OID");
        }
    }

    @Override
    public Metadata getMetaData() {
        return static_md;
    }

    //*********************** Creator methods ***********************//

    public static Role createPersonFromOwnBirth(StoreReference<Birth> original_record_ref, long original_record_type) throws StoreException, BucketException {

        Birth original_record = original_record_ref.getReferend();

        String surname = original_record.getString(Birth.SURNAME);
        String forename = original_record.getString(Birth.FORENAME);
        String sex = original_record.getString(Birth.SEX);

        return new Role(surname, forename, sex, PRINCIPAL, original_record_ref, original_record_type);
    }

    public static Role createPersonFromOwnDeath(StoreReference<Death> original_record_ref, long original_record_type) throws StoreException, BucketException {

        Death original_record = original_record_ref.getReferend();

        String surname = original_record.getString(Death.SURNAME);
        String forename = original_record.getString(Death.FORENAME);
        String sex = original_record.getString(Death.SEX);

        return new Role(surname, forename, sex, PRINCIPAL, original_record_ref, original_record_type);
    }

    public static Role createFatherFromChildsBirth(StoreReference<Birth> original_record_ref, long original_record_type) throws StoreException, BucketException {

        LXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(Birth.FATHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(Birth.FATHERS_SURNAME);
        String forename = BD_record.getString(Birth.FATHERS_FORENAME);

        return new Role(surname, forename, MALE, FATHER, original_record_ref, original_record_type);
    }

    public static Role createFatherFromChildsDeath(StoreReference<Death> original_record_ref, long original_record_type) throws StoreException, BucketException {

        LXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(Birth.FATHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(Death.FATHERS_SURNAME);
        String forename = BD_record.getString(Death.FATHERS_FORENAME);

        return new Role(surname, forename, MALE, FATHER, original_record_ref, original_record_type);
    }

    public static Role createMotherFromChildsBirth(StoreReference<Birth> original_record_ref, long original_record_type) throws StoreException, BucketException {

        LXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(Birth.MOTHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(Birth.MOTHERS_MAIDEN_SURNAME);
        String forename = BD_record.getString(Birth.MOTHERS_FORENAME);

        return new Role(surname, forename, FEMALE, MOTHER, original_record_ref, original_record_type);
    }

    public static Role createMotherFromChildsDeath(StoreReference<Death> original_record_ref, long original_record_type) throws StoreException, BucketException {

        LXP BD_record = original_record_ref.getReferend();

        if (BD_record.getString(Birth.MOTHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = BD_record.getString(Death.MOTHERS_MAIDEN_SURNAME);
        String forename = BD_record.getString(Death.MOTHERS_FORENAME);

        return new Role(surname, forename, FEMALE, MOTHER, original_record_ref, original_record_type);
    }

    /**
     * Creates a Role record for the bride for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return a Person representing the bride
     */
    public static Role createBrideFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.BRIDE_SURNAME);
        String forename = marriage_record.getString(Marriage.BRIDE_FORENAME);

        return new Role(surname, forename, FEMALE, BRIDE, marriage_record_ref, original_record_type);
    }

    /**
     * Creates a Role record for the groom for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the groom
     */
    public static Role createGroomFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.GROOM_SURNAME);
        String forename = marriage_record.getString(Marriage.GROOM_FORENAME);

        return new Role(surname, forename, MALE, GROOM, marriage_record_ref, original_record_type);
    }

    /**
     * Creates a Role record for the brides FATHER for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the brides FATHER
     */
    public static Role createBridesFatherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.BRIDE_FATHERS_SURNAME);
        if (surname.equals("0")) {
            surname = marriage_record.getString(Marriage.BRIDE_SURNAME); // TODO factor out - not sure where to
        }
        String forename = marriage_record.getString(Marriage.BRIDE_FATHERS_FORENAME);

        return new Role(surname, forename, MALE, BRIDES_FATHER, marriage_record_ref, original_record_type);
    }

    /**
     * Creates a Role record for the brides mother for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the brides mother
     */
    public static Role createBridesMotherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.BRIDE_MOTHERS_MAIDEN_SURNAME);
        String forename = marriage_record.getString(Marriage.BRIDE_MOTHERS_FORENAME);

        return new Role(surname, forename, FEMALE, BRIDES_MOTHER, marriage_record_ref, original_record_type);
    }

    /**
     * Creates a Role record for the grooms FATHER for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the grooms FATHER
     */
    public static Role createGroomsFatherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.GROOM_FATHERS_SURNAME);
        if (surname.equals("0")) {
            surname = marriage_record.getString(Marriage.GROOM_SURNAME); // TODO factor out - not sure where to
        }
        String forename = marriage_record.getString(Marriage.GROOM_FATHERS_FORENAME);

        return new Role(surname, forename, MALE, GROOMS_FATHER, marriage_record_ref, original_record_type);
    }

    /**
     * Creates a Role record for the groom's mother for a given marriage record
     *
     * @param marriage_record_ref a reference to a record from which to extract person information
     * @return the Person representing the grooms mother FATHER
     */
    public static Role createGroomsMotherFromMarriageRecord(StoreReference<Marriage> marriage_record_ref, long original_record_type) throws StoreException, BucketException {

        Marriage marriage_record = marriage_record_ref.getReferend();

        String surname = marriage_record.getString(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME);
        String forename = marriage_record.getString(Marriage.GROOM_MOTHERS_FORENAME);

        return new Role(surname, forename, FEMALE, RolePlayed.GROOMS_MOTHER, marriage_record_ref, original_record_type);
    }

    //*********************** Getter methods ***********************//

    // Basic selectors operate over data stored in this role

    public String getSurname() {

        return getString(SURNAME);
    }

    public String getForename() {

        return getString(FORENAME);
    }

    public String getSex() {

        return getString(SEX);
    }

    public RolePlayed getRole() {

        return RolePlayed.valueOf(getString(ROLE));
    }

    public LXP getOriginalRecord() {

        try {
            return new StoreReference(getRepository().getStore(), getString(ORIGINAL_RECORD)).getReferend();
        } catch (BucketException e) {
            throw new StoreException(e);
        }
    }

    public long getOriginalRecordType() {

        return getLong(ORIGINAL_RECORD_TYPE);
    }

    // Complex selectors operate over data stored in original record

    public String getFathersForename() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(Birth.FATHERS_FORENAME);
            }
            case BRIDE: {
                return getOriginalRecord().getString(Marriage.BRIDE_FATHERS_FORENAME);
            }
            case GROOM: {
                return getOriginalRecord().getString(Marriage.GROOM_FATHERS_FORENAME);
            }

            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String getFathersSurname() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(Birth.FATHERS_SURNAME);
            }
            case BRIDE: {
                return getOriginalRecord().getString(Marriage.BRIDE_FATHERS_SURNAME);
            }
            case GROOM: {
                return getOriginalRecord().getString(Marriage.GROOM_FATHERS_SURNAME);
            }

            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String getFathersOccupation() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(Birth.FATHERS_OCCUPATION);
            }
            case BRIDE: {
                return getOriginalRecord().getString(Marriage.BRIDE_FATHER_OCCUPATION);
            }
            case GROOM: {
                return getOriginalRecord().getString(Marriage.BRIDE_FATHER_OCCUPATION);
            }

            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String getMothersForename() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(Birth.MOTHERS_FORENAME);
            }
            case BRIDE: {
                return getOriginalRecord().getString(Marriage.BRIDE_MOTHERS_FORENAME);
            }
            case GROOM: {
                return getOriginalRecord().getString(Marriage.GROOM_MOTHERS_FORENAME);
            }

            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String getMothersMaidenSurname() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(Birth.MOTHERS_MAIDEN_SURNAME);
            }
            case BRIDE: {
                return getOriginalRecord().getString(Marriage.BRIDE_MOTHERS_MAIDEN_SURNAME);
            }
            case GROOM: {
                return getOriginalRecord().getString(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME);
            }

            default:
                return ""; // rest return null - we don't know who they are
        }
    }

    public String getOccupation() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(Death.OCCUPATION); // ????
            }
            case GROOM: {
                return getOriginalRecord().getString(Marriage.GROOM_OCCUPATION);
            }
            case GROOMS_FATHER: {
                return getOriginalRecord().getString(Marriage.GROOM_FATHERS_OCCUPATION);
            }

            case BRIDES_FATHER: {
                return getOriginalRecord().getString(Marriage.BRIDE_OCCUPATION);
            }
            case FATHER: {
                return getOriginalRecord().getString(Birth.FATHERS_OCCUPATION);
            }

            default:
                return "";
        }
    }

    public String getPlaceOfMarriage() {

        switch (getRole()) {

            case PRINCIPAL: {
                return getOriginalRecord().getString(Birth.PARENTS_PLACE_OF_MARRIAGE);
            }
            case GROOM:
            case BRIDE: {
                return getOriginalRecord().getString(Marriage.REGISTRATION_DISTRICT_NUMBER); // TODO IS THIS THE SAME AS PARENTS_PLACE_OF_MARRIAGE???
            }

            default:
                return "";
        }
    }

    public String getDateOfMarriage() {

        switch (getRole()) {

            case PRINCIPAL: {
                return extractDateOfMarriageFromBirthRecord(getOriginalRecord());
            }
            case GROOM:
            case BRIDE: {
                return extractDateOfMarriageFromMarriageRecord(getOriginalRecord());
            }

            default:
                return "";
        }
    }

    private String extractDateOfMarriageFromBirthRecord(final LXP record) {

        return record.getString(Birth.PARENTS_DAY_OF_MARRIAGE) + DATE_SEPARATOR + record.getString(Birth.PARENTS_MONTH_OF_MARRIAGE) + DATE_SEPARATOR + record.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);
    }

    private String extractDateOfMarriageFromMarriageRecord(final LXP record) {

        return record.getString(Marriage.MARRIAGE_DAY) + DATE_SEPARATOR + record.getString(Marriage.MARRIAGE_MONTH) + DATE_SEPARATOR + record.getString(Marriage.MARRIAGE_YEAR);
    }

    //*********************** utility methods ***********************//

    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("\tRole: " + this.getRole() + "\n");
        builder.append("\tSex: " + this.getSex() + "\n");
        builder.append("\tfirstname: " + this.getForename() + "\n");
        builder.append("\tsurname: " + this.getSurname() + "\n");
        builder.append("\tFATHER fn: " + this.getFathersForename() + "\n");
        builder.append("\tFATHER ln: " + this.getFathersSurname() + "\n");
        builder.append("\tmother fn: " + this.getMothersForename() + "\n");
        builder.append("\tmother ln: " + this.getMothersMaidenSurname() + "\n");
        return builder.toString();
    }
}
