#!/bin/bash
#
# This script computes the amount of method Javadoc errors found by
# checkstyle and compares with the master branch. If the amount of
# errors have increased relative to master, it exits non-zero.
#
# WARNING: Running this script resets the state of the repository to the
# latest commit, do NOT run this locally if you have any uncommitted changes.

set -o errexit
set -o nounset
set -o pipefail

COMPARE_BRANCH="master"
JAVADOC_CHECKSTYLE_CONFIG="__SPOON_CI_checkstyle-javadoc.xml"

COMPARE_WITH_MASTER_ARG="COMPARE_WITH_MASTER"

if [[ $(git branch --show-current) == "$COMPARE_BRANCH" ]]; then
    # nothing to compare, we're on the main branch
    exit 0
fi

function cleanup() {
    rm -f "$JAVADOC_CHECKSTYLE_CONFIG"
}

trap cleanup EXIT

function create_checkstyle_config() {
    echo '<?xml version="1.0"?>
<!DOCTYPE module PUBLIC "-//Puppy Crawl//DTD Check Configuration 1.2//EN" "http://www.puppycrawl.com/dtds/configuration_1_2.dtd">
<module name="Checker">
  <module name="TreeWalker">
    <module name="JavadocMethod">
      <property name="scope" value="public"/>
    </module>
  </module>
</module>' > "$JAVADOC_CHECKSTYLE_CONFIG"
}

function run_checkstyle() {
    create_checkstyle_config
    mvn -B checkstyle:check --fail-never -Dcheckstyle.config.location="$JAVADOC_CHECKSTYLE_CONFIG"
}

function compute_num_errors() {
     grep -Po '(?<=There are )\d+(?= errors reported by Checkstyle)' <<< `run_checkstyle`
}

function main() {
    cd "$(git rev-parse --show-toplevel)"

    # compute compare score
    git checkout --force "$COMPARE_BRANCH" &> /dev/null
    compare_num_errors=`compute_num_errors`

    # compute current score
    git checkout --force - &> /dev/null
    current_num_errors=`compute_num_errors`

    echo "JAVADOC QUALITY SCORE (lower is better)
    Compare: $compare_num_errors
    Current: $current_num_errors
    "

    if [[ -z $compare_num_errors ]]; then
        echo "Failed to compute compare score";
        exit 1;
    elif [[ -z $current_num_errors ]]; then
        echo "Failed to compute current score";
        exit 1;
    elif [[ $compare_num_errors < $current_num_errors ]]; then
        echo "Javadoc quality has deteriorated!"
        exit 1
    else
        echo "Javadoc quality has not deteriorated"
        echo "Run the chore/ci-checkstyle-javadoc.sh script locally to find errors"
    fi
}

function usage_and_exit() {
    echo "usage: ci-checkstyle-javadoc.sh [<regex>|$COMPARE_WITH_MASTER_ARG]

    <regex>: A regex to filter output lines by (typically just a file path)
    $COMPARE_WITH_MASTER_ARG: Compare the amount of errors on the current
        branch with those on master and exit non-zero if the current branch has
        more errors. WARNING: Never run this with uncommitted changes, they
        will be lost!

To list all errors in a particular .java file, just run with '/<CLASS_NAME>.java' as the argument. For example:

    \$ ./chore/ci-checkstyle-javadoc.sh '/Launcher.java'"
    exit 1
}

if [[ "$#" != 1 ]]; then
    usage_and_exit
fi

if [[ "$1" == "$COMPARE_WITH_MASTER_ARG" ]]; then
    main
else
    grep "$1" <<< `run_checkstyle`
fi
