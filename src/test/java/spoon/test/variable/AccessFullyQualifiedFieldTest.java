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
package spoon.test.variable;

import org.junit.Test;
import spoon.ContractVerifier;
import spoon.Launcher;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.test.main.MainTest;
import spoon.test.variable.testclasses.Tacos;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class AccessFullyQualifiedFieldTest {
	@Test
	public void testCheckAssignmentContracts() throws Exception {
		final Factory factory = build(Tacos.class);

		new ContractVerifier(factory.Package().getRootPackage()).checkAssignmentContracts();
	}

	private String buildResourceAndReturnResult(String pathResource, String output) {
		Launcher spoon = new Launcher();
		//spoon.setArgs(new String[]{"--with-imports"});
		spoon.addInputResource(pathResource);
		spoon.setSourceOutputDirectory(output);
		spoon.run();
		PrettyPrinter prettyPrinter = spoon.createPrettyPrinter();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);

		prettyPrinter.calculate(element.getPosition().getCompilationUnit(), toPrint);
		return prettyPrinter.getResult();
	}

	@Test
	public void testNoFQNWhenShadowedByField() {
		// contract: no fully qualified name if top package is shadowed by a field variable

		String pathResource = "src/test/java/spoon/test/variable/testclasses/BurritosFielded.java";
		String output = "target/spooned-" + this.getClass().getSimpleName() + "-Field/";
		String result = this.buildResourceAndReturnResult(pathResource, output);

		assertTrue("The java file should contain import for Launcher", result.contains("import spoon.Launcher;"));
		assertTrue("The xx variable is attributed with Launcher.SPOONED_CLASSES", result.contains("xx = Launcher.SPOONED_CLASSES"));
		canBeBuilt(output, 7);
	}

	@Test
	public void testNoFQNWhenShadowedByLocalVariable() {
		// contract: no fully qualified name if top package is shadowed by a local variable
		String output = "target/spooned-" + this.getClass().getSimpleName() + "-Local/";
		String pathResource = "src/test/java/spoon/test/variable/testclasses/Burritos.java";
		String result = this.buildResourceAndReturnResult(pathResource, output);

		assertTrue("The java file should contain import for Launcher", result.contains("import spoon.Launcher;"));
		assertTrue("The x variable should be attributed with SPOONED_CLASSES", result.contains("x = Launcher.SPOONED_CLASSES"));
		assertTrue("The java.util.Map is not imported", !result.contains("import java.util.Map"));
		assertTrue("The Map type use FQN", result.contains("java.util.Map uneMap"));
		assertTrue("The other variable use FQN too", result.contains("ForStaticVariables.Map"));
		canBeBuilt(output, 7);
	}

	@Test
	public void testNoFQNWhenUsedInInnerClassAndShadowedByLocalVariable() {
		// contract: no fully qualified name if top package is shadowed by a local variable
		String output = "target/spooned-" + this.getClass().getSimpleName() + "-StaticMethod/";
		String pathResource = "src/test/java/spoon/test/variable/testclasses/BurritosStaticMethod.java";
		String result = this.buildResourceAndReturnResult(pathResource, output);
		//the package name `spoon.test.variable.testclasses` cannot be used in FQN mode because it is shadowed by local variable `spoon`
		//so use at least Type name
		assertTrue("The inner class should contain call using import", result.contains(" BurritosStaticMethod.toto();"));
		canBeBuilt(output, 7);
	}

	@Test
	public void testNoFQNWhenUsedInTryCatch() {
		// contract: no fully qualified name if top package is shadowed by a local variable
		String output = "target/spooned-" + this.getClass().getSimpleName() + "-TryCatch/";
		String pathResource = "src/test/java/spoon/test/variable/testclasses/BurritosWithTryCatch.java";
		String result = this.buildResourceAndReturnResult(pathResource, output);
		assertTrue("The java file should contain import for Launcher", result.contains("import spoon.Launcher;"));
		assertTrue("The xx variable should be attributed with SPOONED_CLASSES", result.contains("xx = Launcher.SPOONED_CLASSES"));
		canBeBuilt(output, 7);
	}

	@Test
	public void testNoFQNWhenUsedInLoop() {
		// contract: no fully qualified name if top package is shadowed by a local variable
		String output = "target/spooned-" + this.getClass().getSimpleName() + "-Loop/";
		String pathResource = "src/test/java/spoon/test/variable/testclasses/BurritosWithLoop.java";
		String result = this.buildResourceAndReturnResult(pathResource, output);
		assertTrue("The java file should contain import for Launcher", result.contains("import spoon.Launcher;"));
		assertTrue("The xx variable should be attributed with SPOONED_CLASSES", result.contains("xx = Launcher.SPOONED_CLASSES"));
		canBeBuilt(output, 7);
	}

	@Test
	public void testStaticImportWithAutoImport() {
		String output = "target/spooned-" + this.getClass().getSimpleName() + "-MultiAutoImport/";
		String pathResource = "src/test/java/spoon/test/variable/testclasses/MultiBurritos.java";

		Launcher spoon = new Launcher();
		spoon.setArgs(new String[]{"--with-imports"});
		spoon.addInputResource(pathResource);
		spoon.setSourceOutputDirectory(output);
		spoon.run();
		PrettyPrinter prettyPrinter = spoon.createPrettyPrinter();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);

		prettyPrinter.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = prettyPrinter.getResult();
		assertTrue("The result should contain a static import for spoon.Launcher.SPOONED_CLASSES", result.contains("import static spoon.Launcher.SPOONED_CLASSES;"));
		assertTrue("The variable x should be assigned with only SPOONED_CLASSES", result.contains("Object x = SPOONED_CLASSES;"));
		assertTrue("The result should not contain a static import for spoon.test.variable.testclasses.ForStaticVariables.foo as it is in the same package", !result.contains("import static spoon.test.variable.testclasses.ForStaticVariables.foo;"));
		assertTrue("The result should not contain a import static for spoon.test.variable.testclasses.MultiBurritos.toto as it is in the same class", !result.contains("import static spoon.test.variable.testclasses.MultiBurritos.toto;"));
		assertTrue("The result should not contain a FQN for toto", !result.contains("spoon.test.variable.testclasses.MultiBurritos.toto();"));
		assertTrue("The result should not contain a FQN for spoon access", !result.contains("spoon.test.variable.testclasses.MultiBurritos.spoon = \"truc\";"));
		assertTrue("The result should not contain a FQN for foo", !result.contains("spoon.test.variable.testclasses.ForStaticVariables.foo();"));

		canBeBuilt(output, 7);
	}

	@Test
	public void testNoFQNAndStaticImport() {
		// contract: no fully qualified name if top package is shadowed by a local variable
		String output = "target/spooned-" + this.getClass().getSimpleName() + "-MultiNoAutoImport/";
		String pathResource = "src/test/java/spoon/test/variable/testclasses/MultiBurritos.java";
		String result = this.buildResourceAndReturnResult(pathResource, output);
		assertTrue("The result should contain a static import for spoon.Launcher.SPOONED_CLASSES", result.contains("import static spoon.Launcher.SPOONED_CLASSES;"));
		assertTrue("The result should not contain a FQN call for foo (i.e. spoon.test.variable.testclasses.ForStaticVariables.foo())", !result.contains("spoon.test.variable.testclasses.ForStaticVariables.foo()"));

		canBeBuilt(output, 7);
	}

	@Test
	public void testPrivateStaticImportShouldNotBeImportedInSameClass() {
		String output = "target/spooned-" + this.getClass().getSimpleName() + "-privateStatic/";
		String pathResource = "src/test/java/spoon/test/variable/testclasses/digest/DigestUtil.java";
		String result = this.buildResourceAndReturnResult(pathResource, output);
		assertTrue("The result should not contain a static import for STREAM_BUFFER_LENGTH", !result.contains("import static spoon.test.variable.testclasses.digest.DigestUtil.STREAM_BUFFER_LENGTH;"));

		canBeBuilt(output, 7);
	}
}
