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

import java.util.HashMap;

/**
 * Created by al on 02/11/2017.
 */
public class CachingDistanceOverDistance {

    public static CountingDistance<LXP> wrapDistance(CountingDistance<LXP> base) {
        return new CountingDistance<LXP>() {

            private HashMap<Pair, Float> cache = new HashMap<>();

            @Override
            public int getComparisonCount() {
                return base.getComparisonCount();
            }

            @Override
            public float distance(LXP a, LXP b) {

                final Pair key = new Pair(a, b);
                Float result = cache.get(key);
                if (result == null) {
                    // cache miss.
                    // - calculate distance,
                    // - store in cache,
                    result = base.distance(a, b);
                    cache.put(key, result);
                }
                return result;
            }

        };
    }
}
