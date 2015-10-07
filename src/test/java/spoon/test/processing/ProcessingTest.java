package spoon.test.processing;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static spoon.test.TestUtils.build;

public class ProcessingTest {

	@Test
	public void testInsertBegin() throws Exception {
		CtClass<?> type = build("spoon.test.processing",
				"SampleForInsertBefore");
		for (CtMethod<?> meth : type.getMethods()) {
			int i = meth.getBody().getStatements().size();
			meth.getBody().insertBegin(
					type.getFactory().Code()
							.createCodeSnippetStatement("int i = 0;"));
			assertEquals("insert failed for method " + meth.getSimpleName(),
					i + 1, meth.getBody().getStatements().size());
			assertEquals("insert failed for method " + meth.getSimpleName(),
					"int i = 0;", meth.getBody().getStatement(0).toString());
		}
		for (CtConstructor<?> constructor : type.getConstructors()) {
			int i = constructor.getBody().getStatements().size();
			constructor.getBody().insertBegin(
					type.getFactory().Code()
							.createCodeSnippetStatement("int i = 0;"));
			assertEquals(
					"insert failed for constructor "
							+ constructor.getSimpleName(), i + 1, constructor
							.getBody().getStatements().size());
			assertEquals(
					"insert failed for constructor "
							+ constructor.getSimpleName(), "int i = 0;",
					constructor.getBody().getStatement(1).toString());
		}
	}

	@Test
	public void testProcessorNotFoundThrowAnException() throws Exception {
		try {
			new Launcher().run(new String[] {
					"-p", "fr.inria.gforge.spoon.MakeAnAwesomeTacosProcessor"
			});
			fail("The processor doesn't exist. We must throw an exception.");
		} catch (SpoonException ignore) {
		}
	}
}
