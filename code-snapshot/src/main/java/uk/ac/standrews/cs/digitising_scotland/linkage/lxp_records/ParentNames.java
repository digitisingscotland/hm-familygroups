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
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;
import uk.ac.standrews.cs.utilities.JSONReader;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

/**
 * Created by al on 03/10/2014.
 */
public class ParentNames extends StaticLXP {

    private static Metadata static_md;
    static {

        try {
            static_md = new Metadata( ParentNames.class,"ParentNames" );

        } catch (Exception e) {
            ErrorHandling.exceptionError( e );
        }
    }
    
    private long id;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FATHERS_FORENAME ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FATHERS_SURNAME ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int MOTHERS_FORENAME ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int MOTHERS_MAIDEN_SURNAME ;

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static int FAMILY ;

    public ParentNames() {

        super();
    }

    public ParentNames(long persistent_Object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException, IllegalKeyException {

        super(persistent_Object_id, reader, bucket);
    }

    @Override
    public Metadata getMetaData() {
        return static_md;
    }

    public ParentNames(long id, String demographerFamilyId, String ffn, String fln, String mfn, String mln) {
        this.id = id;
        this.put(FATHERS_FORENAME, ffn);
        this.put(FATHERS_SURNAME, fln);
        this.put(MOTHERS_FORENAME, mfn);
        this.put(MOTHERS_MAIDEN_SURNAME, mln);
        this.put(FAMILY, demographerFamilyId);
    }

    public long getId() {
        return this.id;
    }

    public String getFathersForename() {

        return getString(FATHERS_FORENAME);
    }

    public String getFathersSurname() {

        return getString(FATHERS_SURNAME);
    }

    public String getMothersForename() {

        return getString(MOTHERS_FORENAME);
    }

    public String getMothersMaidenSurname() {

        return getString(MOTHERS_MAIDEN_SURNAME);
    }

    public String getDemographerFamilyId() {
        return getString(FAMILY);
    }
}
