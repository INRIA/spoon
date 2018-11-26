#!/bin/bash

# This script computes and publish (through coveralls) the coverage of Spoon
# It is meant to be run on TravisCI
#
# Note thet Coveralls/Jacoco does not work on JDK9, see https://github.com/trautonen/coveralls-maven-plugin/issues/112

source /opt/jdk_switcher/jdk_switcher.sh

jdk_switcher use oraclejdk8 && mvn -Pcoveralls test jacoco:report coveralls:report --fail-never
