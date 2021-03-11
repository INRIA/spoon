#!/bin/bash

# This script analyzes the usage of dependencies (through DepClean) in Spoon.
# The build fails if there are unused direct dependencies.
# It is meant to be run on TravisCI
#
set -e

mvn package -DskipTests -Pdepclean