{
  description = "Spoon is a metaprogramming library to analyze and transform Java source code. 🥄 is made with ❤️, 🍻 and ✨. It parses source files to build a well-designed AST with powerful analysis and transformation API.";

  # Nixpkgs / NixOS version to use.
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = { self, nixpkgs }:
    let
      # Helper function to generate an attrset '{ x86_64-linux = f "x86_64-linux"; ... }'.
      forAllSystems = nixpkgs.lib.genAttrs nixpkgs.lib.systems.flakeExposed;
      mkShell = system: { release ? false, extraChecks ? false, javaVersion }:
        let
          pkgs = import nixpkgs {
            inherit system;
            overlays = [
              (final: prev:
                let
                  base = rec {
                    jdk =
                      if javaVersion <= 23 then prev."jdk${toString javaVersion}"
                      else abort "Not set up yet :)";
                    maven = prev.maven.override { inherit jdk; };
                  };
                  extra = with base; {
                    gradle = prev.gradle.override { java = jdk; };
                  };
                in
                (if extraChecks then base // extra else base))
            ];
          };
          semver = pkgs.buildGoModule rec {
            name = "semver";
            version = "2.4.0";

            vendorHash = "sha256-/8rYdeOHp6Bdc3CD5tzw5M0hjTPd+aYZSW7w2Xi695Y=";
            subPackages = [ "cmd/semver" ];

            src = pkgs.fetchFromGitHub {
              owner = "ffurrer2";
              repo = "semver";
              rev = "v${version}";
              sha256 = "sha256-wl5UEu2U11Q0lZfm9reMhGMCI7y6sabk18j7SPWgy1k=";
            };
          };
          jreleaser = pkgs.stdenv.mkDerivation rec {
            pname = "jreleaser-cli";
            version = "1.11.0";

            src = pkgs.fetchurl {
              url = "https://github.com/jreleaser/jreleaser/releases/download/v${version}/jreleaser-tool-provider-${version}.jar";
              sha256 = "sha256-VkINXKVBBBK6/PIRPMVKZGY9afE7mAsqrcFPh2Algqk=";
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
          codegen = pkgs.writeScriptBin "codegen" ''
            set -eu
            mvn test -Dtest=spoon.testing.assertions.codegen.AssertJCodegen
            mvn spotless:apply
            if ! git diff --exit-code; then
              echo "::error::Generated code is not up to date. Execute mvn test -Dtest=spoon.testing.assertions.codegen.AssertJCodegen, mvn spotless:apply and commit your changes."
              exit 1
            fi
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
            # mvn -q depclean:depclean

            pushd spoon-decompiler || exit 1
            mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
            mvn -q versions:update-parent -DallowSnapshots=true
            git diff
            mvn -q test
            mvn -q checkstyle:checkstyle license:check
            # mvn -q depclean:depclean
            popd || exit 1

            pushd spoon-control-flow || exit 1
            mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
            mvn -q versions:update-parent -DallowSnapshots=true
            git diff
            mvn -q test
            mvn -q checkstyle:checkstyle license:check
            popd || exit 1

            pushd spoon-visualisation || exit 1
            mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
            mvn -q versions:update-parent -DallowSnapshots=true
            git diff
            mvn -q test
            # mvn -q depclean:depclean
            popd || exit 1

            pushd spoon-smpl || exit 1
            mvn -q versions:use-latest-versions -DallowSnapshots=true -Dincludes=fr.inria.gforge.spoon
            mvn -q versions:update-parent -DallowSnapshots=true
            git diff
            mvn -q -Djava.src.version=17 test
            mvn -q checkstyle:checkstyle license:check
            # mvn -q depclean:depclean
            popd || exit 1
          '');
          extraRemote = pkgs.writeScriptBin "extra-remote" ''
            set -eu

            curl https://raw.githubusercontent.com/SpoonLabs/spoon-ci-external/master/spoon-pull-request.sh | bash
          '';
          mavenPomQuality = pkgs.writeScriptBin "maven-pom-quality" ''
            set -eu

            # we dont enforce that the version must be non snapshot as this is not possible for SNAPSHOT versions in our workflow.
            mvn -f spoon-pom org.kordamp.maven:pomchecker-maven-plugin:1.9.0:check-maven-central -D"checker.release=false"
          '';
          reproducibleBuilds = pkgs.writeScriptBin "reproducible-builds" ''
            set -eu

            chore/check-reproducible-builds.sh
          '';
          ciJavadocQuality = pkgs.writeScriptBin "ci-javadoc-quality" ''
            set -eu

            # Help jbang. Build locally and update the version. Otherwise it fails to resolve sometimes.
            pushd spoon-pom || exit 1
            mvn clean install -Dmaven.test.skip=true -DskipDepClean &>/dev/null
            popd || exit 2

            # Use concrete version
            sed -i "s/:RELEASE/:$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)/" chore/CheckJavadoc.java
            git add chore/CheckJavadoc.java
            git config user.email "jbang-fixup@example.com"
            git config user.name "Jbang fixup"
            git commit -m "Fixup version"

            # Delegate
            javadoc-quality
          '';
          javadocQuality = pkgs.writeScriptBin "javadoc-quality" ''
            set -eu

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
            [ jdk maven test codegen coverage mavenPomQuality javadocQuality ciJavadocQuality reproducibleBuilds ]
            ++ (if extraChecks then [ gradle pythonEnv extra extraRemote jbang ] else [ ])
            ++ (if release then [ semver jreleaser ] else [ ]);
        };
    in
    {
      devShells =
        let
          # We might have additional options (currently none) on 64 bit linux systems
          blessedSystem = "x86_64-linux";
          blessed = rec { };
          common = forAllSystems
            (system:
              rec {
                default = jdk17;
                jdk17 = mkShell system { javaVersion = 17; };
                jdk21 = mkShell system { javaVersion = 21; };
                jdk22 = mkShell system { javaVersion = 22; };
                extraChecks = mkShell system { extraChecks = true; javaVersion = 21; };
                jReleaser = mkShell system { release = true; javaVersion = 21; };
              });
        in
        common // { "${blessedSystem}" = common."${blessedSystem}" // blessed; };
    };
}
