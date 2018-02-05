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
package uk.ac.standrews.cs.digitising_scotland.linkage.blocking;

import uk.ac.standrews.cs.digitising_scotland.linkage.importers.DataSetImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.importers.RecordRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
import uk.ac.standrews.cs.storr.impl.*;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Attempt to create a linking framework
 * Created by al on 6/8/2014.
 */
public abstract class BlockingLinker {

    // Repositories and stores

    private static String input_repo_name = "BDM_repo";                             // input repository containing event records
    private static String blocked_birth_repo_name = "blocked_birth_repo";           // repository for blocked Birth records
    private static String FFNFLNMFNMMNPOMDOM_repo_name = "FFNFLNMFNMMNPOMDOM_repo";   // repository for blocked Marriage records
    private static String FFNFLNMFNMMN_repo_name = "FFNFLNMFNMMN_repo";   // repository for blocked Marriage records

    private static String linkage_repo_name = "linkage_repo";                       // repository for Relationship records

    private IStore store;
    private IRepository input_repo;             // Repository containing buckets of BDM records
    private IRepository role_repo;
    private IRepository blocked_births_repo;
    private IRepository FFNFLNMFNMMNPOMDOM_repo;
    private IRepository FFNFLNMFNMMN_repo;
    private IRepository linkage_repo;

    private String store_path_string;

    // Bucket declarations

    private RecordRepository record_repository;

    private IBucket<Role> roles;                      // Bucket containing roles extracted from BDM records
    private IBucket<Relationship> relationships;      // Bucket containing relationships between Roles

    // Paths to sources

    private static String births_name = "birth_records";                            // Name of bucket containing birth records (inputs).
    private static String marriages_name = "marriage_records";                      // Name of bucket containing marriage records (inputs).
    private static String deaths_name = "death_records";                            // Name of bucket containing marriage records (inputs).

    // Names of buckets

    private static String role_name = "roles";                                   // Name of bucket containing roles extracted from BDM records
    private static String relationships_name = "relationships";                  // Name of bucket containing Relationship records

    private IReferenceType birthType;
    private IReferenceType deathType;
    private IReferenceType marriageType;
    private IReferenceType roleType;
    private IReferenceType relationshipType;

    private ArrayList<Long> oids = new ArrayList<>();
    private int birth_count;
    private int death_count;
    private int marriage_count;
    private int families_with_parents;
    private int families_with_children;
    private int single_children;
    private int children_in_groups;

    public BlockingLinker(String birth_records_path, String death_records_path, String marriage_records_path) throws Exception {

        System.out.println("Initialising");
        initialise();

        System.out.println("Importing");
        importRecords(birth_records_path, death_records_path, marriage_records_path);

        System.out.println("Blocking");
        block();

        System.out.println("Examining Blocks");
        printMarriages();
        printFamilies();
        // formFamilies();
        printStats();

        System.out.println("Finished");
    }

    private void initialise() throws Exception {

        Path store_path = Files.createTempDirectory(null);
        store_path_string = store_path.toString();
        record_repository = new RecordRepository(store_path, input_repo_name);

        store = new Store(store_path);

        blocked_births_repo = store.makeRepository(blocked_birth_repo_name);  // a repo of Birth Buckets of records blocked by parents names, DOM, Place of Marriage.
        FFNFLNMFNMMNPOMDOM_repo = store.makeRepository(FFNFLNMFNMMNPOMDOM_repo_name);  // a repo of Marriage Buckets
        FFNFLNMFNMMN_repo = store.makeRepository(FFNFLNMFNMMN_repo_name);  // a repo of Marriage Buckets

        linkage_repo = store.makeRepository(linkage_repo_name);
        initialiseTypes();

        relationships = linkage_repo.makeBucket(relationships_name, BucketKind.DIRECTORYBACKED, Relationship.class);
    }

    private void initialiseTypes() {

        TypeFactory tf = store.getTypeFactory();

        roleType = tf.createType(Role.class, "role");
        relationshipType = tf.createType(Relationship.class, "relationship");
    }

    protected abstract DataSetImporter getDataSetImporter(String store_path, String repo_name, String birth_records_path,String death_records_path, String marriage_records_path) throws Exception;

    private void importRecords(String birth_records_path, String death_records_path, String marriage_records_path) throws Exception {

        getDataSetImporter(store_path_string, input_repo_name, birth_records_path, death_records_path, marriage_records_path).importRecords();
    }

    private void checkBDMRecords() throws BucketException {

        checkIngestedBirths();
        checkIngestedDeaths();
        checkIngestedMarriages();
        checkRoles();
    }

    private void checkIngestedBirths() throws BucketException {

        IInputStream<Birth> stream = record_repository.births.getInputStream();

        System.out.println("Checking Births");

        for (LXP l : stream) {
            Birth birth_record = null;
            try {
                birth_record = (Birth) l;
                System.out.println("Birth for: " + birth_record.get(Birth.FORENAME) + " " + birth_record.get(Birth.SURNAME) + " m: " + birth_record.get(Birth.MOTHERS_FORENAME) + " " + birth_record.get(Birth.MOTHERS_SURNAME) + " f: " + birth_record.get(Birth.FATHERS_FORENAME) + " " + birth_record
                        .get(Birth.FATHERS_SURNAME) + " read OK");

            } catch (ClassCastException e) {
                System.out.println("LXP found (not birth): oid: " + l.getId() + "object: " + l);
                System.out.println("class of l: " + l.getClass().toString());
            }
        }
    }

    private void checkIngestedDeaths() throws BucketException {

        IInputStream<Death> stream = record_repository.deaths.getInputStream();

        System.out.println("Checking Deaths");

        for (Death death_record : stream) {
            System.out.println("Death for: " + death_record.get(Death.FORENAME) + " " + death_record.get(Death.SURNAME) + " m: " + death_record.get(Death.MOTHERS_FORENAME) + " " + death_record.get(Death.MOTHERS_SURNAME) + " f: " + death_record.get(Death.FATHERS_FORENAME) + " " + death_record
                    .get(Death.FATHERS_SURNAME) + " read OK");
        }
    }

    private void checkIngestedMarriages() throws BucketException {

        IInputStream<Marriage> stream = record_repository.marriages.getInputStream();

        System.out.println("Checking Marriages");

        for (Marriage marriage_record : stream) {
            System.out.println("Marriage for b: " + marriage_record.get(Marriage.BRIDE_FORENAME) + " " + marriage_record.get(Marriage.BRIDE_SURNAME) + " g: " + marriage_record.get(Marriage.GROOM_FORENAME) + " " + marriage_record.get(Marriage.GROOM_SURNAME));
            System.out.println("\tbm: " + marriage_record.get(Marriage.BRIDE_MOTHERS_FORENAME) + " " + marriage_record.get(Marriage.BRIDE_MOTHERS_MAIDEN_SURNAME) + " bf: " + marriage_record.get(Marriage.BRIDE_FATHERS_FORENAME) + " " + marriage_record.get(Marriage.BRIDE_FATHERS_SURNAME));
            System.out.println("\tgm: " + marriage_record.get(Marriage.GROOM_MOTHERS_FORENAME) + " " + marriage_record.get(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME) + " gf: " + marriage_record.get(Marriage.GROOM_FATHERS_FORENAME) + " " + marriage_record.get(Marriage.GROOM_FATHERS_SURNAME));
        }
    }

    private void createRoles(IBucket<Birth> births, IBucket<Death> deaths, IBucket<Marriage> marriages) throws BucketException {

        System.out.println("Creating roles from birth records");
        createRolesFromBirths(births);
        System.out.println("Creating roles from death records");
        createRolesFromDeaths(deaths);
        System.out.println("Creating roles from marriage records");
        createRolesFromMarriages(marriages);
    }

    private void checkRoles() throws BucketException {

        IInputStream<Role> stream = roles.getInputStream();

        for (LXP l : stream) {
            Role role = null;
            try {
                role = (Role) l;
                System.out.println("Role for person: " + role.getForename() + " " + role.getSurname() + " role: " + role.getRole());

            } catch (ClassCastException e) {
                System.out.println("LXP found (not role): oid: " + l.getId() + "object: " + l);
                System.out.println("class of l: " + l.getClass().toString());
            }
        }
    }

    /**
     * Blocks the Births and Marriages into buckets.
     */
    private void block() throws RepositoryException, BucketException, IOException {

        blockBirths();
        blockMarriages();
    }

    private void blockBirths() throws RepositoryException, BucketException, IOException {

        System.out.println("Blocking births...");

        IBlocker blocker = new FFNFLNMFNMMNPOMDOMOverBirth(record_repository.births, blocked_births_repo, Birth.class);
        blocker.apply();
    }

    private void blockMarriages() throws RepositoryException, BucketException, IOException {

        blockMarriagesFFNFLNMFNMMNPOMDOM();
        blockMarriagesFFNFLNMFNMMN();
    }

    private void blockMarriagesFFNFLNMFNMMNPOMDOM() throws RepositoryException, BucketException, IOException {

        System.out.println("Blocking marriages by FFNFLNMFNMMNPOMDOM...");

        IBlocker blocker = new FFNFLNMFNMMNPOMDOMOverMarriage(record_repository.marriages, FFNFLNMFNMMNPOMDOM_repo, Marriage.class);
        blocker.apply();
    }

    private void blockMarriagesFFNFLNMFNMMN() throws RepositoryException, BucketException, IOException {

        System.out.println("Blocking marriages by FFNFLNMFNMMN...");

        IBlocker blocker = new FFNFLNMFNMMNOverMarriage(record_repository.marriages, FFNFLNMFNMMN_repo, Marriage.class);
        blocker.apply();
    }

    /**
     * Display the blocks that are formed from the SFNLNFFNFLNMFNDoMOverBirth blocking process
     */
    private void printFamilies() {

        Iterator<IBucket<Birth>> iter = blocked_births_repo.getIterator(Birth.class);

        while (iter.hasNext()) {
            IBucket<Birth> bucket = iter.next();

            String bucket_name = bucket.getName();
            System.out.println("Birth bucket name: " + bucket_name);
            // Look for parents with same blocking key
            System.out.println("Parents: ");
            if (FFNFLNMFNMMNPOMDOM_repo.bucketExists(bucket_name)) {
                printParents(bucket_name);
                families_with_parents++;
            }
            System.out.println("Children: ");
            int children_count = 0;
            try {
                for (Birth birth : bucket.getInputStream()) {
                    System.out.println("\t" + birth.toString());
                    children_count++;
                }
            } catch (BucketException e) {
                System.out.println("Exception whilst getting stream");
            }
            if (children_count > 1) {
                families_with_children++;
                children_in_groups += children_count;
            }
            if (children_count == 1) {
                single_children++;
            }
        }
    }

    /**
     * Display the blocks that are formed from the SFNLNFFNFLNMFNDoMOverMarriage blocking process
     */
    private void printMarriages() {

        Iterator<IBucket<Marriage>> iter = FFNFLNMFNMMNPOMDOM_repo.getIterator(Marriage.class);

        while (iter.hasNext()) {
            IBucket<Marriage> bucket = iter.next();

            System.out.println("Marriage: " + bucket.getName());
            try {
                for (Marriage m : bucket.getInputStream()) {
                    System.out.println("\t" + m.toString());
                }
            } catch (BucketException e) {
                System.out.println("Exception whilst getting stream");
            }
        }
    }

    /**
     * Display the blocks that are formed from the SFNLNFFNFLNMFNDoMOverParents blocking process
     */
    private void printParents(String bucket_name) {

        try {
            IBucket<Marriage> bucket = FFNFLNMFNMMNPOMDOM_repo.getBucket(bucket_name);
            IInputStream<Marriage> stream = bucket.getInputStream();
            for (Marriage m : stream) {
                System.out.println("\t PPPPPPP " + m.toString());
            }
        } catch (BucketException | RepositoryException e) {
            System.out.println("Exception whilst getting parents");
        }

    }

    private void printStats() {

        System.out.println("Stats:");
        System.out.println("Births: " + birth_count);
        System.out.println("Deaths: " + death_count);
        System.out.println("Marriages: " + marriage_count);

        System.out.println("Parents in families: " + families_with_parents);
        System.out.println("Sibling groups (>1 child) " + families_with_children);
        System.out.println("Single children: " + single_children);

    }

    /**
     * Return a list of parents formed from the SFNLNFFNFLNMFNDoMOverParents blocking process for some blocking key
     */
    private List<Marriage> getParents(String bucket_name) {

        List<Marriage> parents = new ArrayList<Marriage>();
        try {
            IBucket<Marriage> bucket = FFNFLNMFNMMNPOMDOM_repo.getBucket(bucket_name);
            IInputStream<Marriage> stream = bucket.getInputStream();
            for (Marriage m : stream) {
                parents.add(m);
            }
        } catch (BucketException | RepositoryException e) {
            System.out.println("Exception whilst getting parents");
        }
        return parents;
    }

    /**
     * Try and form families from the blocked data from SFNLNFFNFLNMFNDoMOverRole
     */
    private void formFamilies() throws BucketException {

        Iterator<IBucket<Birth>> iter = blocked_births_repo.getIterator(Birth.class);

        while (iter.hasNext()) {

            IBucket<Birth> bucket = iter.next();
            String name = bucket.getName();
            List<Birth> siblings = new ArrayList<>();

            for (Birth birth : bucket.getInputStream()) {
                siblings.add(birth);
            }

            if (FFNFLNMFNMMNPOMDOM_repo.bucketExists(name)) {
                List<Marriage> parents = getParents(name);
            }
        }
    }

    /**
     * Try and create a family unit from the blocked data_array
     *
     * @param parents_marriage - a collection of marriage certificates of the potential parents of the family from Marriage blocking
     * @param children         - a collection of Births from SFNLNFFNFLNMFNDoMOverBirth blocking
     */
    private void create_family(List<Marriage> parents_marriage, List<Birth> children) {

    }

    /**
     * This method populates the roles bucket
     * For each record in the Births bucket there will be 3 roles created - e.g. mother, FATHER baby
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createRolesFromBirths(IBucket<Birth> bucket) throws BucketException {

        IOutputStream<Role> role_stream = roles.getOutputStream();
        IInputStream<Birth> stream = bucket.getInputStream();

        for (Birth birth_record : stream) {

            StoreReference<Birth> birth_record_ref = new StoreReference<>(input_repo, bucket, birth_record);

            Role child = Role.createPersonFromOwnBirth(birth_record_ref, birthType.getId());
            role_stream.add(child);

            Role father = Role.createFatherFromChildsBirth(birth_record_ref, birthType.getId());
            if (father != null) {
                role_stream.add(father);
            }

            Role mother = Role.createMotherFromChildsBirth(birth_record_ref, birthType.getId());
            if (mother != null) {
                role_stream.add(mother);
            }

            createRelationship(father, child, Relationship.relationship_kind.fatherof, "Shared certificate1");
            createRelationship(mother, child, Relationship.relationship_kind.motherof, "Shared certificate2");
        }
    }

    /**
     * This method populates the roles bucket
     * For each record in the Deaths bucket there will be 3 roles created - e.g. mother, FATHER baby
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createRolesFromDeaths(IBucket bucket) throws BucketException {

        IOutputStream<Role> role_stream = roles.getOutputStream();
        IInputStream<Death> stream = bucket.getInputStream();

        for (Death death_record : stream) {

            StoreReference<Death> death_record_ref = new StoreReference<>(input_repo, bucket, death_record);

            Role child = Role.createPersonFromOwnDeath(death_record_ref, deathType.getId());
            role_stream.add(child);

            Role father = Role.createFatherFromChildsDeath(death_record_ref, deathType.getId());
            if (father != null) {
                role_stream.add(father);
            }

            Role mother = Role.createMotherFromChildsDeath(death_record_ref, deathType.getId());
            if (mother != null) {
                role_stream.add(mother);
            }

            createRelationship(father, child, Relationship.relationship_kind.fatherof, "Shared certificate3");
            createRelationship(mother, child, Relationship.relationship_kind.motherof, "Shared certificate4");
        }
    }

    /**
     * This method populates the roles bucket
     * For each record in the Marriages bucket there will be 6 roles created - e.g. bride, groom plus the parents of each
     *
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createRolesFromMarriages(IBucket<Marriage> bucket) throws BucketException {

        IOutputStream<Role> roles_stream = roles.getOutputStream();

        IInputStream<Marriage> stream = bucket.getInputStream();

        int count = 0;

        for (Marriage marriage_record : stream) {

            count++;

            StoreReference<Marriage> marriage_record_ref = new StoreReference<>(store, input_repo.getName(), bucket.getName(), marriage_record.getId());

            Role bride = Role.createBrideFromMarriageRecord(marriage_record_ref, marriageType.getId());
            roles_stream.add(bride);

            Role groom = Role.createGroomFromMarriageRecord(marriage_record_ref, marriageType.getId());
            roles_stream.add(groom);

            Role gm = Role.createGroomsMotherFromMarriageRecord(marriage_record_ref, marriageType.getId());
            roles_stream.add(gm);

            Role gf = Role.createGroomsFatherFromMarriageRecord(marriage_record_ref, marriageType.getId());
            roles_stream.add(gf);

            Role bm = Role.createBridesMotherFromMarriageRecord(marriage_record_ref, marriageType.getId());
            roles_stream.add(bm);

            Role bf = Role.createBridesFatherFromMarriageRecord(marriage_record_ref, marriageType.getId());
            roles_stream.add(bf);

            createRelationship(bf, bride, Relationship.relationship_kind.fatherof, "Shared certificate5");
            createRelationship(bm, bride, Relationship.relationship_kind.motherof, "Shared certificate6");
            createRelationship(gf, groom, Relationship.relationship_kind.fatherof, "Shared certificate7");
            createRelationship(gm, groom, Relationship.relationship_kind.motherof, "Shared certificate8");
        }

        System.out.println("Processed : " + count + " marriage records");
    }

    /**
     * Create a relationship between the parties and add to the relationship table.
     *
     * @param subject      - the subject
     * @param object       - the object
     * @param relationship - relationship between subject and object
     * @param evidence     - of the relationship
     */
    private void createRelationship(Role subject, Role object, Relationship.relationship_kind relationship, String evidence) throws BucketException {

        if (subject != null && object != null) {

            StoreReference<Role> subject_ref = new StoreReference<>(store, role_repo.getName(), roles.getName(), subject.getId());
            StoreReference<Role> object_ref = new StoreReference<>(store, role_repo.getName(), roles.getName(), object.getId());

            Relationship r = new Relationship(subject_ref, object_ref, relationship, evidence);
            relationships.makePersistent(r);
        }
    }
}
