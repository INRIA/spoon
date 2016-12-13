---
title: Using Spoon for Architecture Enforcement
tags: [getting-started]
---

Spoon can be used to check the architectural rules of your application.
For this, the idea is to write a standard Junit test case that loads the application code and checks the rule.
You only need to depend on spoon at testing time, ie `<scope>test</scope>` in Maven.

Example: rules on constructor usage
----------------------------------

For instance, let's imagine that you want to forbid the usage of `TreeSet`, in your code base, you would simply write a test case as follows:

```java
@Test
void noTreeSet() throws Exception {
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


Example: checking naming conventions for test cases
----------------------------

A common mistake is to forget to follow a naming convention. For instance, if you use Maven, all test classes must be named `Test*` or `*Test` in order to be run by Maven's standard test plugin `surefire` ([see doc](http://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html)). This rule simply reads:

```java
@Test
public void testGoodTestClassNames() throws Exception {
    SpoonAPI spoon = new Launcher();
    spoon.addInputResource("src/test/java/");
    spoon.buildModel();

    for (CtMethod<?> meth : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtMethod>(CtMethod.class) {
            @Override
            public boolean matches(CtMethod element) {
                    return super.matches(element) && element.getAnnotation(Test.class) != null;
            }
    })) {
            assertTrue("naming contract violated for "+meth.getParent(CtClass.class).getSimpleName(), meth.getParent(CtClass.class).getSimpleName().startsWith("Test") || meth.getParent(CtClass.class).getSimpleName().endsWith("Test"));
    }
}
```
