#!/usr/bin/env python3 -u

import argparse
import csv
import time
import sys

from common import tick, mkUnorderedPair


# Command line argument parsing
parser = argparse.ArgumentParser()
parser.add_argument("filename_1")
parser.add_argument("filename_2")
parser.add_argument("filename_output")
args = parser.parse_args()



parts_1 = args.filename_1.split("/")[1].split(".")[0].split("-")
parts_2 = args.filename_2.split("/")[1].split(".")[0].split("-")
# 0: Data Set
# 1: family_forming_threshold
# 2: family_merging_threshold
# 3: maximum_family_size
# 4: source_of_siblings
# 5: source_of_families
# 6: family_distance_method




if args.filename_1 >= args.filename_2:
    # print("Skipping1: %s" % args.filename_output)
    sys.exit(0)

if parts_1[0] != parts_2[0]:
    # print("Skipping2: %s" % args.filename_output)
    sys.exit(0)

# print("Doing    : %s" % args.filename_output)


with open(args.filename_1, newline='\n') as file:
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

    linkedSameFamily_1 = linkedSameFamily
    nbLinks_1 = len(linkedSameFamily_1)
    nbMatches = len(matchedSameFamily)
    nbAllPairs = len(personIDs) * len(personIDs) / 2 - len(personIDs)

    truePositives_1 = 0
    falsePositives_1 = 0
    for link in linkedSameFamily_1:
        if link in matchedSameFamily:
            truePositives_1 += 1
        else:
            falsePositives_1 += 1

    falseNegatives_1 = nbMatches - truePositives_1
    trueNegatives_1 = nbAllPairs - (truePositives_1 + falsePositives_1 + falseNegatives_1)

    precision_1 = 0
    try:
        precision_1 = truePositives_1 / (truePositives_1 + falsePositives_1)
    except ZeroDivisionError: pass

    recall_1 = 0
    try:
        recall_1    = truePositives_1 / (truePositives_1 + falseNegatives_1)
    except ZeroDivisionError: pass

    f1measure_1 = 0
    try:
        f1measure_1 = (2 * precision_1 * recall_1) / (precision_1 + recall_1)
    except (ZeroDivisionError, NameError): pass



with open(args.filename_2, newline='\n') as file:
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

    linkedSameFamily_2 = linkedSameFamily
    nbLinks_2 = len(linkedSameFamily_2)
    nbMatches = len(matchedSameFamily)
    nbAllPairs = len(personIDs) * len(personIDs) / 2 - len(personIDs)

    truePositives_2 = 0
    falsePositives_2 = 0
    for link in linkedSameFamily_2:
        if link in matchedSameFamily:
            truePositives_2 += 1
        else:
            falsePositives_2 += 1

    falseNegatives_2 = nbMatches - truePositives_2
    trueNegatives_2 = nbAllPairs - (truePositives_2 + falsePositives_2 + falseNegatives_2)

    precision_2 = 0
    try:
        precision_2 = truePositives_2 / (truePositives_2 + falsePositives_2)
    except ZeroDivisionError: pass

    recall_2 = 0
    try:
        recall_2    = truePositives_2 / (truePositives_2 + falseNegatives_2)
    except ZeroDivisionError: pass

    f1measure_2 = 0
    try:
        f1measure_2 = (2 * precision_2 * recall_2) / (precision_2 + recall_2)
    except (ZeroDivisionError, NameError): pass



linkedSameFamily_intersect = set()
for i in linkedSameFamily_1:
    if i in linkedSameFamily_2:
        linkedSameFamily_intersect.add(i)

linkedSameFamily_union = set()
for i in linkedSameFamily_1:
    linkedSameFamily_union.add(i)
for i in linkedSameFamily_2:
    linkedSameFamily_union.add(i)



# INTERSECTION
linkedSameFamily_I = linkedSameFamily_intersect
nbLinks_I = len(linkedSameFamily_I)
nbMatches = len(matchedSameFamily)
nbAllPairs = len(personIDs) * len(personIDs) / 2 - len(personIDs)

truePositives_I = 0
falsePositives_I = 0
for link in linkedSameFamily_I:
    if link in matchedSameFamily:
        truePositives_I += 1
    else:
        falsePositives_I += 1

falseNegatives_I = nbMatches - truePositives_I
trueNegatives_I = nbAllPairs - (truePositives_I + falsePositives_I + falseNegatives_I)

precision_I = 0
try:
    precision_I = truePositives_I / (truePositives_I + falsePositives_I)
except ZeroDivisionError: pass

recall_I = 0
try:
    recall_I    = truePositives_I / (truePositives_I + falseNegatives_I)
except ZeroDivisionError: pass

f1measure_I = 0
try:
    f1measure_I = (2 * precision_I * recall_I) / (precision_I + recall_I)
except (ZeroDivisionError, NameError): pass



# UNION
linkedSameFamily_U = linkedSameFamily_union
nbLinks_U = len(linkedSameFamily_U)
nbMatches = len(matchedSameFamily)
nbAllPairs = len(personIDs) * len(personIDs) / 2 - len(personIDs)

truePositives_U = 0
falsePositives_U = 0
for link in linkedSameFamily_U:
    if link in matchedSameFamily:
        truePositives_U += 1
    else:
        falsePositives_U += 1

falseNegatives_U = nbMatches - truePositives_U
trueNegatives_U = nbAllPairs - (truePositives_U + falsePositives_U + falseNegatives_U)

precision_U = 0
try:
    precision_U = truePositives_U / (truePositives_U + falsePositives_U)
except ZeroDivisionError: pass

recall_U = 0
try:
    recall_U    = truePositives_U / (truePositives_U + falseNegatives_U)
except ZeroDivisionError: pass

f1measure_U = 0
try:
    f1measure_U = (2 * precision_U * recall_U) / (precision_U + recall_U)
except (ZeroDivisionError, NameError): pass



with open(args.filename_output, 'w') as outFile:

    print("1 Data Set                                           : %s"     % parts_1[0]       , file=outFile)
    print("1 family_forming_threshold                           : %s"     % parts_1[1]       , file=outFile)
    print("1 family_merging_threshold                           : %s"     % parts_1[2]       , file=outFile)
    print("1 maximum_family_size                                : %s"     % parts_1[3]       , file=outFile)
    print("1 source_of_siblings                                 : %s"     % parts_1[4]       , file=outFile)
    print("1 source_of_families                                 : %s"     % parts_1[5]       , file=outFile)
    print("1 family_distance_method                             : %s"     % parts_1[6]       , file=outFile)
    print("1 Number of sibling pairs (wrt linkage)              : %10d"   % nbLinks_1        , file=outFile)
    print("1 Number of sibling pairs (wrt ground truth)         : %10d"   % nbMatches        , file=outFile)
    print("1 Number of sibling pairs (all possible)             : %10d"   % nbAllPairs       , file=outFile)
    print("1 True  Positives                                    : %10d"   % truePositives_1  , file=outFile)
    print("1 False Positives                                    : %10d"   % falsePositives_1 , file=outFile)
    print("1 False Negatives                                    : %10d"   % falseNegatives_1 , file=outFile)
    print("1 True  Negatives                                    : %10d"   % trueNegatives_1  , file=outFile)
    print("1 Precision                                          : %10.4f" % precision_1      , file=outFile)
    print("1 Recall                                             : %10.4f" % recall_1         , file=outFile)
    print("1 F1 Measure                                         : %10.4f" % f1measure_1      , file=outFile)
    print(""                                                                                 , file=outFile)

    print("2 Data Set                                           : %s"     % parts_2[0]       , file=outFile)
    print("2 family_forming_threshold                           : %s"     % parts_2[1]       , file=outFile)
    print("2 family_merging_threshold                           : %s"     % parts_2[2]       , file=outFile)
    print("2 maximum_family_size                                : %s"     % parts_2[3]       , file=outFile)
    print("2 source_of_siblings                                 : %s"     % parts_2[4]       , file=outFile)
    print("2 source_of_families                                 : %s"     % parts_2[5]       , file=outFile)
    print("2 family_distance_method                             : %s"     % parts_2[6]       , file=outFile)
    print("2 Number of sibling pairs (wrt linkage)              : %10d"   % nbLinks_2        , file=outFile)
    print("2 Number of sibling pairs (wrt ground truth)         : %10d"   % nbMatches        , file=outFile)
    print("2 Number of sibling pairs (all possible)             : %10d"   % nbAllPairs       , file=outFile)
    print("2 True  Positives                                    : %10d"   % truePositives_2  , file=outFile)
    print("2 False Positives                                    : %10d"   % falsePositives_2 , file=outFile)
    print("2 False Negatives                                    : %10d"   % falseNegatives_2 , file=outFile)
    print("2 True  Negatives                                    : %10d"   % trueNegatives_2  , file=outFile)
    print("2 Precision                                          : %10.4f" % precision_2      , file=outFile)
    print("2 Recall                                             : %10.4f" % recall_2         , file=outFile)
    print("2 F1 Measure                                         : %10.4f" % f1measure_2      , file=outFile)
    print(""                                                                                 , file=outFile)

    print("P Number of sibling pairs (wrt linkage, intersect)   : %10d"   % len(linkedSameFamily_intersect)                                 , file=outFile)
    print("P Number of sibling pairs (wrt linkage, union)       : %10d"   % len(linkedSameFamily_union)                                     , file=outFile)
    print("P Jaccard Measure                                    : %10.4f" % (len(linkedSameFamily_intersect) / len(linkedSameFamily_union)) , file=outFile)

    print("I Number of sibling pairs (wrt linkage)              : %10d"   % nbLinks_I        , file=outFile)
    print("I Number of sibling pairs (wrt ground truth)         : %10d"   % nbMatches        , file=outFile)
    print("I Number of sibling pairs (all possible)             : %10d"   % nbAllPairs       , file=outFile)
    print("I True  Positives                                    : %10d"   % truePositives_I  , file=outFile)
    print("I False Positives                                    : %10d"   % falsePositives_I , file=outFile)
    print("I False Negatives                                    : %10d"   % falseNegatives_I , file=outFile)
    print("I True  Negatives                                    : %10d"   % trueNegatives_I  , file=outFile)
    print("I Precision                                          : %10.4f" % precision_I      , file=outFile)
    print("I Recall                                             : %10.4f" % recall_I         , file=outFile)
    print("I F1 Measure                                         : %10.4f" % f1measure_I      , file=outFile)
    print(""                                                                                 , file=outFile)

    print("U Number of sibling pairs (wrt linkage)              : %10d"   % nbLinks_U        , file=outFile)
    print("U Number of sibling pairs (wrt ground truth)         : %10d"   % nbMatches        , file=outFile)
    print("U Number of sibling pairs (all possible)             : %10d"   % nbAllPairs       , file=outFile)
    print("U True  Positives                                    : %10d"   % truePositives_U  , file=outFile)
    print("U False Positives                                    : %10d"   % falsePositives_U , file=outFile)
    print("U False Negatives                                    : %10d"   % falseNegatives_U , file=outFile)
    print("U True  Negatives                                    : %10d"   % trueNegatives_U  , file=outFile)
    print("U Precision                                          : %10.4f" % precision_U      , file=outFile)
    print("U Recall                                             : %10.4f" % recall_U         , file=outFile)
    print("U F1 Measure                                         : %10.4f" % f1measure_U      , file=outFile)
    print(""                                                                                 , file=outFile)


