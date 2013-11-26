package spoon.test.prettyprinter;

import org.junit.Before;
import org.junit.Test;

import spoon.Spoon;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.Factory;
import spoon.reflect.declaration.CtSimpleType;
import static org.junit.Assert.assertEquals;

public class PrinterTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		factory = Spoon.createFactory();
		Spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/annotation/PersistenceProperty.java",
								"./src/test/java/spoon/test/prettyprinter/Validation.java"))
				.build();
	}

	@Test
	public void testPrettyPrinter() throws Exception {

		for (CtSimpleType<?> t : factory.Type().getAll()) {
			t.toString();
		}
		assertEquals(0, factory.getEnvironment().getWarningCount());
		assertEquals(0, factory.getEnvironment().getErrorCount());

	}

}
