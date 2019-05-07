---
title: Structural elements
tags: [meta-model]
keywords: structural, elements, ast, meta, model
---

A programming language can have different meta models. 
An abstract syntax tree (AST) or model, is an instance of a meta model. 
Each meta model -- and consequently each AST -- is more or less appropriate 
depending on the task at hand. For instance, the Java meta model of Sun's 
compiler (javac) has been designed and optimized for compilation to bytecode, 
while, the main purpose of the Java meta model of the Eclipse IDE (JDT) is to 
support different tasks of software development in an integrated manner 
(code completion, quick fix of compilation errors, debug, etc.).

Unlike a compiler-based AST (e.g. from javac), the Spoon meta model of Java is 
designed to be easily understandable by normal Java developers, so that they can 
write their own program analyses and transformations. The Spoon meta model is complete 
in the sense that it contains all the required information to derive compilable and 
executable Java programs (hence contains annotations, generics, and method bodies).

The Spoon meta model can be split in three parts.

- The structural part contains the declarations of the program elements, such as 
interface, class, variable, method, annotation, and enum declarations.
- The code part contains the executable Java code, such as the one found in method bodies.
- The reference part models the references to program elements (for instance a reference to a type).

As shown in the figure, all elements inherit from `CtElement` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/declaration/CtElement.html)) 
which declares a parent element denoting the containment relation in the source file. 
For instance, the parent of a method node is a class node. All names are prefixed by 
"CT" which means "compile-time".

As of Spoon 6.1.0, Spoon metamodel contains CtModule element to represent a module in Java 9, and 
CtModuleDirective to represent the different directives of the module.

:warning: The root of the model is then no longer an unnamed package, but an unnamed module. 

![Structural part of the Spoon Java 8 metamodel]({{ "/images/structural-elements.png" | prepend: site.baseurl }})