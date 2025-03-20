#!/usr/bin/env bash
set -euo pipefail

# Version used to create the beta release
OLD_VERSION="$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout | sed 's/-SNAPSHOT//')"
# Version that will be reset back to after the beta release
OLD_VERSION_WITH_SNAPSHOT="$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)"

if [[ ! $OLD_VERSION_WITH_SNAPSHOT =~ .*-SNAPSHOT ]]; then
  echo "Not a snapshot version, skipping"
  exit 78
fi

# Compute next beta number
echo "::group::Computing next beta number"
LAST_BETA_NUMBER="$(curl -L "http://search.maven.org/solrsearch/select?q=a:spoon-core+g:fr.inria.gforge.spoon&rows=40&wt=json&core=gav" | jq -r ".response.docs | map(.v) | map((match(\"$OLD_VERSION-beta-(.*)\") | .captures[0].string) // \"0\") | .[0]")"
echo "LAST_BETA_NUMBER $LAST_BETA_NUMBER"

NEW_BETA_NUMBER=$((LAST_BETA_NUMBER + 1))
echo "NEW_BETA_NUMBER $NEW_BETA_NUMBER"
NEXT_BETA_VERSION="$OLD_VERSION-beta-$NEW_BETA_NUMBER"
echo "::endgroup::"

BRANCH_NAME="beta-release/$NEXT_BETA_VERSION"

echo "::group::Setting beta-release version"
mvn -f spoon-pom --no-transfer-progress --batch-mode versions:set -DnewVersion="$NEXT_BETA_VERSION" -DprocessAllModules -DprocessParent=false
echo "::endgroup::"

echo "::group::Commit & Push changes"
git checkout -b "$BRANCH_NAME"
git commit -am "release: Releasing version $NEXT_BETA_VERSION"
git push --set-upstream origin "$BRANCH_NAME"
echo "::endgroup::"

echo "::group::Staging beta-release"
mvn -f spoon-pom --no-transfer-progress --batch-mode -Pjreleaser clean deploy -DaltDeploymentRepository=local::default::file:./target/staging-deploy
mvn --no-transfer-progress --batch-mode -Pjreleaser deploy:deploy-file -Dfile="./spoon-pom/pom.xml" -DpomFile="./spoon-pom/pom.xml" -Durl="file://$(mvn help:evaluate -D"expression=project.basedir" -q -DforceStdout)/target/staging-deploy"
echo "::endgroup::"

echo "::group::Running jreleaser"
JRELEASER_PROJECT_VERSION="$NEXT_BETA_VERSION" jreleaser-cli release
echo "::endgroup::"

echo "::group::Reverting to old SNAPSHOT version"
git revert --no-commit HEAD
git commit -m "release: Reverting to SNAPSHOT version $OLD_VERSION_WITH_SNAPSHOT"
git push origin "$BRANCH_NAME"
echo "::endgroup::"

echo "::group::Merging into master (fast-forward)"
git checkout master
git merge --ff-only "$BRANCH_NAME"
git push origin master
git push origin --delete "$BRANCH_NAME"
echo "::endgroup::"
