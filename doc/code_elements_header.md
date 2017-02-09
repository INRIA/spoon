---
title: Code elements
tags: [meta-model]
keywords: code, elements, ast, meta, model
---

Figure at the end of this page shows the meta model for Java executable code. 
There are two main kinds of code elements. 
First, statements `CtStatement` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtStatement.html)) 
are untyped top-level instructions that can be used directly in a block of code. 
Second, expressions `CtExpression` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtExpression.html)) 
are used inside the statements. For instance, a `CtLoop` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtLoop.html)) 
(which is a statement) points to `CtExpression` which expresses its boolean condition.

Some code elements such as invocations and assignments are both statements 
and expressions (multiple inheritance links). Concretely, this is translated as an 
interface `CtInvocation` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/code/CtInvocation.html)) 
inheriting from both interfaces `CtStatement` and `CtExpression`. 
The generic type of `CtExpression` is used to add static type-checking when transforming programs.

![Code part of the Spoon Java 8 metamodel]({{ "/images/code-elements.png" | prepend: site.baseurl }})