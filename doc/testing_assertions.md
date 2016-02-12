---
title: Testing Spoon Transformations
tags: [assert, assertions]
keywords: testing, assert, assertion
last_updated: February 10, 2015
---

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
