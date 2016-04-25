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

### What is the meaning of each digit in the version X.Y.Z of spoon?

- X is the digit for the major version (major new features or major incompatible API changes).
- Y is the digit for the minor version (bug fixes or minor API changes).
- Z is the digit for the critical bug fixes of the current major or minor version.

## Basics

### Where is the Spoon metamodel?

The Spoon metamodel consists of all interfaces that are in packages `spoon.reflect.declaration` (structural part: classes, methods, etc.) and `spoon.reflect.code` (behavioral part: if, loops, etc.).

### How to get a Spoon model programmatically?

```java
Launcher spoon = new Launcher();
spoon.addInputResource("src/test/resources/spoon/test/api");
spoon.run();
Factory factory = spoon.getFactory();
// list all packages of the model
for(CtPackage p : factory.Package().getAll()) {
  System.out.println("package: "+p.getQualifiedName());
}
// list all classes of the model
for(CtType<?> s : factory.Class().getAll()) {
  System.out.println("class: "+s.getQualifiedName());
}
```

## Advanced

### How to prevent Annotation processors from consuming the annotations that they process?

By default, whenever an Annotation Processor processes a CtElement it will consume (delete) the processed annotation from it. If you want the annotation to be kept, override the init() method from the `AbstractAnnotationProcessor` class, and call the protected method `clearConsumedAnnotationTypes` like so:

```xml
@Override
public void init() {
	super.init();
	clearConsumedAnnotationTypes();
}
```

### How to compare and create type references in a type-safe way?

Use actual classes instead of strings.

```java
CtTypeReference t=...
if(t.getActualClass()==int.class) { ... }
Factory f=...
t=f.Type().createReference(int.class);
```

## How to parametrized the JDT compiler arguments?

`SpoonModelBuilder` exposes a method named `build(JDTBuilder)`. This method compiles the target source code with data specified in the JDTBuilder parameter.

```java
final String[] builder = new JDTBuilderImpl() //
		.classpathOptions(new ClasspathOptions().classpath(TEST_CLASSPATH).bootclasspath(TEST_CLASSPATH).binaries(".").encoding("UTF-8")) //
		.complianceOptions(new ComplianceOptions().compliance(8)) //
		.annotationProcessingOptions(new AnnotationProcessingOptions().compileProcessors()) //
		.advancedOptions(new AdvancedOptions().continueExecution().enableJavadoc().preserveUnusedVars()) //
		.sources(new SourceOptions().sources(".")) //
		.build();
```

## How to use Spoon as an alternative reflection API?

Use `TypeFactory` as follows.

```java
CtType s = new TypeFactory().get(String.class);
System.out.println(s.getSimpleName());
```
