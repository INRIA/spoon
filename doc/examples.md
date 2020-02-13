---
title: Examples of Spoon Usages
keywords: examples
---

We provide examples for learning and teaching Spoon in <https://github.com/SpoonLabs/spoon-examples/>. Don't hesitate to propose new examples as pull-request!

The [HelloWorldProcessor example](https://github.com/SpoonLabs/spoon-examples/blob/master/src/main/java/fr/inria/gforge/spoon/HelloWorldProcessor.java) prints hello world with compile-time reflection.

## Program Analysis

The [CatchProcessor example](https://github.com/SpoonLabs/spoon-examples/blob/master/src/main/java/fr/inria/gforge/spoon/analysis/CatchProcessor.java) detects empty catch blocks.

The [ReferenceProcessor example](https://github.com/SpoonLabs/spoon-examples/blob/master/src/main/java/fr/inria/gforge/spoon/analysis/ReferenceProcessor.java) detects circular references between packages.

This [Factory example](https://github.com/SpoonLabs/spoon-examples/blob/master/src/main/java/fr/inria/gforge/spoon/analysis/FactoryProcessor.java) example detects wrong uses of the factory pattern.

## Program Transformation

### Transformation with API

The [NotNullProcessor example](https://github.com/SpoonLabs/spoon-examples/blob/master/src/main/java/fr/inria/gforge/spoon/transformation/notnullcheck/NotNullCheckAdderProcessor.java) adds a not-null check for all method parameters.

The [LogProcessor example](https://github.com/SpoonLabs/spoon-examples/blob/master/src/main/java/fr/inria/gforge/spoon/transformation/autologging/LogProcessor.java) is an example of Spoon for tracing, it adds a log statement when entering a method.

The [MutationProcessor example](https://github.com/SpoonLabs/spoon-examples/tree/master/src/main/java/fr/inria/gforge/spoon/transformation/mutation) randomly mutates some parts of the abstract syntax tree for [mutation testing](http://en.wikipedia.org/wiki/Mutation_testing).

### Transformation with Annotations 

The [Bound example](https://github.com/SpoonLabs/spoon-examples/tree/master/src/main/java/fr/inria/gforge/spoon/transformation/bound) adds runtime checks based on annotations

The [Database access example](https://github.com/SpoonLabs/spoon-examples/tree/master/src/main/java/fr/inria/gforge/spoon/transformation/dbaccess) shows how to use annotation processing to add persistence into a POJO, ie. to implement a simple URL with Spoon (also uses templates).

The [Nton example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/nton/?root=spoon) introduces a Nton design pattern (extension of singleton but for N instances) into a target class. It inserts static fields, methods, and initializer code into constructors.

The [Visitor example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/visitor/?root=spoon) implements a visitor pattern by automatically introducing an accept method in a visited type hierarchy.

The [Field Access example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/fieldaccess/?root=spoon) implements a refactoring that introduces setters and getters for the fields annotated with the Access annotation and that replaces all the direct accesses to these fields by calls to its new getters and setters.

### Transformation with Templates

The [RetryTemplate example](https://github.com/SpoonLabs/spoon-examples/tree/master/src/main/java/fr/inria/gforge/spoon/transformation/retry) creates retriable methods in case of exceptions if annotated by `@RetryOnFailure`.

### Transformation with Patterns

TBD, see <https://github.com/INRIA/spoon/issues/3140>

## Miscelanous

The [Distributed Calculus example](https://gforge.inria.fr/scm/viewvc.php/trunk/spoon-examples/src/main/java/spoon/examples/distcalc/?root=spoon) creates a fun new language for distributed computing using Java and Spoon.
