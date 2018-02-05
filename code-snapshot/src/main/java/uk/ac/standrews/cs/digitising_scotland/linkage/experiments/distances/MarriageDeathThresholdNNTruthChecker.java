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

import java.lang.invoke.MethodHandles;

/**
 * Attempt to perform linking using MTree matching
 * Links from birth certificate to their own marriage certificate
 *
 * Created by al on 24/4/1017
 */
public class MarriageDeathThresholdNNTruthChecker {

    private static final String[] ARG_NAMES = {"store_path","repo_name"};

    public static void main(String[] args) throws Exception {

        Experiment experiment = new Experiment(ARG_NAMES, args, MethodHandles.lookup().lookupClass());

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];

            System.out.println( "Call to MarriageDeathThresholdNN commented out!!");
//            MarriageDeathThresholdNN matcher = new MarriageDeathThresholdNN(store_path,repo_name);
//
//            experiment.printDescription();
//
//            matcher.compute( true );
//            matcher.showFamilies();

        } else {
            experiment.usage();
        }
    }
}
