package spoon.test.intercession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

public class IntercessionTest {
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


	@Test
	public void testInsertBefore() {
		CtClass clazz = (CtClass) factory.Code().createCodeSnippetStatement(
				""
				+ "class X {"
				+ "public void foo() {"
				+ " int x=0;"
				+ " int y=0;"
				+ " int z=x+y;"
				+ "}"
				+ "};"
		).compile();
		CtMethod foo = (CtMethod) clazz.getMethods().toArray()[0];

		CtBlock body = foo.getBody();
		assertEquals(3, body.getStatements().size());
		
		CtStatement s = (CtStatement) body.getStatements().get(2);
		assertEquals("int z = x + y", s.toString());
		
		// adding a new statement;
		CtCodeSnippetStatement stmt = factory.Core().createCodeSnippetStatement();
		stmt.setValue("System.out.println(x);");
		s.insertBefore(stmt);
		assertEquals(4, body.getStatements().size());
		assertSame(stmt, body.getStatements().get(2));
	}

	@Test
	public void testInsertAfter() {
		CtClass clazz = (CtClass) factory.Code().createCodeSnippetStatement(
				""
				+ "class X {"
				+ "public void foo() {"
				+ " int x=0;"
				+ " int y=0;"
				+ " int z=x+y;"
				+ "}"
				+ "};"
		).compile();
		CtMethod foo = (CtMethod) clazz.getMethods().toArray()[0];

		CtBlock body = foo.getBody();
		assertEquals(3, body.getStatements().size());
		
		CtStatement s = (CtStatement) body.getStatements().get(2);
		assertEquals("int z = x + y", s.toString());
		
		// adding a new statement;
		CtCodeSnippetStatement stmt = factory.Core().createCodeSnippetStatement();
		stmt.setValue("System.out.println(x);");
		s.insertAfter(stmt);
		assertEquals(4, body.getStatements().size());
		assertSame(stmt, body.getStatements().get(3));
	}

}
