{
  description = "A small flake building maven projects";

  # Nixpkgs / NixOS version to use.
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = { self, nixpkgs }:
    let
      # Helper function to generate an attrset '{ x86_64-linux = f "x86_64-linux"; ... }'.
      forAllSystems = nixpkgs.lib.genAttrs nixpkgs.lib.systems.flakeExposed;
    in
    {
      # Provide some binary packages for selected system types.
      devShells = forAllSystems (system:
        let
          mkShell = javaVersion:
            let
              pkgs = import nixpkgs {
                inherit system;
                overlays = [
                  (final: prev: rec {
                    jdk = prev."jdk${toString javaVersion}";
                    gradle = prev.gradle.override { java = jdk; };
                    maven = prev.maven.override { inherit jdk; };
                  })
                ];
              };
            in
            pkgs.mkShell rec {
              test = pkgs.writeScriptBin "test" ''
                set -eu
                # Use silent log config
                cp chore/logback.xml src/test/resources/
                mvn -f spoon-pom -B test-compile

                # this is a hack to download the final test dependencies required to actually run the tests
                timeout 20 mvn -f spoon-pom -B test || echo "Done fetching dependencies"

                # Execute tests
                mvn -f spoon-pom test

                # print test results in log
                cat testResults.spoon
              '';
              coverage = pkgs.writeScriptBin "coverage" ''
                set -eu
                # Use silent log config
                cp chore/logback.xml src/test/resources/
                mvn -f spoon-pom -B test-compile
                mvn -f spoon-pom -Pcoveralls test jacoco:report coveralls:report -DrepoToken=$GITHUB_TOKEN -DserviceName=github -DpullRequest=$PR_NUMBER --fail-never
              '';
              extra = pkgs.writeScriptBin "extra" ''
                set -eu
                # Use silent log config
                cp chore/logback.xml src/test/resources/
                # Verify and Site Maven goals
                mvn verify license:check site -DskipTests -DadditionalJOption=-Xdoclint:syntax,-missing -Dscan
                # Install spoon-pom
                pushd spoon-pom || exit 1
                mvn install -DskipTests
                popd || exit 1

                # Checkstyle in src/tests
                mvn -q checkstyle:checkstyle -Pcheckstyle-test
                # Check documentation links
                python3 ./chore/check-links-in-doc.py
                # Analyze dependencies through DepClean in spoon-core
                mvn -q depclean:depclean

                pushd spoon-decompiler || exit 1
                mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
                mvn -q versions:update-parent -DallowSnapshots=true
                git diff
                mvn -q test
                mvn -q checkstyle:checkstyle license:check
                mvn -q depclean:depclean
                popd || exit 1

                pushd spoon-control-flow || exit 1
                mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
                mvn -q versions:update-parent -DallowSnapshots=true
                git diff
                mvn -q test
                mvn -q checkstyle:checkstyle license:check
                popd || exit 1

                # Requires z3
                pushd spoon-dataflow || exit 1
                ./gradlew build
                popd || exit 1

                pushd spoon-visualisation || exit 1
                mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
                mvn -q versions:update-parent -DallowSnapshots=true
                git diff
                mvn -q test
                mvn -q depclean:depclean
                popd || exit 1

                pushd spoon-smpl || exit 1
                mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
                mvn -q versions:update-parent -DallowSnapshots=true
                git diff
                mvn -q -Djava.src.version=11 test
                mvn -q checkstyle:checkstyle license:check
                mvn -q depclean:depclean
                popd || exit 1
              '';
              extraRemote = pkgs.writeScriptBin "extra-remote" ''
                curl https://raw.githubusercontent.com/SpoonLabs/spoon-ci-external/master/spoon-pull-request.sh | bash
              '';
              mavenPomQuality = pkgs.writeScriptBin "maven-pom-quality" ''
                # we dont enforce that the version must be non snapshot as this is not possible for SNAPSHOT versions in our workflow.
                mvn -f spoon-pom org.kordamp.maven:pomchecker-maven-plugin:1.9.0:check-maven-central -D"checker.release=false"
              '';
              reproducibleBuilds = pkgs.writeScriptBin "reproducible-builds" ''
                chore/check-reproducible-builds.sh
              '';
              javadocQuality = pkgs.writeScriptBin "javadoc-quality" ''
                ./chore/check-javadoc-regressions.py COMPARE_WITH_MASTER
              '';
              pythonEnv = with pkgs; python311.withPackages (ps: [
                ps.requests
                ps.pygithub
                ps.commonmark
              ]);
              packages = with pkgs;
                [ jdk maven gradle pythonEnv z3 test coverage extra extraRemote mavenPomQuality javadocQuality reproducibleBuilds ];
            };
        in
        rec {
          default = jdk20;
          jdk20 = mkShell 20;
          jdk17 = mkShell 17;
          jdk11 = mkShell 11;
        });
    };
}
