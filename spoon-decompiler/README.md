# Spoon-Decompiler

Spoon-decompiler is an extension of spoon that allow to work on bytecode by decompiling it (with your favorite decompiler, or one of those already included).
This can be done offline with the help of [JarLauncher](http://spoon.gforge.inria.fr/launcher.html) or DecompiledResource, as well as at loadtime in an agent or a classLoader with the help of [SpoonClassFileTransformer](http://spoon.gforge.inria.fr/agent.html).

This module is released as a separated artifact. To use it with maven please add the following lines to your pom:

```xml
<!--latest stable release-->
<dependency>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-decompiler</artifactId>
    <version>0.1.0</version>
</dependency>
```

or for the snapshot release:

```xml
<!--SNAPSHOT release-->
<dependency>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-decompiler</artifactId>
    <version>0.2.0-SNAPSHOT</version>
</dependency>
<repositories>
	<repository>
      <id>maven.inria.fr-snapshot</id>
      <name>Maven Repository for Spoon Snapshots</name>
      <url>http://maven.inria.fr/artifactory/spoon-public-snapshot</url>
    </repository>
</repositories>
```