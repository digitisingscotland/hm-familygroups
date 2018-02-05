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
package uk.ac.standrews.cs.digitising_scotland.linkage.old;

/**
 * Attempt to perform linking using MTree matching
 * File is derived from BlockingLinker.
 * Created by al on 17/2/1017
 */
public abstract class KilmarnockBirthMarriageMTreeMatcher {

//    // Repositories and stores
//
//    private static String input_repo_name = "BDM_repo";                             // input repository containing event records
//    private static String blocked_birth_repo_name = "blocked_birth_repo";           // repository for blocked Birth records
//    private static String FFNFLNMFNMMNPOMDOM_repo_name = "FFNFLNMFNMMNPOMDOM_repo";   // repository for blocked Marriage records
//    private static String FFNFLNMFNMMN_repo_name = "FFNFLNMFNMMN_repo";   // repository for blocked Marriage records
//
//    private static String linkage_repo_name = "linkage_repo";                       // repository for Relationship records
//
//    private IStore store;
//    private IRepository input_repo;             // Repository containing buckets of BDM records
//    private IRepository role_repo;
//    private IRepository blocked_births_repo;
//    private IRepository FFNFLNMFNMMNPOMDOM_repo;
//    private IRepository FFNFLNMFNMMN_repo;
//    private IRepository linkage_repo;
//
//    // Bucket declarations
//
//    private IBucket<Birth> births;                     // Bucket containing birth records (inputs).
//    private IBucket<Marriage> marriages;               // Bucket containing marriage records (inputs).
//    private IBucket<Death> deaths;                     // Bucket containing death records (inputs).
//
//    private IBucket<Role> roles;                      // Bucket containing roles extracted from BDM records
//    private IBucket<Relationship> relationships;      // Bucket containing relationships between Roles
//
//    // Paths to sources
//
//    private static String births_name = "birth_records";                            // Name of bucket containing birth records (inputs).
//    private static String marriages_name = "marriage_records";                      // Name of bucket containing marriage records (inputs).
//    private static String deaths_name = "death_records";                            // Name of bucket containing marriage records (inputs).
//
//    // Names of buckets
//
//    private static String role_name = "roles";                                   // Name of bucket containing roles extracted from BDM records
//    private static String relationships_name = "relationships";                  // Name of bucket containing Relationship records
//
//    private IReferenceType birthType;
//    private IReferenceType deathType;
//    private IReferenceType marriageType;
//    private IReferenceType roleType;
//    private IReferenceType relationshipType;
//
//    private BirthFactory birthFactory;
//    private DeathFactory deathFactory;
//    private MarriageFactory marriageFactory;
//    private RoleFactory roleFactory;
//    private RelationshipFactory relationshipFactory;
//    private ArrayList<Long> oids = new ArrayList<>();
//    private int birth_count;
//    private int death_count;
//    private int marriage_count;
//    private int families_with_parents;
//    private int families_with_children;
//    private int single_children;
//    private int children_in_groups;
//
//    // Trees
//
//    private  MTree<Marriage> marriageMtree;
//
//    public KilmarnockBirthMarriageMTreeMatcher(String births_source_path, String deaths_source_path, String marriages_source_path) throws BucketException, RecordFormatException, IOException, RepositoryException, StoreException, JSONException {
//
//        System.out.println("Running KilmarnockBirthMarriageMTreeMatcher" );
//        System.out.println("Initialising");
//        initialise();
//
//        System.out.println("Ingesting");
//        ingestBDMRecords(births_source_path, deaths_source_path, marriages_source_path);
//    }
//
//    private void initialise() throws StoreException, IOException, RepositoryException, RecordFormatException, JSONException {
//
//        Path store_path = Files.createTempDirectory(null);
//
//        store = new Store(store_path);
//
//        System.out.println("Store path = " + store_path);
//
//        input_repo = store.makeRepository(input_repo_name);
//        blocked_births_repo = store.makeRepository(blocked_birth_repo_name);  // a repo of Birth Buckets of records blocked by parents names, DOM, Place of Marriage.
//        FFNFLNMFNMMNPOMDOM_repo = store.makeRepository(FFNFLNMFNMMNPOMDOM_repo_name);  // a repo of Marriage Buckets
//        FFNFLNMFNMMN_repo = store.makeRepository(FFNFLNMFNMMN_repo_name);  // a repo of Marriage Buckets
//
//        linkage_repo = store.makeRepository(linkage_repo_name);
//        initialiseTypes();
//        initialiseFactories();
//
//        births = input_repo.makeBucket(births_name, BucketKind.DIRECTORYBACKED, birthFactory);
//        deaths = input_repo.makeBucket(deaths_name, BucketKind.DIRECTORYBACKED, deathFactory);
//        marriages = input_repo.makeBucket(marriages_name, BucketKind.DIRECTORYBACKED, marriageFactory);
//        relationships = linkage_repo.makeBucket(relationships_name, BucketKind.DIRECTORYBACKED, relationshipFactory);
//    }
//
//    private void initialiseTypes() {
//
//        TypeFactory tf = store.getTypeFactory();
//
//        birthType = tf.createType(Birth.class, "birth");
//        deathType = tf.createType(Death.class, "death");
//        marriageType = tf.createType(Marriage.class, "marriage");
//        roleType = tf.createType(Role.class, "role");
//        relationshipType = tf.createType(Relationship.class, "relationship");
//    }
//
//    private void initialiseFactories() {
//
//        birthFactory = new BirthFactory(birthType.getId());
//        deathFactory = new DeathFactory(deathType.getId());
//        marriageFactory = new MarriageFactory(marriageType.getId());
//        roleFactory = new RoleFactory(roleType.getId());
//        relationshipFactory = new RelationshipFactory(relationshipType.getId());
//    }
//
//    /**
//     * Import the birth,death, marriage records
//     * Initialises the roles bucket with the roles injected - one record for each person referenced in the original record
//     * Initialises the known(100% certain) relationships between roles and stores the relationships in the relationships bucket
//     *
//     * @param births_source_path
//     * @param deaths_source_path
//     * @param marriages_source_path
//     */
//    private void ingestBDMRecords(String births_source_path, String deaths_source_path, String marriages_source_path) throws RecordFormatException, BucketException, IOException {
//
//        System.out.println("Importing BDM records");
//
//        birth_count = new KilmarnockBirthRecordImporter().importBirthRecords(births, births_source_path);
//        System.out.println("Imported " + birth_count + " birth records");
//        death_count = new KilmarnockDeathRecordImporter().importDeathRecords(deaths, deaths_source_path);
//        System.out.println("Imported " + death_count + " death records");
//        marriage_count = new KilmarnockMarriageRecordImporter().importMarriageRecords(marriages, marriages_source_path);
//        System.out.println("Imported " + marriage_count + " marriage records");
//    }
//
//    private void checkBDMRecords() throws BucketException {
//
//        System.out.println("Checking");
//        checkIngestedBirths();
//        checkIngestedDeaths();
//        checkIngestedMarriages();
//    }
//
//    private void checkIngestedBirths() throws BucketException {
//
//        IInputStream<Birth> stream = births.getInputStream();
//
//        for (LXP l : stream) {
//            Birth birth_record = null;
//            try {
//                birth_record = (Birth) l;
//                System.out.println("Birth for: " + birth_record.get(Birth.FORENAME) + " " + birth_record.get(Birth.SURNAME) + " m: " + birth_record.get(Birth.MOTHERS_FORENAME) + " " + birth_record.get(Birth.MOTHERS_SURNAME) + " f: " + birth_record.get(Birth.FATHERS_FORENAME) + " " + birth_record
//                                .get(Birth.FATHERS_SURNAME) + " read OK");
//
//            }
//            catch (ClassCastException e) {
//                System.out.println("LXP found (not birth): oid: " + l.getId() + "object: " + l);
//                System.out.println("class of l: " + l.getClass().toString());
//            }
//        }
//    }
//
//    private void checkIngestedDeaths() throws BucketException {
//
//        IInputStream<Death> stream = deaths.getInputStream();
//
//        System.out.println("Checking Deaths");
//
//        for (Death death_record : stream) {
//            System.out.println("Death for: " + death_record.get(Death.FORENAME) + " " + death_record.get(Death.SURNAME) + " m: " + death_record.get(Death.MOTHERS_FORENAME) + " " + death_record.get(Death.MOTHERS_SURNAME) + " f: " + death_record.get(Death.FATHERS_FORENAME) + " " + death_record
//                            .get(Death.FATHERS_SURNAME) + " read OK");
//        }
//    }
//
//    private void checkIngestedMarriages() throws BucketException {
//
//        IInputStream<Marriage> stream = marriages.getInputStream();
//
//        System.out.println("Checking Marriages");
//
//        for (Marriage marriage_record : stream) {
//            System.out.println("Marriage for b: " + marriage_record.get(Marriage.BRIDE_FORENAME) + " " + marriage_record.get(Marriage.BRIDE_SURNAME) + " g: " + marriage_record.get(Marriage.GROOM_FORENAME) + " " + marriage_record.get(Marriage.GROOM_SURNAME));
//            System.out.println("\tbm: " + marriage_record.get(Marriage.BRIDE_MOTHERS_FORENAME) + " " + marriage_record.get(Marriage.BRIDE_MOTHERS_MAIDEN_SURNAME) + " bf: " + marriage_record.get(Marriage.BRIDE_FATHERS_FORENAME) + " " + marriage_record.get(Marriage.BRIDE_FATHERS_SURNAME));
//            System.out.println("\tgm: " + marriage_record.get(Marriage.GROOM_MOTHERS_FORENAME) + " " + marriage_record.get(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME) + " gf: " + marriage_record.get(Marriage.GROOM_FATHERS_FORENAME) + " " + marriage_record.get(Marriage.GROOM_FATHERS_SURNAME));
//        }
//    }
}
