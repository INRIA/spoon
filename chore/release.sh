#!/usr/bin/env bash
set -euo pipefail

if [ -z "$1" ]; then
  echo "Usage: $0 <patch|major|minor>"
  exit 1
fi

# Get current version from pom and remove snapshot if present.
CURRENT_VERSION="$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout | sed 's/-SNAPSHOT//')"
CURRENT_VERSION_WITH_SNAPSHOT="$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)"

# Calculate release version:
# - if `version` is patch, we just increment drop the `-SNAPSHOT` suffix
#   (e.g. 10.0.1-SNAPSHOT -> 10.0.1)
# - if `version` is minor or major, we increment the minor or major version and
#   set the patch version to `0` (e.g. 10.0.1-SNAPSHOT -> 11.0.0 or 10.1.0)
#
# As we are using a snapshot version, the first call to `semver next` slices
# off only the `-SNAPSHOT` suffix. We therefore run `semver next` on the
# version *without* the `-SNAPSHOT` prefix for major and minor bumps.
#
# After release, we run `semver next` once again and append the `-SNAPSHOT`
# suffix. This results in our patch version from above becoming
# `10.0.2-SNAPSHOT`. The major/minor just get the patch set to `1` and
# `-SNAPSHOT` appended.

if [[ "$1" == "patch" ]]; then
  NEXT_VERSION="$(semver next "$1" "$CURRENT_VERSION_WITH_SNAPSHOT")"
elif [[ "$1" == "minor" ]] || [[ "$1" == "major" ]]; then
  NEXT_VERSION="$(semver next "$1" "$CURRENT_VERSION")"
else
  echo "Unknown next version '$1'"
  exit 1
fi

BRANCH_NAME="release/$NEXT_VERSION"

echo "::group::Setting release version"
mvn -f spoon-pom --no-transfer-progress --batch-mode versions:set -DnewVersion="$NEXT_VERSION" -DprocessAllModules -DprocessParent=false
echo "::endgroup::"

echo "::group::Commit & Push changes"
git checkout -b "$BRANCH_NAME"
git commit -am "release: Releasing version $NEXT_VERSION"
git push --set-upstream origin "$BRANCH_NAME"
echo "::endgroup::"

echo "::group::Staging release"
mvn -f spoon-pom --no-transfer-progress --batch-mode -Pjreleaser clean deploy -DaltDeploymentRepository=local::default::file:./target/staging-deploy
mvn --no-transfer-progress --batch-mode -Pjreleaser deploy:deploy-file \
  -Dfile="./spoon-pom/pom.xml" \
  -DpomFile="./spoon-pom/pom.xml" \
  -Durl="file://$(mvn help:evaluate -D"expression=project.basedir" -q -DforceStdout)/target/staging-deploy"
echo "::endgroup::"

echo "::group::Next version"
mvn help:evaluate -Dexpression=project.version -q -DforceStdout | sed 's/-SNAPSHOT//'
echo "::endgroup::"

echo "::group::Releasing"
JRELEASER_PROJECT_VERSION="$NEXT_VERSION" jreleaser-cli full-release
echo "::endgroup::"

# Set next version (patch of release version) with -SNAPSHOT suffix
NEXT_RELEASE_VERSION="$(semver next patch "$NEXT_VERSION")-SNAPSHOT"

echo "::group::Updating poms to next target version"
mvn -f spoon-pom --no-transfer-progress --batch-mode versions:set -DnewVersion="$NEXT_RELEASE_VERSION" -DprocessAllModules -DprocessParent=false
echo "::endgroup::"

echo "::group::Committing changes"
git commit -am "release: Setting SNAPSHOT version $NEXT_RELEASE_VERSION"
git push --set-upstream origin "$BRANCH_NAME"
echo "::endgroup::"

echo "::group::Merging into master (fast-forward)"
git checkout master
git merge --ff-only "$BRANCH_NAME"
git push origin master
echo "::endgroup::"
