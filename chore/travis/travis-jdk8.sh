#!/bin/bash
set -e

sudo apt-get -y update
sudo apt-get -y install openjdk-8-jdk openjdk-8-jre libatk-wrapper-java-jni libpulse0
ls /usr/lib/jvm/
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/


mvn -version

wget archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
unzip -qq apache-maven-3.3.9-bin.zip
export M2_HOME=$PWD/apache-maven-3.3.9

$M2_HOME/bin/mvn --version
$M2_HOME/bin/mvn clean test
