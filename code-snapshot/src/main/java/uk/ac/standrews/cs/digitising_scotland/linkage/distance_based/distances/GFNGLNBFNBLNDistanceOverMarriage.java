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
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.utilities.m_tree.Distance;

/**
 * Created by al on 06/03/2017.
 */
public class GFNGLNBFNBLNDistanceOverMarriage implements Distance<Marriage> {

    Levenshtein levenshtein = new Levenshtein();

    @Override
    public float distance(Marriage m1, Marriage m2) {

        return GFNdistance(m1,m2) + GLNdistance(m1,m2) + BFNdistance(m1,m2) + BLNdistance(m1,m2);
    }

    private float GFNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance( m1.getGroomsForename(), m2.getGroomsForename() );
    }

    private float GLNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance( m1.getGroomsSurname(), m2.getGroomsSurname() );
    }

    private float BFNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance( m1.getBridesForename(), m2.getBridesForename() );
    }

    private float BLNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance( m1.getBridesSurname(), m2.getBridesSurname() );
    }
}


