#!/usr/bin/env bash
set -euo pipefail

CURRENT_VERSION_WITH_SNAPSHOT="$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)"

if [[ ! $CURRENT_VERSION_WITH_SNAPSHOT =~ .*-SNAPSHOT ]]; then
  echo "Not a snapshot version, skipping"
  exit 78
fi

echo "::group::Staging release"
mvn -f spoon-pom --no-transfer-progress --batch-mode -Pjreleaser clean deploy -DaltDeploymentRepository=local::default::file:./target/staging-deploy
mvn --no-transfer-progress --batch-mode -Pjreleaser deploy:deploy-file -Dfile="./spoon-pom/pom.xml" -DpomFile="./spoon-pom/pom.xml" -Durl="file://$(mvn help:evaluate -D"expression=project.basedir" -q -DforceStdout)/target/staging-deploy"
echo "::endgroup::"

echo "::group::Running jreleaser"
JRELEASER_PROJECT_VERSION="$CURRENT_VERSION_WITH_SNAPSHOT" jreleaser-cli deploy
echo "::endgroup::"
