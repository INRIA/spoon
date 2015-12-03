package spoon.test.literal;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.TestUtils;

import static org.junit.Assert.assertEquals;

public class LiteralTest {
	@Test
	public void testCharLiteralInNoClasspath() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/SecondaryIndexManager.java");
		launcher.setSourceOutputDirectory("./target/literal");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("org.apache.cassandra.index.SecondaryIndexManager");
		final CtLiteral<Character> charLiteral = aClass.getElements(new TypeFilter<CtLiteral<Character>>(CtLiteral.class) {
			@Override
			public boolean matches(CtLiteral element) {
				return element.getValue() instanceof Character && super.matches(element);
			}
		}).get(0);

		assertEquals(':', (char) charLiteral.getValue());
		TestUtils.canBeBuilt("./target/literal", 8, true);
	}
}
