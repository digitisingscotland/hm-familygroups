#!/usr/bin/env python3 -u

# Collect a bunch of files to a csv
# Each individual part contains one line, in the "header : value" style

import argparse
import csv
import time
import sys
import os

def mean(xs):
    return sum(xs) / len(xs)


# Command line argument parsing
parser = argparse.ArgumentParser()
parser.add_argument("directory")
args = parser.parse_args()

headers = None
values = []

for f in os.listdir(args.directory):
    if f.endswith(".metrics"):
        with open("%s/%s" % (args.directory, f), newline='\n') as h:
            reader = csv.reader(h, delimiter=':')

            thisHeader = []
            thisValue = {}
            for line in reader:
                thisHeader.append(line[0].strip())
                thisValue[line[0].strip()] = line[1].strip()

            if headers == None:
                headers = thisHeader
            elif headers == thisHeader:
                pass
            else:
                sys.exit( "headers mismatch."
                        + "\nexpecting: %s" % headers
                        + "\nbut got  : %s" % thisHeader
                        + "\nin file  : %s" % f)

            if thisValue["Data Set"] == "skye":
            # if thisValue["Data Set"] == "kilmarnock":
                values.append(thisValue)


if headers == None:
    sys.exit("Generate the '*.metrics' files first.")

with open("%s.csv" % args.directory, 'w') as outFile:
    print(",".join(headers), file=outFile)
    for value in values:
        print(",".join(value.values()), file=outFile)



categoricalHeaders = [ # "Data Set"
                       "family_forming_threshold"
                     , "family_merging_threshold"
                     , "maximum_family_size"
                     , "source_of_siblings"
                     , "source_of_families"
                     , "family_distance_method"
                     ]

valuesForHeader = {}                          # header -> set of values
for header in categoricalHeaders:
    valuesForHeader[header] = set()
    for line in values:
        valuesForHeader[header].add(line[header])



# dominance analysis
for header in categoricalHeaders:
    print()
    print(header)
    for valueThis in sorted(valuesForHeader[header]):
        for valueOther in sorted(valuesForHeader[header]):
            if valueThis != valueOther:
                # print(header, valueThis, valueOther)
                dominates = True
                dominatesN = 0
                totalN = 0
                for entry1 in values:
                    for entry2 in values:
                        if entry1 != entry2:
                            if all([ entry1[k] == entry2[k] for k in categoricalHeaders if k != header ]) and entry1[header] == valueThis and entry2[header] == valueOther:
                                f1 = entry1["F1 Measure"]
                                f2 = entry2["F1 Measure"]
                                if f1 < f2:
                                    dominates = False
                                else:
                                    dominatesN += 1
                                totalN += 1
                                # print("\t" + str(entry1), f1)
                                # print("\t" + str(entry2), f2)
                if dominates:
                    dominatesStr = "(dominates)"
                else:
                    dominatesStr = ""
                print("\t", valueThis, "vs", valueOther, "\t", "(%4.2f)" % (dominatesN/totalN), "\t", dominatesStr)

sys.exit("Done")


# aggregated f-measures

lvl = 1
meanThres = 0

for header1 in categoricalHeaders:
    for val1 in sorted(valuesForHeader[header1]):
        fs = []
        for line in values:
            if line[header1] == val1:
                fs.append(float(line["F1 Measure"]))
        if len(fs) == 0:
            print("%-147s MISSING"                % ( "%s : %s" % (header1, val1)))
        elif mean(fs) >= meanThres:
            print("%-147s %4.2f (%4.2f -- %4.2f)" % ( "%s : %s" % (header1, val1)
                                                    , mean(fs), min(fs), max(fs)
                                                    ))

        if lvl > 1:
            header2Gen = [ h for h in categoricalHeaders if h != header1 ]
            for header2 in header2Gen :
                for val2 in sorted(valuesForHeader[header2]):
                    fs = []
                    for line in values:
                        if line[header1] == val1 and line[header2] == val2:
                            fs.append(float(line["F1 Measure"]))
                    if len(fs) == 0:
                        print("%-36s %-110s MISSING"                % ( "%s : %s" % (header1, val1)
                                                                      , "%s : %s" % (header2, val2)))
                    elif mean(fs) >= meanThres:
                        print("%-36s %-110s %4.2f (%4.2f -- %4.2f)" % ( "%s : %s" % (header1, val1)
                                                                      , "%s : %s" % (header2, val2)
                                                                      , mean(fs), min(fs), max(fs)))

                    if lvl > 2:
                        header3Gen = [ h for h in header2Gen if h != header2 ]
                        for header3 in header3Gen:
                            for val3 in sorted(valuesForHeader[header3]):
                                fs = []
                                for line in values:
                                    if line[header1] == val1 and line[header2] == val2 and line[header3] == val3:
                                        fs.append(float(line["F1 Measure"]))
                                if len(fs) == 0:
                                    print("%-36s %-36s %-73s MISSING"                % ( "%s : %s" % (header1, val1)
                                                                                       , "%s : %s" % (header2, val2)
                                                                                       , "%s : %s" % (header3, val3)))
                                elif mean(fs) >= meanThres:
                                    print("%-36s %-36s %-73s %4.2f (%4.2f -- %4.2f)" % ( "%s : %s" % (header1, val1)
                                                                                       , "%s : %s" % (header2, val2)
                                                                                       , "%s : %s" % (header3, val3)
                                                                                       , mean(fs), min(fs), max(fs)))

                                if lvl > 3:
                                    header4Gen = [ h for h in header3Gen if h != header3 ]
                                    for header4 in header4Gen:
                                        for val4 in sorted(valuesForHeader[header4]):
                                            fs = []
                                            for line in values:
                                                if line[header1] == val1 and line[header2] == val2 and line[header3] == val3 and line[header4] == val4:
                                                    fs.append(float(line["F1 Measure"]))
                                            if len(fs) == 0:
                                                print("%-36s %-36s %-36s %-36s MISSING"                % ( "%s : %s" % (header1, val1)
                                                                                                         , "%s : %s" % (header2, val2)
                                                                                                         , "%s : %s" % (header3, val3)
                                                                                                         , "%s : %s" % (header4, val4)))
                                            elif mean(fs) >= meanThres:
                                                print("%-36s %-36s %-36s %-36s %4.2f (%4.2f -- %4.2f)" % ( "%s : %s" % (header1, val1)
                                                                                                         , "%s : %s" % (header2, val2)
                                                                                                         , "%s : %s" % (header3, val3)
                                                                                                         , "%s : %s" % (header4, val4)
                                                                                                         , mean(fs), min(fs), max(fs)))
