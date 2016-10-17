package spoon.test.processing;

import org.apache.log4j.Level;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

public class ProcessingTest {

	@Test
	public void testInsertBegin() throws Exception {
		CtClass<?> type = build("spoon.test.processing", "SampleForInsertBefore");
		for (CtMethod<?> meth : type.getMethods()) {
			int i = meth.getBody().getStatements().size();
			meth.getBody().insertBegin(type.getFactory().Code()
					.createCodeSnippetStatement("int i = 0;"));
			assertEquals("insert failed for method " + meth.getSimpleName(),
					i + 1, meth.getBody().getStatements().size());
			assertEquals("insert failed for method " + meth.getSimpleName(),
					"int i = 0;", meth.getBody().getStatement(0).toString());
		}
		for (CtConstructor<?> constructor : type.getConstructors()) {
			int i = constructor.getBody().getStatements().size();
			constructor.getBody().insertBegin(type.getFactory().Code()
					.createCodeSnippetStatement("int i = 0;"));
			assertEquals("insert failed for constructor " + constructor.getSimpleName(),
					i + 1,
					constructor.getBody().getStatements().size());
			assertEquals("insert failed for constructor " + constructor.getSimpleName(),
					"int i = 0;",
					constructor.getBody().getStatement(1).toString());
		}

		CtConstructor<?> constructor = type.getConstructor(type.getFactory().Type().INTEGER_PRIMITIVE);
		String myBeforeStatementAsString = "int before";
		for (CtSwitch<?> ctSwitch : constructor.getElements(new TypeFilter<CtSwitch<?>>(CtSwitch.class))) {
			ctSwitch.insertBefore(type.getFactory().Code()
					.createCodeSnippetStatement(myBeforeStatementAsString));
		}
		assertEquals("insert has not been done at the right position", myBeforeStatementAsString, constructor.getBody().getStatement(3).toString());
		assertEquals("insert has not been done at the right position", myBeforeStatementAsString, constructor.getBody().getStatement(5).toString());
		assertEquals("insert has not been done at the right position", myBeforeStatementAsString, constructor.getBody().getStatement(7).toString());

		assertFalse("switch should not be the same", constructor.getBody().getStatement(6).equals(constructor.getBody().getStatement(8)));
		assertFalse("switch should not be the same", constructor.getBody().getStatement(6).toString().equals(constructor.getBody().getStatement(8).toString()));

	}

	@Test
	public void testInsertEnd() throws Exception {
		CtClass<?> type = build("spoon.test.processing", "SampleForInsertBefore");
		for (CtMethod<?> meth : type.getMethods()) {
			int i = meth.getBody().getStatements().size();
			meth.getBody().insertEnd(type.getFactory().Code()
					.createCodeSnippetStatement("int i = 0"));
			assertEquals("insert failed for method " + meth.getSimpleName(),
					i + 1, meth.getBody().getStatements().size());
			assertEquals("insert failed for method " + meth.getSimpleName(),
					"int i = 0", meth.getBody().getStatement(meth.getBody().getStatements().size() - 1).toString());
		}
		for (CtConstructor<?> constructor : type.getConstructors()) {
			int i = constructor.getBody().getStatements().size();
			constructor.getBody().insertEnd(type.getFactory().Code()
					.createCodeSnippetStatement("int i = 0"));
			assertEquals("insert failed for constructor " + constructor.getSimpleName(),
					i + 1,
					constructor.getBody().getStatements().size());
			assertEquals("insert failed for constructor",
					"int i = 0",
					constructor.getBody().getStatement(constructor.getBody().getStatements().size() - 1).toString());
		}

		CtConstructor<?> constructor = type.getConstructor(type.getFactory().Type().INTEGER_PRIMITIVE);
		String myBeforeStatementAsString = "int after";
		for (CtSwitch<?> ctSwitch : constructor.getElements(new TypeFilter<CtSwitch<?>>(CtSwitch.class))) {
			ctSwitch.insertAfter(type.getFactory().Code()
					.createCodeSnippetStatement(myBeforeStatementAsString));
		}
		assertEquals("insert has not been done at the right position", myBeforeStatementAsString, constructor.getBody().getStatement(3).toString());
		assertEquals("insert has not been done at the right position", myBeforeStatementAsString, constructor.getBody().getStatement(5).toString());
		assertEquals("insert has not been done at the right position", myBeforeStatementAsString, constructor.getBody().getStatement(7).toString());

		assertFalse("switch should not be the same", constructor.getBody().getStatement(6).equals(constructor.getBody().getStatement(8)));
		assertFalse("switch should not be the same", constructor.getBody().getStatement(6).toString().equals(constructor.getBody().getStatement(8).toString()));

	}

	@Test
	public void testProcessorNotFoundThrowAnException() throws Exception {
		try {
			new Launcher().run(new String[]{
					"-p", "fr.inria.gforge.spoon.MakeAnAwesomeTacosProcessor"
			});
			fail("The processor doesn't exist. We must throw an exception.");
		} catch (SpoonException ignore) {
		}
	}

	//toy class for the next test
	class WrongProcessor extends AbstractProcessor<CtElement> {
		public WrongProcessor(int myParameter) {

		}

		@Override
		public void process(CtElement element) {
			System.out.println(element);
		}
	}

	@Test
	public void testProcessorWithNoArgumentsInConstructor() throws Exception {

		/* throw correctly an exception when trying to use a processor with constructor with args */

		Launcher l = new Launcher();
		l.getEnvironment().setLevel(Level.ERROR.toString());
		l.buildModel();
		try {
			new JDTBasedSpoonCompiler(l.getFactory()).instantiateAndProcess(Collections.singletonList("spoon.test.processing.ProcessingTest$WrongProcessor"));
			fail();
		} catch (SpoonException e) {
			assertTrue(e.getMessage().startsWith("Unable to instantiate processor"));
			assertTrue(e.getMessage().endsWith("Your processor should have a constructor with no arguments"));
			assertTrue(e.getCause() instanceof java.lang.InstantiationException);// we are able to retrieve the exception parent
		}
	}
}
