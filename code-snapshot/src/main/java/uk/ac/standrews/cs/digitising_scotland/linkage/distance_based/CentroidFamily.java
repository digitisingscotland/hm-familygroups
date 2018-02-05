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

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.GFNGLNBFNBMNPOMDOMDistanceOverBirth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Family;

import java.util.List;

/**
 * Essentially a set of siblings carrying an id.
 * Created by al on 28/02/2017.
 */
public class CentroidFamily extends Family {

    final static GFNGLNBFNBMNPOMDOMDistanceOverBirth metric = new GFNGLNBFNBMNPOMDOMDistanceOverBirth();

    protected Birth centroid = null; // centroid of family

    public CentroidFamily(Birth child) {
        super(child);
    }

    public String getPlaceOfMarriage() {
        check_centroid();
        return centroid.getPlaceOfMarriage();
    }

    public String getDayOfMarriage() {
        check_centroid();
        return centroid.getString( Birth.PARENTS_DAY_OF_MARRIAGE );
    }

    public String getMonthOfMarriage() {
        check_centroid();
        return centroid.getString( Birth.PARENTS_MONTH_OF_MARRIAGE );
    }

    public String getYearOfMarriage() {
        check_centroid();
        return centroid.getString( Birth.PARENTS_YEAR_OF_MARRIAGE );
    }

    public String getMothersMaidenSurname() {
        check_centroid();
        return centroid.getString( Birth.MOTHERS_MAIDEN_SURNAME );
    }

    public String getMothersForename() {
        check_centroid();
        return centroid.getString( Birth.MOTHERS_FORENAME );
    }

    public String getFathersSurname() {
        check_centroid();
        return centroid.getString( Birth.FATHERS_SURNAME );
    }

    public String getFathersForename() {
        return centroid.getString( Birth.FATHERS_FORENAME );
    }

    @Override
    protected void initParents(Birth child) {
        // don't do anything in this case.
    }

    @Override
    public void addSibling( Birth sibling ) {
        super.addSibling(sibling);
        Birth centroid = null;
    }

    private void check_centroid() {
        if( centroid == null ) {
            centroid = findCentroid( super.getSiblings() );
        }
    }
    /**
     * Find the most centre node of a group of Births based on distances between parents marriage attributes
     * @param children a collection of births from which to find the centremost
     * @return the centre most birth in the collection according to the metric.
     */
    private Birth findCentroid(List<Birth> children ) {
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
