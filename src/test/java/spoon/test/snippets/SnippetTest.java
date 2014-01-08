package spoon.test.snippets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.test.TestUtils;

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

}
