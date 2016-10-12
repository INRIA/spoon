package spoon.test.literal;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtLiteral;
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
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class LiteralTest {
	@Test
	public void testCharLiteralInNoClasspath() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/SecondaryIndexManager.java");
		launcher.setSourceOutputDirectory("./target/literal");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("org.apache.cassandra.index.SecondaryIndexManager");
		TreeSet<CtLiteral<?>> ts = new TreeSet<CtLiteral<?>>(new DeepRepresentationComparator());

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
		assertEquals(0l, literal.getValue());
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
		assertEquals(null, literal.getValue());
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
}
