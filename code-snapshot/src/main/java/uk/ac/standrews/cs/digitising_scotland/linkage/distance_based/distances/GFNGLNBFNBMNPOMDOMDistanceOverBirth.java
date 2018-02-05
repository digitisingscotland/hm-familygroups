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
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.utilities.m_tree.Distance;

/**
 * Created by al on 06/03/2017.
 */
public class GFNGLNBFNBMNPOMDOMDistanceOverBirth implements Distance<Birth> {

    Levenshtein levenshtein = new Levenshtein();

    @Override
    public float distance(Birth b1, Birth m2) {

        return FFNdistance(b1,m2) + FLNdistance(b1,m2) + MFNdistance(b1,m2) + MMNdistance(b1,m2) + POMdistance(b1,m2) + DOMdistance(b1,m2);
    }

    private float FFNdistance(Birth b1, Birth m2) {
        return levenshtein.distance( b1.getFathersSurname(), m2.getFathersSurname() );
    }

    private float FLNdistance(Birth b1, Birth m2) {
        return levenshtein.distance( b1.getFathersForename(), m2.getFathersForename() );
    }

    private float MFNdistance(Birth b1, Birth m2) {
        return levenshtein.distance( b1.getMothersForename(), m2.getMothersForename() );
    }

    private float MMNdistance(Birth b1, Birth m2) {
        return levenshtein.distance( b1.getMothersMaidenSurname(), m2.getMothersMaidenSurname() );
    }

    private float POMdistance(Birth b1, Birth m2) {
        return ( b1.getPlaceOfMarriage().equals( "ng") || m2.getPlaceOfMarriage().equals( "ng" ) ? 0 : levenshtein.distance( b1.getPlaceOfMarriage(), m2.getPlaceOfMarriage() ) );
    }

    private float DOMdistance(Birth b1, Birth m2) {
        float day_dist = b1.getString( Birth.PARENTS_DAY_OF_MARRIAGE ).equals( "--") || m2.getString( Birth.PARENTS_DAY_OF_MARRIAGE ).equals( "--" ) ? 0 : levenshtein.distance( b1.getString( Birth.PARENTS_DAY_OF_MARRIAGE ), m2.getString( Birth.PARENTS_DAY_OF_MARRIAGE ) );
        float month_dist = b1.getString( Birth.PARENTS_MONTH_OF_MARRIAGE ).equals( "---") || m2.getString( Birth.PARENTS_MONTH_OF_MARRIAGE ).equals( "---" ) ? 0 : levenshtein.distance( b1.getString( Birth.PARENTS_MONTH_OF_MARRIAGE ), m2.getString( Birth.PARENTS_MONTH_OF_MARRIAGE ) );
        float year_dist = b1.getString( Birth.PARENTS_YEAR_OF_MARRIAGE).equals( "----") || m2.getString( Birth.PARENTS_YEAR_OF_MARRIAGE ).equals( "----" ) ? 0 : levenshtein.distance( b1.getString( Birth.PARENTS_YEAR_OF_MARRIAGE ), m2.getString( Birth.PARENTS_YEAR_OF_MARRIAGE ) );
        return day_dist + month_dist + year_dist;
    }
}
