package spoon.test.prettyprinter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

public class PrinterTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
	    Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/annotation/testclasses/PersistenceProperty.java",
								"./src/test/java/spoon/test/prettyprinter/Validation.java"))
				.build();
	}

	@Test
	public void testPrettyPrinter() throws Exception {

		for (CtType<?> t : factory.Type().getAll()) {
			t.toString();
		}
		assertEquals(0, factory.getEnvironment().getWarningCount());
		assertEquals(0, factory.getEnvironment().getErrorCount());

	}

}
