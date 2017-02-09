---
title: Processor for annotations
tags: [processor]
keywords: processor, processing, annotations
---

We now discuss how Spoon deals with the processing of annotations. 
Java annotations enable developers to embed metadata in their programs. 
Although by themselves annotations have no explicit semantics, they can 
be used by frameworks as markers for altering the behavior of the programs 
that they annotate. This interpretation of annotations can result, for 
example, on the configuration of services provided by a middleware 
platform or on the alteration of the program source code. 

Annotation processing is the process by which a pre-processor modifies an 
annotated program as directed by its annotations during a pre-compilation phase.
The Java compiler offers the possibility of compile-time processing of annotations 
via the API provided under the `javax.annotation.processing` package. Classes 
implementing the `javax.annotation.processing.Process` interface are used by the 
Java compiler to process annotations present in a client program. 
The client code is modeled by the classes of the `javax.lang.model` package 
(although Java 8 has introduced finer-grained annotations, but not on any 
arbitrary code elements). It is partially modeled: only types, methods, fields and 
parameter declarations can carry annotations. Furthermore, the model does not allow 
the developer to modify the client code, it only allows adding new classes.

The Spoon annotation processor overcomes those two limitations: it can handle 
annotations on any arbitrary code elements (including within method bodies), and it 
supports the modification of the existing code.


## Annotation Processing with Spoon

Spoon provides developers with a way to specify the analyses and transformations 
associated with annotations. Annotations are metadata on code that start with `@` in Java.
For example, let us consider the example of a design-by-contract  annotation. 
The annotation `@NotNull`, when placed on arguments of a method, will ensure that the argument 
is not null when the method is executed. The code below shows both the definition of the 
`NotNull` annotation type, and an example of its use.

```java
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.SOURCE)
public @interface NotNull{}

class Person{
	public void marry(@NotNull Person so){
		if(!so.isMarried())
			// Marrying logic...
	}
}
```

The `NotNull` annotation type definition carries two meta-annotations (annotations on annotation 
definitions) stating which source code elements can be annotated (line 1), and that the annotation 
is intended for compile-time processing (line 2). The `NotNull` annotation is used on the argument 
of the `marry` method of the class `Person`. Without annotation processing, if the method `marry` 
is invoked with a `null` reference, a `NullPointerException` would be thrown by the Java virtual 
machine when invoking the method `isMarried` in line 7.

The implementation of such an annotation would not be straightforward using Java's processing API 
since it would not allow us to just insert the NULL check in the body of the annotated method. 


## The Annotation Processor Interface

In Spoon, the full code model can be used  for compile-time annotation processing. To this end, 
Spoon provides a special kind of processor called `AnnotationProcessor` whose interface is:

```java
public interface AnnotationProcessor<A extends Annotation, E extends CtElement> extends Processor<E> {
	void process(A annotation, E element);
	boolean inferConsumedAnnotationType();
	Set<Class<? extends A>> getProcessedAnnotationTypes();
	Set<Class<? extends A>> getConsumedAnnotationTypes();
	boolean shoudBeConsumed(CtAnnotation<? extends Annotation> annotation);
}
```

Annotation processors extend normal processors by stating the annotation type those elements must 
carry (type parameter `A`), in addition of stating the kind of source code element they process 
(type parameter `E`). The `process` method (line 4) receives as arguments both the CtElement and the 
annotation it carries. The remaining four methods (`getProcessedAnnotationTypes`, `getConsumedAnnotationTypes`, 
`inferConsumedAnnotationTypes` and `shoudBeConsumed`) configure the visiting of the AST during annotation 
processing. The Spoon annotation processing runtime is able to infer the type of annotation a processor 
handles from its type parameter `A`. This restricts each processor to handle a single annotation. To avoid this 
restriction, a developer can override the `inferConsumedAnnotationType()` method to return `false`. When doing 
this, Spoon queries the `getProcessedAnnotationTypes()` method to find out which annotations are handled by the 
processor. Finally, the `getConsumedAnnotationTypes()` returns the set of processed annotations that are to be 
consumed by the annotation processor. Consumed annotations are not available in later processing rounds. 
Similar to standard processors, Spoon provides a default abstract implementation for annotation processors: 
`AbstractAnnotationProcessor`. It provides facilities for maintaining the list of consumed and processed annotation 
types, allowing the developer to concentrate on the implementation of the annotation processing logic. 

Going back to our `@NotNull` example, we implement a Spoon annotation processor that processes and consumes 
`NotNull` annotated method parameters, and modifies the source code of the method by inserting an `assert` statement 
that checks that the argument is not null. 

```java
class NotNullProcessor extends AbstractAnnotationProcessor<NotNull, CtParameter> {
	@Override
	public void process(NotNull anno, CtParameter param){
		CtMethod<?> method = param.getParent(CtMethod.class);
		CtBlock<?> body = method.getBlock();
		CtAssert<?> assertion = constructAssertion(param.getSimpleName());
		body.insertBegin(assertion);
	}
}
```

The `NotNullProcessor` leverages the default implementation provided by the `AbstractAnnotationProcessor` and binds the 
type variables representing the annotation to be processed and the annotated code elements to `NotNull` and `CtParameter` 
respectively. The actual processing of the annotation is implemented in the `process(NotNull,CtParameter)` method 
(lines 10-13). Annotated code is transformed by navigating the AST up from the annotated parameter to the owner method, 
and then down to the method's body code block (lines 10 and 12). The construction of the `assert` statement is delegated 
to a helper method `constructAssertion(String)`, taking as argument the name of the parameter to check. This helper method 
constructs an instance of `CtAssert` (by either programmatically constructing the desired boolean expression. Having obtained 
the desired assert statement, it is injected at the beginning of the body of the method. 

More complex annotation processing scenarios can be tackled with Spoon. For example, when using the `NotNull` annotation, 
the developer is still responsible for manually inspecting which method parameters to place the annotation on. 
A common processing pattern is then to use regular Spoon processors to `auto-annotate` the application's source code. 
Such a processor, in our running example, can traverse the body of a method, looking for expressions that send messages 
to a parameter. Each of these expressions  has as hypothesis that the parameter's value is not null, and thus should result 
in the parameter being annotated with `NotNull`.

With this processing pattern, the programmer can use an annotation processor in two ways: either by explicitly and manually 
annotating the base program, or by using a processor that analyzes and annotates the program for triggering annotation processors 
in an automatic and implicit way. This design decouples the program analysis from the program transformation logics, and leaves 
room for manual configuration.
