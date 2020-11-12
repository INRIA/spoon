#!/bin/bash
set -e

wget https://raw.githubusercontent.com/sormuras/bach/master/install-jdk.sh
chmod +x install-jdk.sh
source ./install-jdk.sh -f 8 -c

mvn -version

wget archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
unzip -qq apache-maven-3.3.9-bin.zip
export M2_HOME=$PWD/apache-maven-3.3.9

$M2_HOME/bin/mvn --version
$M2_HOME/bin/mvn clean test
