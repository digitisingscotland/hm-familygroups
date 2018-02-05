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
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.utilities.m_tree.Distance;

/**
 * Created by al on 06/03/2017.
 */
public class FNLNSFFNFLNMFNMMSDistanceOverDeath implements Distance<Death> {

    Levenshtein levenshtein = new Levenshtein();

    @Override
    public float distance(Death d1, Death d2) {

        return FNdistance(d1,d2) + LNdistance(d1,d2) + Sexdistance(d1,d2) + FFNdistance(d1,d2) + FLNdistance(d1,d2) + MFNdistance(d1,d2) + MMSdistance(d1,d2);
    }

    private float FNdistance(Death d1, Death d2) {
        return levenshtein.distance( d1.getForename(), d2.getForename() );
    }
    
    private float LNdistance(Death d1, Death d2) {
        return levenshtein.distance( d1.getSurname(), d2.getSurname() );
    }

    private float Sexdistance(Death d1, Death d2) {
        return levenshtein.distance( d1.getSex(), d2.getSex() );
    }

    private float FFNdistance(Death d1, Death d2) {
        return levenshtein.distance( d1.getFathersSurname(), d2.getFathersSurname() );
    }

    private float FLNdistance(Death d1, Death d2) {
        return levenshtein.distance( d1.getFathersForename(), d2.getFathersForename() );
    }

    private float MFNdistance(Death d1, Death d2) {
        return levenshtein.distance( d1.getMothersForename(), d2.getMothersForename() );
    }

    private float MMSdistance(Death d1, Death d2) {
        return levenshtein.distance( d1.getMothersMaidenSurname(), d2.getMothersMaidenSurname() );
    }
    
}
