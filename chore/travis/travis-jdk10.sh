#!/bin/bash

# This script intends to be run on TravisCI
# It executes compile and test goals

source /opt/jdk_switcher/jdk_switcher.sh
wget https://raw.githubusercontent.com/sormuras/bach/master/install-jdk.sh
chmod +x install-jdk.sh

export JAVA_HOME=$HOME/openjdk8
./install-jdk.sh --install openjdk10 --target $JAVA_HOME

jdk_switcher use openjdk10 && mvn -Djava.src.version=1.10 test