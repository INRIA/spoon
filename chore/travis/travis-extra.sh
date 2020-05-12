#!/bin/bash

# This script intends to be run on TravisCI
# it runs verify and site maven goals
# and to check documentation links
#
# It also run test, verify and checkstyle goals on spoon-decompiler

# fails if anything fails
set -e

pip install --user CommonMark==0.7.5 requests pygithub

# we need Java 8 for the javadoc below
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/

mvn -version

# javadoc check is included in goal "site"
# it's better to have the doclint here because the pom.xml config of javadoc is a nightmare
mvn -q -Djava.src.version=1.8 verify license:check site install -DskipTests  -DadditionalJOption=-Xdoclint:syntax,-missing

# checkstyle in src/tests
mvn -q  checkstyle:checkstyle -Pcheckstyle-test

python ./chore/check-links-in-doc.py

# compute the revapi API compatibility report
mvn -U revapi:report
cat ./target/revapi_report.md;
if grep -q "changes.*: [123456789]" ./target/revapi_report.md;
then
  # post the revapi report as PR comment
  # the CI job has the spoon-bot password to be able to post the revapi report as comment on the pull request
  # source of the CI job: https://github.com/SpoonLabs/spoon-ci-config/blob/master/jenkins/jobs/push-comment-github.xml
  curl -F file0=@./target/revapi_report.md -F json='{"parameter": [{"name":"revapi_report.md", "file":"file0"},{"name":"pull_request","value":"'$TRAVIS_PULL_REQUEST'"}]}' -v "https://ci.inria.fr/sos/job/push-comment-github/build?token=hgzer87954"
fi

##################################################################
# Spoon-decompiler
##################################################################
cd spoon-decompiler

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/

# always depends on the latest snapshot, just installed with "mvn install" above
mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
git diff

mvn -q test
mvn -q checkstyle:checkstyle license:check

##################################################################
# Spoon-control-flow
##################################################################
cd ../spoon-control-flow

export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/

# always depends on the latest snapshot, just installed with "mvn install" above
mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
git diff

mvn -q test
mvn -q checkstyle:checkstyle license:check

##################################################################
# Spoon-dataflow
##################################################################
cd ../spoon-dataflow

export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/

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

export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/

wget https://raw.githubusercontent.com/sormuras/bach/master/install-jdk.sh
chmod +x install-jdk.sh

export JAVA_HOME=$HOME/openjdk8
source ./install-jdk.sh -f 11 -c

# always depends on the latest snapshot, just installed with "mvn install" above
mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
git diff

mvn -q -Djava.src.version=11 test
