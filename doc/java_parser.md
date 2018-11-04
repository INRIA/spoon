---
title: JavaParser 
keywords: JavaParser
---

Here is JavaParser github project: [JavaPaser](https://github.com/javaparser/javaparser)

## Differences between JavaParser and Spoon

Spoon and [JavaParser](https://github.com/javaparser/javaparser) can both be used for analyzing and transforming Java source code.

### Parsing

Spoon is using Eclipse JDK compiler to build AST of sources, then Spoon AST is then made from compiler AST.
JavaParser parses java source on their own. So JavaParser is faster, but all the heavy work (resolving of fully qualified type names, detection of implicit elements, ...) is done by JavaParser (@ftomassetti could you add there some comment about status)

### AST model

JavaParser AST is near to source code representation. You can find AST nodes like `EnclosedExpr`, which represents brackets like `(x+y)`or `Name`, which represents any java identifier. 
Spoon's AST is pure java semantic model. It means it contains nodes, which represent the same **concepts**, which are used by java programmer when s/he thinks about java program. Spoon AST contains also implicit elements, which are not visible in java source at all. For example this code

```java
if (something) 
	list = (List<String>) new ArrayList<>(FIELD_COUNT);
```

and this code

```java
if (something) {
	OuterType.this.list = (((java.util.List<java.lang.String>) new java.util.ArrayList<java.lang.String>(Constants.FIELD_COUNT));
}
```

has exactly same AST node elements. The only difference is that some nodes of AST model of first source code are marked as `implicit` 


### Comparison about common features:

| Aspect | Spoon | JavaParser |
| ------ | ------- | ------------ |
| Declaration | Native navigation from [usage to declaration](http://spoon.gforge.inria.fr/references.html) | Supported in the [symbol resolver](https://github.com/javaparser/javasymbolsolver) |
|License| [CeCILL-C](http://www.cecill.info/licences/Licence_CeCILL-C_V1-en.html) | LGPL License or Apache License |
|Comments| Stored and attached to elements | Stored and attached to elements |

### Unique features of JavaParser

* JavaParser is much faster with building of AST.
* JavaParser is lighter. Spoon has transitive dependencies to Eclipse JDK compiler and others.

### Unique features of Spoon:

* Spoon provides a way to detect method overriding including resolving of generic arguments on methods and types.
* Spoon provides a way to manipulate library classes as normal Spoon object (so called [shadow classes built using runtime reflection](http://spoon.gforge.inria.fr/reflection.html)). Spoon can access even method bodies by decompiling byte code.
* Spoon provides a [statically typed template engine](http://spoon.gforge.inria.fr/template_definition.html) called Patterns
* Spoon Patterns can be used to search for code which [matches](http://spoon.gforge.inria.fr/matcher.html) the [Pattern](http://spoon.gforge.inria.fr/pattern.html). Result is a found code + Pattern parameter values 
* Spoon Patterns can be used to [generate code](http://spoon.gforge.inria.fr/pattern.html#generator). As input there is Pattern + Pattern parameter values and output is generated code.
* Spoon Patterns can be used to bulk refactoring of code. One Pattern matches old code and delivers parameter values and second Pattern generates new code with the before collected parameter values.
* Spoon gives you [paths](http://spoon.gforge.inria.fr/path.html) to uniquely identify source code elements
* Spoon supports navigation between usage and declaration, a feature very important to us
* Spoon provides a [query engine](http://spoon.gforge.inria.fr/filter.html).
* Spoon's AST is separated into an API (a set of interfaces) and an implementation. The API is self-contained making it easy to extend the AST if needed. JavaParser provides an final implementation for its AST.
* Spoon allows to compile the source code. Very nice in conjunction with AST-transformation.
* Spoon can incrementally update its AST

Even if Spoon has longer list of features, it doesn't mean it is better for all clients. 
A lot of features of Spoon and JavaParser are not listed here, because they are nearly same.
Both tools are mature. It depends on client's needs, which tool fits better.
And as you can see on number of github starts of JavaParser project many clients are satisfied with lower amount of features.

Do you see other major differences?
Did you tried one tool and then switched to the second? Let us know:
* other clients might be interested in your opinion.
* contributors of Spoon and JavaParser needs to know that too, so they can improve.
Please create a pull request or issue here [Spoon](https://github.com/INRIA/spoon)
or here [JavaPaser](https://github.com/javaparser/javaparser).