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
package uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances;

import org.simmetrics.metrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.ParentNamesFamily;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.ParentNames;
import uk.ac.standrews.cs.utilities.m_tree.Distance;

import java.util.ArrayList;
import java.util.Collections;

// the distance between two families is the distance between the closest pair of people
public class GFNGLNBFNBMNDistanceOverParentNamesFamilyFurthest implements Distance<ParentNamesFamily> {

    Levenshtein levenshtein = new Levenshtein();

    @Override
    public float distance(ParentNamesFamily a, ParentNamesFamily b) {

        GFNGLNBFNBMNDistanceOverParentNames siblingDistCalc = new GFNGLNBFNBMNDistanceOverParentNames();

        ArrayList<Float> distances = new ArrayList<>();

        for (ParentNames aSibling : a.getSiblings()){
            for (ParentNames bSibling : b.getSiblings()) {
                float siblingDist = siblingDistCalc.distance(aSibling, bSibling);
                distances.add(siblingDist);
            }
        }

        return Collections.max(distances);
    }

}
