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
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Family;
import uk.ac.standrews.cs.utilities.m_tree.Distance;

/**
 * Created by al on 06/03/2017.
 */
public class GFNGLNBFNBMNPOMDOMDistanceOverFamilyClean implements Distance<Family> {

    Levenshtein levenshtein = new Levenshtein();

    @Override
    public float distance(Family f1, Family f2) {

        return FFNdistance(f1,f2) + FLNdistance(f1,f2) + MFNdistance(f1,f2) + MMNdistance(f1,f2) + POMdistance(f1,f2) + DOMdistance(f1,f2);
    }

    private float FFNdistance(Family f1, Family f2) {
        return levenshtein.distance( f1.getFathersSurnameClean(), f2.getFathersSurnameClean() );
    }

    private float FLNdistance(Family f1, Family f2) {
        return levenshtein.distance( f1.getFathersForenameClean(), f2.getFathersForenameClean() );
    }

    private float MFNdistance(Family f1, Family f2) {
        return levenshtein.distance( f1.getMothersForenameClean(), f2.getMothersForenameClean() );
    }

    private float MMNdistance(Family f1, Family f2) {
        return levenshtein.distance( f1.getMothersSurnameClean(), f2.getMothersSurnameClean() );
    }

    private float POMdistance(Family f1, Family f2) {
        return ( f1.getPlaceOfMarriage().equals( "ng") || f2.getPlaceOfMarriage().equals( "ng" ) ? 0 : levenshtein.distance( f1.getPlaceOfMarriage(), f2.getPlaceOfMarriage() ) );
    }

    private float DOMdistance(Family f1, Family f2) {
        float day_dist = f1.getDayOfMarriage().equals( "--") || f2.getDayOfMarriage().equals( "--" ) ? 0 : levenshtein.distance( f1.getDayOfMarriage(), f2.getDayOfMarriage() );
        float month_dist = f1.getMonthOfMarriage().equals( "---") || f2.getMonthOfMarriage().equals( "---" ) ? 0 : levenshtein.distance( f1.getMonthOfMarriage(), f2.getMonthOfMarriage() );
        float year_dist = f1.getYearOfMarriage().equals( "----") || f2.getYearOfMarriage().equals( "----" ) ? 0 : levenshtein.distance( f1.getYearOfMarriage(), f2.getYearOfMarriage() );
        return day_dist + month_dist + year_dist;
    }
}
