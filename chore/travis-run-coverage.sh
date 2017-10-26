#!/bin/bash

# This script intends to be run on TravisCI
# It only computes and publish (through coveralls) the coverage of Spoon

mvn -Djava.src.version=$JDK_VERSION test jacoco:report coveralls:report -Pcoveralls --fail-never