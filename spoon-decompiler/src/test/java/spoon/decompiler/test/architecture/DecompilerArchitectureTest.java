package spoon.test.architecture;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DecompilerArchitectureTest {

	// this test contains all the architectural rules that are valid for
	// the whole src/main/java of spoon-decompiler
	// we put them in the same test in order to only build the full model once
	// Similar to ArchitectureTest of spoon-core
	@Test
	public void testSrcMainJava() {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setCommentEnabled(true);
		spoon.addInputResource("src/main/java/");

		// contract: all non-trivial public methods should be documented with proper API Javadoc
		spoon.buildModel();
		List<String> notDocumented = new ArrayList<>();
		for (CtMethod method : spoon.getModel().getElements(new TypeFilter<>(CtMethod.class))) {

			// now we see whether this should be documented
			if (method.hasModifier(ModifierKind.PUBLIC) // public methods should be documented
					&& !method.getSimpleName().startsWith("get") // all kinds of setters can be undocumented
					&& !method.getSimpleName().startsWith("set")
					&& !method.getSimpleName().startsWith("is")
					&& !method.getSimpleName().startsWith("add")
					&& !method.getSimpleName().startsWith("remove")
					&& method.getTopDefinitions().isEmpty() // only the top declarations should be documented (not the overriding methods which are lower in the hierarchy)
					&& (
					method.hasModifier(ModifierKind.ABSTRACT) // all interface methods and abstract class methods must be documented

							// GOOD FIRST ISSUE
							// ideally we want that **all** public methods are documented
							// so far, we have this arbitrary limit in the condition below (35)
							// because it's a huge task to document everything at once
							// so to contribute to Spoon, what you can do is
							// 1) you lower the threshold (eg 33)
							// 2) you run test `documentedTest`, it will output a list on undocumented methods
							// 3) you document those methods
							// 4) you run the test again to check that it passes
							// 4) you commit your changes and create the corresponding pull requests
							|| method.filterChildren(new TypeFilter<>(CtCodeElement.class)).list().size() > 35  // means that only large methods must be documented
			)) {

				// OK it should be properly documented

				// is it really well documented?
				if (method.getDocComment().length() <= 15) { // the Javadoc must be at least at least 15 characters (still pretty short...)
					notDocumented.add(method.getParent(CtType.class).getQualifiedName() + "#" + method.getSignature());
				}
			}
		}
		if (!notDocumented.isEmpty()) {
			fail(notDocumented.size() + " public methods should be documented with proper API documentation: \n" + StringUtils.join(notDocumented, "\n"));
		}

		// contract: Spoon's code never uses TreeSet constructor, because they implicitly depend on Comparable (no static check, only dynamic checks)
		List<CtConstructorCall> treeSetWithoutComparators = spoon.getFactory().Package().getRootPackage().filterChildren(new AbstractFilter<CtConstructorCall>() {
			@Override
			public boolean matches(CtConstructorCall element) {
				return element.getType().getActualClass().equals(TreeSet.class) && element.getArguments().isEmpty();
			}
		}).list();

		assertEquals(0, treeSetWithoutComparators.size());
	}

	@Test
	public void testGoodTestClassNames() {
		// contract: to be run by Maven surefire, all test classes must be called Test* or *Test
		// reference: "By default, the Surefire Plugin will automatically include all test classes with the following wildcard patterns:"
		// "**/Test*.java" and "**/*Test.java"
		// http://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/test/java/");
		spoon.buildModel();

		for (CtMethod<?> meth : spoon.getModel().getElements(new TypeFilter<CtMethod>(CtMethod.class) {
			@Override
			public boolean matches(CtMethod element) {
				return super.matches(element) && element.getAnnotation(Test.class) != null;
			}
		})) {
			assertTrue("naming contract violated for " + meth.getParent(CtClass.class).getSimpleName(), meth.getParent(CtClass.class).getSimpleName().startsWith("Test") || meth.getParent(CtClass.class).getSimpleName().endsWith("Test"));
		}

		// contract: the Spoon test suite does not depend on Junit 3 classes and methods
		// otherwise, intellij automatically selects the junit3 runner, finds nothing
		// and crashes with a dirty exception
		assertEquals(0, spoon.getModel().getElements(new TypeFilter<CtTypeReference>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference element) {
				CtMethod parent = element.getParent(CtMethod.class);
				return "junit.framework.TestCase".equals(element.getQualifiedName());
			}
		}).size());
	}

	@Test
	public void testStaticClasses() {
		// contract: helper classes only have static methods and a private constructor

		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/main/java/");
		spoon.buildModel();

		for (CtClass<?> klass : spoon.getModel().getElements(new TypeFilter<CtClass>(CtClass.class) {
			@Override
			public boolean matches(CtClass element) {
				return element.getSuperclass() == null && super.matches(element) && !element.getMethods().isEmpty()
						&& element.getElements(new TypeFilter<>(CtMethod.class)).stream().allMatch(x -> x.hasModifier(ModifierKind.STATIC));
			}
		})) {
			assertTrue("Utility class " + klass.getQualifiedName() + " is missing private constructor", klass.getElements(new TypeFilter<>(CtConstructor.class)).stream().allMatch(x -> x.hasModifier(ModifierKind.PRIVATE)));
		}
	}

	@Test
	public void testSpecPackage() {
		// contract: when a pull-request introduces a new package, it is made explicit during code review
		// when a pull-request introduces a new package, this test fails and the author has to explicitly declare the new package here

		Set<String> officialPackages = new TreeSet<>();
		officialPackages.add("spoon.decompiler");
		officialPackages.add("spoon");
		officialPackages.add(""); // root package

		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/main/java/");
		spoon.buildModel();
		final Set<String> currentPackages = new TreeSet<>();
		spoon.getModel().processWith(new AbstractProcessor<CtPackage>() {
			@Override
			public void process(CtPackage element) {
				currentPackages.add(element.getQualifiedName());
			}
		});

		assertSetEquals("you have created a new package or removed an existing one, please declare it explicitly in SpoonArchitectureEnforcerTest#testSpecPackage", officialPackages, currentPackages);
	}

	private static void assertSetEquals(String msg, Set<?> set1, Set<?> set2) {
		if (set1 == null || set2 == null) {
			throw new IllegalArgumentException();
		}

		if (set1.size() != set2.size()) {
			throw new AssertionError(msg + "\n\nDetails: " + computeDifference(set1, set2));
		}

		if (!set1.containsAll(set2)) {
			throw new AssertionError(msg + "\n\nDetails: " + computeDifference(set1, set2));
		}
	}

	private static String computeDifference(Set<?> set1, Set<?> set2) {
		Set<String> results = new HashSet<>();

		for (Object o : set1) {
			if (!set2.contains(o)) {
				results.add("Missing package " + o + " in computed set");
			} else {
				set2.remove(o);
			}
		}

		for (Object o : set2) {
			results.add("Package " + o + " presents in computed but not expected set.");
		}
		return StringUtils.join(results, "\n");
	}
}
