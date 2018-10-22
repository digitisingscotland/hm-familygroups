#!/usr/bin/env python3

# data file specific file
# imports Skye, is used by explore

from common import mkName, Date, mkDate, mkDateFromYear, mkPlace, mkGender, mkOppositeGender

import os


# for each record, file locations
recordFiles =   { "Birth"    : os.path.expanduser("~/repos/github/digitisingscotland/hub/data/Kilmarnock/KilmarnockBirths.csv")
                , "Death"    : os.path.expanduser("~/repos/github/digitisingscotland/hub/data/Kilmarnock/KilmarnockDeaths.csv")
                , "Marriage" : os.path.expanduser("~/repos/github/digitisingscotland/hub/data/Kilmarnock/KilmarnockMarriages.csv")
                }



# for each record, the list of entities
entityNames =   { "Birth"    : ["Baby", "Father", "Mother"]
                , "Death"    : ["Deceased", "Spouse", "Father", "Mother"]
                , "Marriage" : ["Groom", "Bride", "GroomFather", "GroomMother", "BrideFather", "BrideMother"]
                }



class BirthBaby:

    def getID(rawRecord, useClean=False):
        return rawRecord["KILMidentifier"] + "_baby"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkGender(rawRecord["sex"])

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["cx"])
        else:
            return mkName(rawRecord["child's forname(s)"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["cs"])
        else:
            return mkName(rawRecord["child's surname"])

    def getOccupation(rawRecord, useClean=False):
        return None

    def getSpouseName(rawRecord, useClean=False):
        return None

    def getSpouseSurname(rawRecord, useClean=False):
        return None

    def getSpouseOccupation(rawRecord, useClean=False):
        return None

    def getFatherName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["fx"])
        else:
            return mkName(rawRecord["father's forename"])

    def getFatherSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["fs"])
        else:
            return mkName(rawRecord["father's surname"])

    def getFatherOccupation(rawRecord, useClean=False):
        return rawRecord["father's occupation"]

    def getMotherName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["mx"])
        else:
            return mkName(rawRecord["mother's forename"])

    def getMotherSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ms"])
        else:
            return mkName(rawRecord["mother's maiden surname"])

    def getMotherOccupation(rawRecord, useClean=False):
        return rawRecord["mother's occupation"]

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["year of reg"], rawRecord["month of reg"], rawRecord["day of reg"])

    def getDateOfBirth(rawRecord, useClean=False):
        if rawRecord["date of birth"] == None:
            return None
        [day, month, year] = rawRecord["date of birth"].split('/')
        return mkDate(year, month, day)

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return None

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return mkDate( rawRecord["year of parents' marriage"]
                     , rawRecord["month of parents' marriage"]
                     , rawRecord["day of parents' marriage"]
                     )

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        mkPlace(rawRecord["place of parent's marriage 1"], rawRecord["place of parent's marriage 2"])

    def getKnownLinks(rawRecord, useClean=False):
        if rawRecord["death"] == None:
            return None
        return [ ("Death", "Deceased", rawRecord["death"]) ]



class BirthFather:

    def getID(rawRecord, useClean=False):
        return rawRecord["KILMidentifier"] + "_father"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkGender("m")

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["fx"])
        else:
            return mkName(rawRecord["father's forename"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["fs"])
        else:
            return mkName(rawRecord["father's surname"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["father's occupation"]

    def getSpouseName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["mx"])
        else:
            return mkName(rawRecord["mother's forename"])

    def getSpouseSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ms"])
        else:
            return mkName(rawRecord["mother's maiden surname"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return rawRecord["mother's occupation"]

    def getFatherName(rawRecord, useClean=False):
        return None

    def getFatherSurname(rawRecord, useClean=False):
        return None

    def getFatherOccupation(rawRecord, useClean=False):
        return None

    def getMotherName(rawRecord, useClean=False):
        return None

    def getMotherSurname(rawRecord, useClean=False):
        return None

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["year of reg"], rawRecord["month of reg"], rawRecord["day of reg"])

    def getDateOfBirth(rawRecord, useClean=False):
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return mkDate( rawRecord["year of parents' marriage"]
                     , rawRecord["month of parents' marriage"]
                     , rawRecord["day of parents' marriage"]
                     )

    def getPlaceOfMarriage(rawRecord, useClean=False):
        mkPlace(rawRecord["place of parent's marriage 1"], rawRecord["place of parent's marriage 2"])

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        return None



class BirthMother:

    def getID(rawRecord, useClean=False):
        return rawRecord["KILMidentifier"] + "_mother"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkGender("f")

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["mx"])
        else:
            return mkName(rawRecord["mother's forename"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ms"])
        else:
            return mkName(rawRecord["mother's maiden surname"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["mother's occupation"]

    def getSpouseName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["fx"])
        else:
            return mkName(rawRecord["father's forename"])

    def getSpouseSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["fs"])
        else:
            return mkName(rawRecord["father's surname"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return rawRecord["father's occupation"]

    def getFatherName(rawRecord, useClean=False):
        return None

    def getFatherSurname(rawRecord, useClean=False):
        return None

    def getFatherOccupation(rawRecord, useClean=False):
        return None

    def getMotherName(rawRecord, useClean=False):
        return None

    def getMotherSurname(rawRecord, useClean=False):
        return None

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["year of reg"], rawRecord["month of reg"], rawRecord["day of reg"])

    def getDateOfBirth(rawRecord, useClean=False):
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return mkDate( rawRecord["year of parents' marriage"]
                     , rawRecord["month of parents' marriage"]
                     , rawRecord["day of parents' marriage"]
                     )

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return mkPlace(rawRecord["place of parent's marriage 1"], rawRecord["place of parent's marriage 2"])

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        return None



class DeathDeceased:

    def getID(rawRecord, useClean=False):
        return rawRecord["DeathIdentifier"] + "_deceased"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkGender(rawRecord["sex"])

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["dx"])
        else:
            return mkName(rawRecord["forename(s) of deceased"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ds"])
        else:
            return mkName(rawRecord["surname of deceased"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["occupation"]

    def getSpouseName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["spx"])
        else:
            return mkName(rawRecord["forename of spouse"])

    def getSpouseSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sps"])
        else:
            return mkName(rawRecord["surname of spouse"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return rawRecord["spouse's occ"]

    def getFatherName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["fx"])
        else:
            return mkName(rawRecord["father's forename"])

    def getFatherSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["fs"])
        else:
            return mkName(rawRecord["father's surname"])

    def getFatherOccupation(rawRecord, useClean=False):
        if useClean:
            return rawRecord["father's stanadard occ"]
        else:
            return rawRecord["father's occupation"]

    def getMotherName(rawRecord, useClean=False):
        return mkName(rawRecord["mother's forename"])

    def getMotherSurname(rawRecord, useClean=False):
        return mkName(rawRecord["mother's maiden surname"])

    def getMotherOccupation(rawRecord, useClean=False):
        return rawRecord["mother's occupation"]

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["year of reg"], rawRecord["month of reg"], rawRecord["day of reg"])

    def getDateOfBirth(rawRecord, useClean=False):
        # Using age to guess
        if rawRecord["age at death in years (decimal)"] != None:
            age = int(round(float(rawRecord["age at death in years (decimal)"])))
            now = int(rawRecord["year"])
            return mkDateFromYear(now - age)
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return mkDate(rawRecord["year"], rawRecord["month"], rawRecord["day"])

    def getDateOfMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return None

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        out = []
        # if rawRecord["Birth"] != None:
        #     out.append(("Birth", "Baby", rawRecord["Birth"]))
        ### we won't use the following link field, we will use Marriage.bdeath and Marriage.gdeath instead
        ### there is an email thread with Eilidh explaining why
        # if rawRecord["mar"] != None:
        #     # to known wheter this is a link to the groom or the bride of a marriage record
        #     # we need to lookup the gender
        #     gender = getValue("Death", "Deceased", "Gender", rawRecord, useClean)
        #     if isinstance(gender, Male):
        #         out.append(("Marriage", "Groom", rawRecord["mar"]))
        #     elif isinstance(gender, Female):
        #         out.append(("Marriage", "Bride", rawRecord["mar"]))
        #     else:
        #         print("WARNING: Unknown gender: %s" % str(gender))
        #         print("         Entry:")
        #         for key, value in rawRecord.items():
        #             print("                    %-42s: %s" % ("raw_" + key, value))
        if out == []:
            return None
        else:
            return out



class DeathSpouse:

    def getID(rawRecord, useClean=False):
        return rawRecord["DeathIdentifier"] + "_spouse"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkOppositeGender(rawRecord["sex"])

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["spx"])
        else:
            return mkName(rawRecord["forename of spouse"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sps"])
        else:
            return mkName(rawRecord["surname of spouse"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["spouse's occ"]

    def getSpouseName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["dx"])
        else:
            return mkName(rawRecord["forename(s) of deceased"])

    def getSpouseSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ds"])
        else:
            return mkName(rawRecord["surname of deceased"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return None

    def getFatherName(rawRecord, useClean=False):
        return None

    def getFatherSurname(rawRecord, useClean=False):
        return None

    def getFatherOccupation(rawRecord, useClean=False):
        return None

    def getMotherName(rawRecord, useClean=False):
        return None

    def getMotherSurname(rawRecord, useClean=False):
        return None

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["year of reg"], rawRecord["month of reg"], rawRecord["day of reg"])

    def getDateOfBirth(rawRecord, useClean=False):
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return None

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        return None



class DeathFather:

    def getID(rawRecord, useClean=False):
        return rawRecord["DeathIdentifier"] + "_father"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkGender("m")

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["fx"])
        else:
            return mkName(rawRecord["father's forename"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["fs"])
        else:
            return mkName(rawRecord["father's surname"])

    def getOccupation(rawRecord, useClean=False):
        if useClean:
            return rawRecord["father's stanadard occ"]
        else:
            return rawRecord["father's occupation"]

    def getSpouseName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["mx"])
        else:
            return mkName(rawRecord["mother's forename"])

    def getSpouseSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ms"])
        else:
            return mkName(rawRecord["mother's maiden surname"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return rawRecord["mother's occupation"]

    def getFatherName(rawRecord, useClean=False):
        return None

    def getFatherSurname(rawRecord, useClean=False):
        return None

    def getFatherOccupation(rawRecord, useClean=False):
        return None

    def getMotherName(rawRecord, useClean=False):
        return None

    def getMotherSurname(rawRecord, useClean=False):
        return None

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["year of reg"], rawRecord["month of reg"], rawRecord["day of reg"])

    def getDateOfBirth(rawRecord, useClean=False):
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return None

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        return None



class DeathMother:

    def getID(rawRecord, useClean=False):
        return rawRecord["DeathIdentifier"] + "_mother"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkGender("f")

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["mx"])
        else:
            return mkName(rawRecord["mother's forename"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ms"])
        else:
            return mkName(rawRecord["mother's maiden surname"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["mother's occupation"]

    def getSpouseName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["fx"])
        else:
            return mkName(rawRecord["father's forename"])

    def getSpouseSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["fs"])
        else:
            return mkName(rawRecord["father's surname"])

    def getSpouseOccupation(rawRecord, useClean=False):
        if useClean:
            return rawRecord["father's occupation"]
        else:
            return rawRecord["father's stanadard occ"]

    def getFatherName(rawRecord, useClean=False):
        return None

    def getFatherSurname(rawRecord, useClean=False):
        return None

    def getFatherOccupation(rawRecord, useClean=False):
        return None

    def getMotherName(rawRecord, useClean=False):
        return None

    def getMotherSurname(rawRecord, useClean=False):
        return None

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["year of reg"], rawRecord["month of reg"], rawRecord["day of reg"])

    def getDateOfBirth(rawRecord, useClean=False):
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return None

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        return None



class MarriageGroom:

    def getID(rawRecord, useClean=False):
        return rawRecord["KILM_marriageidentifier"] + "_groom"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkGender("m")

    def getName(rawRecord, useClean=False):
        return mkName(rawRecord["gx"])

    def getSurname(rawRecord, useClean=False):
        return mkName(rawRecord["gs"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["occupation of groom"]

    def getSpouseName(rawRecord, useClean=False):
        return mkName(rawRecord["bx"])

    def getSpouseSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bs"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return rawRecord["occupation of bride"]

    def getFatherName(rawRecord, useClean=False):
        return mkName(rawRecord["gfx"])

    def getFatherSurname(rawRecord, useClean=False):
        return mkName(rawRecord["groom's father's surname"])

    def getFatherOccupation(rawRecord, useClean=False):
        return mkName(rawRecord["groom's father's occupation"])

    def getMotherName(rawRecord, useClean=False):
        return mkName(rawRecord["gmx"])

    def getMotherSurname(rawRecord, useClean=False):
        return mkName(rawRecord["groom's mother's maiden surname"])

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["yearofMar"], rawRecord["monthofMar"], rawRecord["dayofMar"])

    def getDateOfBirth(rawRecord, useClean=False):
        # Using age to guess
        if rawRecord["age of groom"] != None:
            age = int(rawRecord["age of groom"])
            now = int(rawRecord["yearofMar"])
            return mkDateFromYear(now - age)
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return mkDate(rawRecord["yearofMar"], rawRecord["monthofMar"], rawRecord["dayofMar"])

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return mkPlace(rawRecord["place of marriage 1"], rawRecord["place of marriage 2"])

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        out = []
        if rawRecord["groom death"] != None:
            out.append(("Death", "Deceased", rawRecord["groom death"]))
        if out == []:
            return None
        else:
            return out



class MarriageBride:

    def getID(rawRecord, useClean=False):
        return rawRecord["KILM_marriageidentifier"] + "_bride"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkGender("f")

    def getName(rawRecord, useClean=False):
        return mkName(rawRecord["bx"])

    def getSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bs"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["occupation of bride"]

    def getSpouseName(rawRecord, useClean=False):
        return mkName(rawRecord["gx"])

    def getSpouseSurname(rawRecord, useClean=False):
        return mkName(rawRecord["gs"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return rawRecord["occupation of groom"]

    def getFatherName(rawRecord, useClean=False):
        return mkName(rawRecord["bfx"])

    def getFatherSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bride's father's surname"])

    def getFatherOccupation(rawRecord, useClean=False):
        return rawRecord["bride's father's occupation"]

    def getMotherName(rawRecord, useClean=False):
        return mkName(rawRecord["gmx"])

    def getMotherSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bride's mother's maiden surname"])

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["yearofMar"], rawRecord["monthofMar"], rawRecord["dayofMar"])

    def getDateOfBirth(rawRecord, useClean=False):
        # Using age to guess
        if rawRecord["age of bride"] != None:
            age = int(rawRecord["age of bride"])
            now = int(rawRecord["yearofMar"])
            return mkDateFromYear(now - age)
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return mkDate(rawRecord["yearofMar"], rawRecord["monthofMar"], rawRecord["dayofMar"])

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return mkPlace(rawRecord["place of marriage 1"], rawRecord["place of marriage 2"])

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        out = []
        if rawRecord["bride death"] != None:
            out.append(("Death", "Deceased", rawRecord["bride death"]))
        if out == []:
            return None
        else:
            return out



class MarriageGroomFather:

    def getID(rawRecord, useClean=False):
        return rawRecord["KILM_marriageidentifier"] + "_groomFather"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkGender("m")

    def getName(rawRecord, useClean=False):
        return mkName(rawRecord["gfx"])

    def getSurname(rawRecord, useClean=False):
        return mkName(rawRecord["groom's father's surname"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["groom's father's occupation"]

    def getSpouseName(rawRecord, useClean=False):
        return mkName(rawRecord["gmx"])

    def getSpouseSurname(rawRecord, useClean=False):
        return mkName(rawRecord["groom's mother's maiden surname"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return None

    def getFatherName(rawRecord, useClean=False):
        return None

    def getFatherSurname(rawRecord, useClean=False):
        return None

    def getFatherOccupation(rawRecord, useClean=False):
        return None

    def getMotherName(rawRecord, useClean=False):
        return None

    def getMotherSurname(rawRecord, useClean=False):
        return None

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["yearofMar"], rawRecord["monthofMar"], rawRecord["dayofMar"])

    def getDateOfBirth(rawRecord, useClean=False):
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return None

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        return None



class MarriageGroomMother:

    def getID(rawRecord, useClean=False):
        return rawRecord["KILM_marriageidentifier"] + "_groomMother"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkGender("f")

    def getName(rawRecord, useClean=False):
        return mkName(rawRecord["gmx"])

    def getSurname(rawRecord, useClean=False):
        return mkName(rawRecord["groom's mother's maiden surname"])

    def getOccupation(rawRecord, useClean=False):
        return None

    def getSpouseName(rawRecord, useClean=False):
        return mkName(rawRecord["gfx"])

    def getSpouseSurname(rawRecord, useClean=False):
        return mkName(rawRecord["groom's father's surname"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return mkName(rawRecord["groom's father's occupation"])

    def getFatherName(rawRecord, useClean=False):
        return None

    def getFatherSurname(rawRecord, useClean=False):
        return None

    def getFatherOccupation(rawRecord, useClean=False):
        return None

    def getMotherName(rawRecord, useClean=False):
        return None

    def getMotherSurname(rawRecord, useClean=False):
        return None

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["yearofMar"], rawRecord["monthofMar"], rawRecord["dayofMar"])

    def getDateOfBirth(rawRecord, useClean=False):
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return None

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        return None



class MarriageBrideFather:

    def getID(rawRecord, useClean=False):
        return rawRecord["KILM_marriageidentifier"] + "_brideFather"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkGender("m")

    def getName(rawRecord, useClean=False):
        return mkName(rawRecord["bfx"])

    def getSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bride's father's surname"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["bride's father's occupation"]

    def getSpouseName(rawRecord, useClean=False):
        return mkName(rawRecord["bmx"])

    def getSpouseSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bride's mother's maiden surname"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return None

    def getFatherName(rawRecord, useClean=False):
        return None

    def getFatherSurname(rawRecord, useClean=False):
        return None

    def getFatherOccupation(rawRecord, useClean=False):
        return None

    def getMotherName(rawRecord, useClean=False):
        return None

    def getMotherSurname(rawRecord, useClean=False):
        return None

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["yearofMar"], rawRecord["monthofMar"], rawRecord["dayofMar"])

    def getDateOfBirth(rawRecord, useClean=False):
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return None

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        return None



class MarriageBrideMother:

    def getID(rawRecord, useClean=False):
        return rawRecord["KILM_marriageidentifier"] + "_brideMother"

    def getLocation(rawRecord, useClean=False):
        return None

    def getGender(rawRecord, useClean=False):
        return mkGender("f")

    def getName(rawRecord, useClean=False):
        return mkName(rawRecord["bmx"])

    def getSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bride's mother's maiden surname"])

    def getOccupation(rawRecord, useClean=False):
        return None

    def getSpouseName(rawRecord, useClean=False):
        return mkName(rawRecord["bfx"])

    def getSpouseSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bride's father's surname"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return rawRecord["bride's father's occupation"]

    def getFatherName(rawRecord, useClean=False):
        return None

    def getFatherSurname(rawRecord, useClean=False):
        return None

    def getFatherOccupation(rawRecord, useClean=False):
        return None

    def getMotherName(rawRecord, useClean=False):
        return None

    def getMotherSurname(rawRecord, useClean=False):
        return None

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["yearofMar"], rawRecord["monthofMar"], rawRecord["dayofMar"])

    def getDateOfBirth(rawRecord, useClean=False):
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return None

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        return None



def getValue(record, entity, field, line, useClean):
    if field.endswith("_Day"):
        value = getValue(record, entity, field[:-4], line, useClean)
        if value == None: return None
        return value.day
    if field.endswith("_Month"):
        value = getValue(record, entity, field[:-6], line, useClean)
        if value == None: return None
        return value.month
    if field.endswith("_Year"):
        value = getValue(record, entity, field[:-5], line, useClean)
        if value == None: return None
        return value.year

    klass = globals()[record + entity]
    instance = klass()
    method = getattr(klass, "get"+field)
    return method(line, useClean)


def getValueStr(record, entity, field, line, useClean):
    value = getValue(record, entity, field, line, useClean)
    if type(value) is Date or type(value) is int:
        return str(value)
    else:
        return value


