#!/bin/bash

# This script intends to be run in continuous integration.
# it runs verify and site maven goals
# and to check documentation links
#
# It also run test, verify, checkstyle, and depclean goals on spoon-decompiler

# fails if anything fails
set -e

pip install --user CommonMark==0.7.5 requests pygithub

mvn -version

# verify includes checkstyle, , outputing the errors in the log of CI
# javadoc check is included in goal "site"
# it's better to have the doclint here because the pom.xml config of javadoc is a nightmare
mvn verify license:check site install -DskipTests  -DadditionalJOption=-Xdoclint:syntax,-missing -Dscan

# checkstyle in src/tests
mvn -q  checkstyle:checkstyle -Pcheckstyle-test

python ./chore/check-links-in-doc.py

# Analyze the usage of dependencies through DepClean in spoon-core.
# The build fails if DepClean detects at least one unused direct dependency.
mvn -q depclean:depclean

##################################################################
# Spoon-decompiler
##################################################################
cd spoon-decompiler

# always depends on the latest snapshot, just installed with "mvn install" above
mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
git diff

mvn -q test
mvn -q checkstyle:checkstyle license:check
mvn -q depclean:depclean

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
mvn -q depclean:depclean

##################################################################
# Spoon-smpl
##################################################################
cd ../spoon-smpl

# always depends on the latest snapshot, just installed with "mvn install" above
mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
git diff

mvn -q -Djava.src.version=11 test
mvn -q checkstyle:checkstyle license:check
mvn -q depclean:depclean

##################################################################
## Trigerring extra tasks that we don't want to commit to master
## (For experimental CI features, short lived tasks, etc)

if [[ "$GITHUB_REPOSITORY" == "INRIA/spoon" ]] && [[ "$GITHUB_EVENT_NAME" == "pull_request" ]]
then
  echo "downloading extra CI PR script from SpoonLabs/spoon-ci-external"
  curl https://raw.githubusercontent.com/SpoonLabs/spoon-ci-external/master/spoon-pull-request.sh | bash
fi
