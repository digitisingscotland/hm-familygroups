#!/usr/bin/env python3

from common import tick, loadCSV, entityFields, allKeyFields, mkStringComparison, sigmaMerge, calculateDistance

import argparse
import matplotlib
import os
import random
import statistics
import sys

matplotlib.use('cairo')
import matplotlib.pyplot as plt


import Levenshtein
import Skye
import Kilmarnock


keepFields = [ "ID"
             , "FatherName", "FatherSurname"
             , "MotherName", "MotherSurname"
             , "DateOfParentsMarriage", "PlaceOfParentsMarriage"
             , "KnownLinks"
             , "FamilyID"
             ]

keyFields  = [ "FatherName", "FatherSurname"
             , "MotherName", "MotherSurname"
             , "DateOfParentsMarriage", "PlaceOfParentsMarriage"
             ]

def main():

    datasets = {"Skye" : Skye, "Kilmarnock" : Kilmarnock}

    errs = {}
    printed = {}

    for (datasetname, dataset) in datasets.items():
        # the raw data
        recordFilesRaw = {}
        for recordName, filepath in dataset.recordFiles.items():
            recordFilesRaw[recordName] = loadCSV(filepath, verbose=1)

        recordFiles = {}

        # populate "flatTable", "byID", "byRawID", "byRecordEntity", and "familyBundles"
        for (recordName, (fieldNames, values)) in recordFilesRaw.items():
            recordFiles[recordName] = []
            for line in values:
                for entityName in dataset.entityNames[recordName]:
                    flatRow = {}
                    flatRow["Record"] = recordName
                    flatRow["Entity"] = entityName
                    # flatRow["raw"] = line
                    if "family" in line.keys():
                        flatRow["FamilyID"] = line["family"]
                    else:
                        flatRow["FamilyID"] = None
                    for fieldName in entityFields:
                        if fieldName in keepFields:
                            flatRow[fieldName] = dataset.getValueStr(recordName, entityName, fieldName, line, True)
                    recordFiles[recordName].append(flatRow)

        truePositives = {}
        falsePositives = {}
        falseNegatives = {}
        trueNegatives = {}      # unnecessary
        precision = {}
        recall = {}
        f1measure = {}

        thresholds = [2,5,8,10]
        for t in thresholds:
            truePositives[t] = 0
            falsePositives[t] = 0
            falseNegatives[t] = 0
            trueNegatives[t] = 0

        nbBirths = 0
        for x in recordFiles["Birth"]:
            if x["Entity"] == "Baby":
                nbBirths += 1

        xCounter = 0
        yCounter = 0
        for x in recordFiles["Birth"]:
            if x["Entity"] == "Baby":
                xCounter += 1
                # if xCounter >= 1000:
                #     break
                yCounter = 0
                for y in recordFiles["Birth"]:
                    if y["Entity"] == "Baby":
                        yCounter += 1
                        if x["ID"] != y["ID"]:
                            dist = 0
                            for k in keyFields:
                                xk = x[k]
                                yk = y[k]
                                if xk == None:
                                    xk = ""
                                if yk == None:
                                    yk = ""
                                dist += Levenshtein.distance(xk, yk)
                            for t in thresholds:
                                if x["FamilyID"] != None and y["FamilyID"] != None and int(x["FamilyID"]) == int(y["FamilyID"]):            # a link
                                    if dist <= t:                                       # a match
                                        truePositives[t] += 1
                                    else:
                                        falseNegatives[t] += 1          # linked, but wasn't a match
                                else:
                                    if dist <= t:
                                        falsePositives[t] += 1
                                    else:
                                        trueNegatives[t] += 1

                            for t in thresholds:
                                try:    precision[t] = truePositives[t] / (truePositives[t] + falsePositives[t])
                                except: precision[t] = 0
                                try:    recall[t]    = truePositives[t] / (truePositives[t] + falseNegatives[t])
                                except: recall[t]    = 0
                                try:    f1measure[t] = (2 * precision[t] * recall[t]) / (precision[t] + recall[t])
                                except: f1measure[t] = 0

                if xCounter % 500 == 0:
                    out = ""
                    for t in thresholds:
                        out += ("Tick (%d, %d) of (%d, %d) -- %2d -- TP %6d FN %6d FP %6d TN %6d P %.2f R %.2f F %.2f\n"
                        % (xCounter, yCounter, nbBirths, nbBirths, t
                          , truePositives[t], falseNegatives[t]
                          , falsePositives[t], trueNegatives[t]
                          , precision[t], recall[t], f1measure[t]))
                    tick(out)

        out = ""
        for t in thresholds:
            out += ("Tick (%d, %d) of (%d, %d) -- %2d -- TP %6d FN %6d FP %6d TN %6d P %.2f R %.2f F %.2f\n"
            % (xCounter, yCounter, nbBirths, nbBirths, t
              , truePositives[t], falseNegatives[t]
              , falsePositives[t], trueNegatives[t]
              , precision[t], recall[t], f1measure[t]))
        tick(out)

        for t in thresholds:
            try:    precision[t] = truePositives[t] / (truePositives[t] + falsePositives[t])
            except: precision[t] = 0
            try:    recall[t]    = truePositives[t] / (truePositives[t] + falseNegatives[t])
            except: recall[t]    = 0
            try:    f1measure[t] = (2 * precision[t] * recall[t]) / (precision[t] + recall[t])
            except: f1measure[t] = 0


        for t in thresholds:
            print("dataset              : %s"       % datasetname)
            print("threshold            : %10d"     % t)
            print("truePositives        : %10d"     % truePositives[t])
            print("falsePositives       : %10d"     % falsePositives[t])
            print("falseNegatives       : %10d"     % falseNegatives[t])
            print("trueNegatives        : %10d"     % trueNegatives[t])
            print("precision            : %10.4f"   % precision[t])
            print("recall               : %10.4f"   % recall[t])
            print("f1measure            : %10.4f"   % f1measure[t])

    tick("Done.")



if __name__ == "__main__":
    main()

