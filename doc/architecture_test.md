---
title: Using Spoon for Architecture Enforcement
tags: [getting-started]
---

In software, an architectural rule (aka architectural constraint) specifies a design decision on the application. Architectural rules cannot usually be expressed in the programming language itself.
Architectural rules can be written as AST analysis, which makes Spoon very appropriate to express and check them.
Since architectural rules must be automatically checked as often as possible, it's good to have them part of continuous integration.

To write an architectural rule in Spoon that is checked in CI, the idea is to write a standard Junit test case that loads the application code, express the rule and check it. Doing this only requires to depend on Spoon at testing time, ie `<scope>test</scope>` in Maven.

Example rule: never use the TreeSet constructor
----------------------------------

For instance, let's imagine that you want to forbid the usage of `TreeSet`'s constructor, in your code base, you would simply write a test case as follows:

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


Example rule: all test classes must start or end with "Test"
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

Example rule: all public methods must be documented
----------------------------

How to check that all public methods of the API contain proper Javadoc? One can also use Spoon to check documentation rules like this one.

```java
@Test
public void testDocumentation() throws Exception {
    SpoonAPI spoon = new Launcher();
    spoon.addInputResource("src/main/java/");
    spoon.buildModel();
    List<String> notDocumented = new ArrayList<>();
    for (CtMethod method : spoon.getModel().getElements(new TypeFilter<>(CtMethod.class))) {
            // now we see whether this should be documented
            if (method.hasModifier(ModifierKind.PUBLIC)
                && method.getTopDefinitions().size() == 0 // optional: only the top declarations should be documented (not the overriding methods which are lower in the hierarchy)
            )) {
                    // is it really well documented?
                    if (method.getDocComment().length() < 20) { // at least 20 characters
                            notDocumented.add(method.getParent(CtType.class).getQualifiedName() + "#" + method.getSignature());
                    }
            }
    }
    if (notDocumented.size() > 0) {
            fail(notDocumented.size()+" public methods should be documented with proper API documentation: \n"+StringUtils.join(notDocumented, "\n"));
    }
}
```

Related work in architecture enforcement
----------------------------------------

* [Architecture enforcement with Checkstyle](https://saturnnetwork.wordpress.com/2012/11/26/ultimate-architecture-enforcement-prevent-code-violations-at-code-commit-time/)
* [Sonar architecture rule engine](https://docs.sonarqube.org/display/SONARQUBE44/Architecture+Rule+Engine)
* [Archunit:  specify and assert architecture rules in plain Java](https://www.archunit.org/) ([Maven integration](https://github.com/societe-generale/arch-unit-maven-plugin))
* [jqassistant](https://jqassistant.org/) does architectural checking on top of Neo4J.
