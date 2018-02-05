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

import uk.ac.standrews.cs.utilities.dataset.DataSet;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Module to inject and analyse output created by BirthMarriageRangeCSVGenerator
 * This is a csv file containing rows number of matches between births and marriages at edit distance 1,2, ..
 * Created by al on 22/02/2017.
 *
 * @author al@st-andrews.ac.uk
 */
public class BirthMarriageRangeSearchCSVAnalysis {

    private int num_births = 0;
    int[][] data_array;
    private static final int MAX_COLUMN_INDEX = BirthMarriageRangeCSVGenerator.RANGE_MAX;

    public BirthMarriageRangeSearchCSVAnalysis(String csv_source_path) throws IOException {

        num_births = ingestCsv(csv_source_path);

        analyseDataArray();
    }

    private int ingestCsv(String csv_source_path) throws IOException {

        DataSet data_set = new DataSet(Paths.get(csv_source_path));
        num_births = data_set.getRecords().size();
        data_array = new int[num_births][MAX_COLUMN_INDEX];
        int rows_processed = 0;

        for (List<String> row : data_set.getRecords()) {
            int data_column = 0;
            for (String entry : row) {
                data_array[rows_processed][data_column++] = Integer.parseInt(entry);
            }
            rows_processed++;
        }
        return rows_processed;
    }

    private void analyseDataArray() {
        int no_matches = analyseNoMatches();
        int perfect = analysePerfectMatch();
        int unique = analyseUniqueMatches();

        int[] d_to_any_match = analyseDistanceToFirstMatch(false);
        int[] d_to_first_unique = analyseDistanceToFirstMatch(true);
        int[] one_match_runs = analyseSingleMatchRuns();

        System.out.println("No matches found = " + no_matches + " = " + no_matches * 100 /  num_births + "%");
        System.out.println("Number of perfect matches = " + perfect + " = " + perfect * 100 /  num_births + "%");
        System.out.println("Number of unique matches = " + unique + " = " + unique * 100 /  num_births + "%");

        show(d_to_any_match, "Distance to any match");
        show(d_to_first_unique, "Distance to first unique match");
        show(one_match_runs, "Length of one Match runs ");
    }

    private void display(int[] arrai) {
        int[] counter = new int[16];
        int running_total = 0;

        for (int i : arrai) {
            counter[i]++;
        }
        for (int occurrences : counter) {
            running_total += occurrences;
//            System.out.println( occurrences * 100 /  num_births + "%   running total = " + running_total * 100 /  num_births + "%" );
        }
    }

    private void show(int[] arrai, String title) {
        System.out.println(title);
        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("\tAverage: " + df.format(mean(arrai)));
        System.out.println("\tSD: " + df.format(stddev(arrai)));
        System.out.println();
        display(arrai);
    }

    /**
     * Returns the mean of the specified array.
     *
     * @param numbers the array
     */
    private float mean(int[] numbers) {
        float sum = 0;
        for (int i = 0; i < numbers.length; i++)
            sum = sum + numbers[i];

        return sum / numbers.length;
    }

    /**
     * Returns the std dev in the specified array.
     *
     * @param numbers the array
     */
    private float stddev(int[] numbers) {
        return (float) Math.sqrt(variance(numbers));
    }

    /**
     * Returns the variance in the specified array.
     *
     * @param numbers the array
     * @return the sample variance in the array {@code numbers[]};
     */
    public float variance(int[] numbers) {
        if (numbers.length == 0) return Float.NaN;
        float avg = mean(numbers);
        float sum = 0.0f;
        for (int i = 0; i < numbers.length; i++) {
            sum += (numbers[i] - avg) * (numbers[i] - avg);
        }
        return sum / (numbers.length - 1);
    }

    /**
     * @return the number of matches before finding a 1.
     */
    private int[] analyseDistanceToFirstMatch(boolean require_unique) {
        int[] result = new int[num_births];

        for (int row_index = 0; row_index < num_births; row_index++) {
            result[row_index] = firstMatchInRow(data_array[row_index], require_unique);
        }
        return result;
    }

    private int firstMatchInRow(int[] row, boolean require_unique) {
        for (int column_index = 0; column_index < MAX_COLUMN_INDEX; column_index++) {
            if (require_unique) {
                if (row[column_index] == 1) {
                    return column_index;
                }
            } else {
                if (row[column_index] >= 1) {
                    return column_index;
                }
            }
        }
        return MAX_COLUMN_INDEX; // not found a match need to return max_index
    }

    /**
     * @return the number of rows for which there are all ones with possibly some zeros
     * This is equivalent to the last column being 1.
     */
    private int analyseUniqueMatches() {
        int counter = 0;
        for (int[] row : data_array) {
            if (row[MAX_COLUMN_INDEX - 1] == 1) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Counts the number of perfect matches - i.e. edit distance is zero.
     * This is equivalent to the first column being 1.
     */
    private int analysePerfectMatch() {
        int counter = 0;
        for (int[] row : data_array)
            if (row[0] == 1) {
                counter++;
            }
        return counter;
    }

    /**
     * Counts the number of rows that are all zeros.
     */
    private int analyseNoMatches() {
        int counter = 0;

        for (int row_index = 0; row_index < num_births; row_index++) {
            int[] row = data_array[row_index];
            boolean found = false;
            for (int val : row) {
                if (val != 0) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * @return the length of single matches within a row
     * This is equivalent to the finding the first column greater than 1.
     */
    private int[] analyseSingleMatchRuns() {
        int[] result = new int[num_births];

        for (int row_index = 0; row_index < num_births; row_index++) {
            int count = 0;
            for (int column_index = 0; column_index < MAX_COLUMN_INDEX; column_index++) {
                if (data_array[row_index][column_index] > 1) {
                    result[row_index] = count;
                    break;
                }
                if (data_array[row_index][column_index] == 1) {
                    count++;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {

        String csv_source_path = "/Digitising Scotland/KilmarnockBDM/birthMarriageDistances.csv";

        new BirthMarriageRangeSearchCSVAnalysis(csv_source_path);
    }
}
