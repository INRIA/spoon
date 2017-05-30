---
title: Examples
keywords: examples
---

## Program Analysis

The [`CatchProcessor`](https://github.com/SpoonLabs/spoon-examples/blob/master/src/main/java/fr/inria/gforge/spoon/analysis/CatchProcessor.java) detects empty catch blocks.

The [`ReferenceProcessor`](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/analysis/processing/ReferenceProcessor.java?view=markup&root=spoon) detects circular references between packages.

This [`Factory`](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/factory/processing/FactoryProcessor.java?view=markup&root=spoon) example detects wrong uses of the factory pattern.

## Program Transformation

The [`NotNullProcessor`](https://github.com/SpoonLabs/spoon-examples/blob/master/src/main/java/fr/inria/gforge/spoon/transformation/NotNullCheckAdderProcessor.java) adds a not-null check for all method parameters.

The [`LogProcessor`](https://github.com/SpoonLabs/spoon-examples/blob/master/src/main/java/fr/inria/gforge/spoon/transformation/LogProcessor.java) adds a log information when entering a method.

The [`MutationProcessor`](https://github.com/SpoonLabs/spoon-examples/tree/master/src/main/java/fr/inria/gforge/spoon/mutation) randomly mutates some parts of the abstract syntax tree for [mutation testing](http://en.wikipedia.org/wiki/Mutation_testing).

## Templates

The [`RetryTemplate`](https://github.com/SpoonLabs/spoon-examples/tree/master/src/main/java/fr/inria/gforge/spoon/transformation/retry) creates retriable methods in case of exceptions if annotated by `@RetryOnFailure`.

## Annotation Processing

The [Nton example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/nton/?root=spoon) introduces a Nton design pattern (extension of singleton but for N instances) into a target class. It inserts static fields, methods, and initializer code into constructors.

The [Database access example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/dbaccess/processing/DBAccessProcessor.java?view=markup&root=spoon) shows how to use annotation processing to add persistence into a POJO.

The [Visitor example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/visitor/?root=spoon) implements a visitor pattern by automatically introducing an accept method in a visited type hierarchy.

The [Field Access example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/fieldaccess/?root=spoon) implements a refactoring that introduces setters and getters for the fields annotated with the Access annotation and that replaces all the direct accesses to these fields by calls to its new getters and setters.

## Miscelanous

The [Distributed Calculus example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/distcalc/?root=spoon) creates a fun new language for distributed computing using Java and Spoon.
