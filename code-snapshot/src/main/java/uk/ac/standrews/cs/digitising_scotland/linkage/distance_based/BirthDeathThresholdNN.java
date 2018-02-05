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

import uk.ac.standrews.cs.digitising_scotland.linkage.distance_based.distances.FNLNSFFNFLNMFNMMSDistanceOverDeath;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;
import uk.ac.standrews.cs.utilities.m_tree.MTree;

import java.io.IOException;

/**
 * Attempt to perform linking using MTree matching
 * Links Births to own Death records
 * Created by al on 24/4/1017
 */
public class BirthDeathThresholdNN extends BirthDeathLinkageUtils {

    public final float distance_threshold;

    private MTree<Death> deathMTree;

    public BirthDeathThresholdNN(String store_path, String repo_name, float distance_threshold) throws Exception {
        super(store_path,repo_name);
        this.distance_threshold = distance_threshold;
    }

    public void compute( boolean show_progress ) throws Exception {

        if( show_progress ) {
            timedRun("Creating Death MTree", () -> {
                createDeathMTreeOverFNSNSFFNFSNMFNMMS();
                matchBirthToDeathUsingFNSNSFFNFSNMFNMMS();
                return null;
            });
        } else {
            createDeathMTreeOverFNSNSFFNFSNMFNMMS();
            matchBirthToDeathUsingFNSNSFFNFSNMFNMMS();
        }

    }

    private void matchBirthToDeathUsingFNSNSFFNFSNMFNMMS() throws BucketException {

        IInputStream<Birth> stream = record_repository.births.getInputStream();

        for (Birth birth_record : stream) {

            Death inferred_record = new Death();
            inferred_record.put( Death.FORENAME, birth_record.getForename() );
            inferred_record.put(Death.SURNAME, birth_record.getSurname());
            inferred_record.put( Death.SEX, birth_record.getSex());
            inferred_record.put(Death.FATHERS_FORENAME, birth_record.getFathersForename());
            inferred_record.put(Death.FATHERS_SURNAME, birth_record.getFathersSurname());
            inferred_record.put(Death.MOTHERS_FORENAME, birth_record.getMothersForename());
            inferred_record.put(Death.MOTHERS_MAIDEN_SURNAME, birth_record.getMothersMaidenSurname());

            DataDistance<Death> dd = deathMTree.nearestNeighbour(inferred_record);
            if( dd.distance < distance_threshold) {

                birthdeathmap.put(birth_record,dd.value);
            }
        }
    }

    private void createDeathMTreeOverFNSNSFFNFSNMFNMMS() throws RepositoryException, BucketException, IOException {

//        System.out.println("Creating M Tree of deaths by GFNGLNBFNBMNPOMDOMDistanceOverDeath...");

        deathMTree = new MTree<>(new FNLNSFFNFLNMFNMMSDistanceOverDeath());

        IInputStream<Death> stream = record_repository.deaths.getInputStream();

        for (Death death : stream) {

            deathMTree.add(death);
        }
    }

}
