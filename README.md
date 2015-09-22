[![Build Status](https://travis-ci.org/INRIA/spoon.svg?branch=master)](https://travis-ci.org/INRIA/spoon)

# Spoon

Spoon is an open-source library for analyzing and transforming Java source code. External contributions as pull requests are welcome!

The mission of Spoon is to provide a high-quality library for analyzing and transforming Java source code.

- If you use Spoon for industrial purposes, please consider funding Spoon through a research contract with Inria (contact Martin Monperrus for this).

- If you use Spoon for academic purposes, please cite: Renaud Pawlak, Martin Monperrus, Nicolas Petitprez, Carlos Noguera, Lionel Seinturier. “Spoon: A Library for Implementing Analyses and Transformations of Java Source Code”. In Software: Practice and Experience, Wiley-Blackwell, 2015. Doi: 10.1002/spe.2346.

```
@article{pawlak:hal-01169705,
  TITLE = {{Spoon: A Library for Implementing Analyses and Transformations of Java Source Code}},
  AUTHOR = {Pawlak, Renaud and Monperrus, Martin and Petitprez, Nicolas and Noguera, Carlos and Seinturier, Lionel},
  JOURNAL = {{Software: Practice and Experience}},
  PUBLISHER = {{Wiley-Blackwell}},
  PAGES = {na},
  YEAR = {2015},
  doi = {10.1002/spe.2346},
}

```

The official website is available [here](http://spoon.gforge.inria.fr/).

# Design Philosophy


R1) The Spoon metamodel is as close as possible to the language concepts.

R2) The Spoon model of a program is complete and sound.

R3) The text version of a Spoon model is well-formed and semantically equivalent to the original program.

R4) The analysis and transformation API is intuitive and regular.

R5) Transformation operators are designed to warn as fast as possible about invalid programs. This is done either with static type checking or with dynamic checks when the operators are used.

R6) When feasible, the text version of a Spoon model is close to the original one.


# Ecosystem

[SpoonLabs](http://github.com/SpoonLabs) is a group GitHub for software libraries and applications built on top of Spoon.

You can found plugins for [Maven](https://github.com/SpoonLabs/spoon-maven-plugin) and [Gradle](https://github.com/SpoonLabs/spoon-gradle-plugin) to run spoon on a target project with some processors if necessary.

There are also project which merge spoon with other open source projects like [spooet](https://github.com/SpoonLabs/spooet), a Spoon printer implemented with [JavaPoet](https://github.com/square/javapoet).

And so many other projects! All contributions are welcome in this group!

# Download

Stable version:

```xml
<dependency>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-core</artifactId>
    <version>4.3.0</version>
</dependency>
```

Snapshot version:

```xml
<dependencies>
	<dependency>
		<groupId>fr.inria.gforge.spoon</groupId>
		<artifactId>spoon-core</artifactId>
		<version>4.4.0-SNAPSHOT</version>
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

### Eclipse IDE Setup

In order to generate the Eclipse project files required for importing run the following commands from the root spoon directory (requires Maven):
```
mvn eclipse:clean
mvn eclipse:eclipse
```


# Contributors

Alphabetical order of last names

* Olivier Barais
* David Bernard
* Benoit Cornu
* Didier Donsez
* Favio DeMarco
* Christophe Dufour
* Sebastian Lamelas Marcote
* Matias Martinez
* Martin Monperrus
* Carlos Noguera
* Gérard Paligot
* Renaud Pawlak
* Nicolas Pessemier
* Nicolas Petitprez
* Phillip Schichtel
* Lionel Seinturier
* Stefan Wolf
