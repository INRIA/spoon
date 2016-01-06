/*
 * Copyright (C) 2006-2015 INRIA and contributors
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

package spoon.test.type;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.TestUtils;
import spoon.test.type.testclasses.Pozole;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TypeTest {
	@Test
	public void testTypeAccessForDotClass() throws Exception {
		// contract: When we use .class on a type, this must be a CtTypeAccess.
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/type/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> make = aPozole.getMethodsByName("make").get(0);

		final List<CtFieldRead<?>> fieldClasses = make.getElements(new TypeFilter<CtFieldRead<?>>(CtFieldRead.class) {
			@Override
			public boolean matches(CtFieldRead<?> element) {
				return "class".equals(element.getVariable().getSimpleName()) && super.matches(element);
			}
		});
		assertEquals(4, fieldClasses.size());
		for (CtFieldRead<?> fieldClass : fieldClasses) {
			assertTrue(fieldClass.getTarget() instanceof CtTypeAccess);
		}

		TestUtils.canBeBuilt(target, 8, true);
	}

	@Test
	public void testTypeAccessOnPrimitive() throws Exception {
		Factory factory = TestUtils.createFactory();
		CtClass<?> clazz = factory.Code().createCodeSnippetStatement( //
				"class X {" //
						+ "public void foo() {" //
						+ " Class klass=null;" //
						+ "  boolean x= (klass == short.class);" //
						+ "}};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		CtLocalVariable<?> ass = body.getStatement(1);
		CtBinaryOperator<?> op = (CtBinaryOperator<?>) ass.getDefaultExpression();
		assertEquals("Class", op.getLeftHandOperand().getType().getSimpleName());
		assertFalse(op.getLeftHandOperand().getType().isPrimitive());
		assertEquals("Class", op.getRightHandOperand().getType().getSimpleName());
		assertTrue(op.getRightHandOperand() instanceof CtFieldRead);
		assertFalse(op.getRightHandOperand().getType().isPrimitive());
	}

	@Test
	public void testTypeAccessForTypeAccessInInstanceOf() throws Exception {
		// contract: the right hand operator must be a CtTypeAccess.
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/type/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> eat = aPozole.getMethodsByName("eat").get(0);

		final List<CtTypeAccess<?>> typeAccesses = eat.getElements(new TypeFilter<CtTypeAccess<?>>(CtTypeAccess.class));
		assertEquals(2, typeAccesses.size());

		assertTrue(typeAccesses.get(0).getParent() instanceof CtBinaryOperator);
		assertEquals(BinaryOperatorKind.INSTANCEOF, ((CtBinaryOperator) typeAccesses.get(0).getParent()).getKind());
		assertEquals("a instanceof java.lang.String", typeAccesses.get(0).getParent().toString());

		assertTrue(typeAccesses.get(1).getParent() instanceof CtBinaryOperator);
		assertEquals(BinaryOperatorKind.INSTANCEOF, ((CtBinaryOperator) typeAccesses.get(1).getParent()).getKind());
		assertEquals("a instanceof java.util.Collection<?>", typeAccesses.get(1).getParent().toString());
	}

	@Test
	public void testTypeAccessOfArrayObjectInFullyQualifiedName() throws Exception {
		// contract: A type access in fully qualified name must to rewrite well.
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/type/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> season = aPozole.getMethodsByName("season").get(0);

		final List<CtTypeAccess<?>> typeAccesses = season.getElements(new TypeFilter<CtTypeAccess<?>>(CtTypeAccess.class));
		assertEquals(2, typeAccesses.size());

		assertTrue(typeAccesses.get(0).getParent() instanceof CtBinaryOperator);
		assertEquals(BinaryOperatorKind.INSTANCEOF, ((CtBinaryOperator) typeAccesses.get(0).getParent()).getKind());
		assertEquals("a instanceof java.lang.Object[]", typeAccesses.get(0).getParent().toString());

		assertTrue(typeAccesses.get(1).getParent() instanceof CtBinaryOperator);
		assertEquals(BinaryOperatorKind.INSTANCEOF, ((CtBinaryOperator) typeAccesses.get(1).getParent()).getKind());
		assertEquals("a instanceof java.lang.Object[]", typeAccesses.get(1).getParent().toString());

		TestUtils.canBeBuilt(target, 8, true);
	}

	@Test
	public void testIntersectionBindingReturnsFirstType() throws Exception {
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/type/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> prepare = aPozole.getMethodsByName("prepare").get(0);

		final List<CtLambda<?>> lambdas = prepare.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class));
		assertEquals(1, lambdas.size());

		assertEquals(1, lambdas.get(0).getTypeCasts().size());
		assertEquals("java.lang.Runnable", lambdas.get(0).getTypeCasts().get(0).toString());

		TestUtils.canBeBuilt(target, 8, true);
	}
}
