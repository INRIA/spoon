# Spoon Releases

## Versioning

Spoon uses a three digit version number MAJOR.MINOR.HOTPATCH. We follow semantic versioning with the exception that we may increment the major version number for significant new features even when there are no breaking changes

* we bump MAJOR when there is a big new feature or a strongly breaking change. We use it mostly for new features since we take special care to backward compatibility.
* we bump MINOR in the remaining cases (the majority of cases).
* we bump HOTPATCH when a normal release is not operational.

## Continuous delivery

### Beta versions

Once per week, a beta version is pushed to central. This is done by script https://github.com/SpoonLabs/spoon-deploy/blob/master/deploy-spoon-maven-central.sh triggered by a cron-based Github action (https://github.com/SpoonLabs/spoon-deploy/blob/master/.github/workflows/deploy.yml).

## Release reference documentation

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


