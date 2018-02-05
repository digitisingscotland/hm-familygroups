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

import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;

import java.util.HashSet;
import java.util.Set;

/**
 * Essentially a set of siblings carrying an id.
 * Created by al on 28/02/2017.
 */
public class FamilyOLDVersion {

    private static long next_id_to_be_allocated = 1;

    public Set<Birth> siblings;
    public final long id;

    private String place_of_marriage;
    private String day_of_marriage;
    private String month_of_marriage;
    private String year_of_marriage;
    private String mothers_maiden_surname;
    private String mothers_forenname;
    private String fathers_surname;
    private String fathers_forename;

    private String mothers_surname_clean;
    private String mothers_forenname_clean;
    private String fathers_surname_clean;
    private String fathers_forename_clean;

    private FamilyOLDVersion() {

        this.id = next_id_to_be_allocated++;
        this.siblings = new HashSet<>();
    }

    public FamilyOLDVersion(Birth child) {

        this();
        siblings.add(child);
        initParents(child);
    }

    public String getPlaceOfMarriage() {
        return place_of_marriage;
    }

    public String getDayOfMarriage() {
        return day_of_marriage;
    }

    public String getMonthOfMarriage() {
        return month_of_marriage;
    }

    public String getYearOfMarriage() {
        return year_of_marriage;
    }

    public String getMothersMaidenSurname() {
        return mothers_maiden_surname;
    }

    public String getMothersForename() {
        return mothers_forenname;
    }

    public String getFathersSurname() {
        return fathers_surname;
    }

    public String getFathersForename() {
        return fathers_forename;
    }

    public String getMothersSurnameClean() {
        return mothers_surname_clean;
    }

    public String getMothersForenameClean() {
        return mothers_forenname_clean;
    }

    public String getFathersSurnameClean() {
        return fathers_surname_clean;
    }

    public String getFathersForenameClean() {
        return fathers_forename_clean;
    }

    public Set<Birth> getSiblings() {
        return siblings;
    }

    public void addSibling(Birth sibling) {
        siblings.add(sibling);
    }

    protected void initParents(Birth child) {

        this.place_of_marriage = child.getPlaceOfMarriage();
        this.day_of_marriage = child.getString(Birth.PARENTS_DAY_OF_MARRIAGE);
        this.month_of_marriage = child.getString(Birth.PARENTS_MONTH_OF_MARRIAGE);
        this.year_of_marriage = child.getString(Birth.PARENTS_YEAR_OF_MARRIAGE);

        this.mothers_maiden_surname = child.getMothersMaidenSurname();
        this.mothers_forenname = child.getMothersForename();
        this.fathers_surname = child.getFathersSurname();
        this.fathers_forename = child.getFathersForename();

        this.mothers_surname_clean = child.getMothersSurnameClean();
        this.mothers_forenname_clean = child.getMothersForenameClean();
        this.fathers_surname_clean = child.getFathersSurnameClean();
        this.fathers_forename_clean = child.getFathersForenameClean();

    }
}
