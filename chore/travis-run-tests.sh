#!/bin/bash

# This script intends to be run on TravisCI
# It executes compile and test goals

mvn -Djava.src.version=$JDK_VERSION test