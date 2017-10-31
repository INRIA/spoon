#!/bin/bash

# This script intends to be run on TravisCI
# It only computes and publish (through coveralls) the coverage of Spoon


source /opt/jdk_switcher/jdk_switcher.sh

jdk_switcher use oraclejdk8 && mvn -Djava.src.version=1.8 test jacoco:report && mvn -Djava.src.version=1.8 coveralls:report -Pcoveralls --fail-never