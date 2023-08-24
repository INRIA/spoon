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
          mkShell = { release ? false, extraChecks ? false, javaVersion }:
            let
              pkgs = import nixpkgs {
                inherit system;
                overlays = [
                  (final: prev:
                    let
                      base = rec {
                        jdk = prev."jdk${toString javaVersion}";
                        maven = prev.maven.override { inherit jdk; };
                      };
                      extra = with base; {
                        gradle = prev.gradle.override { java = jdk; };
                        z3 = prev.z3.override { inherit jdk; javaBindings = true; };
                      };
                    in
                    (if extraChecks then base // extra else base))
                ];
              };
              semver = pkgs.buildGoModule rec {
                name = "semver";
                version = "2.1.0";

                vendorHash = "sha256-HKqZbgP7vqDJMaHUbSqfSOnBYwzOtIr9o2v/T9S+uNg=";
                subPackages = [ "cmd/semver" ];

                src = pkgs.fetchFromGitHub {
                  owner = "ffurrer2";
                  repo = "semver";
                  rev = "v${version}";
                  sha256 = "sha256-i/XPA2Hr2puJFKupIeBUE/yFPJxSeVsDWcz1OepxIcU=";
                };
              };
              jreleaser = pkgs.stdenv.mkDerivation rec {
                pname = "jreleaser-cli";
                version = "1.7.0";

                src = pkgs.fetchurl {
                  url = "https://github.com/jreleaser/jreleaser/releases/download/v${version}/jreleaser-tool-provider-${version}.jar";
                  sha256 = "sha256-gr1IWisuep00xyoZWKXtHymWkQjbDhlk6+UC16bKXu0=";
                };

                nativeBuildInputs = with pkgs; [ makeWrapper ];

                dontUnpack = true;

                installPhase = ''
                  mkdir -p $out/share/java/ $out/bin/
                  cp $src $out/share/java/${pname}.jar
                  makeWrapper ${pkgs.jdk}/bin/java $out/bin/${pname} \
                    --add-flags "-jar $out/share/java/${pname}.jar"
                '';
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
              extra = pkgs.writeScriptBin "extra" (if !extraChecks then "exit 2" else ''
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
                env LD_LIBRARY_PATH="${pkgs.z3.lib}/lib:${pkgs.z3.java}/lib" ./gradlew build
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
              '');
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
              pythonEnv =
                if extraChecks then
                  with pkgs; python311.withPackages (ps: [
                    ps.requests
                    ps.pygithub
                    ps.commonmark
                  ])
                else [ ];
              packages = with pkgs;
                [ jdk maven test coverage mavenPomQuality javadocQuality reproducibleBuilds ]
                ++ (if extraChecks then [ gradle pythonEnv z3.java z3.lib extra extraRemote ] else [ ])
                ++ (if release then [ semver jreleaser ] else [ ]);
            }
          ;
        in
        rec {
          default = jdk20;
          jdk20 = mkShell { javaVersion = 20; };
          jdk17 = mkShell { javaVersion = 17; };
          jdk11 = mkShell { javaVersion = 11; };
          extraChecks = mkShell { extraChecks = true; javaVersion = 11; };
          jReleaser = mkShell { release = true; javaVersion = 11; };
        });
    };
}
