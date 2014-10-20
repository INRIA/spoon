---
layout: page
title: FAQ
permalink: /Doc/FAQ/
---

## Practical Information

### How to subscribe to Spoon's mailing list?

Go [here](http://lists.gforge.inria.fr/mailman/listinfo/spoon-discuss) and fill the form.

### How to access Spoon's repository?

Go [here](http://gforge.inria.fr/scm/?group_id=73) and follow the instructions.

## Basics

### How to run Spoon in Maven?

Add the right plugin (exec-maven-plugin) and execute task exec:java

{% highlight PowerShell %}
mvn exec:java
{% endhighlight %}

{% highlight xml %}
<build>
  <plugins>
      <plugin>
          <groupId>org.codehaus.mojo</groupId>
          <artifactId>exec-maven-plugin</artifactId>
          <version>1.2.1</version>
          <configuration>
              <mainClass>spoon.Launcher</mainClass>
              <arguments>
                  <argument>-i</argument>
                  <argument>[SOURCE_FOLDER]</argument>
                  <argument>-p</argument>
                  <argument>[PROCESSOR]</argument>
              </arguments>
          </configuration>
      </plugin>
  </plugins>
</build>
{% endhighlight %}

### How to get a Spoon model programmatically?

{% highlight java %}
Launcher spoon = new Launcher();
spoon.addInputResource(new FileSystemFolder(new File("src/test/resources/spoon/test/api")));
spoon.run();
Factory factory = spoon.getFactory();
// list all packages of the model
for(CtPackage p : factory.Package().getAll()) {
  System.out.println("package: "+p.getQualifiedName());
}
// list all classes of the model
for(CtSimpleType s : factory.Class().getAll()) {
  System.out.println("class: "+s.getQualifiedName());
}
{% endhighlight %}

### How to use Spoon in standalone mode?

You can download the Spoon standalone version (here) that includes the Eclipse JDT compiler and can be applied to any Java program using command-line style.

You need to specify a list of compiled processor types to apply to your program, and define the location of the source code to be processed.

{% highlight PowerShell %}
java -cp spoon-core-with-dependencies.jar spoon.Launcher -i sourceFolder -p CatchProcessor -v --compliance 6
{% endhighlight %}

Optionally, if you use templates, you have to specify the location of your template source files.

### How to use Spoon with Ant?

First, get the Spoon standalone version.

You need to set the Spoon jar and the compiled processors in the classpath reference (see ant taskdef).

{% highlight xml %}
<!-- define spoon task -->
<taskdef name="spoon" classname="spoon.SpoonTask" 
   classpathref= "classpath"/>

<!-- process some files -->
<spoon classpathref= "classpath" verbose= "true">
    <sourceSet dir= "${src}" includes= "x/y/z/src/" />
    <templateset dir= "${src}" includes= "x/y/z/template/" />
    <processor type= "x.y.z.MyProcessor1" />
    <processor type= "x.y.z.MyProcessor2" />
    <processor type= "x.y.z.MyProcessor3" />
    ...
</spoon>

<!-- process some files with a spoonlet rather than a processors list -->
<spoon classpathref= "classpath" verbose= "true" spoonlet="myspoonlet.jar">
    <sourceSet dir= "${src}" includes= "x/y/z/src/" />
</spoon>
{% endhighlight %}

### How to use Spoon with the Java launcher?

First, get the Spoon standalone version.

You can process program with the Java launcher. For instance, with the following command:

{% highlight PowerShell %}
java spoon.Launcher -i x/y/z/src/ -t x/y/z/template/ 
  -p x.y.z.MyProcessor1;x.y.z.MyProcessor2;x.y.z.MyProcessor3...
Usage: java <launcher name> [option(s)]
Options : 
  [-h|--help]
  [-v|--verbose]
        Output messages about what the compiler is doing
  [--vvv]
        Generate all debugging info
  [--compliance <compliance>]
        set java compliance level (1,2,3,4,5 or 6) (default: 5)
  [(-s|--spoonlet) <spoonlet>]
        List of spoonlet files to load
  [(-i|--input) <input>]
        List of path to sources files
  [(-p|--processors) <processors>]
        List of processor's qualified name to be used
  [(-t|--template) <template>]
        list of path to templates java files
  [(-o|--output) <output>]
        specify where to place generated java files (default: spooned)
  [--properties <properties>]
        Directory to search for spoon properties files
  [<class>]
        class to launch within the Spoon context (Main class)
  [arguments1 arguments2 ... argumentsN]
        parameters to be passed to the main method
  [--no]
        disable output printing
  [(-b|--build) <build>]
        specify where to place generated class files (default: spoonBuild)
  [-g|--gui]
        show spoon model after processing
{% endhighlight %}

### How to use Spoon as a Maven task?

A Maven plugin by David Bernard is on its way! It is avalaible in beta at this [link](http://alchim.sf.net/spoon-maven-plugin/).

### How to write your own processor(s)?

You need to get the standalone version Spoon jar ([here](http://spoon.gforge.inria.fr/Spoon/HomePage)) and add it to the build path of your Java project. Then you have to subclass the class [spoon.processing.AbstractProcessor](http://spoon.gforge.inria.fr/javadoc/spoon/spoon/processing/AbstractProcessor.html) and implement the process method. This class is parameterized by the type of program element you want to process. These types are those of the Spoon's Java metamodel defined in the [spoon.reflect.declaration package](http://spoon.gforge.inria.fr/javadoc/spoon/spoon/reflect/declaration/package-summary.html) and [spoon.reflect.code package](http://spoon.gforge.inria.fr/javadoc/spoon/spoon/reflect/code/package-summary.html). For example, to process all the Java program elements, you can write the following processor:

{% highlight java %}
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;

public class MyProcessor extends AbstractProcessor<CtElement> {
  public void process(CtElement element) {
    // do your processing here
  }
}
{% endhighlight %}

In the process method, you can access the currenly processed element passed as a parameter. Spoon automatically scan all the elements of the target program so that you do not have to implement the scanning yourself. On contrary to APT or JSR 269, you can also modify the program while scanning it. As an example, the following processor reports warnings when it meet undocumented public methods:

{% highlight java %}
import spoon.processing.AbstractProcessor;
import spoon.processing.Severity;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;

public class MyProcessor extends AbstractProcessor<CtMethod> {
  public void process(CtMethod method) {
    if (method.getModifiers().contains(ModifierKind.PUBLIC)
        && method.getDocComment() == null) {
      getFactory().getEnvironment().report(
           Severity.WARNING, method,"undocumented public method");
    }
  }
}
{% endhighlight %}

Once compiled, you can apply your processor direclty with the Java launcher or Ant (here), or you can package it in a Spoonlet in order to deploy it in Eclipse (here).

### How to internationalize a Spoonlet?

You can use a ResourceBundle named "spoonlet" in your code to internationalize your application. The processor attribute name and doc starting with '%' will be substitued by values found in your properties files with default locale.

More information about java internationalization [here](http://docs.oracle.com/javase/tutorial/i18n/).

Example:

__spoonlet_en.properties file:__

{% highlight PowerShell %}
Idiom = Class implements Cloneable
Idiom_doc = Class implements Cloneable but does not define or use clone method.
{% endhighlight %}

__extract of spoon.xml file:__

{% highlight xml %}
<processor active="true"
	name="%Idiom"
	class="spoon.vsuite.findbugs.am.Idiom"
	doc="%Idiom_doc"/>
{% endhighlight %}

__Sample java code:__

{% highlight java %}
ResourceBundle messages = ResourceBundle.getBundle("spoonlet",Locale.getDefault());
System.out.println(messages.getString("Idiom"));
{% endhighlight %}

### How to process annotations like with APT or JSR 269?

Spoon is fully compatible with annotations and you can process any program element, including annotations. Even simpler, you can declare that you want to process a certain annotation type by subclassing the special kind of processor [spoon.processing.AbstractAnnotationProcessor](http://spoon.gforge.inria.fr/javadoc/spoon/spoon/processing/AbstractAnnotationProcessor.html). For instance, to process the methods annotated with `@SuppressWarnings`:

{% highlight java %}
import spoon.processing.AbstractAnnotationProcessor;
import spoon.reflect.declaration.CtMethod;

public class MyAnnotationProcessor extends 
    AbstractAnnotationProcessor<SuppressWarnings,CtMethod> {
  public void process(SuppressWarnings a,CtMethod method) {
    // do the processing
  }
}
{% endhighlight %}

## Advanced

### How to preserve the transformed source code formatting and one-line comments?

There is a way to preserve comments and formatting of the existing code. However, it requires to use the code fragment API.

The idea of code fragments is that you indicate the changes you make in the code at the compilation unit level. For example to replace an expression e:

{% highlight java %}
public void process(CtExpression e) {
  // gets the compilation unit
  CompilationUnit cu=e.getPosition().getCompilationUnit();
  // creates an initialize the code fragment
  SourceCodeFragment fragment = new SourceCodeFragment();
  // the fragment will start to be printed out at the original 
  // start position of the expression
  fragment.position = e.getPosition().getSourceStart();
  // here we replace the whole expression
  // note: to insert, just leave replacementLength to 0 (default)
  fragment.replacementLength = 
    e.getPosition().getSourceEnd() - e.getPosition().getSourceStart();
  // here put whatever code you want to replace the expression with...
  fragment.code="...";
  // now just add the code fragment to the compilation unit
  cu.addSourceCodeFragment(fragment); 
  // you can add as many code fragments as you wish
}
{% endhighlight %}

You then just need to start Spoon with the -f option (--fragments). In this mode, all the chages in the AST will ignored and the source code will be changed only when code fragments are found on the compilation units. Note that this feature is not supported (yet) by the Eclipse plugin (so you need to run Spoon in standalone).

### How to implement and deploy configurable processors?

To create a configurable processor, you must define properties in your processors that Spoon will fill automatically with some values found in XML files. In processors, you define properties by annotating a field with @spoon.processing.Property. The field can be a primitive value (including java.lang.String), a reference, or a collection/array of those. To set the default value for the property, you can affect a value in the field declaration.

__Properties files in standalone mode__

In standalone mode (no Eclipse plugin), the properties can be stored in XML files (one for each processor) - see the DTD [here](http://spoon.gforge.inria.fr/pub/xml/properties.dtd). Property files should be named with the fully-qualified class name of processor (with the xml extension). You can configure the location of files with option `--properties` location in command-line or `<spoon properties="location">` with Ant.

__Properties in Spoonlets__

If you create a Spoonlet to package your processors, properties default values have to be defined in the 'spoon.xml' deployment descriptor. Note that Spoonlets can be used in standalone mode or with Eclipse or any other Spoonlet containers. Packaging Spoonlets for Eclipse is explained here.

__Examples__

This is a sample processor with properties:

{% highlight java %}
package test;

import java.util.Arrays;
import spoon.processing.AbstractManualProcessor;
import spoon.processing.Property;

public class Sample extends AbstractManualProcessor {
  @Property
  String[] spooners = new String[] { "none" };

  @Property
  double a;

  public void process() {
    System.out.println(Arrays.asList(spooners));
    System.out.println(a);
  }
}
{% endhighlight %}

And its associated property file:

{% highlight xml %}
<?xml version="1.0"?>
<!DOCTYPE note SYSTEM "http://spoon.gforge.inria.fr/pub/xml/properties.dtd">

<properties>
  <property name="spooners">
    <value>Yoda</value>
    <value>Padawan</value>
  </property>
  <property name="a" value="5.0" />
</properties>
{% endhighlight %}

If you run this processor with Spoon in standalone mode by specifying the location of the property file, you should get:

{% highlight PowerShell %}
[Yoda, Padawan]
5.0
Done
{% endhighlight %}

When using Spoonlets, the Spoonlet deployment descriptor would look like:

{% highlight xml %}
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE spoon SYSTEM "http://spoon.gforge.inria.fr/pub/xml/spoonlet.dtd" >

<spoon>
  <processor name="Properties test"
     class="test.Sample" active="true"
     doc="Prints out the contents of the properties.">
    <property name="spooners">
      <value>Yoda</value>
      <value>Padawan</value>
    </property>
    <property name="a" value="5.0" />
  </processor>
</spoon>
{% endhighlight %}

### How to implement program transformations with well-typed Templates?

See the section Generative Programming with Spoon of the Tutorial.

### How to prevent Annotation processors from consuming the annotations that they process?

By default, whenever an Annotation Processor processes a CtElement it will consume (delete) the processed annotation from it. If you want the annotation to be kept, override the init() method from the `AbstractAnnotationProcessor` class, and call the protected method `clearConsumedAnnotationTypes` like so:

{% highlight xml %}
@Override
public void init() {
	super.init();
	clearConsumedAnnotationTypes();
}
{% endhighlight %}

### How to compare and create type references in a type-safe way?

Use actual classes instead of strings.

{% highlight java %}
CtTypeReference t=...
if(t.getActualClass()==int.class) { ... }
Factory f=...
t=f.Type().createReference(int.class);
{% endhighlight %}