package spoon.test.literal;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

import java.util.TreeSet;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.comparator.DeepRepresentationComparator;

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
