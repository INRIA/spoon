---
title: Path
tags: [quering]
keywords: quering, query, path, ast, elements
---

`CtPath` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPath.html))
defines the path to a `CtElement` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtElement.html))
in a model, similarly to XPath for XML. For example, `.spoon.test.path.testclasses.Foo.foo#body#statement[index=0]` represents the first statement of the body of method foo.
A `CtPath`is based on: names of elements (eg `foo`), and roles of elements with respect to their parent (eg `body`).
A role is a relation between two AST nodes.
For instance, a "then" branch in a if/then/else is a role (and not an node). All roles can be found in `CtRole`. In addition, each getter or setter in the metamodel is annotated with its role.

## Evaluating paths

Paths are used to find code elements, from a given root elements.

```java
path = new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.foo#body#statement[index=0]");
List<CtElement> l = path.evaluateOn(root)
```

## Creating paths

### From an existing element

Method `getPath` in `CtElement` returns a path

```java
CtPath path = anElement.getPath();
```

### From a string

`CtPathStringBuilder` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathStringBuilder.html)), creates a path object from a string according to the following syntax:

- `.<name>` which denotes a child element with name `name`, eg `.fr.inria.Spoon` (the fully qualified name)
- `#<role>` which denotes all children on `CtRole` `role` .statements, `#body#statement[index=2]#else` is the else branch of the second statement of a method body
- `name=<somename>` - filter which accepts only elements with `somename`. E.g. `#field[name=abc]`
- `signature=<somesignature>` - filter which accepts only methods and constructors with signature `somesignature`.
  - Example of method signature: `#method[signature=compare(java.lang.String,java.lang.String)]`
  - Example of constructor signature: `#constructor[signature=(int)]`
- `index=<idx>` - fitler which accepts only idx-th element of the List. The first element has index 0. the fifth type memeber in a class `#typeMember[index=4]`

### From the API

The low-level `CtPathBuilder` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/path/CtPathBuilder.html)) defines a fluent api to build your path:

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
CtPath p1 = new CtPathBuilder().recursiveWildcard().name("toto").role(CtPathRole.DEFAULT_VALUE).build();
// equivalent to
CtPath p2 = new CtPathStringBuilder().fromString(".**.toto#default_value").build();

// takes all elements named "toto" in the project. 
new CtPathBuilder().recursiveWildcard().name("toto")

// takes the first element named "toto", a package or a class in the default package, at the root of your project.
new CtPathBuilder().name("toto").recursiveWildcard()
```
