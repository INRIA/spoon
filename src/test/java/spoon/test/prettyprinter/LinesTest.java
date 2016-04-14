package spoon.test.prettyprinter;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

import static org.junit.Assert.assertEquals;

public class LinesTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/prettyprinter/Validation.java"))
				.build();
		factory.getEnvironment().setPreserveLineNumbers(true);
		factory.getEnvironment().setAutoImports(false);
	}

	@Test
	public void testPrettyPrinterWithLines() throws Exception {

		for (CtType<?> t : factory.Type().getAll()) {
			if (t.isTopLevel()) {
				// System.out.println("calculate " + t.getSimpleName());
				DefaultJavaPrettyPrinter pp = new DefaultJavaPrettyPrinter(
						factory.getEnvironment());
				pp.calculate(t.getPosition().getCompilationUnit(), t
						.getPosition().getCompilationUnit().getDeclaredTypes());
				// System.out.println(pp.getResult().toString());
			}
		}
		assertEquals(0, factory.getEnvironment().getWarningCount());
		assertEquals(0, factory.getEnvironment().getErrorCount());

	}

}
