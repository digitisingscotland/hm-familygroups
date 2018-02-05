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
package uk.ac.standrews.cs.digitising_scotland.linkage.experiments.distances;

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.FamilyLinkageUtils;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;

import java.lang.invoke.MethodHandles;
import java.util.*;

public class KilmarnockDateProfiling extends FamilyLinkageUtils {

    private static final String[] ARG_NAMES = {"births_source_path", "deaths_source_path", "marriages_source_path"};

    public KilmarnockDateProfiling(String store_path_string, String repo_name) throws Exception {
        super(store_path_string, repo_name);
    }

    private void profileMarriageDays() throws BucketException {

        Set<Birth> birth_set = loadBirths();

        List<String> days = new ArrayList<>();

        for (Birth birth_record : birth_set) {
            days.add((String) birth_record.get(Birth.PARENTS_DAY_OF_MARRIAGE));
        }

        profileStrings(days);
    }

    private void profileMarriageMonths() throws BucketException {

        Set<Birth> birth_set = loadBirths();

        List<String> months = new ArrayList<>();

        for (Birth birth_record : birth_set) {
            months.add((String) birth_record.get(Birth.PARENTS_MONTH_OF_MARRIAGE));
        }

        profileStrings(months);
    }

    private void profileMarriageYears() throws BucketException {

        Set<Birth> birth_set = loadBirths();

        List<String> years = new ArrayList<>();

        for (Birth birth_record : birth_set) {
            years.add((String) birth_record.get(Birth.PARENTS_YEAR_OF_MARRIAGE));
        }

        profileStrings(years);
    }

    private void profileStrings(List<String> strings) {

        Map<String, Integer> map = new HashMap<>();

        for (String s : strings) {

            if (map.containsKey(s)) {
                map.put(s, map.get(s) + 1);
            } else {
                map.put(s, 1);
            }
        }

        Map<String, Integer> sorted_map = new TreeMap<>(new ValueComparator(map));
        sorted_map.putAll(map);

        for (Map.Entry<String, Integer> entry : sorted_map.entrySet()) {
            System.out.println(entry.getValue() + " \"" + entry.getKey() + "\"");
        }
    }

    private void compute() throws Exception {

        timedRun("Profiling marriage days", () -> {
            profileMarriageDays();
            return null;
        });

        timedRun("Profiling marriage months", () -> {
            profileMarriageMonths();
            return null;
        });

        timedRun("Profiling marriage years", () -> {
            profileMarriageYears();
            return null;
        });
    }

    static class ValueComparator implements Comparator<String> {

        Map<String, Integer> base;

        ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        @Override
        public int compare(String a, String b) {
            Integer x = base.get(a);
            Integer y = base.get(b);
            if (x.equals(y)) {
                return a.compareTo(b);
            }
            return x.compareTo(y);
        }
    }

    //***********************************************************************************

    public static void main(String[] args) throws Exception {

        Experiment experiment = new Experiment(ARG_NAMES, args, MethodHandles.lookup().lookupClass());

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];

            KilmarnockDateProfiling matcher = new KilmarnockDateProfiling(store_path, repo_name);

            experiment.printDescription();

            matcher.compute();

        } else {
            experiment.usage();
        }
    }
}
