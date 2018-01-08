#!/bin/bash
set -e

# This script intends to check if Spoon can be compiled
# using Maven 3.3.9

source /opt/jdk_switcher/jdk_switcher.sh

jdk_switcher use oraclejdk8
wget archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
unzip -qq apache-maven-3.3.9-bin.zip
export M2_HOME=$PWD/apache-maven-3.3.9

$M2_HOME/bin/mvn --version
$M2_HOME/bin/mvn clean install -DskipTests=true