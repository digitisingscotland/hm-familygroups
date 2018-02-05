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
package uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.distances.baseDistances;

import org.simmetrics.StringDistance;
import org.simmetrics.metrics.Levenshtein;

public class NED2 implements StringDistance {

    private final Levenshtein levenshtein = new Levenshtein();

    public float distance(String s1, String s2) {
        int total_length = s1.length() + s2.length();
        if (total_length == 0) {
            return 0.0f;
        }
        return levenshtein.distance(s1, s2) / total_length;
    }

}
