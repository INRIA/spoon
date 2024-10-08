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
package spoon.test.visibility;


import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonAPI;
import spoon.SpoonModelBuilder;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractReferenceFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.StandardEnvironment;
import spoon.test.visibility.testclasses.A;
import spoon.test.visibility.testclasses.A2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class VisibilityTest {
	@Test
	public void testMethodeWithNonAccessibleTypeArgument() throws Exception {
		Factory f = build(spoon.test.visibility.testclasses.MethodeWithNonAccessibleTypeArgument.class,
				spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf.class,
				Class.forName("spoon.test.visibility.packageprotected.NonAccessibleInterf")
		);
		CtClass<?> type = f.Class().get(spoon.test.visibility.testclasses.MethodeWithNonAccessibleTypeArgument.class);
		assertEquals("MethodeWithNonAccessibleTypeArgument", type.getSimpleName());
		CtMethod<?> m = type.getMethodsByName("method").get(0);
		assertEquals(
				"new spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf().method(new spoon.test.visibility.packageprotected.AccessibleClassFromNonAccessibleInterf())",
				m.getBody().getStatement(0).toString()
		);
	}

	@Test
	public void testVisibilityOfClassesNamedByClassesInJavaLangPackage() {
		final File sourceOutputDir = new File("target/spooned/spoon/test/visibility_package/testclasses");
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setAutoImports(true);
		launcher.getEnvironment().setSourceOutputDirectory(sourceOutputDir);
		final Factory factory = launcher.getFactory();
		final SpoonModelBuilder compiler = launcher.createCompiler();
		compiler.addInputSource(new File("./src/test/java/spoon/test/visibility/testclasses/"));
		compiler.build();
		compiler.generateProcessedSourceFiles(OutputType.CLASSES);

		// Class must be imported.
		final CtClass<?> aDouble = (CtClass<?>) factory.Type().get(spoon.test.visibility.testclasses.internal.Double.class);
		assertNotNull(aDouble);
		assertSame(spoon.test.visibility.testclasses.internal.Double.class, aDouble.getActualClass());

		// Class mustn't be imported.
		final CtClass<?> aFloat = (CtClass<?>) factory.Type().get(spoon.test.visibility.testclasses.Float.class);
		assertNotNull(aFloat);
		assertSame(spoon.test.visibility.testclasses.Float.class, aFloat.getActualClass());

		canBeBuilt(new File("./target/spooned/spoon/test/visibility_package/testclasses/"), StandardEnvironment.DEFAULT_CODE_COMPLIANCE_LEVEL);
	}

	@Test
	public void testFullyQualifiedNameOfTypeReferenceWithGeneric() {
		// contract: Generics are written when there are specified in the return type of a method.
		final String target = "./target/spooned/spoon/test/visibility_generics/testclasses/";
		final SpoonAPI launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/visibility/testclasses/A.java");
		launcher.addInputResource("./src/test/java/spoon/test/visibility/testclasses/A2.java");
		launcher.addInputResource("./src/test/java/spoon/test/visibility/testclasses/Foo.java");
		launcher.setSourceOutputDirectory(target);
		launcher.run();

		final CtClass<A> aClass = launcher.getFactory().Class().get(A.class);
		CtType<?> nestedB = aClass.getNestedType("B");
		List<CtFieldAccess> elements = nestedB.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(1, elements.size());
		assertEquals("spoon.test.visibility.testclasses.A.B.i", elements.get(0).toString());

		CtMethod<?> instanceOf = aClass.getMethodsByName("instanceOf").get(0);
		List<CtBinaryOperator> elements1 = instanceOf.getElements(new TypeFilter<>(CtBinaryOperator.class));
		assertEquals(1, elements1.size());
		assertEquals("spoon.test.visibility.testclasses.A.B", elements1.get(0).getRightHandOperand().toString());

		CtMethod<?> returnType = aClass.getMethodsByName("returnType").get(0);
		assertEquals("spoon.test.visibility.testclasses.A<T>.C<T>", returnType.getType().toString());

		final CtClass<A2> secondClass = launcher.getFactory().Class().get(A2.class);
		nestedB = secondClass.getNestedType("B");
		elements = nestedB.getElements(new TypeFilter<>(CtFieldAccess.class));
		assertEquals(1, elements.size());
		assertEquals("spoon.test.visibility.testclasses.A2.B.i", elements.get(0).toString());

		instanceOf = secondClass.getMethodsByName("instanceOf").get(0);
		elements1 = instanceOf.getElements(new TypeFilter<>(CtBinaryOperator.class));
		assertEquals(1, elements1.size());
		assertEquals("spoon.test.visibility.testclasses.A2.B", elements1.get(0).getRightHandOperand().toString());

		returnType = secondClass.getMethodsByName("returnType").get(0);
		assertEquals("spoon.test.visibility.testclasses.A2.C<java.lang.String>", returnType.getType().toString());

		returnType = secondClass.getMethodsByName("returnType2").get(0);
		assertEquals("spoon.test.visibility.testclasses.Foo<java.lang.String>.Bar<java.lang.String>", returnType.getType().toString());

		canBeBuilt(target, 8);
	}

	@Test
	public void testName() {
		final SpoonAPI launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/visibility/testclasses/Tacos.java",
				"-o", "./target/spooned/visibility"
		});

		final List<CtFieldReference<?>> references = Query.getElements(launcher.getFactory(), new AbstractReferenceFilter<CtFieldReference<?>>(CtFieldReference.class) {
			@Override
			public boolean matches(CtFieldReference<?> reference) {
				return "x".equals(reference.getSimpleName());
			}
		});
		assertEquals(1, references.size());
		final CtFieldReference<?> field = references.get(0);
		assertNotNull(field.getDeclaration());
		final CtClass<?> tacos = launcher.getFactory().Class().get("spoon.test.visibility.testclasses.Tacos");
		assertEquals(tacos, field.getDeclaringType().getDeclaration());
		assertEquals(tacos.getFields().get(0), field.getDeclaration());
	}

	@Test
	public void testInvocationVisibilityInFieldDeclaration() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/noclasspath/Solver.java");
		launcher.setSourceOutputDirectory("./target/spooned");
		launcher.buildModel();

		final CtType<Object> aSolver = launcher.getFactory().Type().get("org.sat4j.minisat.core.Solver");
		final CtField<?> lbdTimerField = aSolver.getField("lbdTimer");
		final CtInvocation<?> ctInvocation = lbdTimerField.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				return "bound".equals(element.getExecutable().getSimpleName()) && super.matches(element);
			}
		}).get(0);
		assertNotNull(ctInvocation.getTarget());
		assertTrue(ctInvocation.getTarget().isImplicit());
		assertEquals("bound()", ctInvocation.prettyprint());
	}
}
