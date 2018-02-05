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

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.GFNGLNBFNBLNDistanceOverMarriage;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;
import uk.ac.standrews.cs.utilities.m_tree.MTree;

import java.io.IOException;
import java.util.*;

/**
 * Attempt to perform linking using MTree matching
 * Links from bride-groom marriage certificate to the marriage certificate of the parents' of bride and groom (both),
 *
 * Created by al on 24/4/1017
 */
public class MarriageMarriageThresholdNN extends FamilyLinkageUtils {

    public static final float DISTANCE_THRESHOLD = 8.0F;

    private MTree<Marriage> marriageMtree;
    private Map<Long, List<Marriage>> marriage_to_marriage_map = new HashMap<Long, List<Marriage>>();

    public Map<Long,List<Marriage>> getMarriageToMarriageMap() {
        return marriage_to_marriage_map;
    }

    public MarriageMarriageThresholdNN(String store_path, String repo_name) throws Exception {
        super(store_path,repo_name);
    }

    public void compute( boolean show_progress ) throws Exception {

        if (show_progress) {
            timedRun("Creating Marriage MTrees", () -> {
                createMarriageMTrees();
                return null;
            });

            timedRun("Forming families from Marriage-Marriage links", () -> {
                formMarriageMarriageRelationships();
                return null;
            });
        } else {
            createMarriageMTrees();
            formMarriageMarriageRelationships();
        }
    }

    public void showFamilies() throws BucketException {

        System.out.println("Number of families formed:" + new HashSet<>(id_to_family_map.values()).size());
        printFamilies();
    }

    private void createMarriageMTrees() throws RepositoryException, BucketException, IOException {

        marriageMtree = new MTree<>(new GFNGLNBFNBLNDistanceOverMarriage());

        for (Marriage marriage : record_repository.marriages.getInputStream()) {
            marriageMtree.add(marriage);
        }
    }

    /**
     * Try and find parents' marriages from Marriage M Tree
     */
    private void formMarriageMarriageRelationships() {

        IInputStream<Marriage> stream;
        try {
            stream = record_repository.marriages.getInputStream();
        } catch (BucketException e) {
            System.out.println("Exception whilst getting marriages");
            return;
        }

        for (Marriage m : stream) {

            formGroomParentsMarriageRelationship( m );
            formBrideParentsMarriageRelationship( m );

        }
    }


    private void formGroomParentsMarriageRelationship(Marriage m) {

        // Link from groom to grooms parents' marriage

        Marriage marriage_query = new Marriage();
        DataDistance<Marriage> result;

        marriage_query.put(Marriage.GROOM_FORENAME, m.getGroomsFathersForename());
        marriage_query.put(Marriage.GROOM_SURNAME, m.getGroomsFathersSurname());
        marriage_query.put(Marriage.BRIDE_FORENAME, m.getGroomsMothersForename());
        marriage_query.put(Marriage.BRIDE_SURNAME, m.getGroomsMothersMaidenSurname());

        result = marriageMtree.nearestNeighbour(marriage_query);

        if (result.distance < DISTANCE_THRESHOLD) {
            addMarriageToMap(marriage_to_marriage_map, m.getId(), result.value); // used the marriage id as a unique identifier.
        }
    }

    private void formBrideParentsMarriageRelationship(Marriage m) {

        // Link from bride to brides parents' marriage

        Marriage marriage_query = new Marriage();
        DataDistance<Marriage> result;

        marriage_query.put(Marriage.GROOM_FORENAME, m.getBridesFathersForename());
        marriage_query.put(Marriage.GROOM_SURNAME, m.getBridesFathersSurname());
        marriage_query.put(Marriage.BRIDE_FORENAME, m.getBridesMothersForename());
        marriage_query.put(Marriage.BRIDE_SURNAME, m.getBridesMothersMaidenSurname());

        result = marriageMtree.nearestNeighbour(marriage_query);

        if (result.distance < DISTANCE_THRESHOLD) {
            addMarriageToMap(marriage_to_marriage_map, m.getId(), result.value); // used the marriage id as a unique identifier.
        }
    }


    /**
     * Adds a marriage record to map from marriage ids of children to list of marriages of parents.
     * @param map          the map to which the record should be added
     * @param marriage_record the record to add to the map
     */
    private void addMarriageToMap(Map<Long, List<Marriage>> map, Long key, Marriage marriage_record) {

        if (map.containsKey(key)) { // have already seen a member of this family - so just add the birth to the family map
            // could check here to ensure parents are the same etc.
            List<Marriage> marriages= map.get(key);
            marriages.add(marriage_record);
        } else { // a new family we have not seen before
            List<Marriage> marriages = new ArrayList<Marriage>();
            marriages.add(marriage_record);
            map.put( key,marriages );
        }
    }

}
