#!/bin/bash
set -e

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/

mvn -version

wget archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
unzip -qq apache-maven-3.3.9-bin.zip
export M2_HOME=$PWD/apache-maven-3.3.9

$M2_HOME/bin/mvn --version
#$M2_HOME/bin/mvn clean install

export LEAFACTOR_VERSION=1
export ANDROID_SDK_DIR=/usr/local/android-sdk

cd leafactor-ci
echo "version=1.0.${LEAFACTOR_VERSION}" > gradle.properties
echo "group=com.leafactorci" >> gradle.properties
echo "sourceCompatibility=7" >> gradle.properties
echo "targetCompatibility=7" >> gradle.properties
./gradlew :jar

cd ..
cd sample
echo "sdk.dir=${ANDROID_SDK_DIR}" > gradle.properties
echo "leafactor.dir=../../leafactor-ci/build/libs/leafactor-${LEAFACTOR_VERSION}.jar" >> gradle.properties
./gradlew app:build
./gradlew app:refactor



