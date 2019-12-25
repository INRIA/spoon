#!/bin/bash

export LEAFACTOR_VERSION=1
export ANDROID_SDK_DIR=/usr/local/android-sdk

echo 'PREPARING LEAFACTOR JAR'
cd leafactor-ci
echo "version=1.0.${LEAFACTOR_VERSION}" > gradle.properties
echo "group=com.leafactorci" >> gradle.properties
echo "sourceCompatibility=7" >> gradle.properties
echo "targetCompatibility=7" >> gradle.properties
echo 'PRINTING gradle.properties'
cat gradle.properties
./gradlew :jar

echo 'REFACTORING SAMPLE'
cd ..
cd sample

ls ../../leafactor-ci/build/libs/
echo "sdk.dir=${ANDROID_SDK_DIR}" > local.properties
echo "leafactor.dir=../../leafactor-ci/build/libs/leafactor-${LEAFACTOR_VERSION}.jar" >> local.properties
echo 'PRINTING local.properties'
cat local.properties
./gradlew app:build
./gradlew app:refactor



