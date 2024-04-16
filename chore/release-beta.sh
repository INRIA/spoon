#!/usr/bin/env bash
set -euo pipefail

CURRENT_VERSION="$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout | sed 's/-SNAPSHOT//')"
CURRENT_VERSION_WITH_SNAPSHOT="$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)"

if [[ ! $CURRENT_VERSION_WITH_SNAPSHOT =~ .*-SNAPSHOT ]]; then
  echo "Not a snapshot version, skipping"
  exit 78
fi

# Compute next beta number
echo "::group::Computing next beta number"
LAST_BETA_NUMBER="$(curl -L "http://search.maven.org/solrsearch/select?q=a:spoon-core+g:fr.inria.gforge.spoon&rows=40&wt=json&core=gav" | jq -r ".response.docs | map(.v) | map((match(\"$CURRENT_VERSION-beta-(.*)\") | .captures[0].string) // \"0\") | .[0]")"
echo "LAST_BETA_NUMBER $LAST_BETA_NUMBER"

NEW_BETA_NUMBER=$((LAST_BETA_NUMBER + 1))
echo "NEW_BETA_NUMBER $NEW_BETA_NUMBER"
NEXT_VERSION="$CURRENT_VERSION-beta-$NEW_BETA_NUMBER"
echo "::endgroup::"

BRANCH_NAME="beta-release/$NEXT_VERSION"

echo "::group::Setting beta-release version"
mvn -f spoon-pom --no-transfer-progress --batch-mode versions:set -DnewVersion="$NEXT_VERSION" -DprocessAllModules
mvn --no-transfer-progress --batch-mode versions:set -DnewVersion="$NEXT_VERSION" -DprocessAllModules
mvn -f spoon-javadoc --no-transfer-progress --batch-mode versions:set -DnewVersion="$NEXT_VERSION" -DprocessAllModules
echo "::endgroup::"

echo "::group::Commit & Push changes"
git checkout -b "$BRANCH_NAME"
git commit -am "release: Releasing version $NEXT_VERSION"
git push --set-upstream origin "$BRANCH_NAME"
echo "::endgroup::"

echo "::group::Staging beta-release"
mvn -f spoon-pom --no-transfer-progress --batch-mode -Pjreleaser clean deploy -DaltDeploymentRepository=local::default::file:./target/staging-deploy
mvn --no-transfer-progress --batch-mode -Pjreleaser deploy:deploy-file -Dfile="./spoon-pom/pom.xml" -DpomFile="./spoon-pom/pom.xml" -Durl="file://$(mvn help:evaluate -D"expression=project.basedir" -q -DforceStdout)/target/staging-deploy"
echo "::endgroup::"

echo "::group::Running jreleaser"
JRELEASER_PROJECT_VERSION="$NEXT_VERSION" jreleaser-cli release
echo "::endgroup::"

# Set next version (patch of release version) with -SNAPSHOT suffix
NEXT_RELEASE_VERSION=$CURRENT_VERSION_WITH_SNAPSHOT

echo "::group::Updating poms to next target version"
mvn -f spoon-pom --no-transfer-progress --batch-mode versions:set -DnewVersion="$NEXT_RELEASE_VERSION" -DprocessAllModules
mvn --no-transfer-progress --batch-mode versions:set -DnewVersion="$NEXT_RELEASE_VERSION" -DprocessAllModules
mvn -f spoon-javadoc --no-transfer-progress --batch-mode versions:set -DnewVersion="$NEXT_RELEASE_VERSION" -DprocessAllModules
echo "::endgroup::"

echo "::group::Committing changes"
git commit -am "release: Setting SNAPSHOT version back to $NEXT_RELEASE_VERSION"
git push --set-upstream origin "$BRANCH_NAME"
echo "::endgroup::"

echo "::group::Merging into master (fast-forward)"
git checkout master
git merge --ff-only "$BRANCH_NAME"
git push origin master
echo "::endgroup::"
