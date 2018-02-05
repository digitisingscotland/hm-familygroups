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
public class BFNBLNBDOBBFFNBFLNBMFNBMMNDistanceOverMarriage implements Distance<Marriage> {

    Levenshtein levenshtein = new Levenshtein();

    @Override
    public float distance(Marriage m1, Marriage m2) {

        return BFNdistance(m1, m2) + BLNdistance(m1, m2) + BDOBdistance(m1, m2) + BFFNdistance(m1, m2) +
                BFLNdistance(m1, m2) + BMFNdistance(m1, m2) + BMMNdistance(m1, m2);
    }

    private float BFNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance(m1.getBridesForename(), m2.getBridesForename());
    }

    private float BLNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance(m1.getBridesSurname(), m2.getBridesSurname());
    }

    private float BDOBdistance(Marriage m1, Marriage m2) {
        // return levenshtein.distance(m1.getBridesDob(), m2.getBridesDob());
        System.out.println( "DOB commented in BDOBdistance" );
        return 0;
    }

    private float BFFNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance(m1.getBFFN(), m2.getBFFN());
    }

    private float BFLNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance(m1.getBFLN(), m2.getBFLN());
    }

    private float BMFNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance(m1.getBMFN(), m2.getBMFN());
    }

    private float BMMNdistance(Marriage m1, Marriage m2) {
        return levenshtein.distance(m1.getBMMN(), m2.getBMMN());
    }
    
    
}



