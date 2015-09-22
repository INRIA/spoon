---
title: FAQ
keywords: frequently asked questions, FAQ, question and answer, collapsible sections, expand, collapse
last_updated: September 9, 2015
---

## Practical Information

### How to subscribe to Spoon's mailing list?

Go [here](http://lists.gforge.inria.fr/mailman/listinfo/spoon-discuss) and fill the form. 

### How to access Spoon's source code repository?

See <https://github.com/INRIA/spoon/>.

## Basics

### Where is the Spoon metamodel?

The Spoon metamodel consists of all interfaces that are in packages `spoon.reflect.declaration` (structural part: classes, methods, etc.) and `spoon.reflect.code` (behavioral part: if, loops, etc.).

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

Once compiled, you can apply your processor direclty with the Java launcher or Ant (here)

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
### How to implement program transformations with well-typed templates?

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
