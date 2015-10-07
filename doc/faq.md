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
