---
title: Using Spoon for Architecture Enforcement
tags: [getting-started]
---

Spoon can be used to check the architecture rules of your application.
For this, the idea is to write a test case that checks the rule.
For instance, let's imagine that you want to forbid the usage of `TreeSet`, in your code base, you would simply write a test case as follows:

```java
void noTreeSetInSpoon() throws Exception {
	// we don't use TreeSet, because they implicitly depend on Comparable (no static check, only dynamic checks)
	SpoonAPI spoon = new Launcher();
	spoon.addInputResource("src/main/java/");
	spoon.buildModel();
	
	assertEquals(0, spoon.getFactory().Package().getRootPackage().getElements(new AbstractFilter<CtConstructorCall>() {
		@Override
		public boolean matches(CtConstructorCall element) {
			return element.getType().getActualClass().equals(TreeSet.class);
		};
	}).size());
}
```

That's it! Every time you run the tests, incl. on your continuous integration server, the architectural rules are enforced.

For instance, you can check that you never return null, or always use an appropriate factory, or that all classes implementing an interface are in the same package.

Note that you only need to depend on spoon at testing time, ie `<scope>test</scope>` in Maven.

