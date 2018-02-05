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

import static uk.ac.standrews.cs.storr.types.LXPBaseType.STRING;

/**
 * Created by al on 17/8/16
 */
public class Relationship extends StaticLXP {

    private static Metadata static_md;
    static {

        try {
            static_md = new Metadata( Relationship.class,"Relationship" );

        } catch (Exception e) {
            ErrorHandling.exceptionError( e );
        }
    }

    public enum relationship_kind {fatherof, motherof, marriedto}

    private static int next_slot = 1; // slots start at 1 - zero is reserved.

    @LXP_REF(type = "role")
    public static int SUBJECT ;

    @LXP_REF(type = "role")
    public static int OBJECT ;

    @LXP_SCALAR(type = STRING)
    public static int RELATIONSHIP ;

    @LXP_SCALAR(type = STRING)
    public static int EVIDENCE ;

    public Relationship() {

        super();
    }

    public Relationship(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException, IllegalKeyException {

        super(persistent_object_id, reader, bucket);
    }

    public Relationship(StoreReference<Role> subject, StoreReference<Role> object, relationship_kind relationship, String evidence) throws StoreException {

        this();

        try {
            put(SUBJECT, subject);
            put(OBJECT, object);
            put(RELATIONSHIP, relationship.name());
            put(EVIDENCE, evidence);
        } catch (IllegalKeyException e) {
            throw new StoreException(e);
        }
    }

    @Override
    public Metadata getMetaData() {
        return static_md;
    }

    public Role getSubject() throws BucketException {

        return (Role) getRef(SUBJECT).getReferend();
    }

    public Role getObject() throws BucketException {

        return (Role) getRef(OBJECT).getReferend();
    }

    public relationship_kind getRelationship() {

        return relationship_kind.valueOf(getString(RELATIONSHIP));
    }

    public String getEvidence() {

        return getString(EVIDENCE);
    }
}
