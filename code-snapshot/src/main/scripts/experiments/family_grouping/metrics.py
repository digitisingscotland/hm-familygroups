#!/usr/bin/env python3 -u

import argparse
import csv
import time
import sys

from common import tick, mkUnorderedPair


# Command line argument parsing
parser = argparse.ArgumentParser()
parser.add_argument("filename")
args = parser.parse_args()

with open(args.filename, newline='\n') as file:
    reader = csv.reader(file, delimiter='\t')

    personIDs = set()

    linkedFamilyIDs = {}            # person -> family
    linkedFamilies = {}             # family -> [person]
    linkedSameFamily = set()        # [(person, person)]

    matchedFamilyIDs = {}           # person -> family
    matchedFamilies = {}            # family -> [person]
    matchedSameFamily = set()       # [(person, person)]

    for line in reader:
        if line[0] == "families":
            personID = int(line[1])
            if line[2] == "?":
                linkedFamilyID = None
            else:
                linkedFamilyID = int(line[2])
            if line[3] == "?":
                matchedFamilyID = None
            else:
                matchedFamilyID = int(line[3])

            personIDs.add(personID)

            if linkedFamilyID != None:
                linkedFamilyIDs[personID] = linkedFamilyID
                if not linkedFamilyID in linkedFamilies:
                    linkedFamilies[linkedFamilyID] = set()
                linkedFamilies[linkedFamilyID].add(personID)

            if matchedFamilyID != None:
                matchedFamilyIDs[personID] = matchedFamilyID
                if not matchedFamilyID in matchedFamilies:
                    matchedFamilies[matchedFamilyID] = set()
                matchedFamilies[matchedFamilyID].add(personID)

    # populate *SameFamily
    for p in personIDs:
        if p in linkedFamilyIDs.keys():
            for p2 in linkedFamilies[linkedFamilyIDs[p]]:
                if p != p2:
                    linkedSameFamily.add(mkUnorderedPair(p,p2))
        if p in matchedFamilyIDs.keys():
            for p2 in matchedFamilies[matchedFamilyIDs[p]]:
                if p != p2:
                    matchedSameFamily.add(mkUnorderedPair(p,p2))

    nbLinks = len(linkedSameFamily)
    nbMatches = len(matchedSameFamily)
    nbAllPairs = len(personIDs) * len(personIDs) / 2 - len(personIDs)


    # linkedSameFamily  contains pairs of siblings that are in the same family (w.r.t. linkage)
    # matchedSameFamily contains pairs of siblings that are in the same family (w.r.t. demographers)

    truePositives = 0
    falsePositives = 0
    for link in linkedSameFamily:
        if link in matchedSameFamily:
            truePositives += 1
        else:
            falsePositives += 1

    falseNegatives = nbMatches - truePositives
    trueNegatives = nbAllPairs - (truePositives + falsePositives + falseNegatives)

    parts = args.filename.split("/")[1].split(".")[0].split("-")
    print("Data Set                                   : %s"   % parts[0]      )
    print("family_forming_threshold                   : %s"   % parts[1]      )
    print("family_merging_threshold                   : %s"   % parts[2]      )
    print("maximum_family_size                        : %s"   % parts[3]      )
    print("source_of_siblings                         : %s"   % parts[4]      )
    print("source_of_families                         : %s"   % parts[5]      )
    print("family_distance_method                     : %s"   % parts[6]      )
    print("Number of sibling pairs (wrt linkage)      : %10d" % nbLinks       )
    print("Number of sibling pairs (wrt ground truth) : %10d" % nbMatches     )
    print("Number of sibling pairs (all possible)     : %10d" % nbAllPairs    )
    print("True  Positives                            : %10d" % truePositives )
    print("False Positives                            : %10d" % falsePositives)
    print("False Negatives                            : %10d" % falseNegatives)
    print("True  Negatives                            : %10d" % trueNegatives )

    precision = 0
    try:
        precision = truePositives / (truePositives + falsePositives)
    except ZeroDivisionError: pass

    recall = 0
    try:
        recall    = truePositives / (truePositives + falseNegatives)
    except ZeroDivisionError: pass

    f1measure = 0
    try:
        f1measure = (2 * precision * recall) / (precision + recall)
    except (ZeroDivisionError, NameError): pass

    accuracy = 0
    try:
        accuracy = (truePositives + trueNegatives) / nbAllPairs
    except ZeroDivisionError: pass

    print("Precision                                  : %10.4f" % precision)
    print("Recall                                     : %10.4f" % recall)
    print("F1 Measure                                 : %10.4f" % f1measure)
    # print("Accuracy                                   : %10.4f" % accuracy)


