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
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.ParentNames;
import uk.ac.standrews.cs.utilities.m_tree.Distance;

public class GFNGLNBFNBMNDistanceOverParentNames implements Distance<ParentNames> {

    Levenshtein levenshtein = new Levenshtein();

    @Override
    public float distance(ParentNames a, ParentNames b) {

        return FFNdistance(a, b) + FLNdistance(a, b) + MFNdistance(a, b) + MMNdistance(a, b);
    }

    private float FFNdistance(ParentNames a, ParentNames b) {
        return levenshtein.distance(a.getFathersSurname(), b.getFathersSurname());
    }

    private float FLNdistance(ParentNames a, ParentNames b) {
        return levenshtein.distance(a.getFathersForename(), b.getFathersForename());
    }

    private float MFNdistance(ParentNames a, ParentNames b) {
        return levenshtein.distance(a.getMothersForename(), b.getMothersForename());
    }

    private float MMNdistance(ParentNames a, ParentNames b) {
        return levenshtein.distance(a.getMothersMaidenSurname(), b.getMothersMaidenSurname());
    }
}
