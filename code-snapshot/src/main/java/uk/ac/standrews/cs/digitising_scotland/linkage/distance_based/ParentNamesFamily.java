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

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.ParentNames;

import java.util.HashSet;
import java.util.Set;

/**
 * Essentially a set of siblings carrying an id.
 * Created by al on 28/02/2017.
 */
public class ParentNamesFamily {

    private static long next_id_to_be_allocated = 1;

    public Set<ParentNames> siblings;
    public final long id;

    private ParentNamesFamily() {
        this.id = next_id_to_be_allocated++;
        this.siblings = new HashSet<>();
    }

    public ParentNamesFamily(ParentNames child) {
        this();
        siblings.add(child);
    }

    // this is an escape hatch for when we want to manage the id from the outside...
    public ParentNamesFamily(long id, Set<ParentNames> siblings) {
        this.id = id;
        this.siblings = siblings;
    }

    public Set<ParentNames> getSiblings() {
        return siblings;
    }

    public void addSibling(ParentNames sibling) {
        siblings.add(sibling);
    }

}
