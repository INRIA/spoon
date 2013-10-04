package spoon.test.snippets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

public class SnippetTests {
	Factory factory = new Factory(new DefaultCoreFactory(), new StandardEnvironment());
	
	@Test
	public void testSnippetFullClass() {
		CtClass clazz = (CtClass) factory.Code().createCodeSnippetStatement(
				""
				+ "class X {"
				+ "public void foo() {"
				+ " int x=0;"
				+ "}"
				+ "};"
		).compile();
		CtMethod foo = (CtMethod) clazz.getMethods().toArray()[0];

		assertEquals(1, foo.getBody().getStatements().size());
	}
	
	@Test
	public void testSnippetWihErrors() {
		try {
			CtClass clazz = (CtClass) factory.Code().createCodeSnippetStatement(
					""
					+ "class X {"
					+ "public void foo() {"
					+ " int x=0 sdfsdf;"
					+ "}"
					+ "};"
			).compile();
			fail();
		}
		catch (Exception e) {
			// we excpect an exception the code is incorrect
		}
	}
	
}
