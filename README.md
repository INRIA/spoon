[![Maven Central](https://img.shields.io/maven-central/v/fr.inria.gforge.spoon/spoon-core.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22fr.inria.gforge.spoon%22%20AND%20a%3A%22spoon-core%22)
[![GHA tests Workflow Status](https://github.com/INRIA/spoon/actions/workflows/tests.yml/badge.svg)](https://github.com/INRIA/spoon/actions/workflows/tests.yml)
[![Coverage Status](https://coveralls.io/repos/INRIA/spoon/badge.svg)](https://coveralls.io/r/INRIA/spoon)
[![Maintainability Rating](https://sonarqube.ow2.org/api/project_badges/measure?project=fr.inria.gforge.spoon%3Aspoon-core&metric=sqale_rating)](https://sonarqube.ow2.org/dashboard?id=fr.inria.gforge.spoon%3Aspoon-core)
[![Reproducible Builds](https://img.shields.io/badge/Reproducible_Builds-ok-success?labelColor=1e5b96)](https://github.com/jvm-repo-rebuild/reproducible-central#fr.inria.gforge.spoon:spoon-core)

# Spoon

Spoon is an open-source library to analyze, rewrite, transform, transpile Java source code. It parses source files to build a well-designed AST with powerful analysis and transformation API. It supports modern Java versions up to Java 20. Spoon is an official Inria open-source project, and member of the [OW2](https://www.ow2.org/) open-source consortium.

## Documentation

The latest official documentation is available at <https://spoon.gforge.inria.fr/>.

### Academic usage

If you use Spoon for academic purposes, please cite: Renaud Pawlak, Martin Monperrus, Nicolas Petitprez, Carlos Noguera, Lionel Seinturier. “[Spoon: A Library for Implementing Analyses and Transformations of Java Source Code](https://hal.archives-ouvertes.fr/hal-01078532/document)”. In Software: Practice and Experience, Wiley-Blackwell, 2015. Doi: 10.1002/spe.2346.

```
@article{pawlak:hal-01169705,
  TITLE = "{Spoon: A Library for Implementing Analyses and Transformations of Java Source Code}",
  AUTHOR = {Pawlak, Renaud and Monperrus, Martin and Petitprez, Nicolas and Noguera, Carlos and Seinturier, Lionel},
  JOURNAL = "{Software: Practice and Experience}",
  PUBLISHER = "{Wiley-Blackwell}",
  PAGES = {1155-1179},
  VOLUME = {46},
  URL = {https://hal.archives-ouvertes.fr/hal-01078532/document},
  YEAR = {2015},
  doi = {10.1002/spe.2346},
}
```

### Professional support

If you need professional support on Spoon (development, training, extension), you are welcome to post a comment on https://github.com/INRIA/spoon/issues/3251

## Getting started in 2 seconds

**Required Java version:**

- Spoon 11.x requires JDK 17 or later.
- Spoon 10.x requires JDK 11 or later.
- Spoon 9.x requires Java 8.

Note that Spoon can of course still consume source code for older versions of Java, but it needs the above mentioned JDK version to run.

Get latest stable version with Maven, see <https://search.maven.org/artifact/fr.inria.gforge.spoon/spoon-core>

And start using it:

```java
CtClass l = Launcher.parseClass("class A { void m() { System.out.println(\"yeah\");} }");
```

Documentation:

- Reference documentation: <https://spoon.gforge.inria.fr/> (contains the content of the [doc folder](https://github.com/INRIA/spoon/tree/master/doc))
- Code examples: <https://github.com/SpoonLabs/spoon-examples>
- Videos: [Spoon: Getting Started - Simon Urli @ OW2Con'18 (Paris)](https://www.youtube.com/watch?v=ZZzdVTIu-OY), [Generate Test Assertion with Spoon - Benjamin Danglot @ OW2Con'17 (Paris)](https://www.youtube.com/watch?v=JcCIbjnkfD4)


## Contributing in 2 seconds

Create your first pull request to improve the documentation, see [doc](https://github.com/INRIA/spoon/tree/master/doc)! Proceed with your first bug fix! The community is open-minded, respectful and patient. All external contributions are welcome.

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

## Releases

<!-- .* Marker comment. -->

See [Releases](https://github.com/INRIA/spoon/releases)

## License

Spoon is Free and Open Source, double-licensed under the ([CeCILL-C license](https://cecill.info/licences.en.html) - French equivalent to LGPL) and the MIT license.

## JProfiler

Spoon is developed with the help of JProfiler, a Java profiler by ej-technologies GmbH. JProfiler supports the development of Spoon by providing its full-featured Java Profiler for free. We thank ej-technologies GmbH for this support.

[![JProfiler](https://www.ej-technologies.com/images/product_banners/jprofiler_large.png)](https://www.ej-technologies.com/products/jprofiler/overview.html)

### Powered by

[![JetBrains logo.](https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.svg)](https://jb.gg/OpenSource)


## Github Contributors

This list is generated by `chore/generate-contributor-list.py`. If you're not listed or you'd like to have your full name, please post to https://github.com/INRIA/spoon/issues/3909.

* adamjryan
* Alcides Fonseca
* Alexander Shopov
* Aman Sharma
* andrewbwogi
* André Cruz
* André Silva
* Antoine Mottier
* Anton Lyxell
* argius
* Arnaud Blouin
* arsenkhy
* Artamm
* Artur Bosch
* Arvid Siberov
* aryan
* Ashutosh Kumar Verma
* aveuiller
* Axel Howind
* Benjamin DANGLOT
* Benoit Cornu
* Carlos Noguera
* Ceki Gülcü
* chammp
* Charm
* ChrisSquare
* Christophe Dufour
* Christopher Stokes
* Clemens Bartz
* Clément Fournier
* César Soto Valero
* Daniel Bobbert
* Darius Sas
* David Bernard
* Didier Donsez
* Diorcet Yann
* Dorota Kopczyk
* dufaux
* dwayneb
* dya-tel
* Eddie T
* Egor Bredikhin
* Fabien DUMINY
* Fan Long
* fangzhen
* fav
* Favio DeMarco
* Fernanda Madeiral
* Filip Krakowski
* Gabriel Chaperon Burgos
* gibahjoe
* GitHub Actions Bot
* GluckZhang
* Gregor Zeitlinger
* gtoison
* Guillaume Toison
* Gérard Paligot
* Hannes Greule
* Haris Adzemovic
* HectorSM
* Henry Chu
* Hervé Boutemy
* Horia Constantin
* I-Al-Istannen
* intrigus-lgtm
* jakobbraun
* Jan Galinski
* jon
* Kai Luo
* Lakshya A Agrawal
* leventov
* Lionel Seinturier
* lodart
* Lucas
* Lukas Krejci
* Luke Merrick
* Marcel Manseer
* Marcel Steinbeck
* Martin Monperrus
* Martin Wittlinger
* MartinWitt
* Matias Martinez
* Maxim Stefanov
* Maxime CLEMENT
* Mehdi Kaytoue
* Michael Täge
* Mickael Istria
* Miguel Sozinho Ramalho
* Mikael Forsberg
* Mr. Pine
* Muhammet Ali AKBAY
* Nicolas Harrand
* Nicolas Pessemier
* Nicolas Petitprez
* Noah Santschi-Cooney
* Olivier Barais
* Ondřej Šebek
* Patrick Schmitt
* Pavel Vojtechovsky
* peroksid90
* Philippe Ombredanne
* Phillip Schichtel
* priyanka-28
* Quentin LE DILAVREC
* raymogg
* Renaud Pawlak
* Reza Gharibi
* Rhys Compton
* Rick Kellogg
* Rijnard van Tonder
* Rohitesh Kumar Jain
* Roman Leventov
* Ryota Hiyoshi
* Sander Ploegsma
* santos-samuel
* scootafew
* Scott Dickerson
* Scott Pinwell
* Sebastian Lamelas Marcote
* Sergey Fedorov
* Shantanu
* Simon Larsén
* Simon Urli
* Spencer Williams
* srlm
* ST0NEWALL
* Stefan Wolf
* StepSecurity Bot
* Sébastien Bertrand
* The Spoon Bot
* Thimo Seitz
* Thomas Durieux
* tiagodrcarvalho
* Tomasz Zieliński
* Urs Keller
* Viktor
* Vincenzo Musco
* Wolfgang Schmiesing
* Wouter Smeenk
* Wreulicke
* Yann Diorcet
* Yogya Tulip Gamage
* Zhang Xindong
* Дмитрий
