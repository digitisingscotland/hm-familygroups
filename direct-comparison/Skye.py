#!/usr/bin/env python3

# data file specific file
# imports Skye, is used by explore

from common import mkName, Date, mkDate, mkDateFromYear, mkPlace, mkGender, mkOppositeGender

import os


# for each record, file locations
recordFiles =   { "Birth"    : os.path.expanduser("~/repos/github/digitisingscotland/hub/data/Skye/SkyeBirths.csv")
                , "Death"    : os.path.expanduser("~/repos/github/digitisingscotland/hub/data/Skye/SkyeDeaths.csv")
                , "Marriage" : os.path.expanduser("~/repos/github/digitisingscotland/hub/data/Skye/SkyeMarriages.csv")
                }



# for each record, the list of entities
entityNames =   { "Birth"    : ["Baby", "Father", "Mother"]
                , "Death"    : ["Deceased", "Spouse", "Father", "Mother"]
                , "Marriage" : ["Groom", "Bride", "GroomFather", "GroomMother", "BrideFather", "BrideMother"]
                }



class BirthBaby:

    def getID(rawRecord, useClean=False):
        return rawRecord["IOSBIRTH_Identifier"] + "_baby"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkGender(rawRecord["sex"])

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxchild"])
        else:
            return mkName(rawRecord["child's forname(s)"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sschild"])
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
            return mkName(rawRecord["sxfather"])
        else:
            return mkName(rawRecord["father's forename"])

    def getFatherSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssfather"])
        else:
            return mkName(rawRecord["father's surname"])

    def getFatherOccupation(rawRecord, useClean=False):
        return rawRecord["father's occupation"]

    def getMotherName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxmother"])
        else:
            return mkName(rawRecord["mother's forename"])

    def getMotherSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssmother"])
        else:
            return mkName(rawRecord["mother's maiden surname"])

    def getMotherOccupation(rawRecord, useClean=False):
        return rawRecord["mother's occupation"]

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["year of reg"], rawRecord["month of reg"], rawRecord["day of reg"])

    def getDateOfBirth(rawRecord, useClean=False):
        [day, month, year] = rawRecord["birth date"].split('/')
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
        return mkPlace(rawRecord["place of parent's marriage 1"], rawRecord["place of parent's marriage 2"])

    def getKnownLinks(rawRecord, useClean=False):
        if rawRecord["Death"] == None:
            return None
        return [ ("Death", "Deceased", rawRecord["Death"]) ]



class BirthFather:

    def getID(rawRecord, useClean=False):
        return rawRecord["IOSBIRTH_Identifier"] + "_father"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkGender("m")

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxfather"])
        else:
            return mkName(rawRecord["father's forename"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssfather"])
        else:
            return mkName(rawRecord["father's surname"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["father's occupation"]

    def getSpouseName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxmother"])
        else:
            return mkName(rawRecord["mother's forename"])

    def getSpouseSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssmother"])
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
        return mkPlace(rawRecord["place of parent's marriage 1"], rawRecord["place of parent's marriage 2"])

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        return None



class BirthMother:

    def getID(rawRecord, useClean=False):
        return rawRecord["IOSBIRTH_Identifier"] + "_mother"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkGender("f")

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxmother"])
        else:
            return mkName(rawRecord["mother's forename"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssmother"])
        else:
            return mkName(rawRecord["mother's maiden surname"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["mother's occupation"]

    def getSpouseName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxfather"])
        else:
            return mkName(rawRecord["father's forename"])

    def getSpouseSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssfather"])
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
        return rawRecord["IOSidentifier"] + "_deceased"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkGender(rawRecord["sex"])

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxdec"])
        else:
            return mkName(rawRecord["forename(s) of deceased"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssdec"])
        else:
            return mkName(rawRecord["surname of deceased"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["occupation"]

    def getSpouseName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["spousexn"])
        else:
            return mkName(rawRecord["forename of spouse"])

    def getSpouseSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["spousesn"])
        else:
            return mkName(rawRecord["surname of spouse"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return rawRecord["spouse's occ"]

    def getFatherName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxfather"])
        else:
            return mkName(rawRecord["father's forename"])

    def getFatherSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssfather"])
        else:
            return mkName(rawRecord["father's surname"])

    def getFatherOccupation(rawRecord, useClean=False):
        return rawRecord["father's occupation"]

    def getMotherName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxmother"])
        else:
            return mkName(rawRecord["mother's forename"])

    def getMotherSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssmother"])
        else:
            return mkName(rawRecord["mother's maiden surname"])

    def getMotherOccupation(rawRecord, useClean=False):
        return rawRecord["mother's occupation"]

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["year of reg"], rawRecord["month of reg"], rawRecord["day of reg"])

    def getDateOfBirth(rawRecord, useClean=False):
        # Using age to guess
        if rawRecord["agey"] != None:
            age = int(round(float(rawRecord["agey"])))
            if rawRecord["year of reg"] != None:
                now = int(rawRecord["year of reg"])
                return mkDateFromYear(now - age)
            if rawRecord["year"] != None:
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
        if rawRecord["Birth"] != None:
            out.append(("Birth", "Baby", rawRecord["Birth"]))
        ### we won't use the following link field, we will use Marriage.bdeath and Marriage.gdeath instead
        ### there is an email thread with Eilidh explaining why
        # if rawRecord["mar"] != None:
        #     # to known wheter this is a link to the groom or the bride of a marriage record
        #     # we need to lookup the gender
        #     gender = getValueStr("Death", "Deceased", "Gender", rawRecord, useClean)
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
        return rawRecord["IOSidentifier"] + "_spouse"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkOppositeGender(rawRecord["sex"])

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["spousexn"])
        else:
            return mkName(rawRecord["forename of spouse"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["spousesn"])
        else:
            return mkName(rawRecord["surname of spouse"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["spouse's occ"]

    def getSpouseName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxdec"])
        else:
            return mkName(rawRecord["forename(s) of deceased"])

    def getSpouseSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssdec"])
        else:
            return mkName(rawRecord["surname of deceased"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return rawRecord["occupation"]

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
        return rawRecord["IOSidentifier"] + "_father"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkGender("m")

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxfather"])
        else:
            return mkName(rawRecord["father's forename"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssfather"])
        else:
            return mkName(rawRecord["father's surname"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["father's occupation"]

    def getSpouseName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxmother"])
        else:
            return mkName(rawRecord["mother's forename"])

    def getSpouseSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssmother"])
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
        return rawRecord["IOSidentifier"] + "_mother"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkGender("f")

    def getName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxmother"])
        else:
            return mkName(rawRecord["mother's forename"])

    def getSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssmother"])
        else:
            return mkName(rawRecord["mother's maiden surname"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["mother's occupation"]

    def getSpouseName(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["sxfather"])
        else:
            return mkName(rawRecord["father's forename"])

    def getSpouseSurname(rawRecord, useClean=False):
        if useClean:
            return mkName(rawRecord["ssfather"])
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
        return rawRecord["IOS_identifier"] + "_groom"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkGender("m")

    def getName(rawRecord, useClean=False):
        return mkName(rawRecord["gxn"])

    def getSurname(rawRecord, useClean=False):
        return mkName(rawRecord["gsn"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["occupation of groom"]

    def getSpouseName(rawRecord, useClean=False):
        return mkName(rawRecord["bxn"])

    def getSpouseSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bsn"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return rawRecord["occupation of bride"]

    def getFatherName(rawRecord, useClean=False):
        return mkName(rawRecord["gfxn"])

    def getFatherSurname(rawRecord, useClean=False):
        return mkName(rawRecord["groom's father's surname"])

    def getFatherOccupation(rawRecord, useClean=False):
        return rawRecord["groom's father's occupation"]

    def getMotherName(rawRecord, useClean=False):
        return mkName(rawRecord["gmxn"])

    def getMotherSurname(rawRecord, useClean=False):
        return mkName(rawRecord["groom's mother's maiden surname"])

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["year"], rawRecord["month"], rawRecord["day"])

    def getDateOfBirth(rawRecord, useClean=False):
        # Using age to guess
        if rawRecord["age of groom"] != None:
            age = int(rawRecord["age of groom"])
            now = int(rawRecord["year"])
            return mkDateFromYear(now - age)
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return mkDate(rawRecord["year"], rawRecord["month"], rawRecord["day"])

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return mkPlace(rawRecord["place of marriage 1"], rawRecord["place of marriage 2"])

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        out = []
        if rawRecord["gdeath"] != None:
            out.append(("Death", "Deceased", rawRecord["gdeath"]))
        if out == []:
            return None
        else:
            return out



class MarriageBride:

    def getID(rawRecord, useClean=False):
        return rawRecord["IOS_identifier"] + "_bride"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkGender("f")

    def getName(rawRecord, useClean=False):
        return mkName(rawRecord["bxn"])

    def getSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bsn"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["occupation of bride"]

    def getSpouseName(rawRecord, useClean=False):
        return mkName(rawRecord["gxn"])

    def getSpouseSurname(rawRecord, useClean=False):
        return mkName(rawRecord["gsn"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return rawRecord["occupation of groom"]

    def getFatherName(rawRecord, useClean=False):
        return mkName(rawRecord["bfxn"])

    def getFatherSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bride's father's surname"])

    def getFatherOccupation(rawRecord, useClean=False):
        return rawRecord["bride's father's occupation"]

    def getMotherName(rawRecord, useClean=False):
        return mkName(rawRecord["gmxn"])

    def getMotherSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bride's mother's maiden surname"])

    def getMotherOccupation(rawRecord, useClean=False):
        return None

    def getDateOfRegistry(rawRecord, useClean=False):
        return mkDate(rawRecord["year"], rawRecord["month"], rawRecord["day"])

    def getDateOfBirth(rawRecord, useClean=False):
        # Using age to guess
        if rawRecord["age of bride"] != None:
            age = int(rawRecord["age of bride"])
            now = int(rawRecord["year"])
            return mkDateFromYear(now - age)
        return None

    def getDateOfDeath(rawRecord, useClean=False):
        return None

    def getDateOfMarriage(rawRecord, useClean=False):
        return mkDate(rawRecord["year"], rawRecord["month"], rawRecord["day"])

    def getPlaceOfMarriage(rawRecord, useClean=False):
        return mkPlace(rawRecord["place of marriage 1"], rawRecord["place of marriage 2"])

    def getDateOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getPlaceOfParentsMarriage(rawRecord, useClean=False):
        return None

    def getKnownLinks(rawRecord, useClean=False):
        out = []
        if rawRecord["bdeath"] != None:
            out.append(("Death", "Deceased", rawRecord["bdeath"]))
        if out == []:
            return None
        else:
            return out



class MarriageGroomFather:

    def getID(rawRecord, useClean=False):
        return rawRecord["IOS_identifier"] + "_groomFather"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkGender("m")

    def getName(rawRecord, useClean=False):
        return mkName(rawRecord["gfxn"])

    def getSurname(rawRecord, useClean=False):
        return mkName(rawRecord["groom's father's surname"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["groom's father's occupation"]

    def getSpouseName(rawRecord, useClean=False):
        return mkName(rawRecord["gmxn"])

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
        return mkDate(rawRecord["year"], rawRecord["month"], rawRecord["day"])

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
        return rawRecord["IOS_identifier"] + "_groomMother"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkGender("f")

    def getName(rawRecord, useClean=False):
        return mkName(rawRecord["gmxn"])

    def getSurname(rawRecord, useClean=False):
        return mkName(rawRecord["groom's mother's maiden surname"])

    def getOccupation(rawRecord, useClean=False):
        return None

    def getSpouseName(rawRecord, useClean=False):
        return mkName(rawRecord["gfxn"])

    def getSpouseSurname(rawRecord, useClean=False):
        return mkName(rawRecord["groom's father's surname"])

    def getSpouseOccupation(rawRecord, useClean=False):
        return rawRecord["groom's father's occupation"]

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
        return mkDate(rawRecord["year"], rawRecord["month"], rawRecord["day"])

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
        return rawRecord["IOS_identifier"] + "_brideFather"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkGender("m")

    def getName(rawRecord, useClean=False):
        return mkName(rawRecord["bfxn"])

    def getSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bride's father's surname"])

    def getOccupation(rawRecord, useClean=False):
        return rawRecord["bride's father's occupation"]

    def getSpouseName(rawRecord, useClean=False):
        return mkName(rawRecord["bmxn"])

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
        return mkDate(rawRecord["year"], rawRecord["month"], rawRecord["day"])

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
        return rawRecord["IOS_identifier"] + "_brideMother"

    def getLocation(rawRecord, useClean=False):
        return rawRecord["source"]

    def getGender(rawRecord, useClean=False):
        return mkGender("f")

    def getName(rawRecord, useClean=False):
        return mkName(rawRecord["bmxn"])

    def getSurname(rawRecord, useClean=False):
        return mkName(rawRecord["bride's mother's maiden surname"])

    def getOccupation(rawRecord, useClean=False):
        return None

    def getSpouseName(rawRecord, useClean=False):
        return mkName(rawRecord["bfxn"])

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
        return mkDate(rawRecord["year"], rawRecord["month"], rawRecord["day"])

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


