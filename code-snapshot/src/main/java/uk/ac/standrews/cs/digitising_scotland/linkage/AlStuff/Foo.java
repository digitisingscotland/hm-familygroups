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
package uk.ac.standrews.cs.digitising_scotland.linkage.AlStuff;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by al on 22/11/2017.
 */
public class Foo {

    static long n = 10;
    static long SOMEBIGNUMBER = 1000000;

    public static void main(String[] args) throws Exception {

        long sum = 0;
        loader1();
        for (int i = 0; i < n; i++) {
            sum += loader1();
        }
        System.out.println("loader 1: " + ((float) sum / n));

        sum = 0;
        loader2();
        for (int i = 0; i < n; i++) {
            sum += loader2();
        }
        System.out.println("loader 2: " + ((float) sum / n));

        sum = 0;
        loader3();
        for (int i = 0; i < n; i++) {
            sum += loader3();
        }
        System.out.println("loader 3: " + ((float) sum / n));

    }

    private static long loader1() throws Exception {
        ArrayList<Data> values = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (long i = 1; i < SOMEBIGNUMBER; i++) {
            long n = i;
            Callable<Data> loader = new Callable<Data>() {
                @Override
                public Data call() throws Exception {
                    if (isPrime(n)) {
                        return new Data(n);
                    } else {
                        return new Data(n * n);
                    }
                }
            };
            values.add(loader.call());
        }

//        System.out.println("1 It took " + (System.currentTimeMillis() - start) + " milliseconds");
        return System.currentTimeMillis() - start;
    }

    private static boolean isPrime(long n) {
        for (long i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return true;
            }
        }
        return false;
    }


    private static long loader2() throws Exception {
        ArrayList<Data> values = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (long i = 1; i < SOMEBIGNUMBER; i++) {
            long n = i;
            Callable<Data> loader = () -> {
                if (isPrime(n)) {
                    return new Data(n);
                } else {
                    return new Data(n * n);
                }
            };
            values.add(loader.call());
        }

//        System.out.println("2 It took " + (System.currentTimeMillis() - start) + " milliseconds");
        return System.currentTimeMillis() - start;
    }

    private static long loader3() throws Exception {
        ArrayList<Data> values = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (long i = 1; i < SOMEBIGNUMBER; i++) {
            long n = i;
            if (isPrime(n)) {
                values.add(new Data(n));
            } else {
                values.add(new Data(n * n));
            }

        }

//        System.out.println("3 It took " + (System.currentTimeMillis() - start) + " milliseconds");
        return System.currentTimeMillis() - start;
    }
}


class Data {
    long value;

    Data(long value) {
        this.value = value;
    }
}
