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
import uk.ac.standrews.cs.storr.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.storr.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.IOutputStream;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

/**
 * Provides exact match filtering of OID records based on a label and a value.
 * Created by al on 29/04/2014.
 */
public class ExactMatch<T extends LXP> extends Filter {

    private final String value;
    private final int label;

    public ExactMatch(final IInputStream<T> input, final IOutputStream<T> output, final int label, final String value) {

        super(input, output);
        this.label = label;
        this.value = value;
    }

    public boolean select(final LXP record) {

        try {
            return record.getString(label).equals(value);
        } catch (KeyNotFoundException e) {
            ErrorHandling.exceptionError(e, "Key not found");
            return false;
        } catch (TypeMismatchFoundException e) {
            ErrorHandling.exceptionError(e, "Type mismatch");
            return false;
        }
    }
}
