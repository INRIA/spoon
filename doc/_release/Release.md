# How to release Spoon?

This article is a short summary of the [official documentation of sonatype](http://central.sonatype.org/pages/ossrh-guide.html), an [article by yegor](http://www.yegor256.com/2014/08/19/how-to-release-to-maven-central.html) and [official documentation of maven release plugin](http://maven.apache.org/maven-release/maven-release-plugin/).

## Core tasks:

* [ ] Release on Maven Central
    * with `mvn deploy` (using profile `release`, see below)
* [ ] Release on Github
    * With `git push` (git push <release_tag>)
* [ ] PR to update the information
* [ ] PR to remove deprecated methods

## Prerequisites

1. Gforge: open an account on `https://gforge.inria.fr`, add an SSH key there, check that you are a member of project `spoon`
1. check that gpg2, keepass are installed
1. get the Sonatype credentials for Spoon (on `davs://partage.inria.fr/alfresco/webdav/Sites/spirals/documentLibrary/security/`, in `Spirals Team >> Document Library >> security >> spoon.kdbx`)
1. get the GPG credentials for Spoon (on `davs://partage.inria.fr/alfresco/webdav/Sites/spirals/documentLibrary/security/`, in `Spirals Team >> Document Library >> security >> spoon.kdbx`)
    1. download the keepass file
    1. import the keys with `gpg2 --import` (one key for Spoon, one key for Spoon Maven plugin)
    1. check with `gpg2 --list-keys`
1. update  `~/.m2/settings.xml`  with
    * Sonatype username and password
    * GPG keyname and passphrase

## Maven Release

1. update the version number (remove the snapshopt)
    * `mvn release:clean release:prepare`
    * alternatively, change `pom.xml` if master is a protected branch on Github
1. send to Maven Central
    * `mvn release:perform` (sends the new version on Maven Central)
    * alternatively `mvn -Prelease deploy`
1. check that the new version is on Maven Central (connect to `oss.sonatype.org`)

## Github Release
1. push the release tag on Github (git push)
    - `git push origin master`
    - `git push origin spoon-core-X.X.X`
1. open `Releases` tab, click on `Draft a new release`.
    - Add the changelod (`node doc/_release/changelog_generator/changelog.js 7.2.0`)

## Checklists 

**Before release**

- Prepare changelog with [changelog_generator](https://github.com/INRIA/spoon/tree/master/doc/_release/changelog_generator)
- Abnnounce the release with a new issue, a few days in advance (eg #2489)

**After release**

- Create Pull request on Github with (example #1732)
    - News section in `doc/doc_homepage.md`
    - Information in `pom.xml`
- Update version information and date on [INRIA's BIL](https://bil.inria.fr/en/software/view/251/tab)
- Update version information on <https://projects.ow2.org/view/spoon/>
- Announce the release:
  * mailing-lists (gdr-gpl, OW2)
  * social media (twitter, reddit, linkedin)  

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


