---
title: Launcher
tags: [usage]
keywords: usage, java
---

## Dependency

Stable version:

```xml
<dependency>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-core</artifactId>
    <version>{{site.spoon_release}}</version>
</dependency>
```

Snapshot version:

```xml
<dependencies>
	<dependency>
		<groupId>fr.inria.gforge.spoon</groupId>
		<artifactId>spoon-core</artifactId>
		<version>{{site.spoon_snapshot}}</version>
	</dependency>
</dependencies>
<repositories>
	<repository>
		<id>gforge.inria.fr-snapshot</id>
		<name>Maven Repository for Spoon Snapshot</name>
		<url>http://spoon.gforge.inria.fr/repositories/snapshots/</url>
		<snapshots />
	</repository>
</repositories>
```

## Launcher

The Spoon `Launcher` ([JavaDoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/Launcher.html)) is used to create the AST model of the project.

### Usage

```java
Launcher launcher = new Launcher();
launcher.addInputResource("<path_to_source>");
launcher.getEnvironment().setAutoImports(true); // optional
launcher.getEnvironment().setComplianceLevel(7); // optional
launcher.getEnvironment().setNoClasspath(true); // optional
launcher.getEnvironment().setSourceClasspath("<classpath_project>"); // optional
launcher.buildModel();
CtModel model = launcher.getModel();
```


## MavenLauncher

The Spoon `MavenLauncher` ([JavaDoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/MavenLauncher.html)) is used to create the AST model of the project by inferring automatically the list of source folder and the dependencies from a Maven `pom.xml` file.
This Launcher simplify the creation of model on complex multi-module project.
If you consider to only use `Processors` in your Spoon, consider to use the [Spoon Maven Plugin](http://spoon.gforge.inria.fr/maven.html) which will do a better job to detect the dependencies.

### Usage

```java
MavenLauncher launcher = new MavenLauncher("<path_to_project>", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
launcher.buildModel();
CtModel model = launcher.getModel();
```