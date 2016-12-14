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
For this, we provide the query API. 
* The creation of first or next query step is in responsibility of `CtQueryable` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/chain/CtQueryable.html)).
* The methods which executes the query are in `CtQuery` 
([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/chain/CtQuery.html)).
* The result of query can be consumed using functional interface `CtConsumer` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/chain/CtConsumer.html)).
* The core contract of each query step is based on `CtQueryStep` 
([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/chain/CtQueryStep.html)), 
which gets two input parameters:
  * a `CtConsumer` which is used by this query step to send produced results to the next step - next step consumes these results.
  * a `<T> input` which represents input of the query step. Usually an AST node - a object based on `CtElement` 

A query is a chain of the steps, which can be used to browse spoon model (AST). Spoon provides these types of query steps:
* filtering of child nodes of AST tree. All children nodes of input element, which matches the Filter are sent to next step. If the input element matches the filter then it is sent to next step too. Use `CtQueryable#filterChildren(Filter)`.
* filtering of nodes (but only of input node, not it's children). Use the `CtQueryable#map(CtFunction)`. 
The input element of query step is evaluated by your implementation of CtFunction 
and the returned value of type boolean tells whether input element 
is sent to next step (`return true`) or input element is skipped (`return false`).
* navigation from one node to another node or collection of nodes. 
Use the `CtQueryable#map(CtFunction)`. The input element of query step is evaluated 
by your implementation of CtFunction and the returned element is sent to next step. 
If returned element is the `Collection` or `array` then each item is sent to next step sequentially. 
This approach is good for navigation using existing spoon model API, 
with code of query step like `aQuery.map((CtType t)->t.getSuperclasses())` 
It is easy to use and it performs well, because the returned value contains only few items. 
But in case of complex query steps, 
which for example has to return all references to an package protected field, 
it is better to use `CtQueryable#map(CtQueryStep)`, 
which performs well on many thousands of returned elements without need to store all the results into memory. 
The results can be immediately processed by next query step. 
Use an existing implementations of `CtQueryStep` interface or implement one, which fits to your needs.

Spoon implements several Filters. 
A Filter defines a predicate of the form of a `matches` method that 
returns `true` if an element is part of the filter.
A Filter is given as parameter to a `CtQueryable#filterChildren(Filter)` method which implements depth-first search algorithm.
During AST traversal, the elements satisfying the matching predicate are 
given to the next query step for subsequent treatment.

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
list1 = methodBody.filterChildren(new TypeFilter(CtAssignment.class)).list();

// processing all assignments of a method body
methodBody.filterChildren(new TypeFilter(CtAssignment.class)).forEach((CtAssignment assignment)->{
	... process assignment ...
});

// collecting all deprecated classes
list2 = rootPackage.filterChildren(new AnnotationFilter(Deprecated.class)).list();

// creating a custom filter to select all public fields
list3 = rootPackage.filterChildren(
  new AbstractFilter<CtField>(CtField.class) {
    @Override
    public boolean matches(CtField field) {
      return field.getModifiers.contains(ModifierKind.PUBLIC);
    }
  }
);

//creating a custom filter to select all public fields using java 8 lambda
list3 = rootPackage.filterChildren((CtField field)->field.getModifiers.contains(ModifierKind.PUBLIC));

// a query, which processes all not deprecated methods of all deprecated classes
rootPackage
  .filterChildren((CtClass clazz)->clazz.getAnnotation(Deprecated.class)!=null)
  .map((CtClass clazz)->clazz.getMethods())
  .map((CtMethod<?> method)->method.getAnnotation(Deprecated.class)==null)
  .forEach((CtMethod<?> method)->{
  	... do something with found methods ...
  });
  
// query which returns overridden methods of deprecated methods
list4 = rootPackage
  .filterChildren((CtMethod method)->method.getAnnotation(Deprecated.class)!=null)
  .map(new OverriddenMethodFilter())
  .list();
```