{% include linkrefs.html %}

## Spoon

Spoon is an open-source library that enables you to transform (see [example](/first_analysis_processor.html)) and analyze Java source code (see [example](/first_transformation.html)) . Spoon provides a complete and fine-grained Java metamodel where any program element (classes, methods, fields, statements, expressions...) can be accessed both for reading and modification. Spoon takes as input source code and produces transformed source code ready to be compiled. Spoon can be integrated in [Maven](/maven.html) and [Gradle](/gradle.html).

- If you use Spoon for industrial purposes, please consider funding Spoon through a research contract with Inria (contact Martin Monperrus for this).

- If you use Spoon for academic purposes, please cite: Renaud Pawlak, Martin Monperrus, Nicolas Petitprez, Carlos Noguera, Lionel Seinturier. “[Spoon: A Library for Implementing Analyses and Transformations of Java Source Code](https://hal.archives-ouvertes.fr/hal-01078532/document)”. In Software: Practice and Experience, Wiley-Blackwell, 2015. Doi: 10.1002/spe.2346.

```latex
@article{pawlak:hal-01169705,
  TITLE = {Spoon: A Library for Implementing Analyses and Transformations of Java Source Code},
  AUTHOR = {Pawlak, Renaud and Monperrus, Martin and Petitprez, Nicolas and Noguera, Carlos and Seinturier, Lionel},
  JOURNAL = {Software: Practice and Experience},
  PUBLISHER = {Wiley-Blackwell},
  PAGES = {1155-1179},
  VOLUME = {46},
  URL = {https://hal.archives-ouvertes.fr/hal-01078532/document},
  YEAR = {2015},
  doi = {10.1002/spe.2346},
  url = {https://hal.archives-ouvertes.fr/hal-01078532/document}
}
```

## Support

You have different options:

* Open an issue on [Github](https://github.com/INRIA/spoon/issues) (preferred).
* Send an email to the [Spoon mailing list](http://lists.gforge.inria.fr/mailman/listinfo/spoon-discuss). 
* Post a question to [StackOverflow with tag inria-spoon](http://stackoverflow.com/tags/inria-spoon).

## News

Star Spoon on Github: [https://github.com/INRIA/spoon](https://github.com/INRIA/spoon). :-)

<!-- .* Marker comment. -->
- January 11, 2017: Spoon 5.5.0 is released. Happy new year!
- October 27, 2016: Spoon 5.4.0 is released.
- September 19, 2016: Spoon 5.3.0 is released.
- June 30, 2016: Spoon 5.2.0 is released.
- June 22, 2016: Spoon 5.1.1 is released.
- March 21, 2016: Spoon 5.1.0 is released.
- February 12, 2016: Spoon 5.0.2 is released.
- February 3, 2016: Spoon 5.0.1 is released.
- January 25, 2016: Spoon 5.0.0 is released.
- November 18, 2015: Spoon 4.4.1 is released.
- November 16, 2015: Spoon 4.4.0 is released.
- September 22, 2015: Spoon 4.3.0 is released.
- June 15, 2015: Spoon 4.2.0 is released.
- May 7, 2015: Spoon 4.1.0 is released.
- April 8, 2015: Spoon 4.0.0 is released.
- February 11, 2015: Spoon 3.1 is released.
- December 9, 2014: Spoon 3.0 is released.
- November 12, 2014: Spoon 2.4 is released.
- October 9, 2014: Spoon 2.3.1 is released.
- September 12, 2014: Spoon 2.1 is released.
- April 2, 2014: Spoon 2.0 is released.
- September 30, 2013: Spoon 1.6 is released.
- April 12, 2012: Spoon 1.5 is released.

## Download

### Jar file

[v{{site.spoon_release}} JAR](https://gforge.inria.fr/frs/?group_id=73) - [Javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs)

The source code to the Spoon and this website is [available on GitHub](https://github.com/INRIA/spoon).

### Maven

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

To know more about the usage of Spoon, you can read the documentation in the ["Usage" section](/command_line.html).

## Contributing

If you would like to contribute code you can do so through GitHub by forking the repository and sending a pull request.

When submitting code, please make every effort to follow existing conventions and style in order to keep the code as readable as possible. Please also make sure your code compiles by running `mvn clean verify`.

## License

Spoon is Free and Open Source (CeCILL-C license - French equivalent to LGPL).
