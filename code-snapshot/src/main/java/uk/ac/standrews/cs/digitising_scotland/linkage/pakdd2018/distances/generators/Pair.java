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

package uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.distances.generators;

import uk.ac.standrews.cs.storr.impl.LXP;

import java.util.Objects;

/**
 * Extracted by al on 29/10/2017.
 * Hand crafted by Oz on 27/10/2017.
 */
public class Pair {

    private final long id1;
    private final long id2;
    private final int hash_code;

    Pair(final LXP lxp1, final LXP lxp2) {
        id1 = lxp1.getId();
        id2 = lxp2.getId();
        hash_code = Objects.hash(id1, id2);
    }

    Pair(final long id1, final long id2) {
        this.id1 = id1;
        this.id2 = id2;
        hash_code = Objects.hash(id1, id2);
    }

    @Override
    public boolean equals(final Object o) {

        try {
            final Pair other = (Pair) o;
            return id1 == other.id1 && id2 == other.id2;
        }
        catch (final ClassCastException ignored) {}

        return false;
    }

    @Override
    public int hashCode() {
        return hash_code;
    }
}
