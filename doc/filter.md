---
title: Filter
tags: [quering]
keywords: quering, query, filter, ast, elements
last_updated: May 27, 2016
---

Spoon gives developers a way to query code elements.

Filters
-------

A Filter defines a predicate of the form of a `matches` method that
returns `true` if an element has to be selected in the filtering operation.
A Filter is given as parameter to `CtElement#getElement(Filter)` ()or `CtQueryable#filterChildren(Filter)`) which implements a depth-first search algorithm. During AST traversal, the elements satisfying the matching predicate are selected by the filter.

Here are code examples about the usage of filters. The first example returns all AST nodes of type `CtAssignment`.

```java
// collecting all assignments of a method body
list1 = methodBody.getElements(new TypeFilter(CtAssignment.class));
```

The second example selects all deprecated classes.

```java
// collecting all deprecated classes
list2 = rootPackage.getElements(new AnnotationFilter(Deprecated.class));
```

Now let's consider a user-defined filter that only matches public fields across all classes.

```java
// creating a custom filter to select all public fields
list3 = rootPackage.filterChildren(
  new AbstractFilter<CtField>(CtField.class) {
    @Override
    public boolean matches(CtField field) {
      return field.getModifiers.contains(ModifierKind.PUBLIC);
    }
  }
);
```

Queries
-------

The Query, introduced in Spoon 5.5 by Pavel Vojtechovsky, is an improved filter mechanism:

* matching can be done with a Java 8 lambda
* queries can be chained

`CtQueryable#filterChildren(Filter)` is a filtering query that can be chained:

```java
// collecting all methods of deprecated classes
list2 = rootPackage
    .filterChildren(new AnnotationFilter(Deprecated.class))
    .filterChildren(new TypeFilter(CtMethod.class)).list();
```

`CtQueryable#map(CtFunction)`enables you to give Java 8 lambda as query.
A boolean return value of the lambda tells whether the elements are selected for inclusion or not.

```java
// creating a custom filter to select all public fields using java 8 lambda
list3 = rootPackage.filterChildren((CtField field)->field.getModifiers.contains(ModifierKind.PUBLIC)).list();
```

If the CtFunction returns an object, this object is given as result to the query:

```java
// a query which processes all not deprecated methods of all deprecated classes
rootPackage
  .filterChildren((CtClass clazz)->clazz.getAnnotation(Deprecated.class)!=null)
  .map((CtClass clazz)->clazz.getMethods())
  .map((CtMethod<?> method)->method.getAnnotation(Deprecated.class)==null)
  .list()
;
```

Finally, if the CtFunction returns and Iterable or an Array then each item of the collection/array is sent to next query step or result.

