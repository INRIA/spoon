---
title: Processor for elements
tags: [processor]
keywords: processor, processing, elements
---

A program analysis is a combination of query and analysis code.
In Spoon, this conceptual pair is reified in a `processor`.
A Spoon processor is a class that focuses on the analysis of one 
kind of program elements. For instance, the processor at the end of 
this page presents a processor that analyzes a program to find 
empty catch blocks.

The elements to be analyzed (here catch blocks), are given by generic typing: 
the programmer declares the AST node type under analysis as class generics. 
The processed element type is automatically inferred through runtime introspection 
of the processor class. There is also an optional overridable method for querying 
elements at a finer grain.

The process method takes the requested element as input and does the analysis 
(here detecting empty catch blocks). At any time, you can interrupt the processing 
of the model with a call to `interrupt()` (this stops all processors, and proceeds 
with the next step which is usually pretty-printing the code to disk).

Since a real world analysis combines multiple queries, multiple processors can 
be used at the same time. The launcher applies them in the order they have been declared. 

Processors are implemented with a visitor design pattern applied to the Spoon 
Java model. Each node of the metamodel implements an `accept` method so that it 
can be visited by a visitor object, which can perform any kind of action, 
including modification. 

{{site.data.alerts.tip}}
 Spoon provides developers with an intuitive Java metamodel and concise abstractions 
 to query and process AST elements.
{{site.data.alerts.end}}

```java
package fr.inria.gforge.spoon.processors;

import org.apache.log4j.Level;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCatch;

/**
 * Reports warnings when empty catch blocks are found.
 */
public class CatchProcessor extends AbstractProcessor<CtCatch> {
	public void process(CtCatch element) {
		if (element.getBody().getStatements().size() == 0) {
			getFactory().getEnvironment().report(this, Level.WARN, element, "empty catch clause");
		}
	}
}
```
## Parallel Processor

Lets assume you want to use multiple cores for your processor. Spoon provides a simple high-level API for this task.
Using the CatchProcessor from before create a `AbstractParallelProcessor`. 

```java
			Processor<CtCatch> parallelProcessor = new AbstractParallelProcessor<CtCatch>(
				Arrays.asList(new CatchProcessor(), new CatchProcessor())) {};
```
Now you have the same processor behavior as before, but 2 parallel running processor.
You can upscale this pretty high, but keep in mind to not use more parallel processors than available cores for maximum speedup.
For more information about parallel processor and the API have a look in the [documentation](http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/spoon/processing/AbstractParallelProcessor.html). 