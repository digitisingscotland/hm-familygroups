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
package uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.lxp_records;

import uk.ac.standrews.cs.storr.impl.Metadata;
import uk.ac.standrews.cs.storr.impl.StaticLXP;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.types.LXPBaseType;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;
import uk.ac.standrews.cs.utilities.JSONReader;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import java.util.Arrays;
import java.util.List;

/**
 * Created by al on 29/11/2017.
 */
public class NCVR extends StaticLXP {

    private static Metadata static_md;

    static {
        try {
            static_md = new Metadata( NCVR.class,"NCVR" );

        } catch (Exception e) {
            ErrorHandling.exceptionError( e );
        }
    }

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int VOTER_ID;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FIRST_NAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int MIDDLE_NAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int LAST_NAME;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int AGE;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int GENDER;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int STREET_ADDRESS;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int CITY;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int STATE;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int ZIP_CODE;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FULL_PHONE_NUM;

    static List<Integer> excludeList = Arrays.asList( VOTER_ID );

    static  List<Integer> matchList = Arrays.asList( FIRST_NAME,MIDDLE_NAME, LAST_NAME, AGE, GENDER, STREET_ADDRESS, CITY, STATE, ZIP_CODE, FULL_PHONE_NUM );

    public NCVR() { super(); }

    public NCVR(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException {
        super( persistent_object_id, reader, bucket );
    }

    public NCVR create(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException, IllegalKeyException {
        return new NCVR(persistent_object_id, reader, bucket);
    }

    public static List<Integer> getExcludedFields() {
        return excludeList;
    }

    public static List<Integer> getRecordLinkingFields() {
        return matchList;
    }

    @Override
    public Metadata getMetaData() {
        return static_md;
    }

}
