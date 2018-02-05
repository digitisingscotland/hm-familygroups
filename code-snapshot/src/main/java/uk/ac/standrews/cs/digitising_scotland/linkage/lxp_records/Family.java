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


import uk.ac.standrews.cs.storr.impl.Metadata;
import uk.ac.standrews.cs.storr.impl.StaticLXP;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.types.LXPBaseType;
import uk.ac.standrews.cs.storr.types.LXP_LIST;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;
import uk.ac.standrews.cs.utilities.JSONReader;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import java.util.ArrayList;
import java.util.List;

/**
 * Essentially a set of siblings carrying an id.
 * Created by al on 16/7/2017.
 * The updates to siblings are not protected by any transaction.
 * Therefore this class assumes that updates are performed before committing the record or are externally transactionally protected.
 */
public class Family extends StaticLXP {

    private static Metadata static_md;
    static {

        try {
            static_md = new Metadata( Family.class,"Family" );

        } catch (Exception e) {
            ErrorHandling.exceptionError( e );
        }
    }

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int place_of_marriage ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int day_of_marriage ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int month_of_marriage ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int year_of_marriage ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int mothers_maiden_surname ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int mothers_forenname ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int fathers_surname ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int fathers_forename ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int mothers_surname_clean ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int mothers_forenname_clean ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int fathers_surname_clean ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int fathers_forename_clean ;

    @LXP_LIST(reftype = "Birth")
    public static int siblings ;


    public Family() {

        put( siblings, new ArrayList<Birth>() );
    }

    public Family(Birth child) {

        ArrayList<Birth> sibs = new ArrayList<Birth>();
        sibs.add(child);
        put( siblings, sibs );
        initParents(child);
    }

    public Family(long persistent_Object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException, IllegalKeyException {

        super(persistent_Object_id, reader, bucket);
    }

    @Override
    public Metadata getMetaData() {
        return static_md;
    }

    public String getPlaceOfMarriage() {
        return getString(place_of_marriage);
    }

    public String getDayOfMarriage() {
        return getString(day_of_marriage);
    }

    public String getMonthOfMarriage() {
        return getString(month_of_marriage);
    }

    public String getYearOfMarriage() {
        return getString(year_of_marriage);
    }

    public String getMothersMaidenSurname() {
        return getString(mothers_maiden_surname);
    }

    public String getMothersForename() { return getString(mothers_forenname); }

    public String getFathersSurname() {
        return getString(fathers_surname);
    }

    public String getFathersForename() {
        return getString(fathers_forename);
    }

    public String getMothersSurnameClean() {
        return getString(mothers_surname_clean);
    }

    public String getMothersForenameClean() {
        return getString(mothers_forenname_clean);
    }

    public String getFathersSurnameClean() {
        return getString(fathers_surname_clean);
    }

    public String getFathersForenameClean() {
        return getString(fathers_forename_clean);
    }

    public List<Birth> getSiblings() { return getList(siblings); }

    /**
     * Adds a sibling to a family
     * NOTE: If called on a persistent record this method must be called from within a transactional context.
     * @param sibling - the sibling to add
     */
    public void addSibling(Birth sibling) {
        List<Birth> sibs = getSiblings();
        if( ! sibs.contains(sibling) ) {                // maintain set semantics
            sibs.add(sibling);
            put(siblings, sibs);
        }
    }

    public void addSiblings(List<Birth> siblings) {
        for( Birth sib : siblings ) {
            addSibling( sib );
        }
    }

    protected void initParents(Birth child) {

        put( place_of_marriage, child.getPlaceOfMarriage() );
        put( day_of_marriage,  child.getString(Birth.PARENTS_DAY_OF_MARRIAGE) );
        put( month_of_marriage,  child.getString(Birth.PARENTS_MONTH_OF_MARRIAGE) );
        put( year_of_marriage,  child.getString(Birth.PARENTS_YEAR_OF_MARRIAGE) );

        put( mothers_maiden_surname,  child.getMothersMaidenSurname() );
        put( mothers_forenname,  child.getMothersForename() );
        put( fathers_surname,  child.getFathersSurname() );
        put( fathers_forename,  child.getFathersForename() );

        put( mothers_surname_clean,  child.getMothersSurnameClean() );
        put( mothers_forenname_clean,  child.getMothersForenameClean() );
        put( fathers_surname_clean,  child.getFathersSurnameClean() );
        put( fathers_forename_clean,  child.getFathersForenameClean() );

    }
}

