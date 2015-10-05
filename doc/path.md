---
title: Path
tags: [quering]
keywords: quering, query, path, ast, elements
last_updated: October 5, 2015
---

`CtPath` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPath.html)) 
defines the path to a `CtElement` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtElement.html)) 
in a model respectively to another element. A `CtPath` can also be used 
to make a query to get elements and is based on three concepts: 
names of elements, types of elements and roles of code elements.

A role is a relation between two AST nodes, encoded as an AST node field.
For instance, a "then" branch in a if/then/else is a role (and not an node). 

To build a path, you have two possibilities: `CtPathBuilder` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathBuilder.html)) 
and `CtPathStringBuilder` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathStringBuilder.html)).
`CtPathBuilder` defines a fluent api to build  your path. 
`CtPathStringBuilder` creates a path object from a string according to a 
syntax inspired from XPath and CSS selectors.

## CtPathStringBuilder

`CtPathStringBuilder` exposes only one method to build a path from a string. 

- `fromString(String)` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathStringBuilder.html#fromString-java.lang.String-)) 
builds a path from a string representation.

For instance, if we want the first statement in the body of method `foo`, declared 
in the class `spoon.test.path.Foo`. 

```java
new CtPathStringBuilder().fromString(".spoon.test.path.Foo.foo#body[index=0]");
```

## CtPathBuilder

`CtPathBuilder` exposes the following methods:

- `name(String, String[])` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathBuilder.html#name-java.lang.String-java.lang.String:A...-)) 
adds a name matcher to the current path.
- `type(Class, String[])` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathBuilder.html#type-java.lang.Class-java.lang.String:A...-)) 
matches on element of a given type.
- `role(CtPathRole, String[])` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathBuilder.html#role-spoon.reflect.path.CtPathRole-java.lang.String:A...-)) 
matches on elements by their role (where `CtPathRole` gives all constants supported).
- `wildcard()` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathBuilder.html#wildcard--)) 
matches only on elements child of current one.
- `recursiveWildcard()` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathBuilder.html#recursiveWildcard--)) 
matches on any child and sub-children.

For instance, if we want all elements named by "toto" and with a default value in 
a project. Use `CtPathBuilder` like the example below.

```java
new CtPathBuilder().recursiveWildcard().name("toto").role(CtPathRole.DEFAULT_VALUE).build();
```

The corresponding string syntax would be:

```java
new CtPathStringBuilder().fromString("**.toto#defaultValue");
```

The order in instructions is important and have a meaning. These two pieces of code below have
a different meaning. The first one takes all toto elements in the project. The second takes 
the first element named by "toto" at the root of your project and after, makes a search recursively
in your project according to the rest of your path request.

```
new CtPathBuilder().recursiveWildcard().name("toto")
new CtPathBuilder().name("toto").recursiveWildcard()
```