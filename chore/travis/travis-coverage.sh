#!/bin/bash

# This script computes and publish (through coveralls) the coverage of Spoon
# It is meant to be run on TravisCI
#
# Note thet Coveralls/Jacoco does not work on JDK9, see https://github.com/trautonen/coveralls-maven-plugin/issues/112
set -e

wget https://raw.githubusercontent.com/sormuras/bach/master/install-jdk.sh
chmod +x install-jdk.sh

source ./install-jdk.sh -f 8 -c
mvn -Pcoveralls test jacoco:report coveralls:report --fail-never
