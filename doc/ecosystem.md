---
title: Spoon Ecosystem
keywords: ecosystem
---

Spoon in Maven and Gradle
--------------------------

You can find plugins for [Maven](https://github.com/SpoonLabs/spoon-maven-plugin) and [Gradle](https://github.com/SpoonLabs/spoon-gradle-plugin) to run spoon on a target project.

Active projects using Spoon
---------------

Here are some active projects already using Spoon:

- [Spooet](https://github.com/SpoonLabs/spooet): A Spoon printer implemented with JavaPoet.
- [Metamutator](https://github.com/SpoonLabs/metamutator): A muta-mutation tool for Java.
- [Astor](https://github.com/SpoonLabs/astor): Evolutionary automatic software repair tool for Java. 
- [Nopol](https://github.com/SpoonLabs/nopol): Automatic repair system developed at the University of Lille and Inria.
- [Gumtree Spoon AST diff](https://github.com/SpoonLabs/gumtree-spoon-ast-diff): Computes the AST difference between two Spoon abstract syntax trees.
- [c2spoon](https://github.com/SpoonLabs/c2spoon): Loads C code as a Spoon model using srcml.
- [trebuchet](https://github.com/slipperyseal/trebuchet): a Java to C++ transpiler
- [CoCoSpoon](https://github.com/SpoonLabs/CoCoSpoon) and [CoCoSpoon-ui](https://github.com/SpoonLabs/CoCoSpoon-ui): Measuring and visualizing code coverage with Spoon
- [jmixer](https://github.com/seintur/jmixer) is an implementation of mixins for Java
- [syringe](https://github.com/DIVERSIFY-project/syringe) defines high-level transformation events
- Yours?

[SpoonLabs](https://github.com/SpoonLabs) is a Github group for centralizing all projects and experiments that use Spoon.


Old projects using Spoon
------------------------

[SpoonJDT](https://gforge.inria.fr/scm/viewvc.php/spoon/trunk/spoon-jdt-core/) provided an Eclipse plugin that allows for tight integration of Spoon within the Eclipse environment. With SpoonJDT, a set of validations and transformations packaged as a Spoonlet can occur on the fly and the reports are well-integrated into the Eclipse JDT.

[SpoonLoader](http://mir.cs.illinois.edu/~bdaniel3/spoonloader/) is A simple facade to load classes, modify their source code, recompile the changes, and execute the modified code at runtime (by Brett Daniel).

A previous maven plugin to run spoon on a target maven project was made by David Bernard ([link to the project](http://alchim.sf.net/spoon-maven-plugin/)).
