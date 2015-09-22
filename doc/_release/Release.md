# How to release Spoon?

This article is a short summary of the [official documentation of sonatype][ossrh-guide], an [article](yegor) by yegor and [official documentation of maven release plugin](maven-release-plugin).

## Prerequisites

### Sign with GPG

To push your archive on Maven Central, you must sign before your jar with GPG, a tool multi platform based on a pair of keys (public/private).

1. Generate your pair of keys: `gpg --gen-key`
2. Check if your key is generated: `gpg2 --list-keys`
3. Distributing your public key on a server key (used by maven release plugin): `gpg2 --keyserver hkp://pool.sks-keyservers.net --send-keys <your-public-id-key>`
4. Update your `settings.xml` of your Maven
```
<settings>
  <profiles>
    <profile>
      <id>spoon</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <gpg.executable>gpg</gpg.executable>
        <gpg.passphrase><!-- password of your pair of keys--></gpg.passphrase>
        <gpg.useagent>true</gpg.useagent>
        <gpg.keyname><!-- your public id key --></gpg.keyname> 
      </properties>
    </profile>
  </profiles>
</settings>
```

### Create a repo for your project in Sonatype

In the JIRA of Sonatype, create a new ticket to create your repository. You will fill a form with some information about your project like Git repository, scm, etc.

This process may take +/- 48 hours.

After that, you can update your `settings.xml` of your Maven:

```
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username><!-- your username in JIRA --></username>
      <password><!-- your password in JIRA --></password>
    </server>
  </servers>
</settings>
```

## Initialize his project

All steps in this sections are details in the [official documentation][apache-maven] and modify `pom.xml` of the project.

1. Specify sonatype plugin and distributions managements for release and snapshot (if necessary).
3. Specify GPG plugin to verify and sign your project.
4. Specify Nexus staging plugin if you would like push your archive on the sonatype and manually push it on the central.
5. Specify sonatype as parent of your pom.xml.
6. Specify a scm about your Sonatype repository.

## Deploy your project

To deploy, we will use maven release plugin :

1. Clean your project for the release and prepare the release :

```
mvn release:clean release:prepare
```

2. Perform your release

```
mvn release:perform
```

This process push your archive on the repository in Sonatype and on Maven Central. This last may take some hours.

[ossrh-guide]: http://central.sonatype.org/pages/ossrh-guide.html
[yegor]: http://www.yegor256.com/2014/08/19/how-to-release-to-maven-central.html
[maven-release-plugin]: http://maven.apache.org/maven-release/maven-release-plugin/
[apache-maven]: http://central.sonatype.org/pages/apache-maven.html
