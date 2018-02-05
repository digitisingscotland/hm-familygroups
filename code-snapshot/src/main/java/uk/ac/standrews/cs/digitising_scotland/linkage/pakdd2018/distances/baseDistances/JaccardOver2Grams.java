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
import org.simmetrics.metrics.Jaccard;

import java.util.HashSet;
import java.util.Set;

public class JaccardOver2Grams implements StringDistance {

    private final Jaccard<String> jaccard = new Jaccard<>();

    public float distance(String s1, String s2) {

        Set<String> two_gram_s1 = ngrams(s1, 2);
        Set<String> two_gram_s2 = ngrams(s2, 2);

        return jaccard.distance(two_gram_s1, two_gram_s2);
    }

    private static Set<String> ngrams(String source, int n) {
        HashSet<String> ngrams = new HashSet<String>();
        for (int i = 0; i < source.length() - n + 1; i++)
            ngrams.add(source.substring(i, i + n));
        return ngrams;
    }

}
