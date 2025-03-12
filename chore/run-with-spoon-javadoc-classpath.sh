#!/usr/bin/env bash

# If anything fails we are done for
set -eu

# Our dependencies
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
export CLASSPATH="$(cd "$SCRIPT_DIR/../spoon-javadoc" && mvn dependency:build-classpath -Dmdep.outputFile="/dev/stderr" 2>&1 > /dev/null)"

# Ourselves
LOCAL_REPO="$(mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)"
SPOON_VERSION="$(cd "$SCRIPT_DIR/../spoon-javadoc" && mvn help:evaluate -Dexpression=project.version -q -DforceStdout)"
export CLASSPATH="$CLASSPATH:$LOCAL_REPO/fr/inria/gforge/spoon/spoon-javadoc/$SPOON_VERSION/spoon-javadoc-$SPOON_VERSION.jar"

# A simple logger to disable the warning
mvn dependency:get -Dartifact=org.slf4j:slf4j-nop:1.7.36
export CLASSPATH="$CLASSPATH:$LOCAL_REPO/org/slf4j/slf4j-nop/1.7.36/slf4j-nop-1.7.36.jar"

# Delegate
java "$@"
