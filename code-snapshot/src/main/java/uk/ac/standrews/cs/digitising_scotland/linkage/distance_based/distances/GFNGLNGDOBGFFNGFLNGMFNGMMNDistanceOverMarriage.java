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
 * Created by al on 24/4/2017.
 */
public class GFNGLNGDOBGFFNGFLNGMFNGMMNDistanceOverMarriage implements Distance<Marriage> {

    Levenshtein levenshtein = new Levenshtein();

    @Override
    public float distance(Marriage m1, Marriage m2) {

        return GFNdistance(m1, m2) + GLNdistance(m1, m2) + GDOBdistance(m1, m2) + GFFNdistance(m1, m2) +
                GFLNdistance(m1, m2) + GMFNdistance(m1, m2) + GMMNdistance(m1, m2);
    }

    private float GFNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance(m1.getGroomsForename(), m2.getGroomsForename());
    }

    private float GLNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance(m1.getGroomsSurname(), m2.getGroomsSurname());
    }

    private float GDOBdistance(Marriage m1, Marriage m2) {
        // return levenshtein.distance(m1.getGroomsDob(), m2.getGroomsDob());
        System.out.println( "DOB commented in GDOBdistance" );
        return 0;
    }

    private float GFFNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance(m1.getGFFN(), m2.getGFFN());
    }

    private float GFLNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance(m1.getGFLN(), m2.getGFLN());
    }

    private float GMFNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance(m1.getGMFN(), m2.getGMFN());
    }

    private float GMMNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance(m1.getGMMN(), m2.getGMMN());
    }


}



