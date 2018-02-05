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

import uk.ac.standrews.cs.storr.impl.DynamicLXP;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.utilities.JSONReader;

/**
 * Created by al on 19/06/2014.
 */
public class Pair<T extends LXP> extends DynamicLXP {

    private long typeLabel;
    private T first;
    private T second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public Pair(long persistent_Object_id, JSONReader reader, long required_type_labelID, IBucket bucket) throws PersistentObjectException, IllegalKeyException {
        super(persistent_Object_id, reader, bucket);
        this.typeLabel = required_type_labelID;
    }

    public T first() {
        return first;
    }

    public T second() {
        return second;
    }

    @Override
    public long getId() {
        return this.getId();
    }

    @Override
    public Object get(String label) throws KeyNotFoundException {
        if( label.equals("first")) {
            return first;
        }
        if( label.equals("second")) {
            return second;
        }
        throw new KeyNotFoundException( "Key not found: " + label );
    }

    @Override
    public String getString(int key) {
        return null;
    }

    @Override
    public double getDouble(int label) throws KeyNotFoundException, TypeMismatchFoundException {
        throw new KeyNotFoundException( "Key not found: " + label );
    }

    @Override
    public int getInt(int label) throws KeyNotFoundException, TypeMismatchFoundException {
        throw new KeyNotFoundException( "Key not found: " + label );
    }

    @Override
    public boolean getBoolean(int label) throws KeyNotFoundException, TypeMismatchFoundException {
        throw new KeyNotFoundException( "Key not found: " + label );
    }

    @Override
    public long getLong(int label) throws KeyNotFoundException, TypeMismatchFoundException {
        throw new KeyNotFoundException( "Key not found: " + label );
    }

    @Override
    public void put(int key, String value) {

    }

    @Override
    public void put( int key, double value) {

    }

    @Override
    public void put(int key, int value) {

    }

    @Override
    public void put(int key, boolean value) {

    }

    @Override
    public void put(int key, long value) {

    }

}
