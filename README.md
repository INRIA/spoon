[![Build Status](https://travis-ci.org/INRIA/spoon.svg?branch=master)](https://travis-ci.org/INRIA/spoon)

# Spoon

Spoon is an open-source library for analyzing and transforming Java source code.

- If you use Spoon for academic purposes, please cite: Renaud Pawlak, Martin Monperrus, Nicolas Petitprez, Carlos Noguera, Lionel Seinturier. “Spoon v2: Large Scale Source Code Analysis and Transformation for Java”. Technical Report hal-01078532, Inria. 2014.
- If you use Spoon for industrial purposes, please consider funding Spoon through a research contract with Inria (contact Martin Monperrus for this).

Pull requests are welcome!

The official website is available [here](http://spoon.gforge.inria.fr/).

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
    <version>4.0.0</version>
</dependency>
```

Snapshot version:

```xml
<dependencies>
	<dependency>
		<groupId>fr.inria.gforge.spoon</groupId>
		<artifactId>spoon-core</artifactId>
		<version>4.1.0-SNAPSHOT</version>
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
