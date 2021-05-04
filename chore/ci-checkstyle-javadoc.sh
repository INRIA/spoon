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
JAVADOC_CHECKSTYLE_CONFIG="checkstyle-javadoc.xml"

if [[ $(git branch --show-current) == "$COMPARE_BRANCH" ]]; then
    # nothing to compare, we're on the main branch
    exit 0
fi

function compute_num_errors() {
    mvn -B --fail-never checkstyle:check -Dcheckstyle.config.location="$JAVADOC_CHECKSTYLE_CONFIG" \
        | grep -Po '(?<=There are )\d+(?= errors reported by Checkstyle)'
}

# compute compare score
cd "$(git rev-parse --show-toplevel)"
git checkout --force "$COMPARE_BRANCH" &> /dev/null
compare_num_errors=`compute_num_errors`

# compute current score
git checkout - &> /dev/null
current_num_errors=`compute_num_errors`

echo "JAVADOC QUALITY SCORE (lower is better)
Compare: $compare_num_errors
Current: $current_num_errors
"

if [[ $compare_num_errors == 0 || $current_num_errors == 0 ]]; then
    echo "Unexpectedly low score, either script is no longer needed or something went wrong!"
    exit 1
elif [[ $compare_num_errors < $current_num_errors ]]; then
    echo "Javadoc quality has deteriorated!"
    exit 1
else
    echo "Javadoc quality has not deteriorated"
fi
