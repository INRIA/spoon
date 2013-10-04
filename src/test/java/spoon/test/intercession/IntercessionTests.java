package spoon.test.intercession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

public class IntercessionTests {
	Factory factory = new Factory(new DefaultCoreFactory(), new StandardEnvironment());
	
	@Test
	public void testInsertBegin() {
		CtClass clazz = (CtClass) factory.Code().createCodeSnippetStatement(
				""
				+ "class X {"
				+ "public void foo() {"
				+ " int x=0;"
				+ "}"
				+ "};"
		).compile();
		CtMethod foo = (CtMethod) clazz.getMethods().toArray()[0];

		CtBlock body = foo.getBody();
		assertEquals(1, body.getStatements().size());
		
		// adding a new statement;
		CtReturn<Object> returnStmt = factory.Core().createReturn();
		body.insertBegin(returnStmt);
		assertEquals(2, body.getStatements().size());
		assertSame(returnStmt, body.getStatements().get(0));
	}
}
