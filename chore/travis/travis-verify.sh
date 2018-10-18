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
