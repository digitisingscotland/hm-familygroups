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

import uk.ac.standrews.cs.storr.impl.Metadata;
import uk.ac.standrews.cs.storr.impl.StaticLXP;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;
import uk.ac.standrews.cs.utilities.dataset.DataSet;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * Utility classes for importing records in csv format
 * Created by al on 8/11/2016.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class GenericCSVRecordImporter {

    private static final String[] ARG_NAMES = { "store_path", "repo_name", "bucket_name", "source_file","classname" };

    private final String file_name;
    Class clazz;
    public IStore store;
    private IBucket destination_bucket;

    /**
     *
     * @param store_name the path to the store root
     * @param repo_name the name of the repo into which the records will be injested
     * @param bucket_name the name of the bucket into which the records will be injested
     * @param file_name containing the source records in csv format
     */
    public GenericCSVRecordImporter(String store_name, String repo_name, String bucket_name, String file_name, String class_name) throws Exception {

        this.file_name = file_name;
        Path store_path = Paths.get(store_name);
        Store store = new Store(store_path);
        IRepository repo = store.getRepository(repo_name);
        destination_bucket = repo.getBucket(bucket_name);

        try {
            clazz = Class.forName(class_name);
        } catch (ClassNotFoundException e) {
            throw new Exception( e );
        }
    }


    /**
     * @return the number of records read in
     * @throws Exception if something bad happened.
     */
    public int importRecords() throws Exception {

        DataSet data = new DataSet(Paths.get(file_name));
        List<String> labels = data.getColumnLabels();

        check_compliance( clazz,labels );

        int count = 0;
        int err_count = 0;


        for (List<String> record : data.getRecords()) {

            if( record.size() != labels.size() ) {
                ErrorHandling.error( "Size of record does not match expected labels found\n\trecord: " + record + "\n\texpected: " + labels );
                err_count++;
            } else {

                StaticLXP lxp = importRecord(record);
                destination_bucket.makePersistent(lxp);

                count++;
            }
        }
        if( err_count != 0 ) {
            ErrorHandling.error( "Encountered " + err_count + " badly formed records" );
        }

        return count;
    }

    public void check_compliance( Class<StaticLXP> clazz, List<String> dataset_labels ) throws Exception {
        StaticLXP lxp_instance = (StaticLXP) clazz.newInstance();  // can throw an exception if wrong type - thrown by this method.
        System.out.println( "instance = " + lxp_instance.getClass() );
        Metadata md = lxp_instance.getMetaData();
        if( md == null ) {
            throw new Exception( "Cannot get MetaData for class: " + clazz );
        }
        if( md.getFieldCount() != dataset_labels.size() ) {
            throw new Exception( "Size of supplied record and supplied class do not match, # fields in supplied class: " + md.getFieldCount() + " # fields in dataset: " + dataset_labels.size() );
        }
        IReferenceType type = md.getType();
        System.out.println( "type = " + type );
        // now check all fields are of type string.
        for( String field : md.getFields() ) {
            IType fieldtype = type.getFieldType(field);
            if( ! fieldtype.valueConsistentWithType( "any string") ) {
                throw new Exception( "Field " + field + " in supplied class is not a STRING as required - generic importer only imports STRING fields");
            }
        }
    }

    protected StaticLXP importRecord(List<String> record) throws Exception {

        StaticLXP lxp_instance = (StaticLXP) clazz.newInstance() ;  // can throw an exception if wrong type - but shown not since caught by check_compliance.

        int slot = 0;
        for( String field : record ) {
            lxp_instance.put( slot++,field );
        }
        return lxp_instance;
    }

    public static void main( String[] args ) throws Exception {
        if (args.length >= ARG_NAMES.length) {

            String store_path = args[0];
            String repo_name = args[1];
            String bucket_name = args[2];
            String file_name = args[3];
            String class_name = args[4];

            GenericCSVRecordImporter importer = new GenericCSVRecordImporter(store_path, repo_name, bucket_name, file_name, class_name );
            int count = importer.importRecords();

            System.out.println( "Imported " + count + " records");

        } else {
            System.err.println("Usage: run with " + String.join(" ", ARG_NAMES));
        }
    }

}
