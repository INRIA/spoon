package spoon.test.intercession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.test.TestUtils;

public class IntercessionTest {
	Factory factory = TestUtils.createFactory();
	@Test
	public void testInsertBegin() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " int x=0;"
								+ "}" + "};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		assertEquals(1, body.getStatements().size());

		// adding a new statement;
		CtReturn<Object> returnStmt = factory.Core().createReturn();
		body.insertBegin(returnStmt);
		assertEquals(2, body.getStatements().size());
		assertSame(returnStmt, body.getStatements().get(0));
	}

	@Test
	public void testInsertEnd() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " int x=0;"
								+ " String foo=\"toto\";" + "}" + "};")
				.compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		assertEquals(2, body.getStatements().size());

		// adding a new statement;
		CtReturn<Object> returnStmt = factory.Core().createReturn();
		body.insertEnd(returnStmt);
		assertEquals(3, body.getStatements().size());
		assertSame(returnStmt, body.getStatements().get(2));
	}

	@Test
	public void testInsertBefore() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " int x=0;"
								+ " int y=0;" + " int z=x+y;" + "}" + "};")
				.compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		assertEquals(3, body.getStatements().size());

		CtStatement s = body.getStatements().get(2);
		assertEquals("int z = x + y", s.toString());

		// adding a new statement;
		CtCodeSnippetStatement stmt = factory.Core()
				.createCodeSnippetStatement();
		stmt.setValue("System.out.println(x);");
		s.insertBefore(stmt);
		assertEquals(4, body.getStatements().size());
		assertSame(stmt, body.getStatements().get(2));
	}

	@Test
	public void test_setThrownExpression() {
		CtThrow throwStmt = factory.Core().createThrow();
		CtExpression<Exception> exp = factory.Code()
				.createCodeSnippetExpression("e");
		throwStmt.setThrownExpression(exp);
		assertEquals("throw e", throwStmt.toString());
	}

	@Test
	public void testInsertIfIntercession() {
		String ifCode = "if (1 == 0)\n" + "    return 1;\n" + "else\n"
				+ "    return 0;\n" + "";
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public int bar() {" + ifCode + "}"
								+ "};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		assertEquals(1, body.getStatements().size());

		CtIf ifStmt = (CtIf) foo.getBody().getStatements().get(0);
		String s = ifStmt.toString().replace("\r", "");
		assertEquals(ifCode, s);
		CtReturn<?> r1 = (CtReturn<?>) ifStmt.getThenStatement();
		CtReturn<?> r2 = (CtReturn<?>) ifStmt.getElseStatement();

		ifStmt.setThenStatement(r2);
		assertSame(r2, ifStmt.getThenStatement());
		ifStmt.setElseStatement(r1);
		assertSame(r1, ifStmt.getElseStatement());

		s = ifStmt.toString().replace("\r", "");
		String ifCodeNew = "if (1 == 0)\n" + "    return 0;\n" + "else\n"
				+ "    return 1;\n" + "";
		assertEquals(ifCodeNew, s);
	}

	@Test
	public void testInsertAfter() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " int x=0;"
								+ " int y=0;" + " int z=x+y;" + "}" + "};")
				.compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		assertEquals(3, body.getStatements().size());

		CtStatement s = body.getStatements().get(2);
		assertEquals("int z = x + y", s.toString());

		// adding a new statement;
		CtCodeSnippetStatement stmt = factory.Core()
				.createCodeSnippetStatement();
		stmt.setValue("System.out.println(x);");
		s.insertAfter(stmt);
		assertEquals(4, body.getStatements().size());
		assertSame(stmt, body.getStatements().get(3));
	}

}
