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
package uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.filter;

import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IFilter;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.IOutputStream;

/**
 * Created by al on 28/04/2014.
 */
public abstract class Filter<T extends LXP> implements IFilter<T> {

    private final IInputStream<T> input;
    private final IOutputStream<T> output;

    public Filter(final IInputStream<T> input, final IOutputStream<T> output) {
        this.input = input;
        this.output = output;
    }

    public void apply() throws BucketException {

        for (T record : input) {
            if (select(record)) {
                output.add(record);
            }
        }
    }

    public IInputStream<T> getInput() {
        return input;
    }

    public IOutputStream<T> getOutput() {
        return output;
    }
}
