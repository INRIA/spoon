#!/bin/bash

# This script intends to be run on TravisCI
# It executes compile and test goals

source /opt/jdk_switcher/jdk_switcher.sh

jdk_switcher use oraclejdk9 && mvn -Djava.src.version=1.9 test