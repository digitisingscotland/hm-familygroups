====
    Copyright 2017 Systems Research Group, University of St Andrews:
    <https://github.com/stacs-srg>

    This file is part of the module linkage-java.

    linkage-java is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
    version.

    linkage-java is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
    warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with linkage-java. If not, see
    <http://www.gnu.org/licenses/>.
====

A Java program can be run from the command line using Maven to configure the class path. For example:

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.linkage.tools.GenerateEventRecords" -Dexec.args="-u10"
