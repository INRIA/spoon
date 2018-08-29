---
title: Path
tags: [quering]
keywords: quering, query, path, ast, elements
---

`CtPath` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPath.html)) 
defines the path to a `CtElement` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtElement.html)) 
in a model. For example, `.spoon.test.path.testclasses.Foo.foo#body#statement[index=0]` represents the first statement of the body of method foo.

A `CtPath`is based on: names of elements (eg `foo`), and roles of elements with respect to their parent (eg `body`).
A role is a relation between two AST nodes.
For instance, a "then" branch in a if/then/else is a role (and not an node). All roles can be found in `CtRole`. In addition, each getter or setter in the metamodel is annotated with its role.

To build a path, there are several possibilities:
 
* method `getPath` in `CtElement`
* `CtPathStringBuilder` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathStringBuilder.html)).
it creates a path object from a string according to a 
syntax inspired from XPath and CSS selectors.
* the low-level `CtPathBuilder` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathBuilder.html)), it defines a fluent api to build  your path. 

To evaluate a path, ie getting the elements represented by it, use `evaluateOn(List<CtElement>)`

```java
path = new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.foo#body#statement[index=0]");
List<CtElement> l = path.evaluateOn(root)
```


## CtPathStringBuilder

`CtPathStringBuilder` exposes only one method to build a path from a string. 

- `fromString(String)` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathStringBuilder.html#fromString-java.lang.String-)) 
builds a path from a string representation.

For instance, if we want the first statement in the body of method `foo`, declared 
in the class `spoon.test.path.testclasses.Foo`. 

```java
new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.foo#body#statement[index=0]");
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
