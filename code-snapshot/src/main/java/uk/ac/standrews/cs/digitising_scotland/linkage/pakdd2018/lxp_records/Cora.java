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

//import org.bouncycastle.asn1.cms.MetaData;

/**
 * Created by al on 29/11/2017.
 */
public class Cora extends StaticLXP {

    private static Metadata static_md;

    static {
        try {
            static_md = new Metadata( Cora.class,"Cora" );

        } catch (Exception e) {
            ErrorHandling.exceptionError( e );
        }
    }

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FIELD_1;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int CITATION_KEY;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int AUTHOR;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int TITLE;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int VENUE;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int LOCATION;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int PUBLISHER;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int YEAR;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FIELD9;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FIELD10;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FIELD11;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FIELD12;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FIELD13;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FIELD14;

    static List<Integer> excludeList = Arrays.asList( FIELD_1, CITATION_KEY );

    static List<Integer> matchList = Arrays.asList( AUTHOR, TITLE, VENUE, LOCATION, PUBLISHER, YEAR );

    public static List<Integer> getExcludedFields() {
        return excludeList;
    }

    public static List<Integer> getRecordLinkingFields() {
        return matchList;
    }

    public Cora() { super(); }

    public Cora(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException {
        super( persistent_object_id,reader,bucket );
    }

    @Override
    public Metadata getMetaData() {
        return static_md;
    }

    public Cora create(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException, IllegalKeyException {
        return new Cora(persistent_object_id, reader, bucket);
    }

    public static Metadata getStaticMetadata() { return static_md; }

}