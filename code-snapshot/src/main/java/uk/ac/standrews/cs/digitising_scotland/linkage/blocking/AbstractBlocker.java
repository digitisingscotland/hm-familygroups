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
package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.Blocker;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import static org.apache.commons.codec.language.RefinedSoundex.US_ENGLISH;

public abstract class AbstractBlocker<T extends LXP> extends Blocker<T> {

    /**
     * @param input the stream over which to block
     * @param output_repo - the repository into which results are written
     * @param clazz
     */
    public AbstractBlocker(final IInputStream<T> input, final IRepository output_repo, final Class<T> clazz) {

        super(input, output_repo, clazz);
    }

    protected String concatenate(String... attributes) {

        StringBuilder builder = new StringBuilder();

        for (String attribute : attributes) {

            if (builder.length() > 0) {
                builder.append("-");
            }
            builder.append(attribute);
        }

        return clean(builder.toString());
    }

    protected String normaliseName(String name) {

        return US_ENGLISH.soundex(name);
    }

    protected String normalisePlace(String place) {

        if (place.equals("") || place.equals("na") || place.equals("ng")) {
            return "___";
        }
        else {
            return place;
        }
    }

    private String clean(final String s) {

        return s.replace("/", "_").replace("\"", "").replace(" ", "");
    }
}
