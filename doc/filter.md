---
title: Filter
tags: [quering]
keywords: quering, query, filter, ast, elements
last_updated: October 5, 2015
---

Spoon aims at giving developers a way to query code elements in 
one single line of code in the normal cases. Code query is Spoon 
is done in plain Java, in the spirit of an embedded DSL.
The information that can be queried is that of a well-formed typed AST.
For this, we provide the query API, based on the notion of `Filter` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/visitor/Filter.html)).
A Filter defines a predicate of the form of a `matches` method that 
returns `true` if an element is part of the filter.
A Filter is given as parameter to a depth-first search algorithm.
During AST traversal, the elements satisfying the matching predicate are 
given to the developer for subsequent treatment.
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