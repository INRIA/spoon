package spoon.test.prettyprinter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

import org.junit.Test;

import org.mockito.internal.matchers.Null;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.test.prettyprinter.testclasses.MissingVariableDeclaration;
import spoon.testing.utils.ModelUtils;

import java.util.ArrayList;
import java.util.List;

public class PrinterTest {

	@Test
	public void testPrettyPrinter() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources(
								"./src/test/java/spoon/test/annotation/testclasses/PersistenceProperty.java",
								"./src/test/java/spoon/test/prettyprinter/Validation.java"))
				.build();
		for (CtType<?> t : factory.Type().getAll()) {
			t.toString();
		}
		assertEquals(0, factory.getEnvironment().getWarningCount());
		assertEquals(0, factory.getEnvironment().getErrorCount());

	}

	@Test
	public void testChangeAutoImportModeWorks() throws Exception {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setAutoImports(false);
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.addInputResource("./src/test/java/spoon/test/prettyprinter/testclasses/AClass.java");
		spoon.buildModel();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		assertTrue("The result should not contain imports: "+result, !result.contains("import java.util.List;"));

		// recreating an auto-immport  printer
		spoon.getEnvironment().setAutoImports(true);
		printer = spoon.createPrettyPrinter();

		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		result = printer.getResult();
		assertTrue("The result should now contain imports: "+result, result.contains("import java.util.List;"));
	}

	@Test
	public void testFQNModeWriteFQNConstructorInCtVisitor() {
		Launcher spoon = new Launcher();
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.getEnvironment().setAutoImports(false);
		spoon.addInputResource("./src/main/java/spoon/support/visitor/replace/ReplacementVisitor.java");
		spoon.buildModel();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		assertTrue("The result should contain FQN for constructor: "+result, result.contains("new spoon.support.visitor.replace.ReplacementVisitor("));
		assertTrue("The result should not contain reduced constructors: "+result, !result.contains("new ReplacementVisitor("));
	}

	@Test
	public void testAutoimportModeDontImportUselessStatic() {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setAutoImports(true);
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.addInputResource("./src/test/java/spoon/test/prettyprinter/testclasses/ImportStatic.java");
		spoon.buildModel();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		assertTrue("The result should not contain import static: ", !result.contains("import static spoon.test.prettyprinter.testclasses.sub.Constants.READY"));
		assertTrue("The result should contain import type: ", result.contains("import spoon.test.prettyprinter.testclasses.sub.Constants"));
		assertTrue("The result should contain import for TestCase: ", result.contains("import junit.framework.TestCase;"));
		assertTrue("The result should contain assertTrue(...): ", result.contains("TestCase.assertTrue(\"blabla\".equals(\"toto\"));"));
		assertTrue("The result should use System.out.println(Constants.READY): "+result, result.contains("System.out.println(Constants.READY);"));
	}

	@Test
	public void testRuleCanBeBuild() {
		Launcher spoon = new Launcher();
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.getEnvironment().setAutoImports(true);
		String output = "./target/spoon-rule/";
		spoon.addInputResource("./src/test/java/spoon/test/prettyprinter/testclasses/Rule.java");
		spoon.setSourceOutputDirectory(output);
		spoon.run();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		assertTrue("The result should contain direct this accessor for field: "+result, !result.contains("Rule.Phoneme.this.phonemeText"));
		canBeBuilt(output, 7);
	}

	@Test
	public void testJDTBatchCompilerCanBeBuild() {
		Launcher spoon = new Launcher();
		PrettyPrinter printer = spoon.createPrettyPrinter();
		spoon.getEnvironment().setAutoImports(false);
		String output = "./target/spoon-jdtbatchcompiler/";
		spoon.addInputResource("./src/main/java/spoon/support/compiler/jdt/JDTBatchCompiler.java");
		spoon.setSourceOutputDirectory(output);
		spoon.run();

		CtType element = spoon.getFactory().Class().getAll().get(0);
		List<CtType<?>> toPrint = new ArrayList<>();
		toPrint.add(element);
		printer.calculate(element.getPosition().getCompilationUnit(), toPrint);
		String result = printer.getResult();

		//assertTrue("The result should contain direct this accessor for field: "+result, !result.contains("Rule.Phoneme.this.phonemeText"));
		canBeBuilt(output, 7);
	}

	@Test
	public void testPrintingOfOrphanFieldReference() throws Exception {
		CtType<?> type = ModelUtils.buildClass(MissingVariableDeclaration.class);
		//delete the field, so the model is broken.
		//It may happen during substitution operations and then it is helpful to display descriptive error message
		type.getField("testedField").delete();
		//contract: printer fails with descriptive exception and not with NPE
		try {
			type.getMethodsByName("failingMethod").get(0).getBody().getStatement(0).toString();
			fail();
		} catch (SpoonException e) {
			assertTrue(e.getCause() instanceof NullPointerException);
			//the name of the missing field declaration is part of exception
			assertTrue(e.getMessage().indexOf("testedField")>=0);
			//the name of the method where field declaration is missing is part of exception
			assertTrue(e.getMessage().indexOf("failingMethod")>=0);
			//the name of the class where field is missing is part of exception
			assertTrue(e.getMessage().indexOf("MissingVariableDeclaration")>=0);
		} //other exceptions are not OK
	}
}
