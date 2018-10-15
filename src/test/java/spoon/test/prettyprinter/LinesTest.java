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
package spoon.test.prettyprinter;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class LinesTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/prettyprinter/testclasses/Validation.java"))
				.build();
		factory.getEnvironment().setPreserveLineNumbers(true);
		factory.getEnvironment().setAutoImports(false);
	}

	@Test
	public void testPrettyPrinterWithLines() {

		for (CtType<?> t : factory.Type().getAll()) {
			if (t.isTopLevel()) {
				DefaultJavaPrettyPrinter pp = new DefaultJavaPrettyPrinter(
						factory.getEnvironment());
				pp.calculate(t.getPosition().getCompilationUnit(), t
						.getPosition().getCompilationUnit().getDeclaredTypes());
			}
		}
		assertEquals(0, factory.getEnvironment().getWarningCount());
		assertEquals(0, factory.getEnvironment().getErrorCount());

		// contract: in line preserve mode, toString is not prefixed or suffixed by newlines
		String meth = factory.Type().get("spoon.test.prettyprinter.testclasses.Validation").getMethodsByName("isIdentifier").get(0).toString();
		// the added linebreaks due to line preservation are removed
		assertFalse(Pattern.compile("^\\s", Pattern.DOTALL).asPredicate().test(meth));

	}

	@Test
	public void testIdenticalPrettyPrinter() {
		// contract: the end line should also be preserved

		// setup
		String[] options = {"--output-type", "compilationunits",
				"--output", "target/testIdenticalPrettyPrinter",
				// those three options together are the closest to what the developer wrote
				"--enable-comments", "--lines", "--with-imports"};

		List<String> paths = new ArrayList<>();
		paths.add("spoon/test/prettyprinter/testclasses/A.java");
		paths.add("spoon/test/prettyprinter/testclasses/AClass.java");

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
			if (e.getPosition().isValidPosition()) {
				assertEquals(e.toString() + " not handled", e.getPosition().getLine(), el2.getPosition().getLine());
				assertEquals(e.toString() + " not handled", e.getPosition().getEndLine(), el2.getPosition().getEndLine());
			} else {
				assertFalse(el2.getPosition().isValidPosition());
			}
		}
		assertTrue(n>20);
	}

	@Test
	public void testCompileWhenUsingLinesArgument() {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--compile", "--with-imports", "--lines"});
		launcher.addInputResource("./src/test/java/spoon/test/prettyprinter/testclasses/FooCasper.java");
		launcher.run();

		List<CtType> fooCasperClass = launcher.getModel().getElements(new NamedElementFilter<>(CtType.class, "FooCasper"));
		assertEquals(1, fooCasperClass.size());
	}
}
