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
package spoon.reflect.ast;


import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.refactoring.Refactoring;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConditional;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.visitor.equals.CloneHelper;
import spoon.testing.utils.ModelUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static spoon.testing.utils.Check.assertCtElementEquals;

public class CloneTest {

	@Test
	public void testCloneMethodsDeclaredInAST() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.getEnvironment().setNoClasspath(true);
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		// implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.run();

		new CtScanner() {
			@Override
			public <T> void visitCtClass(CtClass<T> ctClass) {
				if (!ctClass.getSimpleName().startsWith("Ct")) {
					return;
				}
				final CtMethod<Object> clone = ctClass.getMethod("clone");
				assertNotNull(clone, ctClass.getQualifiedName() + " hasn't clone method.");
				assertTrue(clone.getAnnotations().stream().map( ctAnnotation -> ctAnnotation.getActualAnnotation().annotationType()).collect(Collectors.toList()).contains(Override.class), ctClass.getQualifiedName() + " hasn't Override annotation on clone method.");
			}

			@Override
			public <T> void visitCtInterface(CtInterface<T> intrface) {
				if (!intrface.getSimpleName().startsWith("Ct")) {
					return;
				}
				final CtMethod<Object> clone = intrface.getMethod("clone");
				if (hasConcreteImpl(intrface)) {
					assertNotNull(clone, intrface.getQualifiedName() + " hasn't clone method.");
					if (!isRootDeclaration(intrface)) {
						assertTrue(clone.getAnnotations().stream().map( ctAnnotation -> ctAnnotation.getActualAnnotation().annotationType()).collect(Collectors.toList()).contains(Override.class), intrface.getQualifiedName() + " hasn't Override annotation on clone method.");
					}
				}
			}

			private <T> boolean hasConcreteImpl(CtInterface<T> intrface) {
				return !Query.getElements(intrface.getFactory(), new TypeFilter<CtClass<?>>(CtClass.class) {
					@Override
					public boolean matches(CtClass<?> element) {
						return super.matches(element) && element.getSuperInterfaces().contains(intrface.getReference());
					}
				}).isEmpty();
			}

			private <T> boolean isRootDeclaration(CtInterface<T> intrface) {
				return "CtElement".equals(intrface.getSimpleName());
			}
		}.scan(launcher.getModel().getRootPackage());
	}

	@Test
	public void testCloneCastConditional() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.getEnvironment().setNoClasspath(true);

		launcher.addInputResource("./src/test/resources/spoon/test/visitor/ConditionalRes.java");

		launcher.addProcessor(new AbstractProcessor<CtConditional<?>>() {
			@Override
			public void process(CtConditional<?> conditional) {
				CtConditional clone = conditional.clone();
				assertEquals(0, conditional.getTypeCasts().size());
				assertEquals(0, clone.getTypeCasts().size());
				assertEquals(conditional, clone);
				conditional.addTypeCast(getFactory().Type().bytePrimitiveType());
				assertEquals(1, conditional.getTypeCasts().size());
				assertNotEquals(conditional, clone);
				clone = conditional.clone();
				assertEquals(conditional, clone);
				assertEquals(1, clone.getTypeCasts().size());
			}
		});
		launcher.run();
	}

	@Test
	public void testCloneListener() {
		// contract: it is possible to extend the cloning behavior

		// in this example extension, a listener of cloning process gets access to origin node and cloned node
		// we check the contract with some complicated class as target of cloning
		Factory factory = ModelUtils.build(new File("./src/main/java/spoon/reflect/visitor/DefaultJavaPrettyPrinter.java"));
		CtType<?> cloneSource = factory.Type().get(DefaultJavaPrettyPrinter.class);
		class CloneListener extends CloneHelper {
			Map<CtElement, CtElement> sourceToTarget = new IdentityHashMap<>();
			@Override
			public <T extends CtElement> T clone(T source) {
				if (source == null) {
					return null;
				}
				T target = super.clone(source);
				onCloned(source, target);
				return target;
			}
			private void onCloned(CtElement source, CtElement target) {
				CtElement previousTarget = sourceToTarget.put(source, target);
				assertNull(previousTarget);
			}
		}

		CloneListener cl = new CloneListener();
		CtType<?> cloneTarget = cl.clone(cloneSource);

		cloneSource.filterChildren(null).forEach(sourceElement -> {
			//contract: there exists cloned target for each visitable element
			CtElement targetElement = cl.sourceToTarget.remove(sourceElement);
			assertNotNull(targetElement, "Missing target for sourceElement\n" + sourceElement);
			assertCtElementEquals((CtElement) sourceElement, targetElement);
		});
		//contract: each visitable elements was cloned exactly once. No more no less.
		assertTrue(cl.sourceToTarget.isEmpty());
	}

	@Test
	public void testCopyMethod() {
		// contract: the copied method is well-formed, lookup of executable references is preserved after copying, especially for recursive methods
		Launcher l = new Launcher();
		l.getEnvironment().setNoClasspath(true);
		l.addInputResource("./src/test/resources/noclasspath/A2.java");
		l.buildModel();
		CtClass<Object> klass = l.getFactory().Class().get("A2");
		CtMethod<?> method = klass.getMethodsByName("c").get(0);
		List<CtExecutableReference> elements = method.getElements(new TypeFilter<>(CtExecutableReference.class));
		CtExecutableReference methodRef = elements.get(0);

		// the lookup is OK in the original node
		assertSame(method, methodRef.getDeclaration());

		assertEquals("A2", methodRef.getDeclaringType().toString());

		// we copy the method
		CtMethod<?> methodClone = method.copyMethod();
		assertEquals("cCopy", methodClone.getSimpleName());

		// useful for debug
		methodClone.getBody().insertBegin(l.getFactory().createCodeSnippetStatement("// debug info"));

		CtExecutableReference reference = methodClone.getElements(new TypeFilter<>(CtExecutableReference.class)).get(0);
		// all references have been updated
		assertEquals("cCopy", reference.getSimpleName());
		assertSame(methodClone, reference.getDeclaration());
		assertEquals("A2", methodClone.getDeclaringType().getQualifiedName());

		// now we may want to rename the copied method
		Refactoring.changeMethodName(methodClone, "foo");
		assertEquals("foo", methodClone.getSimpleName()); // the method has been changed
		assertEquals("foo", reference.getSimpleName()); // the reference has been changed
		assertSame(methodClone, reference.getDeclaration()); // the lookup still works
		assertEquals("A2", methodClone.getDeclaringType().getQualifiedName());

		// one can even copy the method several times
		methodClone = Refactoring.copyMethod(method);
		assertEquals("cCopy", methodClone.getSimpleName());
		methodClone = Refactoring.copyMethod(method);
		assertEquals("cCopyX", methodClone.getSimpleName());
	}

	@Test
	public void testCopyType() {
		// contract: the copied type is well formed, it never points to the initial type
		Factory factory = ModelUtils.build(new File("./src/main/java/spoon/reflect/visitor/DefaultJavaPrettyPrinter.java"));
		CtType<?> intialElement = factory.Type().get(DefaultJavaPrettyPrinter.class);
		CtType<?> cloneTarget = intialElement.copyType();
		assertEquals("spoon.reflect.visitor.DefaultJavaPrettyPrinterCopy", cloneTarget.getQualifiedName());
		// we go over all references
		for (CtReference reference: cloneTarget.getElements(new TypeFilter<>(CtReference.class))) {
			CtElement declaration = reference.getDeclaration();
			if (declaration == null) {
				continue;
			}

			// the core assertion: not a single reference points to the initial element
			if (declaration.hasParent(intialElement)) {
				fail();
			}
		}
	}

	@Test
	public void testIssue3389() {
		// test case for https://github.com/INRIA/spoon/issues/3389
		Launcher launcher = new Launcher();
		launcher.addInputResource( "./src/test/resources/JavaCode.java" );
		launcher.buildModel();
		CtModel model = launcher.getModel();

		CtType<?> c = launcher.getFactory().Type().get("HelloWorld");

		// sanity check: the field is at the end as in the source
		assertEquals(c.getFields().get(0), c.getTypeMembers().get(2));

		List<CtField<?>> fields = c.getFields();
		for(CtField<?> field : fields) {
			c.removeField(field);
			c.addFieldAtTop(field);
		}

		// sanity check: the field is now at the top after the implicit constructor
		assertEquals(c.getFields().get(0), c.getTypeMembers().get(0));

		// contract: clone preserves the order
		assertEquals(c.getFields().get(0), c.clone().getTypeMembers().get(0));
	}

	@Test
	public void testCloneKeepsImplicitModifierState() {
		// contract: When cloning an element the implicit state is kept as well
		CtClass<?> test = new Launcher().getFactory().createClass("Test");
		Set<CtExtendedModifier> modifiers = new HashSet<>();
		modifiers.add(CtExtendedModifier.implicit(ModifierKind.PUBLIC));
		modifiers.add(CtExtendedModifier.explicit(ModifierKind.ABSTRACT));
		test.setExtendedModifiers(modifiers);

		CtClass<?> clonedTest = test.clone();

		// Sanity checks
		assertEquals(2, test.getExtendedModifiers().size(), "Class has the wrong amount of modifiers"
		);
		assertModifierImplicitness(test.getExtendedModifiers(), ModifierKind.PUBLIC, true);
		assertModifierImplicitness(test.getExtendedModifiers(), ModifierKind.ABSTRACT, false);

		// Test that the implicit state was kept
		assertEquals(2, clonedTest.getExtendedModifiers().size(), "Clone has wrong amount of modifiers");
		assertModifierImplicitness(clonedTest.getExtendedModifiers(), ModifierKind.PUBLIC, true);
		assertModifierImplicitness(clonedTest.getExtendedModifiers(), ModifierKind.ABSTRACT, false);
	}

	private void assertModifierImplicitness(Set<CtExtendedModifier> modifiers, ModifierKind kind,
			boolean shouldBeImplicit) {
		for (CtExtendedModifier modifier : modifiers) {
			if (modifier.getKind() == kind) {
				assertEquals(shouldBeImplicit, modifier.isImplicit(), "Unexpected CtExtendedModifier#isImplicit");
				return;
			}
		}
		fail("Modifier " + kind + " was not found at all in " + modifiers);
	}

	@Test
	public void testCloneClonesExtendedModifiers() {
		// contract: When cloning an element the extended modifiers are cloned as well
		CtClass<?> test = new Launcher().getFactory().createClass("Test");
		Set<CtExtendedModifier> modifiers = new HashSet<>();
		modifiers.add(CtExtendedModifier.implicit(ModifierKind.PUBLIC));
		modifiers.add(CtExtendedModifier.explicit(ModifierKind.ABSTRACT));
		test.setExtendedModifiers(modifiers);

		CtClass<?> clonedTest = test.clone();

		assertModifiersAreDistinctInstances(
				test.getExtendedModifiers(),
				clonedTest.getExtendedModifiers()
		);
	}

	private void assertModifiersAreDistinctInstances(Set<CtExtendedModifier> real,
			Set<CtExtendedModifier> cloned) {
		Set<CtExtendedModifier> referenceBasedModifierSet = Collections.newSetFromMap(
				new IdentityHashMap<>()
		);
		referenceBasedModifierSet.addAll(real);
		referenceBasedModifierSet.addAll(cloned);

		// Verify the modifier instances are actually different so changes in one do not affect the other
		assertEquals(4, referenceBasedModifierSet.size(), "The extended modifiers are the same instance as the original");
	}
}
