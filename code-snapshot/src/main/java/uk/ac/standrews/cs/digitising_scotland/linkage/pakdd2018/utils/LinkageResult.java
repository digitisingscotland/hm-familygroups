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
package uk.ac.standrews.cs.digitising_scotland.linkage.pakdd2018.utils;

import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;
import uk.ac.standrews.cs.utilities.m_tree.DataDistance;

import java.util.*;

public class LinkageResult implements ILinkageResult {

    private final long starttime;

    /**
     * These fields uniquely identifies a record.
     * It is what we use to represent the ground truth.
     * If two records are true matches, they should have the same value in this field.
     */
    private int source_matchfield;
    private int sink_matchfield;

    /**
     * Represents an algorithm description.
     * <p>
     * First component of each entry is the name of the algorithm "setting".
     * second component is the value.
     * <p>
     * So like "Linker: LSH, Distance: Normalised Levenshtein, Threshold: 0.8"
     */
    private LinkedHashMap<String, String> algorithmDescription;

    /**
     * All links as made by a linkage algorithm.
     */
    private HashSet<Link> links;

    /**
     * All matches as reported by the dataset
     */
    private HashSet<Match> matches;

    /**
     * All matches as reported by the dataset, indexed by record.getID()
     */
    private HashMap<Long, HashSet<IStoreReference<LXP>>> matchesPerRecord;

    /**
     * All candidate_results
     */
    private final HashSet<Candidates> candidate_results;

    /**
     * Maximum memory used by the client app.
     */
    private long max_memory;
    private int overallComparisonCount;
    private int setupComparisonCount;

    private final IBucket<LXP> source_bucket;
    private final IBucket<LXP> sink_bucket;

    public LinkageResult(long starttime, int source_matchfield, int sink_matchfield,
                         LinkedHashMap<String, String> algorithmDescription, IBucket<LXP> source_bucket, IBucket<LXP> sink_bucket) {
        this.starttime = starttime;
        this.source_matchfield = source_matchfield;
        this.sink_matchfield = sink_matchfield;
        this.algorithmDescription = algorithmDescription;
        this.links = new HashSet<>();
        this.matches = new HashSet<>();
        this.matchesPerRecord = new HashMap<>();
        this.source_bucket = source_bucket;
        this.sink_bucket = sink_bucket;
        this.candidate_results = new HashSet<>();
    }


    /**
     * Adds a link to the linkage results
     * The expectation is that:
     *
     * @param a        - is drawn from the sourcebucket
     * @param b        - is drawn from the sinkbucket, important since matchfield is applied to b (which may be of a different type from a.
     * @param distance - the distance between a and b.
     */
    public void addLink(IStoreReference<LXP> a, IStoreReference<LXP> b, float distance) throws BucketException {
        // ignoring the distance for now!
        links.add(new Link(a, b));
    }

    public void addMemoryUsage(long max_memory) {
        this.max_memory = max_memory;
    }


    public void addOverallComparisonCount(int comparisonCount) {
        this.overallComparisonCount = comparisonCount;
    }

    public void addSetupComparisonCount(int comparisonCount) {
        this.setupComparisonCount = comparisonCount;
    }

    public void addMatch(IStoreReference<LXP> a, IStoreReference<LXP> b) throws BucketException {
        matches.add(new Match(a, b));

        {
            long aKey = a.getReferend().getId();
            HashSet<IStoreReference<LXP>> aSet = matchesPerRecord.get(aKey);
            if (aSet == null) {
                aSet = new HashSet<>();
            }
            aSet.add(b);
            matchesPerRecord.put(aKey, aSet);
        }

        {
            long bKey = a.getReferend().getId();
            HashSet<IStoreReference<LXP>> bSet = matchesPerRecord.get(bKey);
            if (bSet == null) {
                bSet = new HashSet<>();
            }
            bSet.add(a);
            matchesPerRecord.put(bKey, bSet);
        }

    }

    public void populateMatches(IInputStream<LXP> source_stream, IInputStream<LXP> sink_stream) {
        // Populate the matches

        // a map from the key of the source record to all records that belong to that key
        HashMap<String, HashSet<LXP>> source_indexed = new HashMap<>();
        for (LXP source_record : source_stream) {
            String key = source_record.getString(source_matchfield);
            HashSet<LXP> values = source_indexed.get(key);
            if (values == null) {
                values = new HashSet<>();
            }
            values.add(source_record);
            source_indexed.put(key, values);
        }

        // a map from the key of the sink record to all records that belong to that key
        HashMap<String, HashSet<LXP>> sink_indexed = new HashMap<>();
        for (LXP sink_record : sink_stream) {
            String key = sink_record.getString(sink_matchfield);
            HashSet<LXP> values = sink_indexed.get(key);
            if (values == null) {
                values = new HashSet<>();
            }
            values.add(sink_record);
            sink_indexed.put(key, values);
        }

        // for each entry in the source map (all records with label X)
        // we look up corresponding entries from the sink map (again, all records with the same label X)
        // we create a match (ground-truth) which are pairs of (source, sink) records

        for (Map.Entry<String, HashSet<LXP>> chunk : source_indexed.entrySet()) {
            HashSet<LXP> recordsFromSource = chunk.getValue();
            HashSet<LXP> recordsFromSink = sink_indexed.get(chunk.getKey());
            if (recordsFromSink == null) {
                recordsFromSink = new HashSet<>();
            }
            for (LXP source : recordsFromSource) {
                for (LXP sink : recordsFromSink) {
                    try {
                        addMatch(source.getThisRef(), sink.getThisRef());
                    } catch (PersistentObjectException e) {
                        ErrorHandling.exceptionError(e, "Adding a match for: " + source + " and " + sink);
                    } catch (BucketException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * The linkage algorithm claimed they were a match, and they were!
     * Happiness!
     *
     * @return The number of true-positives.
     */
    public int getTruePositives() {
        int tp = 0;
        for (Link link : links) {
            if (link.isMatch()) {
                tp++;
            }
        }
        return tp;
    }

    /**
     * The linkage algorithm claimed they were a match, but they actually weren't.
     *
     * @return The number of false-positives.
     */
    public int getFalsePositives() {
        int fp = 0;
        for (Link link : links) {
            if (!link.isMatch()) {
                fp++;
            }
        }
        return fp;
    }

    /**
     * The linkage algorithm said they weren't a match, but they were.
     * To be able to calculate these, we need access to all true-matches.
     *
     * @return The number of false-negatives.
     */
    public int getFalseNegatives() {
        int fn = 0;
        for (Match match : matches) {
            if (!links.contains(match.getAsLink())) {
                fn++;
            }
        }
        return fn;
    }

    public void printLinkageStatsHeader() {
        for (Map.Entry<String, String> descr : algorithmDescription.entrySet()) {
            System.out.print(descr.getKey() + "\t");
        }
        System.out.print("Time taken" + "\t");
        System.out.print("Memory used" + "\t");
        System.out.print("Setup comparisons" + "\t");
        System.out.print("Total comparisons" + "\t");
        System.out.print("Number of links" + "\t");
        System.out.print("Number of matches" + "\t");
        System.out.print("True Positives" + "\t");
        System.out.print("False Positives" + "\t");
        System.out.print("False Negatives" + "\t");
        System.out.print("Min links quality" + "\t");
        System.out.print("Max links quality" + "\t");
        System.out.print("Average links quality" + "\t");
        System.out.print("Min pairs quality" + "\t");
        System.out.print("Max pairs quality" + "\t");
        System.out.print("Average pairs quality" + "\t");
        System.out.print("Min pairs completeness" + "\t");
        System.out.print("Max pairs completeness" + "\t");
        System.out.print("Average pairs completeness" + "\t");
        System.out.print("Precision" + "\t");
        System.out.print("Recall" + "\t");
        System.out.print("F1 Measure" + "\n");

    }

    public void printLinkageStats() {
        for (Map.Entry<String, String> descr : algorithmDescription.entrySet()) {
            System.out.print(descr.getValue() + "\t");
        }

        int truePositives = getTruePositives();
        int falsePositives = getFalsePositives();
        int falseNegatives = getFalseNegatives();

        float precision = 0;
        if (truePositives + falsePositives > 0) {
            precision = ((float) truePositives) / (truePositives + falsePositives);
        }

        float recall = 0;
        if (truePositives + falseNegatives > 0) {
            recall = ((float) truePositives) / (truePositives + falseNegatives);
        }

        float f1measure = 0;
        if (precision + recall > 0) {
            f1measure = 2 * precision * recall / (precision + recall);
        }

        List<Float> linksQualities = new ArrayList<>();
        List<Float> pairsQualities = new ArrayList<>();
        List<Float> pairsCompleteness = new ArrayList<>();
        for (Candidates c : candidate_results) {
            linksQualities.add(c.getLinksQuality());
            pairsQualities.add(c.getPairsQuality());
            pairsCompleteness.add(c.getPairsCompleteness());
        }

        System.out.printf("%16d \t", System.currentTimeMillis() - starttime);
        System.out.printf("%16d \t", max_memory);
        System.out.printf("%16d \t", setupComparisonCount);
        System.out.printf("%16d \t", overallComparisonCount);
        System.out.printf("%16d \t", links.size());
        System.out.printf("%16d \t", matches.size());
        System.out.printf("%16d \t", truePositives);
        System.out.printf("%16d \t", falsePositives);
        System.out.printf("%16d \t", falseNegatives);
        if (linksQualities.size() != 0) {
            System.out.printf("%16.2f \t", Collections.min(linksQualities));
            System.out.printf("%16.2f \t", Collections.max(linksQualities));
            System.out.printf("%16.2f \t", average(linksQualities));
        } else {
            System.out.print("NA\tNA\tNA\t");
        }
        if (pairsQualities.size() != 0) {
            System.out.printf("%16.2f \t", Collections.min(pairsQualities));
            System.out.printf("%16.2f \t", Collections.max(pairsQualities));
            System.out.printf("%16.2f \t", average(pairsQualities));
        } else {
            System.out.print("NA\tNA\tNA\t");
        }
        if (pairsCompleteness.size() != 0) {
            System.out.printf("%16.2f \t", Collections.min(pairsCompleteness));
            System.out.printf("%16.2f \t", Collections.max(pairsCompleteness));
            System.out.printf("%16.2f \t", average(pairsCompleteness));
        } else {
            System.out.print("NA\tNA\tNA\t");

        }
        System.out.printf("%16.2f \t", precision);
        System.out.printf("%16.2f \t", recall);
        System.out.printf("%16.2f \n", f1measure);

    }

    private float average(List<Float> list) {
        float sum = 0;
        for (float x : list) {
            sum += x;
        }
        if (list.size() == 0) {
            return 0.0f;
        } else {
            return sum / list.size();
        }
    }


    @Override
    public void addCandidates(int number_of_candidates, int no_links, int match_count, int non_match_count) throws Exception {

        Candidates c = new Candidates(number_of_candidates, no_links, match_count, non_match_count);
        candidate_results.add(c);
    }

    public boolean isMatch(IStoreReference<LXP> source, IStoreReference<LXP> sink) {
        try {
            return source.getReferend().get(source_matchfield).equals(sink.getReferend().get(sink_matchfield));
        } catch (BucketException e) {
            ErrorHandling.error("Exception in isMatch() - fixit!!!!!");
            return false; // naughty but will do for now
        }
    }

    public int countMissingMatches(LXP sink_record, List<DataDistance<IStoreReference<LXP>>> closest_set) throws PersistentObjectException, BucketException {
        int result = 0;
        Set<IStoreReference<LXP>> candidates = new HashSet<>();
        for (DataDistance<IStoreReference<LXP>> dd : closest_set) {
            candidates.add(dd.value);
        }
        HashSet<IStoreReference<LXP>> allMatches = matchesPerRecord.get(sink_record.getId());
        if (allMatches != null) {
            for (IStoreReference<LXP> m : allMatches) {
                if (!candidates.contains(m)) {
                    result++;
                }
            }
        }
        return result;
    }

    class Link {
        private IStoreReference<LXP> a;
        private IStoreReference<LXP> b;

        /**
         * We assume that this is always called from an iterator, therefore a and b won't be null.
         *
         * @param a Not-null
         * @param b Not-null
         */
        public Link(IStoreReference<LXP> a, IStoreReference<LXP> b) {

            if (source_bucket.getName().equals(sink_bucket.getName())) {        // assumes that bucket come from the same repo
                // we know that the source bucket is the same as the sink bucket
                try {
                    if (a.getReferend().getId() > b.getReferend().getId()) {
                        // The object created here will be placed in a HashSet.
                        // We normalise the order here, so it is only inserted once - enforced by HashSet.
                        // We can only do this when the source bucket and the sink bucket are the same.
                        this.a = b;
                        this.b = a;
                    } else {
                        this.a = a;
                        this.b = b;
                    }
                } catch (BucketException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            } else {
                this.a = a;
                this.b = b;
            }
        }

        /**
         * Returns if this link is a true-match
         *
         * @return true if the link is a true match, false if not.
         */
        public boolean isMatch() {
            try {
                return a.getReferend().get(source_matchfield).equals(b.getReferend().get(sink_matchfield));
            } catch (BucketException e) {
                ErrorHandling.error("Exception in isMatch() - fixit!!!!!");
                return false; // naughty but will do for now
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Link) {
                Link other = (Link) o;
                try {
                    return this.a.getReferend().getId() == other.a.getReferend().getId()
                            && this.b.getReferend().getId() == other.b.getReferend().getId();
                } catch (BucketException e) {
                    e.printStackTrace();
                    System.exit(1);
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            try {
                return Objects.hash(a.getReferend().getId(), b.getReferend().getId());
            } catch (BucketException e) {
                e.printStackTrace();
                System.exit(1);
                return 0;
            }
        }
    }

    class Match {
        private IStoreReference<LXP> a;
        private IStoreReference<LXP> b;

        public Match(IStoreReference<LXP> a, IStoreReference<LXP> b) {
            try {
                if (a.getReferend().getId() > b.getReferend().getId()) {
                    this.a = b;
                    this.b = a;
                } else {
                    this.a = a;
                    this.b = b;
                }
            } catch (Exception e) {
                ErrorHandling.error("Exception in Match constructor - fixit!!!!!");
            }
        }

        public Link getAsLink() {
            return new Link(a, b);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Match) {
                Match other = (Match) o;
                try {
                    return this.a.getReferend().getId() == other.a.getReferend().getId()
                            && this.b.getReferend().getId() == other.b.getReferend().getId();
                } catch (BucketException e) {
                    e.printStackTrace();
                    System.exit(1);
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            try {
                return Objects.hash(a.getReferend().getId(), b.getReferend().getId());
            } catch (BucketException e) {
                e.printStackTrace();
                System.exit(1);
                return 0;
            }
        }
    }

}
