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
package spoon.test.literal;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.LiteralBase;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.comparator.DeepRepresentationComparator;
import spoon.test.literal.testclasses.Tacos;

import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class LiteralTest {

	@Test
	public void testCharLiteralInNoClasspath() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/SecondaryIndexManager.java");
		launcher.setSourceOutputDirectory("./target/literal");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("org.apache.cassandra.index.SecondaryIndexManager");
		TreeSet<CtLiteral<?>> ts = new TreeSet<>(new DeepRepresentationComparator());

		ts.addAll(aClass.getElements(new TypeFilter<CtLiteral<Character>>(CtLiteral.class) {
			@Override
			public boolean matches(CtLiteral element) {
				return element.getValue() instanceof Character && super.matches(element);
			}
		}));

		assertTrue(ts.last().getType().isPrimitive());
		assertEquals(':', ts.last().getValue());
		canBeBuilt("./target/literal", 8, true);
	}

	@Test
	public void testLiteralInForEachWithNoClasspath() {
		Launcher runLaunch = new Launcher();
		runLaunch.getEnvironment().setNoClasspath(true);
		runLaunch.addInputResource("./src/test/resources/noclasspath/LiteralInForEach.java");
		runLaunch.buildModel();
	}

	@Test
	public void testBuildLiternal() throws Exception {
		CtType<Tacos> ctType = buildClass(Tacos.class);
		TypeFactory typeFactory = ctType.getFactory().Type();

		CtLiteral<?> literal = (CtLiteral<?>) ctType.getField("a").getDefaultExpression();
		assertEquals(0, literal.getValue());
		assertTrue(literal.getType().isPrimitive());
		assertEquals(typeFactory.INTEGER_PRIMITIVE, literal.getType());


		literal = (CtLiteral<?>) ctType.getField("b").getDefaultExpression();
		assertEquals(0x0, literal.getValue());
		assertTrue(literal.getType().isPrimitive());
		assertEquals(typeFactory.INTEGER_PRIMITIVE, literal.getType());


		literal = (CtLiteral<?>) ctType.getField("c").getDefaultExpression();
		assertEquals(0f, literal.getValue());
		assertTrue(literal.getType().isPrimitive());
		assertEquals(typeFactory.FLOAT_PRIMITIVE, literal.getType());


		literal = (CtLiteral<?>) ctType.getField("d").getDefaultExpression();
		assertEquals(0L, literal.getValue());
		assertTrue(literal.getType().isPrimitive());
		assertEquals(typeFactory.LONG_PRIMITIVE, literal.getType());


		literal = (CtLiteral<?>) ctType.getField("e").getDefaultExpression();
		assertEquals(0d, literal.getValue());
		assertTrue(literal.getType().isPrimitive());
		assertEquals(typeFactory.DOUBLE_PRIMITIVE, literal.getType());


		literal = (CtLiteral<?>) ctType.getField("f").getDefaultExpression();
		assertEquals('0', literal.getValue());
		assertTrue(literal.getType().isPrimitive());
		assertEquals(typeFactory.CHARACTER_PRIMITIVE, literal.getType());


		literal = (CtLiteral<?>) ctType.getField("g").getDefaultExpression();
		assertEquals("0", literal.getValue());
		assertFalse(literal.getType().isPrimitive());
		assertEquals(typeFactory.STRING, literal.getType());

		literal = (CtLiteral<?>) ctType.getField("h").getDefaultExpression();
		assertNull(literal.getValue());
		assertFalse(literal.getType().isPrimitive());
		assertEquals(typeFactory.NULL_TYPE, literal.getType());

	}

	@Test
	public void testFactoryLiternal() {
		Launcher runLaunch = new Launcher();
		Factory factory = runLaunch.getFactory();
		CodeFactory code = factory.Code();

		CtLiteral literal = code.createLiteral(1);
		assertEquals(1, literal.getValue());
		assertEquals(factory.Type().integerPrimitiveType(), literal.getType());

		literal = code.createLiteral(new Integer(1));
		assertEquals(1, literal.getValue());
		assertEquals(factory.Type().integerPrimitiveType(), literal.getType());

		literal = code.createLiteral(1.0);
		assertEquals(1.0, literal.getValue());
		assertEquals(factory.Type().doublePrimitiveType(), literal.getType());

		literal = code.createLiteral("literal");
		assertEquals("literal", literal.getValue());
		assertEquals(factory.Type().stringType(), literal.getType());
	}

	@Test
	public void testEscapedString() {

		/* test escaped char: spoon change octal values by equivalent unicode values */

		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/literal/testclasses/EscapedLiteral.java");
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().setAutoImports(true);
		launcher.buildModel();
		final CtClass<?> ctClass = launcher.getFactory().Class().get("spoon.test.literal.testclasses.EscapedLiteral");

		assertTrue('\u0000' == (char) ((CtLiteral) ctClass.getField("c1").getDefaultExpression()).getValue());
		assertTrue('\0' == (char) ((CtLiteral) ctClass.getField("c1").getDefaultExpression()).getValue());

		assertTrue('\u0007' == (char) ((CtLiteral) ctClass.getField("c2").getDefaultExpression()).getValue());
		assertTrue('\7' == (char) ((CtLiteral) ctClass.getField("c2").getDefaultExpression()).getValue());

		assertTrue('\77' == (char) ((CtLiteral) ctClass.getField("c3").getDefaultExpression()).getValue());
		assertTrue('?' == (char) ((CtLiteral) ctClass.getField("c3").getDefaultExpression()).getValue());

		assertTrue('\177' == (char) ((CtLiteral) ctClass.getField("c4").getDefaultExpression()).getValue());
		assertTrue('\u007f' == (char) ((CtLiteral) ctClass.getField("c4").getDefaultExpression()).getValue());

		assertTrue('\277' == (char) ((CtLiteral) ctClass.getField("c5").getDefaultExpression()).getValue());
		assertTrue('\u00bf' == (char) ((CtLiteral) ctClass.getField("c5").getDefaultExpression()).getValue());

		assertTrue('\377' == (char) ((CtLiteral) ctClass.getField("c6").getDefaultExpression()).getValue());
		assertTrue('\u00ff' == (char) ((CtLiteral) ctClass.getField("c6").getDefaultExpression()).getValue());

		assertTrue('\u0000' == (char) ((CtLiteral) ctClass.getField("c7").getDefaultExpression()).getValue());
		assertTrue('\u0001' == (char) ((CtLiteral) ctClass.getField("c8").getDefaultExpression()).getValue());
		assertTrue('\u0002' == (char) ((CtLiteral) ctClass.getField("c9").getDefaultExpression()).getValue());
	}

	@Test
	public void testLiteralBase() {
		// contract: CtLiteral should provide correct base (2, 8, 10, 16 or empty)
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/literal/testclasses/BasedLiteral.java");
		launcher.buildModel();
		final CtClass<?> ctClass = launcher.getFactory().Class().get("spoon.test.literal.testclasses.BasedLiteral");

		assertEquals(LiteralBase.DECIMAL, ((CtLiteral) ctClass.getField("i1").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.DECIMAL, ((CtLiteral) ctClass.getField("i2").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.OCTAL, ((CtLiteral) ctClass.getField("i3").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.OCTAL, ((CtLiteral) ctClass.getField("i4").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.HEXADECIMAL, ((CtLiteral) ctClass.getField("i5").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.HEXADECIMAL, ((CtLiteral) ctClass.getField("i6").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.OCTAL, ((CtLiteral) ctClass.getField("i7").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.BINARY, ((CtLiteral) ctClass.getField("i8").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.BINARY, ((CtLiteral) ctClass.getField("i9").getDefaultExpression()).getBase());

		assertEquals(LiteralBase.DECIMAL, ((CtLiteral) ctClass.getField("l1").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.DECIMAL, ((CtLiteral) ctClass.getField("l2").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.OCTAL, ((CtLiteral) ctClass.getField("l3").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.OCTAL, ((CtLiteral) ctClass.getField("l4").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.HEXADECIMAL, ((CtLiteral) ctClass.getField("l5").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.HEXADECIMAL, ((CtLiteral) ctClass.getField("l6").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.BINARY, ((CtLiteral) ctClass.getField("l7").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.BINARY, ((CtLiteral) ctClass.getField("l8").getDefaultExpression()).getBase());

		assertEquals(LiteralBase.DECIMAL, ((CtLiteral) ctClass.getField("f1").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.DECIMAL, ((CtLiteral) ctClass.getField("f2").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.DECIMAL, ((CtLiteral) ctClass.getField("f3").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.DECIMAL, ((CtLiteral) ctClass.getField("f4").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.DECIMAL, ((CtLiteral) ctClass.getField("f5").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.HEXADECIMAL, ((CtLiteral) ctClass.getField("f6").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.HEXADECIMAL, ((CtLiteral) ctClass.getField("f7").getDefaultExpression()).getBase());

		assertEquals(LiteralBase.DECIMAL, ((CtLiteral) ctClass.getField("d1").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.DECIMAL, ((CtLiteral) ctClass.getField("d2").getDefaultExpression()).getBase());
		assertEquals(LiteralBase.DECIMAL, ((CtLiteral) ctClass.getField("d3").getDefaultExpression()).getBase());

		assertNull(((CtLiteral) ctClass.getField("c1").getDefaultExpression()).getBase());
		assertNull(((CtLiteral) ctClass.getField("s1").getDefaultExpression()).getBase());
    }

	@Test
	public void testLiteralBasePrinter() {
		// contract: PrettyPrinter should output literals in the specified base
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/literal/testclasses/BasedLiteral.java");
		launcher.buildModel();
		final CtClass<?> ctClass = launcher.getFactory().Class().get("spoon.test.literal.testclasses.BasedLiteral");

		assertEquals("42", ctClass.getField("i1").getDefaultExpression().toString());
		((CtLiteral) ctClass.getField("i1").getDefaultExpression()).setBase(LiteralBase.OCTAL);
		assertEquals("052", ctClass.getField("i1").getDefaultExpression().toString());
		assertEquals("0", ctClass.getField("i2").getDefaultExpression().toString());
		assertEquals("00", ctClass.getField("i3").getDefaultExpression().toString());
		assertEquals("042", ctClass.getField("i4").getDefaultExpression().toString());
		assertEquals("0x42", ctClass.getField("i5").getDefaultExpression().toString());
		assertEquals("0x42", ctClass.getField("i6").getDefaultExpression().toString());
		assertEquals("0142", ctClass.getField("i7").getDefaultExpression().toString());
		assertEquals("0b1", ctClass.getField("i8").getDefaultExpression().toString());
		assertEquals("0b1010", ctClass.getField("i9").getDefaultExpression().toString());

		assertEquals("42L", ctClass.getField("l1").getDefaultExpression().toString());
		assertEquals("0L", ctClass.getField("l2").getDefaultExpression().toString());
		assertEquals("00L", ctClass.getField("l3").getDefaultExpression().toString());
		assertEquals("042L", ctClass.getField("l4").getDefaultExpression().toString());
		assertEquals("0x42L", ctClass.getField("l5").getDefaultExpression().toString());
		assertEquals("0x42L", ctClass.getField("l6").getDefaultExpression().toString());
		assertEquals("0b0L", ctClass.getField("l7").getDefaultExpression().toString());
		assertEquals("0b1010L", ctClass.getField("l8").getDefaultExpression().toString());

		assertEquals("42.42F", ctClass.getField("f1").getDefaultExpression().toString());
		assertEquals("42.0F", ctClass.getField("f2").getDefaultExpression().toString());
		assertEquals("0.0F", ctClass.getField("f3").getDefaultExpression().toString());
		assertEquals("0.0F", ctClass.getField("f4").getDefaultExpression().toString());
		assertEquals("0.0F", ctClass.getField("f5").getDefaultExpression().toString());
		assertEquals("0x1.2p7F", ctClass.getField("f6").getDefaultExpression().toString());
		assertEquals("0x1.2p7F", ctClass.getField("f7").getDefaultExpression().toString());

		assertEquals("0.0", ctClass.getField("d1").getDefaultExpression().toString());
		assertEquals("0.0", ctClass.getField("d2").getDefaultExpression().toString());
		assertEquals("42.0", ctClass.getField("d3").getDefaultExpression().toString());

		assertEquals("'c'", ctClass.getField("c1").getDefaultExpression().toString());
		assertEquals("\"hello\"", ctClass.getField("s1").getDefaultExpression().toString());
	}
}
