#!/bin/bash

# This script intends to be run on TravisCI only for commits on master branch
# It compiles Spoon and launches tests with a seed for setting the order of compilation units
# The purpose of this script is to check that all tests are passing even when the compilation units are not sorted in the same order

# Uncomment after Travis passes
if [ "$TRAVIS_PULL_REQUEST" = "false" ]; then
    source /opt/jdk_switcher/jdk_switcher.sh
    jdk_switcher use oraclejdk8 && SPOON_SEED_CU_COMPARATOR=1 mvn -Djava.src.version=1.8 test
fi