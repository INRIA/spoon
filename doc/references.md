---
title: References
tags: [meta-model]
keywords: references, elements, ast, meta, model
---

The reference part of the meta model expresses the fact that program references 
elements that are not necessarily reified into the meta model 
(they may belong to third party libraries). For instance, an expression node 
returning a `String` is bound to a type reference to `String` and not to the 
compile-time model of String.java since the source code of `String` is (usually) 
not part of the application code under analysis.

In other terms, references are used by meta model elements to reference elements 
in a weak way. Weak references make it more flexible to construct and modify a 
program model without having to get strong references on all referred elements.

{{site.data.alerts.note}}
From Spoon 5.0.0, CtReference is a subclass of CtElement.
{{site.data.alerts.end}}

![References of the Spoon Java 8 metamodel]({{ "/images/references-elements.png" | prepend: site.baseurl }})

##  How are references resolved? 

References are resolved when the model is built, the resolved references are those 
that point to classes for which the source code is available in the Spoon input path.

## Do targets of references have to exist before you can reference them?

Since the references are weak, the targets of references do not have to exist before one references them. 

## How does this limit transforming code? 

The price to pay for this low coupling is that to navigate from one code element to another, 
one has to chain a navigation to the reference and then to the target. For instance, 
to navigate from a field to the type of the field, one writes `field.getType().getDeclaration()` ([javadoc](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/reflect/reference/CtTypeReference.html#getDeclaration--)).