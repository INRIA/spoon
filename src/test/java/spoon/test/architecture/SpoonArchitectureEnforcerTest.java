/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.test.architecture;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.metamodel.Metamodel;
import spoon.processing.AbstractManualProcessor;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.adaption.TypeAdaptor;
import spoon.testing.utils.ModelTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static spoon.metamodel.ConceptKind.ABSTRACT;

public class SpoonArchitectureEnforcerTest {

	private static CtModel spoonSrcMainModel;
	private static Factory spoonSrcMainFactory;

	@BeforeAll
	static void beforeAll() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(17);
		launcher.addInputResource("src/main/java/");
		spoonSrcMainModel = launcher.buildModel();
		spoonSrcMainFactory = launcher.getFactory();
	}

	@Test
	public void statelessFactory() {
		// the factories must be stateless
		for (CtType t : spoonSrcMainFactory.Package().getRootPackage().getElements(new AbstractFilter<CtType>() {
			@Override
			public boolean matches(CtType element) {
				return super.matches(element)
						&& element.getSimpleName().contains("Factory");
			}
		})) {
			for (Object o : t.getFields()) {
				CtField f = (CtField) o;
				if ("factory".equals(f.getSimpleName())) {
					continue;
				}
				if (f.hasModifier(ModifierKind.FINAL) || f.hasModifier(ModifierKind.TRANSIENT)) {
					continue;
				}

				fail("architectural constraint: a factory must be stateless");
			}
		}
	}

	@Test
	public void testFactorySubFactory() {
		// contract:: all subfactory methods must also be in the main factory
		// this is very important for usability and discoverability
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/main/java/spoon/reflect/factory");
		class SanityCheck { int val = 0; }
		SanityCheck sanityCheck = new SanityCheck();
		launcher.addProcessor(new AbstractManualProcessor() {
			@Override
			public void process() {
				CtType factoryImpl = getFactory().Interface().get(Factory.class);
				CtPackage factoryPackage = getFactory().Package().getOrCreate("spoon.reflect.factory");
				CtInterface itf = getFactory().Interface().create("MegaFactoryItf");
				CtClass impl = getFactory().Class().create("MegaFactory");
				for (CtType<?> t : factoryPackage.getTypes()) {
					if (t.getSimpleName().startsWith("Mega")) {
						continue;
					}

					for (CtMethod<?> m : t.getMethods()) {
						// we check only public methods
						if (m.hasModifier(ModifierKind.PUBLIC) == false) {
							continue;
						}
						// we only consider factory methods
						if (!m.getSimpleName().startsWith("create")) {
							continue;
						}

						// too generic, what should we create??
						if ("create".equals(m.getSimpleName())) {
							String simpleNameType = m.getType().getSimpleName().replace("Ct", "");
							CtMethod method = m.clone();

							method.setSimpleName("create" + simpleNameType);
							assertTrue(factoryImpl.hasMethod(method), method.getSignature() + " (from " + t.getQualifiedName() + ") is not present in the main factory");
							continue;
						}

						// too generic, is it a fieldref? an execref? etc
						if ("createReference".equals(m.getSimpleName())) {
							continue;
						}

						if (m.getModifiers().contains(ModifierKind.ABSTRACT)) {
							continue;
						}

						sanityCheck.val++;

						// the core assertion
						assertTrue(factoryImpl.hasMethod(m), m.getSignature() + " is not present in the main factory");
					}
				}
			}
		});
		launcher.run();
		assertTrue(sanityCheck.val > 100);
	}

	// this test contains all the architectural rules that are valid for the whole src/main/java
	// we put them in the same test in order to only build the full model once
	@Test
	public void testSrcMainJava() {
		// contract: all non-trivial public methods should be documented with proper API Javadoc
		List<String> notDocumented = new ArrayList<>();
		for (CtMethod method : spoonSrcMainModel.getElements(new TypeFilter<>(CtMethod.class))) {

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
							// so far, we have this arbitrary limit in the condition below (32)
							// because it's a huge task to document everything at once
							// so to contribute to Spoon, what you can do is
							// 1) you lower the threshold (eg 30)
							// 2) you run test `documentedTest`, it will output a list on undocumented methods
							// 3) you document those methods
							// 4) you run the test again to check that it passes
							// 4) you commit your changes and create the corresponding pull requests
							|| method.filterChildren(new TypeFilter<>(CtCodeElement.class)).list().size() > 32  // means that only large methods must be documented
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
		List<CtConstructorCall> treeSetWithoutComparators = spoonSrcMainFactory.Package().getRootPackage().filterChildren(new AbstractFilter<CtConstructorCall>() {
			@Override
			public boolean matches(CtConstructorCall element) {
				return element.getType().getActualClass().equals(TreeSet.class) && element.getArguments().isEmpty();
			}
		}).list();

		assertEquals(0, treeSetWithoutComparators.size());
		// contract: every private method in spoon must be called.
		checkPrivateMethodInvocations(spoonSrcMainModel);
		// contract: every private field in spoons code is useful. Useful means it has a read.
		checkFields(spoonSrcMainModel);

	}

	@Test
	public void metamodelPackageRule() {
		// all implementations of the metamodel classes have a corresponding interface in the appropriate package
		List<String> exceptions = Arrays.asList("CtTypeMemberWildcardImportReferenceImpl", "InvisibleArrayConstructorImpl");

		SpoonAPI implementations = new Launcher();
		implementations.addInputResource("src/main/java/spoon/support/reflect/declaration");
		implementations.addInputResource("src/main/java/spoon/support/reflect/code");
		implementations.addInputResource("src/main/java/spoon/support/reflect/reference");
		implementations.buildModel();

		SpoonAPI interfaces = new Launcher();
		interfaces.addInputResource("src/main/java/spoon/reflect/declaration");
		interfaces.addInputResource("src/main/java/spoon/reflect/code");
		interfaces.addInputResource("src/main/java/spoon/reflect/reference");
		interfaces.addInputResource("src/main/java/spoon/support/DefaultCoreFactory.java");
		interfaces.buildModel();

		for (CtType<?> implType : implementations.getModel().getAllTypes()) {
			if (!exceptions.contains(implType.getSimpleName())) {
				String impl = implType.getQualifiedName().replace(".support", "").replace("Impl", "");
				CtType interfaceType = interfaces.getFactory().Type().get(impl);
				// the implementation is a subtype of the superinterface
				assertTrue(implType.getReference().isSubtypeOf(interfaceType.getReference()));
			}
		}
	}

	@Test
	public void testGoodTestClassNames() {
		// contract: to be run by Maven surefire, all test classes must be called Test* or *Test
		// reference: "By default, the Surefire Plugin will automatically include all test classes with the following wildcard patterns:"
		// "**/Test*.java" and "**/*Test.java"
		// http://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html
		for (CtMethod<?> meth : spoonSrcMainModel.getElements(new TypeFilter<CtMethod>(CtMethod.class) {
			@Override
			public boolean matches(CtMethod element) {
				return super.matches(element) && element.getAnnotation(Test.class) != null;
			}
		})) {
			CtType<Object> topLevelType = meth.getParent(CtClass.class).getTopLevelType();
			assertTrue(
					topLevelType.getSimpleName().startsWith("Test")
							|| topLevelType.getSimpleName().endsWith("Test"),
					"naming contract violated for " + meth.getParent(CtClass.class).getSimpleName());
		}

		// contract: the Spoon test suite does not depend on Junit 3 classes and methods
		// otherwise, intellij automatically selects the junit3 runner, finds nothing
		// and crashes with a dirty exception
		assertEquals(0, spoonSrcMainModel.getElements(new TypeFilter<CtTypeReference>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference element) {
				CtMethod parent = element.getParent(CtMethod.class);
				return "junit.framework.TestCase".equals(element.getQualifiedName());
			}
		}).size());

		// contract: all public methods of those classes are properly tested in a JUnit test
//		List<String> l = new ArrayList<>();
//		l.add("spoon.pattern.PatternBuilder");
//		l.add("spoon.pattern.Pattern");
//		List<String> errorsMethods = new ArrayList<>();
//		for (String klass : l) {
//			CtType<?> ctType = spoon.getFactory().Type().get(Class.forName(klass));
//			for (CtMethod m : ctType.getMethods()) {
//				if (!m.hasModifier(ModifierKind.PUBLIC)) continue;
//
//				if (spoon.getModel().getElements(new Filter<CtExecutableReference>() {
//					@Override
//					public boolean matches(CtExecutableReference element) {
//						return element.getExecutableDeclaration() == m;
//					}
//				}).size() == 0) {
//					errorsMethods.add(klass+"#"+m.getSimpleName());
//				}
//			}
//		}
//		assertTrue("untested public methods: "+errorsMethods.toString(), errorsMethods.size()==0);
	}

	@Test
	public void testStaticClasses() {
		// contract: helper classes only have static methods and a private constructor

//		spoon.compiler.SpoonResourceHelper
//		spoon.reflect.visitor.Query
//		spoon.support.compiler.jdt.JDTTreeBuilderQuery
//		spoon.support.compiler.SnippetCompilationHelper
//		spoon.support.util.ByteSerialization
//		spoon.support.util.RtHelper
//		spoon.support.visitor.equals.CloneHelper
//		spoon.template.Substitution
//		spoon.testing.utils.Check
//		spoon.testing.utils.ProcessorUtils
//		spoon.testing.Assert

		for (CtClass<?> klass : spoonSrcMainModel.getElements(new TypeFilter<>(CtClass.class) {
			@Override
			public boolean matches(CtClass element) {
				return element.getSuperclass() == null && super.matches(element) && !element.getMethods().isEmpty()
						&& element.getElements(new TypeFilter<>(CtMethod.class)).stream().allMatch(x -> x.hasModifier(ModifierKind.STATIC));
			}
		})) {
			assertTrue(klass.getElements(new TypeFilter<>(CtConstructor.class)).stream().allMatch(x -> x.hasModifier(ModifierKind.PRIVATE)), "Utility class " + klass.getQualifiedName() + " is missing private constructor");
		}
	}

	@Test
	public void testInterfacesAreCtScannable() {
		// contract: all non-leaf interfaces of the metamodel should be visited by CtInheritanceScanner
		Launcher interfaces = new Launcher();
		interfaces.addInputResource("src/main/java/spoon/support");
		interfaces.addInputResource("src/main/java/spoon/reflect/declaration");
		interfaces.addInputResource("src/main/java/spoon/reflect/code");
		interfaces.addInputResource("src/main/java/spoon/reflect/reference");
		interfaces.addInputResource("src/main/java/spoon/support/reflect/declaration");
		interfaces.addInputResource("src/main/java/spoon/support/reflect/code");
		interfaces.addInputResource("src/main/java/spoon/support/reflect/reference");
		interfaces.buildModel();

		CtClass<?> ctScanner = interfaces.getFactory().Class().get(CtInheritanceScanner.class);

		List<String> missingMethods = new ArrayList<>();

		Metamodel.getInstance().getConcepts().forEach(mmConcept -> {
			if (mmConcept.getKind() == ABSTRACT && mmConcept.getMetamodelInterface() != null) {
				CtInterface abstractIface = mmConcept.getMetamodelInterface();
				String methodName = "scan" + abstractIface.getSimpleName();
				if (ctScanner.getMethodsByName(methodName).isEmpty()) {
					missingMethods.add(methodName);
				}
			}
		});

		assertTrue(missingMethods.isEmpty(), "The following methods are missing in " + ctScanner.getSimpleName() + ": \n" + StringUtils.join(missingMethods, "\n"));
	}

	@Test
	public void testSpecPackage() {
		// contract: when a pull-request introduces a new package, it is made explicit during code review
		// when a pull-request introduces a new package, this test fails and the author has to explicitly declare the new package here

		Set<String> officialPackages = new HashSet<>();
		officialPackages.add("spoon.compiler.builder");
		officialPackages.add("spoon.compiler");
		officialPackages.add("spoon.javadoc");
		officialPackages.add("spoon.javadoc.internal");
		officialPackages.add("spoon.support.modelobs.action");
		officialPackages.add("spoon.support.modelobs.context");
		officialPackages.add("spoon.support.modelobs");
		officialPackages.add("spoon.experimental");
		officialPackages.add("spoon.legacy");
		officialPackages.add("spoon.metamodel");
		officialPackages.add("spoon.pattern");
		officialPackages.add("spoon.pattern.internal");
		officialPackages.add("spoon.pattern.internal.matcher");
		officialPackages.add("spoon.pattern.internal.node");
		officialPackages.add("spoon.pattern.internal.parameter");
		officialPackages.add("spoon.processing");
		officialPackages.add("spoon.refactoring");
		officialPackages.add("spoon.reflect.annotations");
		officialPackages.add("spoon.reflect.code");
		officialPackages.add("spoon.reflect.cu.position");
		officialPackages.add("spoon.reflect.cu");
		officialPackages.add("spoon.reflect.declaration");
		officialPackages.add("spoon.reflect.eval");
		officialPackages.add("spoon.reflect.factory");
		officialPackages.add("spoon.reflect.path.impl");
		officialPackages.add("spoon.reflect.path");
		officialPackages.add("spoon.reflect.reference");
		officialPackages.add("spoon.reflect.visitor.chain");
		officialPackages.add("spoon.reflect.visitor.filter");
		officialPackages.add("spoon.reflect.visitor.printer");
		officialPackages.add("spoon.reflect.visitor");
		officialPackages.add("spoon.reflect");
		officialPackages.add("spoon.support.adaption");
		officialPackages.add("spoon.support.comparator");
		officialPackages.add("spoon.support.compiler.jdt");
		officialPackages.add("spoon.support.compiler");
		officialPackages.add("spoon.support.gui");
		officialPackages.add("spoon.support.sniper");
		officialPackages.add("spoon.support.sniper.internal");
		officialPackages.add("spoon.support.reflect.code");
		officialPackages.add("spoon.support.reflect.cu.position");
		officialPackages.add("spoon.support.reflect.cu");
		officialPackages.add("spoon.support.reflect.declaration");
		officialPackages.add("spoon.support.reflect.eval");
		officialPackages.add("spoon.reflect.meta");
		officialPackages.add("spoon.reflect.meta.impl");
		officialPackages.add("spoon.support.reflect.reference");
		officialPackages.add("spoon.support.reflect");
		officialPackages.add("spoon.support.template");
		officialPackages.add("spoon.support.util");
		officialPackages.add("spoon.support.util.internal");
		officialPackages.add("spoon.support.visitor.clone");
		officialPackages.add("spoon.support.visitor.equals");
		officialPackages.add("spoon.support.visitor.java.internal");
		officialPackages.add("spoon.support.visitor.java.reflect");
		officialPackages.add("spoon.support.visitor.java");
		officialPackages.add("spoon.support.visitor.replace");
		officialPackages.add("spoon.support.visitor");
		officialPackages.add("spoon.support");
		officialPackages.add("spoon.template");
		officialPackages.add("spoon.testing.utils");
		officialPackages.add("spoon.testing");
		officialPackages.add("spoon");
		officialPackages.add(""); // root package

		final Set<String> currentPackages = new HashSet<>();
		spoonSrcMainModel.processWith(new AbstractProcessor<CtPackage>() {
			@Override
			public void process(CtPackage element) {
				currentPackages.add(element.getQualifiedName());
			}
		});

		assertSetEquals("you have created a new package or removed an existing one, please declare it explicitly in SpoonArchitectureEnforcerTest#testSpecPackage", officialPackages, currentPackages);
	}

	public static void assertSetEquals(String msg, Set<?> set1, Set<?> set2) {
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


	private void checkPrivateMethodInvocations(CtModel model) {
		List<CtMethod<?>> methods = model.getElements(new TypeFilter<>(CtMethod.class));
		// only look at private methods
		methods.removeIf(v -> !v.isPrivate());
		// remove methods for serialization gods
		methods.removeIf(v -> v.getSimpleName().matches("(readObject)|(readResolve)"));
		// some CtInvocation have no declaration in model
		List<CtInvocation<?>> methodInvocations =
				model.getElements(new TypeFilter<>(CtInvocation.class));
		methodInvocations.removeIf(v -> v.getExecutable().getExecutableDeclaration() == null);
		List<CtExecutableReferenceExpression<?, ?>> executableReferences =
				model.getElements(new TypeFilter<>(CtExecutableReferenceExpression.class));
		// convert to HashSet for faster lookup. We trade memory for lookup speed.
		HashSet<CtExecutable<?>> lookUp = methodInvocations.stream()
				.map(CtInvocation::getExecutable)
				.map(v -> v.getExecutableDeclaration())
				.collect(Collectors.toCollection(HashSet::new));
		// add executableReferences to our lookup
		executableReferences.stream()
				.map(v -> v.getExecutable().getExecutableDeclaration())
				.filter(Objects::nonNull)
				.forEach(lookUp::add);
		List<CtMethod<?>> methodsWithInvocation = methods.stream()
				// 	 every method must have an invocation
				.filter(method -> lookUp.contains(method))
				.collect(Collectors.toList());
		methods.removeAll(methodsWithInvocation);
		assertEquals(Collections.emptyList(), methods, "Some methods have no invocation");
	}


	private void checkFields(CtModel model) {
		// implNote: we can skip checking for writes, because a read without a write will never happen.
		List<CtField<?>> fields = model.getElements(new TypeFilter<>(CtField.class));
		// only look at private fields
		fields.removeIf(v -> !v.isPrivate());
		// remove fields for serialization gods
		fields.removeIf(v -> v.getSimpleName().equals("serialVersionUID"));
		// some fieldReads have no variable declaration
		List<CtFieldRead<?>> fieldRead = model.getElements(new TypeFilter<>(CtFieldRead.class));
		fieldRead.removeIf(v -> v.getVariable().getFieldDeclaration() == null);
		// convert to HashSet for faster lookup. We trade memory for lookup speed.
		HashSet<CtField<?>> lookUp = fieldRead.stream()
				.map(CtFieldRead::getVariable)
				.map(v -> v.getFieldDeclaration())
				.collect(Collectors.toCollection(HashSet::new));
		List<CtField<?>> fieldsWithRead = fields.stream()
				// 	 every field must have a read
				.filter(field -> lookUp.contains(field))
		.collect(Collectors.toList());
		fields.removeAll(fieldsWithRead);
		assertEquals(Collections.emptyList(), fields, "Some Fields have no read/write");
	}

	@Test
	void fluentSetterReturnsThis() {
		// contract: Fluent API setters return this and not null
		CtTypeReference<?> element = spoonSrcMainFactory.Type().get(CtElement.class).getReference();
		for (CtModule module : spoonSrcMainModel.getAllModules()) {
			module.accept(new CtScanner() {
				@Override
				public <T> void visitCtMethod(CtMethod<T> m) {
					if (!TypeAdaptor.isSubtype(m.getDeclaringType(), element)) {
						return;
					}
					if(!isFluentSetter(m)) {
						return;
					}

					m.accept(new CtScanner() {
						@Override
						public <R> void visitCtReturn(CtReturn<R> returnStatement) {
							if (returnStatement.getReturnedExpression() instanceof CtThisAccess) {
								return;
							}
							if (returnStatement.getReturnedExpression() instanceof CtInvocation) {
								CtInvocation<?> invocation = ((CtInvocation<R>) returnStatement.getReturnedExpression());
								// returning "super.foo()" in "foo" is allowed
								if (invocation.getTarget() instanceof CtSuperAccess) {
									return;
								}
								// returning "this.bar()" in "foo" is allowed
								if (invocation.getTarget() instanceof CtThisAccess) {
									return;
								}
							}
							fail(
								"Return statement in property setter "
									+ m.getDeclaringType().getQualifiedName() + "#" + m.getSignature()
									+ " does not return this: " + returnStatement
							);
						}
					});
				}

				private <T> boolean isFluentSetter(CtMethod<T> m) {
					if (m.getType().isPrimitive()) {
						return false;
					}
					if (m.hasAnnotation(PropertySetter.class)) {
						return true;
					}
					for (CtMethod<?> topDefinition : m.getTopDefinitions()) {
						if (topDefinition.hasAnnotation(PropertySetter.class)) {
							return true;
						}
					}
					return false;
				}
			});
		}
	}

}
