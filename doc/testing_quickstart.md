---
title: Quickstart
tags: [quickstart]
keywords: testing, quickstart
---

## Overview

Spoon module testing is a Java library that provides a fluent api for writing assertions. 
Its main goal is to propose an easy way to test Java source code transformation.

This module is directly integrated in the spoon project and can be used as soon as the
dependency is specified in your project.

## Getting started

The Assert class is the entry point for assertion methods for different data types.
Each method in this class is a static factory for the type-specific assertion objects. 
The purpose of this class is to make test code more readable.

All methods in this class are named `assertThat` and take only one argument. For example, 
if you use the method `assertThat(File)`, you will be able to use the method 
`isEqualTo(File)` to check the equality between these two files.

```java
Assert.assertThat(new File("actual.java")).isEqualTo(new File("expected.java"));
```

Spoon provides a way to test transformations as follows.

```
import static spoon.testing.Assert.assertThat;
...
assertThat('Foo.java').withProcessor(new AProcessor()).isEqualTo('FooTransformed.java');
```

## Assertion Types

There are three types of assertions:

Assert type | Description
-------------|------------
FileAssert | Assertions available on a file.
CtElementAssert | Assertions available on a `CtElement`.
CtPackageAssert | Assertions available between two `CtPackage`.

## CtElement assertion example

Let's say that you have a processor which change the name of all fields by the name "j".

```java
class MyProcessor extends AbstractProcessor<CtField<?>> {
	@Override
	public void process(CtField<?> element) {
		element.setSimpleName("j");
	}
}
```

To check that the transformation is well done when you apply it on a class, see the following example

```java
final SpoonAPI spoon = new Launcher();
spoon.addInputResource("path/of/my/file/Foo.java");
spoon.run();

final CtType<Foo> type = spoon.getFactory().Type().get(Foo.class);
assertThat(type.getField("i")).withProcessor(new MyProcessor()).isEqualTo("public int j;");
```

Note that, method `withProcessor` takes as parameter either with a processor  instance, a processor class name, a class object.
