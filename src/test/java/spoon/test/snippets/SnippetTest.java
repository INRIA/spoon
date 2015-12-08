package spoon.test.snippets;

import org.junit.Test;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.test.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SnippetTest {
	Factory factory = TestUtils.createFactory();
	@Test
	public void testSnippetFullClass() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " int x=0;"
								+ "}" + "};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		assertEquals(1, foo.getBody().getStatements().size());
	}

	@Test
	public void testSnippetWihErrors() {
		try {
			factory.Code()
					.createCodeSnippetStatement(
							"" + "class X {" + "public void foo() {"
									+ " int x=0 sdfsdf;" + "}" + "};")
					.compile();
			fail();
		} catch (Exception e) {
			// we expect an exception the code is incorrect
		}
	}

	@Test
	public void testCompileSnippetSeveralTimes() throws Exception {
		// contract: a snippet object can be reused several times
		final Factory factory = TestUtils.createFactory();
		final CtCodeSnippetExpression<Object> snippet = factory.Code().createCodeSnippetExpression("1 > 2");

		// Compile a first time the snippet.
		final CtExpression<Object> compile = snippet.compile();
		// Compile a second time the same snippet.
		final CtExpression<Object> secondCompile = snippet.compile();

		assertTrue(compile instanceof CtBinaryOperator);
		assertEquals("1 > 2", compile.toString());
		assertTrue(secondCompile instanceof CtBinaryOperator);
		assertEquals("1 > 2", secondCompile.toString());

		// Compile a third time a snippet but with an expression set.
		snippet.setValue("1 > 3");
		final CtExpression<Object> thirdCompile = snippet.compile();
		assertTrue(thirdCompile instanceof CtBinaryOperator);
		assertEquals("1 > 3", thirdCompile.toString());
	}

	@Test
	public void testCompileSnippetWithContext() throws Exception {
		// contract: a snippet object can be compiled with a context in the factory.
		try {
			// Add a class in the context.
			factory.Class().create("AClass");
			// Try to compile a snippet with a context.
			factory.Code().createCodeSnippetStatement("int i = 1;").compile();
		} catch (ClassCastException e) {
			fail();
		}
	}
}
