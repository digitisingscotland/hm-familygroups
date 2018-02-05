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
package uk.ac.standrews.cs.digitising_scotland.linkage.interfaces;


import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Pair;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.interfaces.IOutputStream;

/**
 * Created by al on 21/05/2014.
 */
public interface IPairWiseLinker<T extends LXP> {

    void pairwiseUnify();

    float compare(T first, T second);

    /**
     * Adds a matched result to a result collection.
     * @param pair
     * @param differentness
     */
    void addToResults(final Pair<T> pair, float differentness, final IOutputStream results);
}
