---
title: Maven
tags: [usage]
keywords: maven, central, usage, java, plugin
last_updated: October 1, 2015
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

## Plugin

A Maven plugin allows easily launching Spoon when using Maven. This plugin is available in Maven Central 
and can be directly inserted in a pom.xml file at the root of a project 
(or in the pom.xml of one of a maven module, where you want use Spoon).

```xml
<plugin>
  <groupId>fr.inria.gforge.spoon</groupId>
  <artifactId>spoon-maven-plugin</artifactId>
  <version>2.2</version>
  <executions>
    <execution>
      <phase>generate-sources</phase>
      <goals>
        <goal>generate</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <processors>
      <processor>fr.inria.gforge.spoon.processors.CatchProcessor</processor>
    </processors>
  </configuration>
  <!-- To be sure that you use the latest version of Spoon, specify it as dependency. -->
  <dependencies>
    <dependency>
      <groupId>fr.inria.gforge.spoon</groupId>
      <artifactId>spoon-core</artifactId>
      <version>{{site.spoon_release}}</version>
    </dependency>
  </dependencies>
</plugin>
```

You simply specify your processors, in fully qualified name, in the configuration 
of the plugin, the processors will be applied on your target project before compilation.

In short, the Maven plugin gives the classpath of your project to Spoon, 
applies Spoon on all source directories and rewrites the transformed Java files in the target 
directory. These parameters can be changed in the configuration of the plugin. 

{{site.data.alerts.warning}}
If you want use a processor which isn't in your project, specify the dependency 
where the Maven plugin can retrieve it. Otherwise, your processor won't be applied.
{{site.data.alerts.end}}

To know more about this Maven plugin, check the README of its [GitHub project](https://github.com/SpoonLabs/spoon-maven-plugin). 
