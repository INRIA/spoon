#!/bin/bash
# script run by Travis CI only for branch master of INRIA/spoon

set -e

if [ "$TRAVIS_REPO_SLUG" != "INRIA/spoon" ]; then
  exit
fi
if [ "$TRAVIS_BRANCH" != "master" ]; then
  exit
fi

echo 

echo "$0 executed!"

# we will add tasks here in the future
true
