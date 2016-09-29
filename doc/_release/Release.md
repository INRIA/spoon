# How to release Spoon?

This article is a short summary of the [official documentation of sonatype](http://central.sonatype.org/pages/ossrh-guide.html), an [article by yegor](http://www.yegor256.com/2014/08/19/how-to-release-to-maven-central.html) and [official documentation of maven release plugin](http://maven.apache.org/maven-release/maven-release-plugin/).

## Main Workflow
1. open an account on `https://gforge.inria.fr`, add an SSH key there, check that you are a member of project `spoon`
1. check that gpg2, keepass are installed
1. take the latest commit of master (`git pull` on master)
1. get the GPG credentials of Spoon (on `davs://partage.inria.fr/alfresco/webdav/Sites/spirals/documentLibrary/security/`, in `Spirals Team >> Document Library >> security >> keepass.kdbx`)
  1. download the keepass file
  1. import the keys with `gpg2 --import` (doc in keypass.kdbx)
  1. check with `gpg2 --list-keys`
1. update  `~/.m2/settings.xml`  with passphrase and keyname (see below)
1. clean your project for the release and prepare the release `mvn release:clean release:prepare`
1. `mvn release:perform` (sends the new version on Maven Central)
1. check that the new version is on Maven Central (connect to `oss.sonatype.org`)
1. push the release commit on Github (git push origin master)
    - `git push origin master`
    - `git push origin spoon-core-X.X.X`
1. update the `stable` branch
    - `git checkout stable`
    - `git reset --hard <commit-id-last-master>` # We need two commits from previous release to deploy a hotfix.
    - `git push origin stable`
1. update the doc, etc., see checklist below

### Checklists 

**Before the release**

- Verify that all critical bug fixes from stable to master branch

**After release**

- Uploads archives on Maven Central (see above)
- Uploads archives on INRIA's forge
- Prepare changelog (see `doc/_release/changelog_generator/`)
- Create Pull request on Github with
    - Update of Spoon's website
    	- News section in `doc/doc_homepage.md`
    	- Maven version in `doc/_config.yml`
    	- Maven snippets in `doc/doc_homepage.md`
    - Updates main `README.md`
- Add changelog on release page on GitHub 
- Announces release on the mailing list (give credits to the contributors)
- If necessary, removes all methods deprecated after the release!


###  `settings.xml` of your Maven

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
        <gpg.passphrase><!-- password of your pair of keys--></gpg.passphrase>
        <gpg.useagent>true</gpg.useagent>
        <gpg.keyname><!-- your public id key --></gpg.keyname> 
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

## How to generate new keys with GPG

To push your archive on Maven Central, you must sign before your jar with GPG, a tool multi platform based on a pair of keys (public/private).

1. Generate your pair of keys: `gpg --gen-key`
2. Check if your key is generated: `gpg2 --list-keys`
3. Distributing your public key on a server key (used by maven release plugin): `gpg2 --keyserver hkp://pool.sks-keyservers.net --send-keys <your-public-id-key>`


## Create a repo for your project in Sonatype

In the JIRA of Sonatype, create a new ticket to create your repository. You will fill a form with some information about your project like Git repository, scm, etc.

This process may take +/- 48 hours.

After that, you can update your `settings.xml` of your Maven:


## Initialize the project

All steps in this section are detailed in the [official documentation](http://central.sonatype.org/pages/apache-maven.html) and modify `pom.xml` of the project.

1. Specify sonatype plugin and distributions managements for release and snapshot (if necessary).
3. Specify GPG plugin to verify and sign your project.
4. Specify Nexus staging plugin if you would like push your archive on the sonatype and manually push it on the central.
5. Specify sonatype as parent of your pom.xml.
6. Specify a scm about your Sonatype repository.


