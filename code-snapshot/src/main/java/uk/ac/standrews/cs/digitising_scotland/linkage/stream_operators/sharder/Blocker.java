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
package uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder;

import uk.ac.standrews.cs.storr.impl.BucketKind;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.NoSuitableBucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBlocker;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.utilities.archive.Diagnostic;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

/**
 * Blocker takes a stream and blocks it into buckets based on the assigner
 * Created by al on 29/04/2014.
 */
public abstract class Blocker<T extends LXP> implements IBlocker<T> {

    private final IInputStream<T> input;
    protected final IRepository output_repo;
    private final Class<T> clazz;

    /**
     * @param input the stream over which to block
     * @param output_repo - the repository into which results are written
     */
    public Blocker(final IInputStream<T> input, final IRepository output_repo, Class<T> clazz) {

        this.input = input;
        this.output_repo = output_repo;
        this.clazz = clazz;
    }

    public IInputStream getInput() {

        return input;
    }

    /**
     * Apply the method assign to all (non-null) records in the stream
     */
    @Override
    public void apply() {

        for (T record : input) {
            if (record != null) {
                assign(record);
            }
        }
    }

    @Override
    public void assign(final T record) {

        try {
            for (String bucket_name : determineBlockedBucketNamesForRecord(record)) {

                if (bucket_name == null || bucket_name.equals("")) {
                    Diagnostic.trace("Illegal (empty or null) bucket name encountered whilst creating bucket for: " + record, Diagnostic.FULL);
                    return;
                }
                if (output_repo.bucketExists(bucket_name)) {
                    try {
                        output_repo.getBucket(bucket_name, clazz).getOutputStream().add(record);
                    }
                    catch (RepositoryException | BucketException e) {
                        ErrorHandling.exceptionError(e, "Exception obtaining bucket instance for record: " + record);
                    }
                }
                else { // need to create it
                    try {
                        output_repo.makeBucket(bucket_name, BucketKind.INDIRECT, clazz).getOutputStream().add(record);
                    }
                    catch (RepositoryException | BucketException e) {
                        ErrorHandling.exceptionError(e, "Exception creating bucket for record: " + record);
                    }
                }
            }
        }
        catch (NoSuitableBucketException e) {
            ErrorHandling.error("No suitable bucket for record: " + record);
        }
    }

    public abstract String[] determineBlockedBucketNamesForRecord(T record) throws NoSuitableBucketException;
}
