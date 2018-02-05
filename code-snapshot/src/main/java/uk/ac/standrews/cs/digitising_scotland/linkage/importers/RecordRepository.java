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
package uk.ac.standrews.cs.digitising_scotland.linkage.importers;

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.storr.impl.BucketKind;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.TypeFactory;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IReferenceType;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Module to initialise the store ready for ingesting of birth/death/marriage records.
 * Created by al on 22/3/2017.
 *
 * @author al@st-andrews.ac.uk
 */
public class RecordRepository {

    public static final String BIRTHS_BUCKET_NAME = "birth_records";              // Name of bucket containing birth records (inputs).
    public static final String DEATHS_BUCKET_NAME = "death_records";              // Name of bucket containing death records (inputs).
    public static final String MARRIAGES_BUCKET_NAME = "marriage_records";        // Name of bucket containing marriage records (inputs).

    public static final String BIRTH_TYPE_NAME = "Birth";
    public static final String DEATH_TYPE_NAME = "Death";
    public static final String MARRIAGE_TYPE_NAME = "Marriage";

    public IStore store;
    public TypeFactory type_factory;

    public IBucket<Birth> births;
    public IBucket<Marriage> marriages;
    public IBucket<Death> deaths;

    // Bucket declarations

    private static final String[] ARG_NAMES = {"store_path", "repo_name"};

    public RecordRepository(Path store_path, String repository_name) throws Exception {

        store = getStore(store_path);
        type_factory = store.getTypeFactory();

        initialiseBuckets(repository_name);
    }

    public RecordRepository(String store_path_string, String repository_name) throws Exception {

        this(Paths.get(store_path_string), repository_name);
    }

    private void initialiseBuckets(String repository_name) throws RepositoryException {

        try {
            IRepository input_repository = store.getRepository(repository_name);

            births = input_repository.getBucket(BIRTHS_BUCKET_NAME, Birth.class);
            deaths = input_repository.getBucket(DEATHS_BUCKET_NAME, Death.class);
            marriages = input_repository.getBucket(MARRIAGES_BUCKET_NAME, Marriage.class);

        } catch (RepositoryException e) {

            // The repository hasn't previously been initialised.

            IRepository input_repository = store.makeRepository(repository_name);

            births = input_repository.makeBucket(BIRTHS_BUCKET_NAME, BucketKind.DIRECTORYBACKED, Birth.class);
            deaths = input_repository.makeBucket(DEATHS_BUCKET_NAME, BucketKind.DIRECTORYBACKED, Death.class);
            marriages = input_repository.makeBucket(MARRIAGES_BUCKET_NAME, BucketKind.DIRECTORYBACKED, Marriage.class);
        }
    }

    private static IStore getStore(Path store_path) throws RepositoryException {

        return new Store(store_path);
    }

    private IReferenceType getBirthType() {

        return type_factory.containsKey(BIRTH_TYPE_NAME) ? type_factory.getTypeWithName(BIRTH_TYPE_NAME) : type_factory.createType(Birth.class, BIRTH_TYPE_NAME);
    }

    private IReferenceType getDeathType() {

        return type_factory.containsKey(DEATH_TYPE_NAME) ? type_factory.getTypeWithName(DEATH_TYPE_NAME) : type_factory.createType(Death.class, DEATH_TYPE_NAME);
    }

    private IReferenceType getMarriageType() {

        return type_factory.containsKey(MARRIAGE_TYPE_NAME) ? type_factory.getTypeWithName(MARRIAGE_TYPE_NAME) : type_factory.createType(Marriage.class, MARRIAGE_TYPE_NAME);
    }

    //***********************************************************************************

    public static void usage() {

        System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
    }

    public static void main(String[] args) throws Exception {

        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];

            new RecordRepository(store_path, repo_name);

            System.out.println("Initialised repository " + repo_name + " in store at " + store_path);

        } else {
            usage();
        }
    }
}
