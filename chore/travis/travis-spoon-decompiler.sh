#!/bin/bash

# CI for the spoon-decompiler module

# fails if anything fails
set -e

source /opt/jdk_switcher/jdk_switcher.sh

jdk_switcher use oraclejdk9

cd spoon-decompiler

mvn test

mvn verify license:check site javadoc:jar install -DskipTests -DadditionalJOption=-Xdoclint:none

# checkstyle in src/tests
mvn  checkstyle:checkstyle -Pcheckstyle-test
