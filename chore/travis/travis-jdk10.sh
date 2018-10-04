#!/bin/bash

# This script intends to be run on TravisCI
# It executes compile and test goals

source /opt/jdk_switcher/jdk_switcher.sh
wget https://raw.githubusercontent.com/sormuras/bach/master/install-jdk.sh
chmod +x install-jdk.sh

export JAVA_HOME=$HOME/openjdk8

# Use the default (the one present in the base container) collection of trusted certificate authority (CA) certificates for java
source ./install-jdk.sh -f 10 -c

mvn -Djava.src.version=1.10 test