package spoon.test.prettyprinter;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.prettyprinter.testclasses.QualifiedThisRef;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static spoon.test.limits.StaticFieldAccesOnInstance.test;

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

	@Test
	public void testIdenticalPrettyPrinter() throws  Exception{
		// contract: the end line should also be preserved

		// setup
		String[] options = {"--output-type", "compilationunits",
				"--output", "target/testIdenticalPrettyPrinter",
				// those three options together are the closest to what the developer wrote
				"--enable-comments", "--lines", "--with-imports"};

		List<String> paths = new ArrayList<>();
		paths.add("spoon/test/prettyprinter/testclasses/A.java");
		paths.add("spoon/test/prettyprinter/testclasses/AClass.java");
		//paths.add("spoon/test/prettyprinter/testclasses/QualifiedThisRef.java");
		//paths.add("spoon/test/prettyprinter/testclasses/ImportStatic.java");
		//paths.add("spoon/test/prettyprinter/testclasses/QualifiedThisRef.java");
		//paths.add("spoon/test/prettyprinter/testclasses/Rule.java");
		//paths.add("spoon/test/prettyprinter/testclasses/TypeIdentifierCollision.java");


		final Launcher launcher = new Launcher();
		launcher.setArgs(options);
		for (String path : paths) {
			launcher.addInputResource("./src/test/java/" + path);
		}
		launcher.run();

		final Launcher launcher2 = new Launcher();
		launcher2.setArgs(options);
		for (String path : paths) {
			launcher2.addInputResource("./target/testIdenticalPrettyPrinter/" + path);
		}
		launcher2.run();

		int n=0;
		List<CtElement> elements = launcher.getModel().getElements(new TypeFilter<>(CtElement.class));
		for (int i = 0; i < elements.size(); i++) {
			n++;
			CtElement e = elements.get(i);
			CtElement el2 = launcher2.getModel().getElements(new TypeFilter<>(CtElement.class)).get(i);
			assertNotSame(e, el2);
			assertEquals(e.toString() + " not handled", e.getPosition().getLine(), el2.getPosition().getLine());
			assertEquals(e.toString() + " not handled", e.getPosition().getEndLine(), el2.getPosition().getEndLine());
		}
		assertTrue(n>20);
	}
}
