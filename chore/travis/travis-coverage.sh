#!/bin/bash

# This script intends to be run on TravisCI
# It only computes and publish (through coveralls) the coverage of Spoon


source /opt/jdk_switcher/jdk_switcher.sh

jdk_switcher use oraclejdk9 && mvn -Djava.src.version=1.9 test jacoco:report && mvn -Djava.src.version=1.9 coveralls:report -Pcoveralls --fail-never