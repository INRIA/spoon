#!/bin/bash

# This script intends to be run on TravisCI
# it runs verify and site maven goals
# and to check documentation links
#
# It also run test, verify and checkstyle goals on spoon-decompiler

# fails if anything fails
set -e

source /opt/jdk_switcher/jdk_switcher.sh

pip install --user CommonMark==0.7.5 requests pygithub

jdk_switcher use oraclejdk9

# javadoc check is included in goal "site"
# it's better to have the doclint here because the pom.xml config of javadoc is a nightmare
mvn -Djava.src.version=1.9 verify license:check site install -DskipTests  -DadditionalJOption=-Xdoclint:syntax,-missing

# checkstyle in src/tests
mvn  checkstyle:checkstyle -Pcheckstyle-test

python ./chore/check-links-in-doc.py

# Spoon-decompiler
cd spoon-decompiler

mvn test

mvn verify license:check javadoc:jar install -DskipTests

# checkstyle in src/tests
mvn  checkstyle:checkstyle -Pcheckstyle-test

# Spoon-control-flow
cd ../spoon-control-flow

mvn test

mvn verify license:check javadoc:jar install -DskipTests

# checkstyle in src/tests
mvn  checkstyle:checkstyle -Pcheckstyle-test

# Spoon-dataflow
cd ../spoon-dataflow

# download and install z3 lib
wget https://github.com/Z3Prover/z3/releases/download/z3-4.8.4/z3-4.8.4.d6df51951f4c-x64-ubuntu-14.04.zip
unzip z3-4.8.4.d6df51951f4c-x64-ubuntu-14.04.zip
export LD_LIBRARY_PATH=./z3-4.8.4.d6df51951f4c-x64-ubuntu-14.04/bin

# build and run tests
./gradlew build
