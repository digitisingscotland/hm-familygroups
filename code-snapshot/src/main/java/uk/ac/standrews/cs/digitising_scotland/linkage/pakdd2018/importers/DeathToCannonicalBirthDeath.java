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
package uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.importers;

import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.lxp_records.BirthDeath;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Utility classes for converting Skye and Kilmarnock birth records into a cannonical LXP format
 * performs som remapping of first and second names
 *
 * Created by al on 30/11/2017
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class DeathToCannonicalBirthDeath {

    IBucket<Death> source_bucket;
    IBucket destination_bucket;

    public DeathToCannonicalBirthDeath(String store_name, String repo_name, String source_bucket_name, String dest_bucket_name) throws RepositoryException {
        Path store_path = Paths.get(store_name);
        Store store = new Store(store_path);

        IRepository repo = store.getRepository(repo_name);

        source_bucket = repo.getBucket(source_bucket_name);
        destination_bucket = repo.getBucket(dest_bucket_name);
    }

    public int convertRecords() throws BucketException {

        IInputStream<Death> source_stream = source_bucket.getInputStream();

        int count = 0;

        for (Death record : source_stream) {

            LXP bd = new BirthDeath();

            bd.put( BirthDeath.FORENAME, record.getForename() );

            String sex = record.getSex();
            bd.put( BirthDeath.SEX,sex  );
            if (sex.equals("f")) { // assumes males keep the same surname for ever.
                bd.put(BirthDeath.SURNAME_AT_BIRTH, record.getFathersSurname() );
            } else {
                bd.put(BirthDeath.SURNAME_AT_BIRTH, record.getSurname() );
            }
            bd.put( BirthDeath.FATHERS_SURNAME, record.getSurname() );
            bd.put( BirthDeath.FATHERS_FORENAME, record.getFathersForename() );
            bd.put( BirthDeath.MOTHERS_FORENAME, record.getMothersForename() );
            bd.put( BirthDeath.MOTHERS_MAIDEN_SURNAME, record.getMothersMaidenSurname() );
            bd.put( BirthDeath.GROUND_TRUTH, record.getString( Death.ORIGINAL_ID ) );

            destination_bucket.makePersistent(bd);
            count++;
        }
        return count;
    }

    private static final String[] ARG_NAMES = { "store_path", "repo_name", "source_bucket_name", "destination_bucket_name" };

    public static void main( String[] args ) throws IOException, RepositoryException, RecordFormatException, BucketException {
        if (args.length >= ARG_NAMES.length) {

            String store_name = args[0];
            String repo_name = args[1];
            String source_bucket_name = args[2];
            String dest_bucket_name = args[3];

            DeathToCannonicalBirthDeath mapper = new DeathToCannonicalBirthDeath(store_name, repo_name, source_bucket_name, dest_bucket_name);
            int count = mapper.convertRecords();

            System.out.println( "Converted " + count + " records");

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
        }
    }

}
