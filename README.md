[![Maven Central](https://img.shields.io/maven-central/v/fr.inria.gforge.spoon/spoon-core.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22fr.inria.gforge.spoon%22%20AND%20a%3A%22spoon-core%22)
[![Travis Build Status](https://travis-ci.org/INRIA/spoon.svg?branch=master)](https://travis-ci.org/INRIA/spoon)
[![Coverage Status](https://coveralls.io/repos/INRIA/spoon/badge.png)](https://coveralls.io/r/INRIA/spoon)
[![Jenkins Build Status](https://ci.inria.fr/sos/job/Spoon%20Install%20Commit%20Hook/badge/icon)](https://ci.inria.fr/sos/job/Spoon%20Install%20Commit%20Hook/)

[SonarQube Dashboard](https://sonarqube.ow2.org/dashboard?id=fr.inria.gforge.spoon%3Aspoon-core)

# Spoon

Spoon is an open-source library to analyze, rewrite, transform, transpile Java source code. It parses source files to build a well-designed AST with powerful analysis and transformation API. It fully supports Java 8, Java 9 and Java 10.
Spoon is an official Inria open-source project, and member of the [OW2](https://www.ow2.org/) open-source consortium.
The official website is available at <http://spoon.gforge.inria.fr/>.

If you use Spoon for academic purposes, please cite: Renaud Pawlak, Martin Monperrus, Nicolas Petitprez, Carlos Noguera, Lionel Seinturier. “[Spoon: A Library for Implementing Analyses and Transformations of Java Source Code](https://hal.archives-ouvertes.fr/hal-01078532/document)”. In Software: Practice and Experience, Wiley-Blackwell, 2015. Doi: 10.1002/spe.2346.

```
@article{pawlak:hal-01169705,
  TITLE = {{Spoon: A Library for Implementing Analyses and Transformations of Java Source Code}},
  AUTHOR = {Pawlak, Renaud and Monperrus, Martin and Petitprez, Nicolas and Noguera, Carlos and Seinturier, Lionel},
  JOURNAL = {{Software: Practice and Experience}},
  PUBLISHER = {{Wiley-Blackwell}},
  PAGES = {1155-1179},
  VOLUME = {46},
  URL = {https://hal.archives-ouvertes.fr/hal-01078532/document},
  YEAR = {2015},
  doi = {10.1002/spe.2346},
}

```


## Getting started in 2 seconds

Get latest stable version with Maven:
```xml
<dependency>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-core</artifactId>
    <version>7.1.0</version>
</dependency>
```

And start using it:

```java
CtClass l = Launcher.parseClass("class A { void m() { System.out.println(\"yeah\");} }");
```

Some examples about the usage of Spoon can be found on [spoon-examples](https://github.com/SpoonLabs/spoon-examples).


## Contributing in 2 seconds

Create your first pull-request to improve the documentation, see [doc](https://github.com/INRIA/spoon/tree/master/doc)! Proceed with your first bug fix! The community is open-minded, respectful and patient. All external contributions are welcome.

## Design Philosophy

R1) The Spoon metamodel is as close as possible to the language concepts.

R2) The Spoon model of a program is complete and sound.

R3) The text version of a Spoon model is well-formed and semantically equivalent to the original program.

R4) The analysis and transformation API is intuitive and regular.

R5) Transformation operators are designed to warn as fast as possible about invalid programs. This is done either with static type checking or with dynamic checks when the operators are used.

R6) When feasible, the text version of a Spoon model is close to the original one.

### Compiling

To compile Spoon, you need a Java Development Kit (JDK) and Maven:

```
git clone https://github.com/INRIA/spoon
cd spoon
mvn compile
```

To run the tests:
```
mvn test
```

### Download

Stable version:

```xml
<dependency>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-core</artifactId>
    <version>7.1.0</version>
</dependency>
```

Snapshot version:

```xml
<dependencies>
	<dependency>
		<groupId>fr.inria.gforge.spoon</groupId>
		<artifactId>spoon-core</artifactId>
		<version>7.2.0-SNAPSHOT</version>
	</dependency>
</dependencies>
<repositories>
	<repository>
      <id>maven.inria.fr-snapshot</id>
      <name>Maven Repository for Spoon Snapshots</name>
      <url>http://maven.inria.fr/artifactory/spoon-public-snapshot</url>
    </repository>
</repositories>
```

### Eclipse IDE Setup

In order to generate the Eclipse project files required for importing run the following commands from the root spoon directory (requires Maven):
```
mvn eclipse:clean
mvn eclipse:eclipse
```

### Ecosystem

See <http://spoon.gforge.inria.fr/ecosystem.html>

## On Youtube

  - [Spoon: Getting Started - Simon Urli @ OW2Con'18 (Paris)](https://www.youtube.com/watch?v=ZZzdVTIu-OY)
  - [Generate Test Assertion with Spoon - Benjamin Danglot @ OW2Con'17 (Paris)](https://www.youtube.com/watch?v=JcCIbjnkfD4)

## Contributors

Alphabetical order of last names

* Olivier Barais
* David Bernard
* Benjamin Danglot
* Benoit Cornu
* Didier Donsez
* Favio DeMarco
* Christophe Dufour
* Thomas Durieux
* Alcides Fonseca
* Nicolas Harrand
* Sebastian Lamelas Marcote
* Romain Leventov
* Matias Martinez
* Martin Monperrus
* Carlos Noguera
* Gérard Paligot
* Renaud Pawlak
* Nicolas Pessemier
* Nicolas Petitprez
* Phillip Schichtel
* Lionel Seinturier
* Marcel Steinbeck
* Simon Urli
* Pavel Vojtechovsky
* Stefan Wolf
