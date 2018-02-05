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
package uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.utils;

/**
 * Created by al on 15/11/2017.
 */
public class Candidates {

    int number_of_candidates;       // size of the candidate set
    int links_count;                   // number of links (those with distance less than threshold) within the candidate set
    int match_count;                // number of matches within the candidate set
    int non_match_count;            // number of matches missing from the candidate set

    public Candidates(int number_of_candidates, int links_count, int match_count, int non_match_count) throws Exception {
        if (number_of_candidates == 0) {
            throw new Exception("number_of_candidates cannot be 0");
        }
        if (match_count + non_match_count == 0) {
            throw new Exception("match_count and non_match_count cannot be both 0");
        }
        this.number_of_candidates = number_of_candidates;
        this.links_count = links_count;
        this.match_count = match_count;
        this.non_match_count = non_match_count;
    }

    /**
     * This shows the quality of the blocking method in comparison to complete methods like brute or mtree.
     * For complete methods, it would always return 1. Or else, there is a bug.
     *
     * @return The proportion of links in the candidate set with respect to the size of the candidate set
     */
    public float getLinksQuality() {
        return ((float) links_count) / number_of_candidates;
    }
    
    /**
     * @return The proportion of matches in the candidate set with respect to the size of the candidate set
     */
    public float getPairsQuality() {
        return ((float) match_count) / number_of_candidates;
    }

    /**
     * @return The proportion of matches that we find in the candidate set with respect to the full set of matches
     */
    public float getPairsCompleteness() {
        return ((float) match_count) / (match_count + non_match_count);
    }

}
