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

public class BirthDeath extends StaticLXP {

    private static Metadata static_md;
    static {
        try {
            static_md = new Metadata( BirthDeath.class,"BirthDeath" );

        } catch (Exception e) {
            ErrorHandling.exceptionError( e );
        }
    }

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FORENAME ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int SURNAME_AT_BIRTH ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int SEX ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FATHERS_SURNAME ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FATHERS_FORENAME ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int MOTHERS_FORENAME ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int MOTHERS_MAIDEN_SURNAME ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int GROUND_TRUTH ;

    private static List<Integer> matchList = Arrays.asList( FORENAME, SURNAME_AT_BIRTH, SEX,
            FATHERS_FORENAME, FATHERS_SURNAME,
            MOTHERS_FORENAME, MOTHERS_MAIDEN_SURNAME  );

    private static List<Integer> excludeList = Arrays.asList( GROUND_TRUTH );

    public BirthDeath() {
        super();
    }

    public BirthDeath(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException {
        super( persistent_object_id, reader, bucket );
    }

    public BirthDeath create(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException, IllegalKeyException {
        return new BirthDeath(persistent_object_id, reader, bucket);
    }

    public static List<Integer> getRecordLinkingFields() {
        return matchList;
    }

    public static List<Integer> getExcludedFields() { return excludeList; }

    @Override
    public Metadata getMetaData() {
        return static_md;
    }

}
