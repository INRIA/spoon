#!/bin/bash

# This script intends to be run on TravisCI
# it runs verify and site maven goals
# and to check documentation links
#
# It also run test, verify and checkstyle goals on spoon-decompiler

# fails if anything fails
set -e

pip install --user CommonMark==0.7.5 requests pygithub

mvn -version

# javadoc check is included in goal "site"
# it's better to have the doclint here because the pom.xml config of javadoc is a nightmare
mvn -q -Djava.src.version=1.8 verify license:check site install -DskipTests  -DadditionalJOption=-Xdoclint:syntax,-missing

# checkstyle in src/tests
mvn -q  checkstyle:checkstyle -Pcheckstyle-test

python ./chore/check-links-in-doc.py

##################################################################
# Spoon-decompiler
##################################################################
cd spoon-decompiler

# always depends on the latest snapshot, just installed with "mvn install" above
mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
git diff

mvn -q test
mvn -q checkstyle:checkstyle license:check

##################################################################
# Spoon-control-flow
##################################################################
cd ../spoon-control-flow

# always depends on the latest snapshot, just installed with "mvn install" above
mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
git diff

mvn -q test
mvn -q checkstyle:checkstyle license:check

##################################################################
# Spoon-dataflow
##################################################################
cd ../spoon-dataflow

# download and install z3 lib
# the github URL is rate limited, and this results in flaky CI
# wget https://github.com/Z3Prover/z3/releases/download/z3-4.8.4/z3-4.8.4.d6df51951f4c-x64-ubuntu-14.04.zip
# so we have a copy on OW2
wget https://projects.ow2.org/download/spoon/WebHome/z3-4.8.4.d6df51951f4c-x64-ubuntu-14.04.zip

unzip z3-4.8.4.d6df51951f4c-x64-ubuntu-14.04.zip
export LD_LIBRARY_PATH=./z3-4.8.4.d6df51951f4c-x64-ubuntu-14.04/bin

# build and run tests
./gradlew build


##################################################################
# Spoon-visualisation
##################################################################
cd ../spoon-visualisation

# always depends on the latest snapshot, just installed with "mvn install" above
mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
git diff

mvn -q -Djava.src.version=11 test

##################################################################
## Trigerring extra tasks that we don't want to commit to master
## (For experimental CI features, short lived tasks, etc)

if [[ "$TRAVIS_REPO_SLUG" == "INRIA/spoon" ]] && [[ "$TRAVIS_PULL_REQUEST" != "false" ]]
then
  echo "downloading extra CI PR script from SpoonLabs/spoon-ci-external"
  curl https://raw.githubusercontent.com/SpoonLabs/spoon-ci-external/master/spoon-pull-request.sh | bash
fi
