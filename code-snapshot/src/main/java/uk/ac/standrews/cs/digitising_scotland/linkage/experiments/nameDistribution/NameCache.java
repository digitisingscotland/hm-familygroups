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
package uk.ac.standrews.cs.digitising_scotland.linkage.experiments.nameDistribution;

import java.util.HashSet;

/**
 * Created by al on 02/11/2017.
 */
public class NameCache {

    private HashSet<Pair> cache = new HashSet<>();
    int family_count = 0;

    public boolean isUnseenPair(String name1, String name2, boolean same_family) {

        boolean unseen = cache.add(name1.compareTo(name2) > 0 ? new Pair(name1, name2) : new Pair(name2, name1) );  // swap and keep names in cannonical order - doesn't matter which!
        if( unseen && same_family ) {
            family_count++;
        }
        return unseen;
    }


    public int getFamilyCount() {
        return family_count;
    }

}
