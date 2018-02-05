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
package uk.ac.standrews.cs.digitising_scotland.linkage.rUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by al on 09/10/2017.
 */
public class Rcaller {

     public static int callR(String script_path, String data_path, String image_path, String diagram_label) throws IOException {

         String[] commands = {"Rscript", script_path, data_path, image_path, diagram_label};
         ProcessBuilder pb = new ProcessBuilder(commands);

         Process proc = pb.start();

         BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

         String next_line = null;

         while((next_line = stdInput.readLine())!=null) {
             System.out.println( "R message: " + next_line); // forward all R messages to stdout with a message
         }

         try {
             return proc.waitFor();
         } catch (InterruptedException e) {
             return 1; // fail
         }


     }
}
