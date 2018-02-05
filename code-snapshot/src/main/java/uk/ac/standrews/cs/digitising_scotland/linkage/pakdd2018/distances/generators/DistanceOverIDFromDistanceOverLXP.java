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
package uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.distances.generators;

import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.distances.interfaces.CountingDistance;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

/**
 * Created by al on 06/03/2017.
 */
public class DistanceOverIDFromDistanceOverLXP {

    public static CountingDistance<IStoreReference<LXP>> wrapCountingDistanceOverLXP(CountingDistance<LXP> base) {

        return new CountingDistance<IStoreReference<LXP>>() {

            @Override
            public int getComparisonCount() {
                return base.getComparisonCount();
            }

            @Override
            public float distance(IStoreReference<LXP> a, IStoreReference<LXP> b) {
                try {
                    return base.distance(a.getReferend(), b.getReferend());
                } catch (BucketException e) {
                    ErrorHandling.error("Exception in distance - fix me!!!!!!");
                    return 0.0f;
                }
            }

        };

    }
}
