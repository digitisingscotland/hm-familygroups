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
package uk.ac.standrews.cs.digitising_scotland.linkage.distance_based;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.GFNGLNBFNBMNPOMDOMDistanceOverBirth;

import java.util.ArrayList;

/**
 * Created by al@st-andrews.ac.uk on 13/03/2017.
 *
 */
public class CentreOfChildren {

    final static GFNGLNBFNBMNPOMDOMDistanceOverBirth metric = new GFNGLNBFNBMNPOMDOMDistanceOverBirth();

    /**
     * Find the most centre node of a group of Births based on distances between parents marriage attributes
     * @param children a collection of births from which to find the centremost
     * @return the centre most birth in the collection according to the metric.
     */
    public Birth findCentroid(ArrayList<Birth> children ) {
        long[] distances = new long[children.size()]; // sums of distances between node i and other children
        for( Birth child : children ) {
            long distance = 0l;
            for( int i = 0; i < children.size(); i++ ) { // sum the distances to other children
                distance += metric.distance( child, children.get(i) );
            }
        }
        // now find the lowest total distance in diatances, hence the central child.
        int min_index = 0;
        for( int i = 0; i < distances.length; i++ ) {
            if( distances[i] < distances[min_index] ) {
                min_index = i;
            }
        }
        return children.get(min_index); // the child with the smallest distance to all other nodes
    }
}
