---
title: Filter
tags: [quering]
keywords: quering, query, filter, ast, elements
last_updated: May 27, 2016
---

Spoon aims at giving developers a way to query code elements in 
one single line of code in the normal cases. Code query in Spoon 
is done in plain Java, in the spirit of an embedded DSL.
The information that can be queried is that of a well-formed typed AST.
For this, we provide the query API, based on the `QueryStep` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/chain/QueryStep.html)).
A QueryStep is a chain of the steps, which can be used to browse spoon model (AST).
The `QueryStep` can be create by `Query#query()` or `CtElement#query()`. Query can be constructed using these functional interfaces

QueryStep method | Functional class | Description
-----------------|--------------|--------
then(AsyncFunction fnc) | `AsyncFunction<T,R>#apply(T input, Consumer<R> output)` | a functional which gets an input element and can return zero, one or more output elements by calling of output.accept(result)
then(Function fnc) | `Function<T,R>#apply(T input):R` | a functional which gets an input element and returns element or collection of elements. Each not null element from the collection is then sent as input to the next `QueryStep`
then(Consumer consumer) | `Consumer<T>#accept(T input):void` | a functional which gets an input element. It is used to collect the results of browsing. 
matches(Predicate predicate) | `Predicate<T>#matches(T input):boolean` | if predicate.matches(input)==true then input is sent to next step.
scan(Predicate filter)  | `Predicate<T>#matches(T input):boolean` | scans all children of the step input element and the input elements which `predicate.matches(input)==true` are sent to next step

Spoon has defined several Filter classes, which are based on `Predicate` functional interface.
A Filter defines a predicate of the form of a `matches` method that 
returns `true` if an element is part of the filter.
There are three ways how to use Filter in query

1) `QueryStep#scan(Filter filter)` - scans all children of input element using a depth-first search algorithm, and each element which matches the filter is sent to next step

2) `QueryStep#then(AsyncFunction filter)` - input element is used to initialize Filter context. Then Filter scans all children from start node detected by this Filter. All the elements which matches the Filter are sent to next step. Only some filters, these which implements `AsyncFunction`, can supports this kind of Filtering.
  
3) `QueryStep#matches(Filter filter)` - if input element matches the Filter then it is sent to next step. No scanning is done 

This table gives an excerpt of built-in filters.

Filter class | Description
-------------|------------
`AbstractFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/AbstractFilter.html)) | defines an abstract filter based on matching on the element types.
`TypeFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/TypeFilter.html)) | returns all meta-model elements of a certain type (e.g. all assignment statements).
`AnnotationFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/AnnotationFilter.html)) | returns all elements annotated with a given annotation type.
`ReturnOrThrowFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/ReturnOrThrowFilter.html)) | returns all elements that ends the execution flow of a method.
`InvocationFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/InvocationFilter.html)) | returns all accesses to a given executable or any executable that overrides it.
`VariableAccessFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/VariableAccessFilter.html)) | returns all accesses to a given variable.
`FieldAccessFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/FieldAccessFilter.html)) | returns all accesses to a given field.
`ReferenceTypeFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/ReferenceTypeFilter.html)) | returns all references of a given type.
`DirectReferenceFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/DirectReferenceFilter.html)) | returns all references to a given element by using reference equality.
`NameFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/NameFilter.html)) | filters elements by name.
`RegexFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/RegexFilter.html)) | filters elements with a regular expression on the element's code.
`CompositeFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/CompositeFilter.html)) | defines a composite filter, which can compose several filters together by using `FilteringOperator` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/FilteringOperator.html)).
`OverridingMethodFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/OverridingMethodFilter.html)) | get all overriding methods from the method given.
`OverriddenMethodFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/OverriddenMethodFilter.html)) | get all overridden methods from the method given.
`LineFilter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/filter/LineFilter.html)) | get all elements that can be considered as line of code (e.g. directly contained in a block, or a then statement).

See below a code example about the usage of these filters. Three filters of 
them are used. The first returns all AST nodes of type `CtAssignment` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtAssignment.html)).
The second one selects all deprecated classes. The last one is a user-defined 
filter that only matches public fields across all classes.

```java
// collecting all assignments of a method body
list1 = methodBody.getElements(new TypeFilter(CtAssignment.class));

// collecting all deprecated classes
list2 = rootPackage.getElements(new AnnotationFilter(Deprecated.class));

// creating a custom filter to select all public fields
list3 = rootPackage.getElements(
  new AbstractFilter<CtField>(CtField.class) {
    @Override
    public boolean matches(CtField field) {
      return field.getModifiers.contains(ModifierKind.PUBLIC);
    }
  }
);
```

##Filter scope
The result of the filtering depends on the starting element of the AST traversal. In the examples above the starting elements were 
- methodBody element - it means only elements in body of method can be in result 
- rootPackage element - it means any element of the spoon model can be in result

The starting element was the Element on which getElements method is called.

Some filters returns correct result only if their AST traversal starts at correct element. 
For example 'OverriddenMethodFilter' filter should be processed

A) on root package if the method is public

B) on top level class if the method is private

If the Filter needs to influence the scanning scope then
 
1) the filter must implement interface `AsyncFunction`
 
2) the client must add the filter to the query by `QueryStep#then(AsyncFunction filter)` method

```java
CtMethod<?> method = ...//the input method for the filter
List<CtMethod<?>> overridenMethods = Query.query().then(new OverriddenMethodFilter(method)).list()
```

##Filter input element 
Note that some filters needs an input parameter. For example `OverriddenMethodFilter` needs a method, 
whose overridden methods has to be returned by searching using this filter. 
Such filters can implement interface `AsyncFunction`  
([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/chain/AsyncFunction.html)).
Then client can use following code to search for overridden methods

```java
CtMethod<?> method = ...//the input method for the filter
List<CtMethod<?>> overridenMethods = method.query().then(new OverriddenMethodFilter()).list()
```