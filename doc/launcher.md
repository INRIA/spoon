---
title: How to use Spoon in Java?
tags: [usage]
keywords: usage, java
---

## Basic Launcher

The Spoon `Launcher` ([JavaDoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/Launcher.html)) is used to create the AST model of a project. It can be as short as:

```java
CtClass l = Launcher.parseClass("class A { void m() { System.out.println(\"yeah\");} }");
```

The Launcher is highly configurable:

```java
Launcher launcher = new Launcher();

// path can be a folder or a file
// addInputResource can be called several times
launcher.addInputResource("<path_to_source>"); 

// if true, the pretty-printed code is readable without fully-qualified names
launcher.getEnvironment().setAutoImports(true); // optional

// if true, the model can be built even if the dependencies of the analyzed source code are not known or incomplete
// the classes that are in the current classpath are taken into account
launcher.getEnvironment().setNoClasspath(true); // optional

launcher.buildModel();
CtModel model = launcher.getModel();
```


## Maven Launcher

The Spoon `MavenLauncher` ([JavaDoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/MavenLauncher.html)) is used to create the AST model of a Maven project.
It automatically infers the list of source folders and the dependencies from the `pom.xml` file.
This Launcher handles multi-module Maven projects.

```java
// the second parameter can be APP_SOURCE / TEST_SOURCE / ALL_SOURCE
MavenLauncher launcher = new MavenLauncher("<path_to_maven_project>", MavenLauncher.SOURCE_TYPE.APP_SOURCE);
launcher.buildModel();
CtModel model = launcher.getModel();

// list all packages of the model
for(CtPackage p : model.getAllPackages()) {
  System.out.println("package: "+p.getQualifiedName());
}
// list all classes of the model
for(CtType<?> s : model.getAllTypes()) {
  System.out.println("class: "+s.getQualifiedName());
}

```

Note that by default, MavenLauncher relies on an existing local maven binary to build the project's classpath. But a constructor allowing the user to skip this step and to provide a custom classpath is available.
```java
MavenLauncher launcher = new MavenLauncher("<path_to_maven_project>",
        MavenLauncher.SOURCE_TYPE.APP_SOURCE,
        new String[] {
            "/home/user/.m2/repository/org/my/jar/1.0/org-my-jar-1.0.jar"
        }
    );
launcher.buildModel();
CtModel model = launcher.getModel();
```
To avoid invoking maven over and over to build a classpath that has not changed, it is stored in a file `spoon.classpath.tmp` (or depending on the scope `spoon.classpath-app.tmp` or `spoon.classpath-test.tmp`) in the same folder as the `pom.xml`. This classpath will be refreshed is the file is deleted or if it has not been modified since 1h.

## About the classpath


Spoon analyzes source code. However, this source code may refer to libraries (as a field, parameter, or method return type). There are two cases:

* Full classpath: all dependencies are in the JVM classpath or are given to the Laucher with `launcher.getEnvironment().setSourceClasspath("<classpath_project>");` (optional)
* No classpath: some dependencies are unknown and `launcher.getEnvironment().setNoClasspath(true)` is set.

This has a direct impact on Spoon references.
When you're consider a reference object (say, a TypeReference), there are three cases:

- Case 1 (code available as source code): the reference points to a code element for which the source code is present. In this case, reference.getDeclaration() returns this code element (e.g. TypeReference.getDeclaration returns the CtType representing the given java file). reference.getTypeDeclaration() is identical to reference.getDeclaration().
- Case 2 (code available as binary in the classpath): the reference points to a code element for which the source code is NOT present, but for which the binary class is in the classpath (either the JVM classpath or the --source-classpath argument). In this case, reference.getDeclaration() returns null and reference.getTypeDeclaration returns a partial CtType built using runtime reflection. Those objects built using runtime reflection are called shadow objects; and you can identify them with method isShadow. (This also holds for getFieldDeclaration and getExecutableDeclaration)
- Case 3 (code not available, aka noclasspath): the reference points to a code element for which the source code is NOT present, but for which the binary class is NOT in the classpath. This is called in Spoon the noclasspath mode. In this case, both reference.getDeclaration() and reference.getTypeDeclaration() return null. (This also holds for getFieldDeclaration and getExecutableDeclaration)


## Declaring the dependency to Spoon

### Maven

```xml
<dependency>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-core</artifactId>
    <version>{{site.spoon_release}}</version>
</dependency>
```

### Gradle

```groovy
compile 'fr.inria.gforge.spoon:spoon-core:{{site.spoon_release}}'
```
