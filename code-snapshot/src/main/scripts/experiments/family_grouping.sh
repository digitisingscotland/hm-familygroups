#!/usr/bin/env bash
#
# Copyright 2017 Systems Research Group, University of St Andrews:
# <https://github.com/stacs-srg>
#
# This file is part of the module linkage-java.
#
# linkage-java is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
# License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
# version.
#
# linkage-java is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
# warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along with linkage-java. If not, see
# <http://www.gnu.org/licenses/>.
#

# run this from the repository home

set -o errexit
set -o nounset

########## COMPILE ####################

mvn compile


########## IMPORT #####################

# digitisingscotland/hub repository is assumed to be at ~/repos/github/stacs-srg/digitisingscotland-hub
# and the Skye and Kilmarnock datasets are assumed to be unzipped there
#bash src/main/scripts/import_skye.sh ${HOME}/storr_files skye ~/repos/github/stacs-srg/digitisingscotland-hub/data/Skye
#bash src/main/scripts/import_kilmarnock.sh ${HOME}/storr_files kilmarnock ~/repos/github/stacs-srg/digitisingscotland-hub/data/Kilmarnock


########## RUN ########################

export MVN_ARGS="-q -Dexec.cleanupDaemonThreads=false"
export MVN_ARGS="$MVN_ARGS -Dexec.mainClass=uk.ac.standrews.cs.digitising_scotland.linkage.experiments.SiblingBundling -e"

export DIR_OUTPUTS="experiment_results"

mkdir -p ${DIR_OUTPUTS}

# become root
# mkdir /data/digitising_scotland_outputs
# chown -R secure:secure /data/digitising_scotland_outputs

# do the same with /data/digitising_scotland_tmp
# and set the tmpdir to this directory. otherwise we risk running out of disk space with /tmp
#export _JAVA_OPTIONS=-Djava.io.tmpdir=${DIR_TMP}

function run {
    store_path=$1
    repo_name=$2
    family_forming_threshold=$3
    family_merging_threshold=$4
    maximum_family_size=$5
    source_of_siblings=$6
    source_of_families=$7
    family_distance_method=$8

    if [ -f ${DIR_OUTPUTS}/$2-$3-$4-$5-$6-$7-$8.stdout ]; then
        echo "Skipping."
    else
        mvn exec:java ${MVN_ARGS} -Dexec.args="$1 $2 $3 $4 $5 $6 $7 $8" > ${DIR_OUTPUTS}/$2-$3-$4-$5-$6-$7-$8.stdout
    fi
}

export -f run

# set the number of cores by setting the environment variable, e.g. PARALLEL=-j12
#parallel --no-notice --joblog ${DIR_OUTPUTS}/joblog \
#    --eta \
#    run \
#    ::: ${HOME}/storr_files \
#    ::: skye kilmarnock \
#    ::: $(seq -f "%03g" 0 10) \
#    ::: $(seq -f "%03g" 0 10) \
#    ::: $(seq -f "%03g" 4 4 20) \
#    ::: Birth Death \
#    ::: Birth Death Marriage

parallel --no-notice --joblog ${DIR_OUTPUTS}/joblog \
    --eta \
    run \
    ::: ${HOME}/storr_files \
    ::: skye kilmarnock \
    ::: 002 005 010 \
    ::: 002 005 010 \
    ::: 008 020 \
    ::: Birth \
    ::: Birth Death Marriage \
    ::: Closest Furthest Mean

########## CALCULATE METRICS ########################

parallel "src/main/scripts/experiments/family_grouping/metrics.py {} > {.}.metrics" \
    ::: experiment_results/*.stdout

parallel "src/main/scripts/experiments/family_grouping/metrics-pairs.py {1} {2} {1.}---{2/.}.metrics2" \
    ::: experiment_results/kilmarnock*.stdout \
    ::: experiment_results/kilmarnock*.stdout

parallel "src/main/scripts/experiments/family_grouping/metrics-pairs.py {1} {2} {1.}---{2/.}.metrics2" \
    ::: experiment_results/skye*.stdout \
    ::: experiment_results/skye*.stdout
