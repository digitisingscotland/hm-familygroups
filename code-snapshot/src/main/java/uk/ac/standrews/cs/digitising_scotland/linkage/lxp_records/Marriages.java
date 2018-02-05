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
import uk.ac.standrews.cs.storr.types.LXP_LIST;
import uk.ac.standrews.cs.utilities.JSONReader;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import java.util.List;

/**
 * Created on 19/7/17
 * @author al@st-andrews.ac.uk
 */
public class Marriages extends StaticLXP {

    private static Metadata static_md;
    static {

        try {
            static_md = new Metadata( Marriages.class,"Marriages" );

        } catch (Exception e) {
            ErrorHandling.exceptionError( e );
        }
    }

    @LXP_LIST(reftype = "Marriage")
    public static int LIST = 1;

    //******************** Constructors ********************

    public Marriages() { super(); } // provided for reflective creation only

    public Marriages(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException, IllegalKeyException {
        super(persistent_object_id, reader, bucket);
    }

    public Marriages(List<Marriage> list) {
        super();
        put( LIST,list );
    }

    @Override
    public Metadata getMetaData() {
        return static_md;
    }

    //******************** Selectors ********************

    List<Marriage> getMarriages() {
        return getList( LIST);
    }
}
