#!/bin/bash

# This script intends to be run on TravisCI
# it runs verify and site maven goals
# and to check documentation links

# fails if anything fails
set -e

source /opt/jdk_switcher/jdk_switcher.sh

pip install --user CommonMark==0.7.5 requests pygithub

jdk_switcher use oraclejdk9

mvn -Djava.src.version=1.9 verify license:check site javadoc:jar install -DskipTests -DadditionalJOption=-Xdoclint:none

# checkstyle in src/tests
mvn  checkstyle:checkstyle -Pcheckstyle-test

python ./chore/check-links-in-doc.py

#Spoon-decompiler
cd spoon-decompiler

mvn test

mvn verify license:check site javadoc:jar install -DskipTests -DadditionalJOption=-Xdoclint:none

# checkstyle in src/tests
mvn  checkstyle:checkstyle -Pcheckstyle-test

cd ..

# Maven 3.3.9
wget archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
unzip -qq apache-maven-3.3.9-bin.zip
export M2_HOME=$PWD/apache-maven-3.3.9

$M2_HOME/bin/mvn --version
$M2_HOME/bin/mvn clean install -DskipTests=true