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

> **Java version:** Spoon version 10 and up requires Java 11 or later. Spoon 9.1.0 is the final Spoon release compatible
> with Java 8, and we do not plan to backport any bug fixes or features to Spoon 9. Note that Spoon can of course still
> consume source code for older versions of Java, but it needs JDK 11+ to run.

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

### Download

Latest version: <https://search.maven.org/remote_content?g=fr.inria.gforge.spoon&a=spoon-core&v=LATEST&c=jar-with-dependencies> - [Javadoc](https://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/index.html)

Maven:

```xml
<dependency>
    <groupId>fr.inria.gforge.spoon</groupId>
    <artifactId>spoon-core</artifactId>
    <!-- See rendered release value at https://spoon.gforge.inria.fr/ -->
    <version>{{site.spoon_release}}</version>
</dependency>
```

## Releases

<!-- .* Marker comment. -->

- August 31 2023, Spoon 10.4.1 [(changelog)](https://github.com/INRIA/spoon/releases/tag/v10.4.1)
- Jul 6 2023, Spoon 10.4.0 [(changelog)](https://github.com/INRIA/spoon/releases/tag/v10.4.0)
- March 13 2023, Spoon 10.3.0 [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-10.3.0)
- Oct 2022, Spoon 10.2.0 [(changelog)](https://github.com/INRIA/spoon/pull/4946)
- April 2022, Spoon 10.1.0 [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-10.1.0)
- October 2021, Spoon 10.0.0 [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-10.0.0)
- August 2021: Spoon 9.1.0 [(changelog)](https://github.com/INRIA/spoon/pull/4104)
- March 2021: Spoon 9.0.0 [(changelog)](https://github.com/INRIA/spoon/issues/3845)
- October 2020: Spoon 8.3.0 [(changelog)](https://github.com/INRIA/spoon/pull/3647)
- July 2020: Spoon 8.2.0 [(changelog)](https://github.com/INRIA/spoon/pull/3501)
- March 2020: Spoon 8.1.0 [(changelog)](https://github.com/INRIA/spoon/pull/3310)
- November 2019, Spoon 8.0.0 [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-8.0.0)
- July 2019: Spoon 7.5.0 is released [(changelog)](https://github.com/INRIA/spoon/pull/3057)
- May 2019: Spoon 7.4.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-7.4.0)
- February 10, 2019: Spoon 7.3.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-7.3.0) 
- December 4, 2018: Spoon 7.2.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-7.2.0) 
- October 10, 2018: Spoon 7.1.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-7.1.0) 
- July 4, 2018: Spoon 7.0.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-7.0.0) 
- March 8, 2018: Spoon 6.2.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-6.2.0)
- December 20, 2017: Spoon 6.1.0 is released, merry christmas! :christmas_tree: [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-6.1.0)
- November 17, 2017: Spoon 6.0.0 is released! Check the [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-6.0.0) as there are few non backward-compatible changes :warning:
- September 6, 2017: Spoon 5.9.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.9.0) *back-to-work* release!
- July 11, 2017: Spoon 5.8.0 is released  [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.8.0) Summer release :beer: To be preferred wrt the previous one: fix lot of bugs.
- June 01, 2017: Spoon 5.7.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.7.0)
- March 16, 2017: Spoon 5.6.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.6.0) Spring release :-)
- January 11, 2017: Spoon 5.5.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.5.0). Happy new year!
- October 27, 2016: Spoon 5.4.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.4.0).
- September 19, 2016: Spoon 5.3.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.3.0).
- June 30, 2016: Spoon 5.2.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.2.0).
- June 22, 2016: Spoon 5.1.1 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.1.1).
- March 21, 2016: Spoon 5.1.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.1.0).
- February 12, 2016: Spoon 5.0.2 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.0.2).
- February 3, 2016: Spoon 5.0.1 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.0.1).
- January 25, 2016: Spoon 5.0.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.0.0).
- November 18, 2015: Spoon 4.4.1 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-5.5.0).
- November 16, 2015: Spoon 4.4.0 is released [(changelog)](https://github.com/INRIA/spoon/releases/tag/spoon-core-4.4.0).
- September 22, 2015: Spoon 4.3.0 is released [(changelog)](https://lists.gforge.inria.fr/pipermail/spoon-discuss/2015-September/001803.html).
- June 15, 2015: Spoon 4.2.0 is released [(changelog)](https://lists.gforge.inria.fr/pipermail/spoon-discuss/2015-June/001781.html).
- May 7, 2015: Spoon 4.1.0 is released [(changelog)](https://lists.gforge.inria.fr/pipermail/spoon-discuss/2015-May/001774.html).
- April 8, 2015: Spoon 4.0.0 is released [(changelog)](https://lists.gforge.inria.fr/pipermail/spoon-discuss/2015-April/001769.html).
- February 11, 2015: Spoon 3.1 is released [(changelog)](https://lists.gforge.inria.fr/pipermail/spoon-discuss/2015-February/001741.html).
- December 9, 2014: Spoon 3.0 is released [(changelog)](https://lists.gforge.inria.fr/pipermail/spoon-discuss/2014-December/001721.html).
- November 12, 2014: Spoon 2.4 is released [(changelog)](https://lists.gforge.inria.fr/pipermail/spoon-discuss/2014-November/001710.html).
- October 9, 2014: Spoon 2.3.1 is released [(changelog)](https://lists.gforge.inria.fr/pipermail/spoon-discuss/2014-October/001688.html).
- September 12, 2014: Spoon 2.1 is released [(changelog)](https://lists.gforge.inria.fr/pipermail/spoon-discuss/2014-September/001683.html).
- April 2, 2014: Spoon 2.0 is released [(changelog)](https://lists.gforge.inria.fr/pipermail/spoon-discuss/2014-March/001639.html).
- September 30, 2013: Spoon 1.6 is released.
- April 12, 2012: Spoon 1.5 is released.

## License

Spoon is Free and Open Source, double-licensed under the ([CeCILL-C license](https://cecill.info/licences.en.html) - French equivalent to LGPL) and the MIT license.

## JProfiler

Spoon is developed with the help of JProfiler, a Java profiler by ej-technologies GmbH. JProfiler supports the development of Spoon by providing its full-featured Java Profiler for free. We thank ej-technologies GmbH for this support.

[![JProfiler](https://www.ej-technologies.com/images/product_banners/jprofiler_large.png)](https://www.ej-technologies.com/products/jprofiler/overview.html)
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
