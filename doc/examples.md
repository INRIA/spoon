---
title: Examples
keywords: examples
last_updated: September 9, 2015
---

## Program Analysis

The [`CatchProcessor`](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/analysis/processing/CatchProcessor.java?view=markup&root=spoon) detects empty catch blocks.

The [`ReferenceProcessor`](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/analysis/processing/ReferenceProcessor.java?view=markup&root=spoon) detects circular references between packages.

This [`Factory`](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/factory/processing/FactoryProcessor.java?view=markup&root=spoon) example detects wrong uses of the factory pattern.

## Program Transformation

The [`NotNullProcessor`](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/notnull/NotNullCheckAdderProcessor.java?view=markup&root=spoon) adds a not-null check for all method parameters.

The [`MutationProcessor`](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/mutation/MutationProcessor.java?view=markup&root=spoon) randomly mutates some parts of the abstract syntax tree for [mutation testing](http://en.wikipedia.org/wiki/Mutation_testing).

## Annotation Processing

The [Nton example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/nton/?root=spoon) introduces a Nton design pattern (extension of singleton but for N instances) into a target class. It inserts static fields, methods, and initializer code into constructors.

The [Database access example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/dbaccess/processing/DBAccessProcessor.java?view=markup&root=spoon) shows how to use annotation processing to add persistence into a POJO.

The [Visitor example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/visitor/?root=spoon) implements a visitor pattern by automatically introducing an accept method in a visited type hierarchy.

The [Field Access example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/fieldaccess/?root=spoon) implements a refactoring that introduces setters and getters for the fields annotated with the Access annotation and that replaces all the direct accesses to these fields by calls to its new getters and setters.

## Miscelanous

The [Distributed Calculus example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/distcalc/?root=spoon) creates a fun new language for distributed computing using Java and Spoon.