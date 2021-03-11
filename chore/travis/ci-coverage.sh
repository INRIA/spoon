#!/bin/bash

# This script computes and publish (through coveralls) the coverage of Spoon
# It is meant to be run on TravisCI
#
set -e

mvn -Pcoveralls test jacoco:report coveralls:report --fail-never
