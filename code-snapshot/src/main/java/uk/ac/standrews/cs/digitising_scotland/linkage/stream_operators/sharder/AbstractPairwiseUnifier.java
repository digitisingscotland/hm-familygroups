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
package uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder;

import uk.ac.standrews.cs.digitising_scotland.linkage.interfaces.IPairWiseLinker;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Pair;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.IOutputStream;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by al on 21/05/2014.
 */
public abstract class AbstractPairwiseUnifier<T extends LXP> implements IPairWiseLinker<T> {

    private final IInputStream<T> input;
    private final IOutputStream<Pair<T>> output;

    public AbstractPairwiseUnifier(final IInputStream<T> input, final IOutputStream<Pair<T>> output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public void pairwiseUnify() {

        List<T> records = new ArrayList<T>();

        for (T record : input) {
            records.add(record);
        }
        linkRecords(records);
    }

    /**
     * @param records a collection of b & d records for people with the same first name, last name, FATHER's first name and mother's first name.
     */
    private void linkRecords(final List<T> records) {

        for (Pair<T> pair : allPairs(records)) {
            float differentness = compare(pair.first(),pair.second());
            if( similarEnough(differentness) )
                addToResults(pair, differentness , output);
        }
    }

    private Iterable<Pair> allPairs(final List<T> records) {

        List<Pair> all = new ArrayList<>();

        LXP[] recordsArray = records.toArray(new LXP[0]);

        for (int i = 0; i < recordsArray.length; i++) {
            for (int j = i + 1; j < recordsArray.length; j++) {
                all.add(new Pair(recordsArray[i], recordsArray[j]));
            }
        }

        return all;
    }

    protected abstract boolean similarEnough(float differentness);


    public abstract float compare(T first, T second);

    /**
     * Adds a matched result to a result collection.
     *
     * @param pair
     * @param differentness
     */
    public abstract void addToResults(Pair pair, float differentness, IOutputStream results);
}
