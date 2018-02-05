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
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.types.LXPBaseType;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;
import uk.ac.standrews.cs.utilities.JSONReader;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

/**
 * Birth Record extended wih Family ground truth for processing Kilmarnock and Isle of Skye datasets
 * <p>
 * Created by al on 03/10/2014.
 */
public class CensusPair extends StaticLXP {

    private static Metadata static_md;
    static {

        try {
            static_md = new Metadata( CensusPair.class,"CensusPair" );

        } catch (Exception e) {
            ErrorHandling.exceptionError( e );
        }
    }

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int CENSUS_YEAR_1;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int SCHEDULE_ID1;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int PERSON_ID1;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FIRSTNAME1;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int SURNAME1;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FIRSTNAME_CLEAN1;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int SURNAME_CLEAN1;

    // *********************************

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int CENSUS_YEAR_2;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int SCHEDULE_ID2;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int PERSON_ID2;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FIRSTNAME2;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int SURNAME2;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FIRSTNAME_CLEAN2;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int SURNAME_CLEAN2;


    public CensusPair() {

        super();
    }

    public CensusPair(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException {

            super(persistent_object_id, reader, bucket);
    }

    @Override
    public Metadata getMetaData() {
        return static_md;
    }


    @Override
    public boolean equals( final Object o ) {
        return o instanceof CensusPair && (( ((CensusPair) o).getId()) == this.getId());
    }

    @Override
    public int hashCode() { return new Long( this.getId() ).hashCode(); }

    public String toString() { return "CensusPair"; }
}
