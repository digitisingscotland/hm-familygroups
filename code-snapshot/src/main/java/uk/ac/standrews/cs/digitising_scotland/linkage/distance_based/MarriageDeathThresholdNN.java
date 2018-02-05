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
package uk.ac.standrews.cs.digitising_scotland.linkage.distance_based;

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.BFNBLNBDOBBFFNBFLNBMFNBMMNDistanceOverMarriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.GFNGLNGDOBGFFNGFLNGMFNGMMNDistanceOverMarriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Deaths;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.transaction.exceptions.TransactionFailedException;
import uk.ac.standrews.cs.storr.impl.transaction.interfaces.ITransaction;
import uk.ac.standrews.cs.storr.impl.transaction.interfaces.ITransactionManager;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;
import uk.ac.standrews.cs.utilities.m_tree.MTree;

import java.io.IOException;
import java.util.*;

/**
 * Attempt to perform linking using MTree matching
 * Links from birth certificate to their own marriage certificate
 *
 * Created by al on 24/4/1017
 */
public class MarriageDeathThresholdNN extends FamilyLinkageUtils {

    public static final float DISTANCE_THRESHOLD = 8.0F;

    private MTree<Marriage> maleMarriageMtree;
    private MTree<Marriage> femaleMarriageMtree;
    private Map<Long, Deaths> marriage_to_death_map;
    private IBucket<Deaths> deaths_bucket;

    public MarriageDeathThresholdNN(String store_path, String repo_name, IBucket<Deaths> deaths_bucket) throws Exception {
        super(store_path,repo_name);
        marriage_to_death_map = new HashMap<>();
        this.deaths_bucket = deaths_bucket;
    }

    public Map<Long, Deaths> getMarriageToDeathMap() { return marriage_to_death_map; }

    public void compute( boolean show_progress ) throws Exception {

        if( show_progress ) {
            timedRun("Creating Marriage MTrees", () -> {
                createMarriageMTrees();
                return null;
            });

            timedRun("Forming families from Marriage-Birth links", () -> {
                formBirthMarriageRelationships();
                return null;
            });
        } else {
            createMarriageMTrees();
            formBirthMarriageRelationships();
        }
    }

    public void showFamilies() throws BucketException {

        System.out.println("Number of families formed:" + new HashSet<>(id_to_family_map.values()).size());
        printFamilies();
    }

    private void createMarriageMTrees() throws RepositoryException, BucketException, IOException {

        maleMarriageMtree = new MTree<>(new GFNGLNGDOBGFFNGFLNGMFNGMMNDistanceOverMarriage());
        femaleMarriageMtree = new MTree<>(new BFNBLNBDOBBFFNBFLNBMFNBMMNDistanceOverMarriage());

        for (Marriage marriage : record_repository.marriages.getInputStream()) {
            maleMarriageMtree.add(marriage);
            femaleMarriageMtree.add(marriage);
        }
    }

    /**
     * Try and form families from Marriage M Tree data_array
     */
    private void formBirthMarriageRelationships() {

        IInputStream<Death> stream;
        try {
            stream = record_repository.deaths.getInputStream();
        } catch (BucketException e) {
            System.out.println("Exception whilst getting births");
            return;
        }

        for (Death d : stream) {

            Marriage marriage_query = new Marriage();
            DataDistance<Marriage> result;

            if( d.getString(Death.SEX).equals("m")) {

                marriage_query.put(Marriage.GROOM_FORENAME, d.getString(Death.FORENAME));
                marriage_query.put(Marriage.GROOM_SURNAME, d.getString(Death.SURNAME));
                marriage_query.put(Marriage.GROOM_AGE_OR_DATE_OF_BIRTH, d.getDOB()); // TODO check this
                marriage_query.put(Marriage.GROOM_FATHERS_FORENAME, d.getFathersForename());
                marriage_query.put(Marriage.GROOM_FATHERS_SURNAME, d.getFathersSurname());
                marriage_query.put(Marriage.GROOM_MOTHERS_FORENAME, d.getMothersForename());
                marriage_query.put(Marriage.GROOM_MOTHERS_MAIDEN_SURNAME, d.getMothersMaidenSurname());

                result = maleMarriageMtree.nearestNeighbour(marriage_query);

            } else {
                marriage_query.put(Marriage.BRIDE_FORENAME, d.getString(Death.FORENAME));
                marriage_query.put(Marriage.BRIDE_SURNAME, d.getString(Death.SURNAME));
                marriage_query.put(Marriage.BRIDE_AGE_OR_DATE_OF_BIRTH, d.getDOB()); // TODO check this
                marriage_query.put(Marriage.BRIDE_FATHERS_FORENAME, d.getFathersForename());
                marriage_query.put(Marriage.BRIDE_FATHERS_SURNAME, d.getFathersSurname());
                marriage_query.put(Marriage.BRIDE_MOTHERS_FORENAME, d.getMothersForename());
                marriage_query.put(Marriage.BRIDE_MOTHERS_MAIDEN_SURNAME, d.getMothersMaidenSurname());

                result = femaleMarriageMtree.nearestNeighbour(marriage_query);
            }

            if (result.distance < DISTANCE_THRESHOLD) {
                addDeathToMap(marriage_to_death_map, result.value.getId(), d); // used the marriage id as a unique identifier.
            }
        }
    }

    /**
     * Adds a death record
     *
     * @param map          the map to which the record should be added
     * @param death_record the record to add to the map
     */
    private void addDeathToMap(Map<Long, Deaths> map, Long key, Death death_record) {

        List<Death> list;

        if( map.containsKey(key)) {
            Deaths record = map.get(key);
            list = record.getDeaths();
            list.add(death_record);
            map.put( key, record );

            // Update Deaths record in deaths under transactional control

            ITransactionManager tm = Store.getInstance().getTransactionManager();
            ITransaction trans = null;
            try {
                trans = tm.beginTransaction();
                try {
                    deaths_bucket.update( record );
                } catch (BucketException e) {
                    ErrorHandling.exceptionError( e, "Error updating Deaths" );
                }
                trans.commit();
            } catch (TransactionFailedException e) {
               ErrorHandling.exceptionError( e, "Transaction exception updating Deaths record");
            }
        } else {
            list = new ArrayList<>();
            list.add(death_record);
            Deaths record = new Deaths(list);
            try {
                deaths_bucket.makePersistent(record);
            } catch (BucketException e) {
                ErrorHandling.exceptionError( e, "Error committing deaths" );
            }
            map.put(key, record);
        }
    }
}
