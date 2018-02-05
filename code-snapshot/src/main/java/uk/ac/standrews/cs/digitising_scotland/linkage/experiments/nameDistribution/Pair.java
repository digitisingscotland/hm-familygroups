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

package uk.ac.standrews.cs.digitising_scotland.linkage.experiments.nameDistribution;


import java.util.Objects;

public class Pair<T> {

    public final T first;
    public final T second;
    private final int hash_code;

    Pair(final T first, final T second) {
        this.first = first;
        this.second = second;
        hash_code = Objects.hash(first, second);
    }

    @Override
    public boolean equals(final Object o) {

        try {
            final Pair other = (Pair) o;
            return first.equals( other.first) && second.equals( other.second);
        }
        catch (final ClassCastException ignored) {}

        return false;
    }

    @Override
    public int hashCode() {
        return hash_code;
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }


}
