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

# we compute and save some data on https://github.com/SpoonLabs/spoon-ci-data
curl -s -X POST -H "Content-Type: application/json" -H "Accept: application/json" -H "Travis-API-Version: 3" -H "Authorization: token $TRAVIS_TOKEN" https://api.travis-ci.com/repo/SpoonLabs%2Fspoon-ci-external/requests

