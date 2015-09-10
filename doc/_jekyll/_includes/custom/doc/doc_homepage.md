{% include linkrefs.html %}

## Overview 

Spoon is an open-source library that enables you to transform (see below) and analyze Java source code (see example) . Spoon provides a complete and fine-grained Java metamodel where any program element (classes, methods, fields, statements, expressions...) can be accessed both for reading and modification. Spoon takes as input source code and produces transformed source code ready to be compiled.

For documentation, there is the [Spoon technical report](https://hal.inria.fr/hal-01078532) and slides [here](http://www.monperrus.net/martin/lecture-slides-source-code-analysis-and-transformation.pdf).

If you use Spoon for academic purposes, please cite: Renaud Pawlak, Martin Monperrus, Nicolas Petitprez, Carlos Noguera, Lionel Seinturier. "Spoon v2: Large Scale Source Code Analysis and Transformation for Java". Technical Report hal-01078532, Inria. 2014.

```latex
@article{pawlak:hal-01169705,
  TITLE = {Spoon: A Library for Implementing Analyses and Transformations of Java Source Code},
  AUTHOR = {Pawlak, Renaud and Monperrus, Martin and Petitprez, Nicolas and Noguera, Carlos and Seinturier, Lionel},
  JOURNAL = {Software: Practice and Experience},
  PUBLISHER = {Wiley-Blackwell},
  PAGES = {na},
  YEAR = {2015},
  doi = {10.1002/spe.2346},
}
```

If you use Spoon for industrial purposes, please consider funding Spoon through a research contract with Inria (contact [Martin Monperrus](http://www.monperrus.net/martin/) for this).

Do you want to improve this site? pull requests on <https://github.com/INRIA/spoon/tree/website> are welcome!

## News

Star Spoon on Github: [https://github.com/INRIA/spoon](https://github.com/INRIA/spoon) :-)

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

## Using Spoon

To display the AST of a program contained in sourceFolder:

```bash
$ java -jar spoon-core-{{site.spoon_release}}-jar-with-dependencies.jar -i sourceFolder -g
```


To detect empty catch blocks, the following Spoon code has to be written:

```java
/**
 * Reports warnings when empty catch blocks are found.
 */
public class CatchProcessor extends AbstractProcessor<CtCatch> {

	public List<CtCatch> emptyCatchs = new ArrayList<CtCatch>();

	public void process(CtCatch element) {
		if (element.getBody().getStatements().size() == 0) {
			emptyCatchs.add(element);
			getFactory().getEnvironment().report(this, Severity.WARNING,
					element, "empty catch clause");
		}
	}

}
```

and launched as follows (the main class is spoon.Launcher): 

```bash
$ java -cp your-bin-folder:spoon-core-{{site.spoon_release}}-jar-with-dependencies.jar spoon.Launcher -i sourceFolder -p CatchProcessor
```

Spoon processes all source files of sourceFolder and writes to the resulting code to the folder "spooned".

## Download

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

## Contributing

If you have any problems with spoon or need help, you can send an email to the mailing list <a href="mailto:{{ site.email }}">{{ site.email }}</a> and if you would like to contribute code you can do so through GitHub by forking the repository and sending a pull request.

When submitting code, please make every effort to follow existing conventions and style in order to keep the code as readable as possible. Please also make sure your code compiles by running `mvn clean verify`.

## License

Spoon is Free and Open Source (CeCILL-C license - French equivalent to LGPL).