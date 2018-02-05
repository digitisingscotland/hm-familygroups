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
package uk.ac.standrews.cs.digitising_scotland.linkage.importers.generic;

import uk.ac.standrews.cs.storr.impl.BucketKind;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Utility classes for creating repo and bucket
 * Created by al on 8/11/2016.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class GenericInit {

    private static final String[] ARG_NAMES = { "store_path", "repo_name", "bucket_name",  };


    public static void main( String[] args ) throws IOException, RepositoryException {
        if (args.length >= ARG_NAMES.length) {

            String store_name = args[0];
            String repo_name = args[1];
            String bucket_name = args[2];

            Path store_path = Paths.get(store_name);
            Store store = new Store(store_path);

            IRepository repo = null;
            try {
                repo = store.getRepository(repo_name);
            } catch (RepositoryException e) {
                // Doesn't exist so create it.
                repo =  store.makeRepository(repo_name);

            }

            IBucket records;
            try {
                records = repo.getBucket(bucket_name);
            } catch (RepositoryException e) {
                // Doesn't exist so create it.
                records = repo.makeBucket(bucket_name, BucketKind.DIRECTORYBACKED);
            }

            System.out.println( repo_name + " and " + bucket_name + " created" );

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
        }
    }

}
