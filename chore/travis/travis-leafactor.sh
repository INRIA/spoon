#!/bin/bash

export LEAFACTOR_VERSION=1.0.1
export ANDROID_SDK_DIR=/usr/local/android-sdk

set -e

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/

mvn -version

wget archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
unzip -qq apache-maven-3.3.9-bin.zip
export M2_HOME=$PWD/apache-maven-3.3.9

$M2_HOME/bin/mvn --version
$M2_HOME/bin/mvn clean install

# Build spoon-core
mvn jar:jar

echo 'PREPARING LEAFACTOR JAR'
cd leafactor-ci
echo "version=${LEAFACTOR_VERSION}" > gradle.properties
echo "group=com.leafactorci" >> gradle.properties
echo "sourceCompatibility=7" >> gradle.properties
echo "targetCompatibility=7" >> gradle.properties
echo 'PRINTING gradle.properties'
cat gradle.properties
./gradlew :jar

echo 'REFACTORING SAMPLE'
cd ..
cd sample

ls ../leafactor-ci -R
echo "sdk.dir=${ANDROID_SDK_DIR}" > local.properties
echo "leafactor.dir=../../leafactor-ci/build/libs/leafactor-${LEAFACTOR_VERSION}.jar" >> local.properties
echo 'PRINTING local.properties'
cat local.properties
./gradlew app:build
ls -R
./gradlew app:refactor
cd ..


diff ./sample/app/build/leafactor-ci/release/anupam/acrylic/EasyPaint.java ./chore/test/EasyPaintExpected.java



