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


 Aide de Memoire on how to run this workflow:

Working dir for all:     /stacs-srg/linkage-java


1. Create new store:
        mkdir /DigitisingScotland/Storr-root

2. Initialise with RecordRepository:

    uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordRepository
        /DigitisingScotland/Storr-root
        Skye


3. Import the records from the dataset (e.g. for Skye):

    uk.ac.standrews.cs.digitising_scotland.linkage.importers.skye.SkyeDataSetImporter
        /DigitisingScotland/Storr-root
        Skye
        ../../digitisingscotland/hub/data/Skye/SkyeBirths.csv
        ../../digitisingscotland/hub/data/Skye/SkyeDeaths.csv
        ../../digitisingscotland/hub/data/Skye/SkyeMarriages/SkyeMarriages.csv

4. Initialise the end to end linker (sets up results buckets etc.):

    uk.ac.standrews.cs.digitising_scotland.linkage.end_to_end.InitialiseEndToEndLinker
        /DigitisingScotland/Storr-root
        Skye
        Skye-results

5. Run end to end linker:

    uk.ac.standrews.cs.digitising_scotland.linkage.end_to_end.EndToEndLinker
        /DigitisingScotland/Storr-root
        Skye
        Skye-results


