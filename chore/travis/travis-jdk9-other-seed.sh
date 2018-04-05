#!/bin/bash

# This script intends to be run on TravisCI
# It executes compile and test goals
if [ "$TRAVIS_PULL_REQUEST" = "false" ]; then
    source /opt/jdk_switcher/jdk_switcher.sh
    jdk_switcher use oraclejdk9 && SPOON_SEED_CU_COMPARATOR=$(( ( RANDOM % 10 )  + 1 )) mvn -Djava.src.version=1.9 test
fi