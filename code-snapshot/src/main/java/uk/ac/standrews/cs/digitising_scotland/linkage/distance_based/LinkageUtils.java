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

import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.utilities.TimeManipulation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by al on 21/09/2017.
 */
public class LinkageUtils {
    final RecordRepository record_repository;

    public LinkageUtils(String store_path_string, String repo_name) throws Exception {

        record_repository = new RecordRepository(store_path_string, repo_name);
    }

    public IBucket<Birth> getBirths() {

        return record_repository.births;
    }

    public IBucket<Marriage> getMarriages() {

        return record_repository.marriages;
    }

    public IBucket<Death> getDeaths() {

        return record_repository.deaths;
    }

    public int getBirthsCount() {

        try {
            return record_repository.births.size();
        } catch (BucketException e) {
            return -1;
        }
    }

    public int getMarriagesCount() {

        try {
            return record_repository.marriages.size();
        } catch (BucketException e) {
            return -1;
        }
    }

    public int getDeathsCount() {

        try {
            return record_repository.marriages.size();
        } catch (BucketException e) {
            return -1;
        }
    }

    static float mean(Collection<Integer> values) {

        int sum = 0;
        for (int i : values) {
            sum += i;
        }
        return ( sum / values.size());
    }

    protected Set<Birth> loadBirths() throws BucketException {

        return loadBirths(Integer.MAX_VALUE);
    }

    protected Set<Birth> loadBirths(int number_of_births_to_process) throws BucketException {

        Set<Birth> birth_set = new HashSet<>();

        int count = 0;

        for (Birth birth_record : record_repository.births.getInputStream()) {
            birth_set.add(birth_record);
            if (++count >= number_of_births_to_process) break;
        }

        return birth_set;
    }


    public void timedRun(String description, Callable<Void> function) throws Exception {

        System.out.println();
        System.out.println(description);
        long time = System.currentTimeMillis();
        function.call();
        long elapsed = System.currentTimeMillis() - time;
        System.out.println(description + " - took " + TimeManipulation.formatMillis(elapsed) + " (h:m:s)");
    }


}

