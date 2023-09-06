# Spoon CI/CD config

## Continuous integration

Spoon uses Github Actions and a Jenkins server for regression testing.

The Github Actions pipelines currently runs unit tests for all spoon projects and executes multiple static analysis and
code quality tools.
Most commands executed in the pipeline are run in a [Nix](https://nixos.org)
flake dev-shell, which you can also enter locally.
This should ensure that running tests locally or on CI has the same results.
Currently, the environment is not completely reproducible, as Nix has no good
way to package maven dependencies.

To enter the CI test environment for a given jdk, run `nix develop
.#jdk<version>` or `nix develop` to use the default, latest version.
The development shell provides multiple commands used by CI.
The most important commands are
- **`test`**: Executes tests for the core module
- **`javadoc-quality`**: Runs the Javadoc quality check to ensure the
  documentation quality does not degrade
- **`reproducible-builds`**: Builds spoon twice and verifies the output is
  identical using [diffoscope](https://diffoscope.org/)

More commands exist for testing code coverage, ensuring the various spoon
submodules still compile and their tests pass, as well as helpers for releasing
spoon versions.

## Continuous delivery

Spoon has three different release channels:
- **Snapshot**: The latest development version, published daily to the Sonatype
  snapshot repository. This version is not guaranteed to be stable and may
  contain breaking changes. After 90 days, snapshots are automatically deleted by Sonatype.
- **Beta**: A release candidate for the next stable release. Weekly beta
  releases are published to the SonaType release repository. They may contain unstable features, but should be mostly stable.
- **Stable**: A stable release following the [Semantic Versioning](https://semver.org)
  specification. These releases are published to the Sonatype release
  repository.

For the automatic release process, we use the [JReleaser](https://jreleaser.org) in GitHub actions.
Beta and Snapshot releases are published automatically, see [Beta](https://github.com/INRIA/spoon/blob/master/.github/workflows/release-beta.yml) and [Snapshot](https://github.com/INRIA/spoon/blob/master/.github/workflows/release-nightly.yml).
Stable releases are triggered manually using the [SemVer](https://github.com/INRIA/spoon/blob/master/.github/workflows/release-manual.yml) workflow, which starts the [JReleaser](https://jreleaser.org) release process.
The input for this workflow is the next semver version: major, minor, patch.
The script will automatically create a new branch, update the version number, create a tag, push the tag, and create a release on GitHub.
Also, there will be a new release on Maven Central afterwards.
The release process is:
1. Create a new branch from the master branch
2. Update the version number in the pom.xml files, parent + all child modules.
3. Create a commit with the new version number.
4. Execute the complete `maven verify` phase and deploy the artifacts to the staging repository.
5. Execute `JReleaser` to create a release on GitHub and Maven Central.
6. Update the version number in the pom.xml files to the next snapshot version.
7. Merge the branch into master with a fast-forward merge.
## Versioning

Spoon uses a three digit version number MAJOR.MINOR.HOTPATCH. We follow semantic versioning with the exception that we may increment the major version number for significant new features even when there are no breaking changes

* we bump MAJOR when there is a big new feature or a strongly breaking change. We use it mostly for new features since we take special care to backward compatibility.
* we bump MINOR in the remaining cases (the majority of cases).
* we bump HOTPATCH when a normal release is not operational.


## Manual Releases

This article is a short summary of the [official documentation of sonatype](http://central.sonatype.org/pages/ossrh-guide.html), an [article by yegor](http://www.yegor256.com/2014/08/19/how-to-release-to-maven-central.html) and [official documentation of maven release plugin](http://maven.apache.org/maven-release/maven-release-plugin/).


## Checklists 

**Before release**

- Prepare changelog with the changelog_generator, see folder `doc/_release/changelog_generator`
- Abnnounce the release with a new issue, a few days in advance (eg #2489)

**Release**

* [ ] Checkout commit to be released: 
  * `git checkout -b <version> <commit>`
  * eg `git checkout -b 10.2.0 ee73f4376aa929d8dce950202fabb8992a77c9fb`
* [ ] Modify the pom.xml 
  * `sed -i -e 's/<version>-SNAPSHOT/<version>/' pom.xml `
  * eg `sed -i -e 's/10.2.0-SNAPSHOT/10.2.0/' pom.xml `
* [ ] Create release on Maven Central with `mvn deploy` (using profile `release`, see below)
  * JAVA_HOME=/usr/lib/jvm/java-1.11.0-openjdk-amd64/  mvn clean deploy -Prelease -DskipTests -Dgpg. -Dgpg.keyname=074F73B36D8DD649B132BAC18035014A2B7BFA92
* [ ] Create release on Github
    * `git tag spoon-core-<version>`
    * eg `git tag spoon-core-10.2.0`
    * `git push inria spoon-core-<version>`
    * eg `git push inria spoon-core-10.2.0`

**After release**

- [ ] Create Pull request on Github with (example #1732)
    - News section in `doc/doc_homepage.md`
- [ ] Check if release documentation it up-to-date and improve accordingly
- [ ] Update version information and date on [INRIA's BIL](https://bil.inria.fr/en/software/view/251/tab)
- [ ] Update version information on <https://projects.ow2.org/view/spoon/>
- [ ] Announce the release:
  * mailing-lists (eg gdr-gpl, OW2)
  * social media (eg twitter, reddit, linkedin)  

## Additional Information
###  Typical `settings.xml` for Maven

```
<settings>
  <profiles>
    <profile>
      <id>spoon</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg2</gpg.executable>
        <gpg.passphrase><!-- password of the keys--></gpg.passphrase>
        <gpg.useagent>true</gpg.useagent>
        <gpg.keyname><!-- the name of the key file --></gpg.keyname> 
      </properties>
    </profile>
  </profiles>
  <servers>
    <server>
      <id>ossrh</id>
      <username>monperrus</username>
      <password><!-- monperrus' password of sonatype --></password>
    </server>
  </servers>
</settings>
```

### How to generate new keys with GPG

To push your archive on Maven Central, you must sign before your jar with GPG, a tool multi platform based on a pair of keys (public/private).

1. Generate your pair of keys: `gpg --gen-key`
2. Check if your key is generated: `gpg2 --list-keys`
3. Distributing your public key on a server key (used by maven release plugin): `gpg2 --keyserver hkp://pool.sks-keyservers.net --send-keys <your-public-id-key>`


