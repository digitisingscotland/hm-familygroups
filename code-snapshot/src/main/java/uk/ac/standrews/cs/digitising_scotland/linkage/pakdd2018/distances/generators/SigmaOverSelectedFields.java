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

import org.simmetrics.StringDistance;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.utilities.m_tree.Distance;

import java.util.List;

/**
 * Created by al on 02/11/2017.
 */
public class SigmaOverSelectedFields {

    public static Distance<LXP> wrapDistance(List<Integer> exclude_fields, StringDistance base) {

        return new Distance<LXP>() {

            public float distance(LXP r1, LXP r2) {

                float result = 0.0f;

                for (int i : r1.getMetaData().getSlots()) {
                    if (!exclude_fields.contains(i)) { // do not match the excluded fields
                        String field1 = r1.getString(i);
                        String field2 = r2.getString(i);

                        result += base.distance(field1, field2);
                    }
                }

                return result;

            }
        };
    }
}
