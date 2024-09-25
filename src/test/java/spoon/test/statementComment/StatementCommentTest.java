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
package spoon.test.statementComment;

import java.util.Iterator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import spoon.Launcher;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.support.reflect.code.CtCatchImpl;
import spoon.testing.utils.LineSeparatorExtension;
import static org.junit.jupiter.api.Assertions.*;

public class StatementCommentTest {
	String EOL = "\n";
		
	private Factory getSpoonFactory() {
		final Launcher launcher = new Launcher();
		launcher.run(new String[]{
				"-i", "./src/test/java/spoon/test/statementComment/testclasses",
				"-o", "./target/spooned/",
				"-c"
		});
		return launcher.getFactory();
	}
	
	private Launcher setUpTest() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/statementComment/testclasses/AllStmtExtensions.java");
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.run();
		return launcher;
	}
	
	@Test
	public void testAssertStatement(){
		// contract: test assert statement can be singly commented out
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m1 =  allstmt.getMethod("m1");
		m1.getBody().getStatement(0).comment();
		assertTrue(m1.getBody().getStatement(0) instanceof CtComment);
		CtComment assertAsComment = ((CtComment) m1.getBody().getStatement(0));
		assertEquals(assertAsComment.getContent(), "assert 1 == 5;");
	}

	@Test
	public void testAssignmentStatement(){
		// contract: test assignment statement can be singly commented out
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m1 =  allstmt.getMethod("m1");
		assertTrue(m1.getBody().getStatement(2) instanceof CtAssignment);
		CtAssignment<?, ?> assignmentStatement = (CtAssignment<?, ?>) m1.getBody().getStatement(2);
		assignmentStatement.comment();
		assertTrue(m1.getBody().getStatement(2) instanceof CtComment);
		CtComment assignmentAsComment = ((CtComment) m1.getBody().getStatement(2));
		assertEquals(assignmentAsComment.getContent(), "r = 20;");
	}
	
	@Test
	@ExtendWith(LineSeparatorExtension.class)
	public void testBlockStatementWithinBody(){
		// contract: test a CtBlock within body is commented out as a block comment
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m1 =  allstmt.getMethod("m1");
		assertTrue(m1.getBody().getStatement(4) instanceof CtBlock);
		CtBlock<?> blockWithinBody = m1.getBody().getStatement(4);
		blockWithinBody.comment();
		assertTrue(m1.getBody().getStatement(4) instanceof CtComment);
		CtComment blockAsComment = (CtComment) m1.getBody().getStatement(4);
		assertEquals("{" + EOL  + 
				"int j = 10;" + EOL  + 
				"}", blockAsComment.getContent());
	}
	
	@Test
	@ExtendWith(LineSeparatorExtension.class)
	public void testMethodBodyEmptyStatement(){
		// contract: test that a CtBlock representing empty method body doesn't change anything
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m2 =  allstmt.getMethod("m2");
		m2.getBody().comment();
		assertEquals("{" + EOL +
				"}", m2.getBody().prettyprint());
	}
	
	@Test
	@ExtendWith(LineSeparatorExtension.class)
	public void testMethodBodyNonEmptyStatement(){
		// contract: test CtBlock representing method body is commented out by commenting all contained statements individually
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m1 =  allstmt.getMethod("m1");
		m1.getBody().comment();
		for(CtStatement stmt: m1.getBody().getStatements()) {
			assertTrue(stmt instanceof CtComment);
		}
		assertEquals("{" + EOL  + 
				"    // assert 1 == 5;" + EOL  + 
				"    // int r = 10;" + EOL  + 
				"    // r = 20;" + EOL  + 
				"    // java.lang.String s = \"This is a new String!\";" + EOL  + 
				"    /* {" + EOL  + 
				"    int j = 10;" + EOL  + 
				"    }" + EOL  + 
				"     */" + EOL  + 
				"}", m1.getBody().prettyprint());
	}
	
	public void testCaseStatement(){
		// contract: test an isolated case statement commented out leads to UnsupportedOperationException
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m5 =  allstmt.getMethod("m5");
		assertTrue(m5.getBody().getStatement(2) instanceof CtSwitch);
		CtSwitch<?> switchStmt = (CtSwitch<?>) m5.getBody().getStatement(2);
		CtCase<?> caseStmt = (CtCase<?>) switchStmt.getCases().get(1);
		assertThrows(UnsupportedOperationException.class, () -> caseStmt.comment());
	}

	public void testClassStatement(){
		// contract: test an entire class cannot be commented out
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		assertThrows(UnsupportedOperationException.class, () -> allstmt.comment());
	}

	@Test
	public void testCommentStatement(){
		// contract: test commenting out of an existing comment doesn't change it
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m6 =  allstmt.getMethod("m6");
		assertTrue(m6.getBody().getStatement(3) instanceof CtComment);
		CtComment comment = (CtComment) m6.getBody().getStatement(3);
		final String initialContent = comment.getContent();
		comment.comment();
		assertTrue(m6.getBody().getStatement(3) instanceof CtComment);
		assertEquals(((CtComment) m6.getBody().getStatement(3)).getContent(), initialContent);
	}
	
	@Test
	@ExtendWith(LineSeparatorExtension.class)
	public void testIfStatement(){
		// contract: test commenting of if statement
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m4 =  allstmt.getMethod("m4");
		assertTrue(m4.getBody().getStatement(0) instanceof CtIf);
		CtIf ifStmt = (CtIf) m4.getBody().getStatement(0);
		ifStmt.comment();
		assertTrue(m4.getBody().getStatement(0) instanceof CtComment);
		CtComment ifAsComment = (CtComment) m4.getBody().getStatement(0);
		assertEquals("if (5 > 6) {" + EOL  + 
				"java.lang.System.out.println(\"Impossible!\");" + EOL  + 
				"} else {" + EOL  + 
				"java.lang.System.out.println(\"Seems right...\");" + EOL  + 
				"}", ifAsComment.getContent());
	}

	@Test
	public void testLocalVariableStatement(){
		// contract: test commenting of local variable declaration statement
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m3 =  allstmt.getMethod("m3");
		assertTrue(m3.getBody().getStatement(1) instanceof CtLocalVariable);
		CtLocalVariable<?> locAsStmt = (CtLocalVariable<?>) m3.getBody().getStatement(1);
		locAsStmt.comment();
		assertTrue(m3.getBody().getStatement(1) instanceof CtComment);
		CtComment comm = (CtComment) m3.getBody().getStatement(1);
		assertEquals("int r = 30;", comm.getContent());
	}
	
	@Test
	@ExtendWith(LineSeparatorExtension.class)
	public void testLoopStatement(){
		// contract: test commenting of loop statement
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m6 =  allstmt.getMethod("m6");
		assertTrue(m6.getBody().getStatement(2) instanceof CtLoop);
		CtLoop loopAsStmt = m6.getBody().getStatement(2);
		loopAsStmt.comment();
		assertTrue(m6.getBody().getStatement(2) instanceof CtComment);
		assertEquals("for (int i = 0; i < 10; ++i) {" + EOL  + 
				"java.lang.System.out.println(i);" + EOL  + 
				"}", ((CtComment) m6.getBody().getStatement(2)).getContent());
	}
	
	@Test
	@ExtendWith(LineSeparatorExtension.class)
	public void testSwitchStatement(){
		// contract: test commenting of switch statement
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m5 =  allstmt.getMethod("m5");
		assertTrue(m5.getBody().getStatement(2) instanceof CtSwitch);
		CtSwitch<?> switchStmt = (CtSwitch<?>) m5.getBody().getStatement(2);
		switchStmt.comment();
		assertTrue(m5.getBody().getStatement(2) instanceof CtComment);
		CtComment switchAsComment = (CtComment) m5.getBody().getStatement(2);
		assertEquals("switch (t) {" + EOL +
				"case 1 :" + EOL +
				"java.lang.System.out.println(\"1\");" + EOL +
				"break;" + EOL + 
				"default :" + EOL + 
				"java.lang.System.out.println(\"None\");" + EOL + 
				"}", switchAsComment.getContent());
	}
	
	@Test
	@ExtendWith(LineSeparatorExtension.class)
	public void testSynchronousStatement(){
		// contract: test commenting of synchronous statement
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m6 =  allstmt.getMethod("m6");
		assertTrue(m6.getBody().getStatement(1) instanceof CtSynchronized);
		CtSynchronized synchronizedAsStmt = (CtSynchronized) m6.getBody().getStatement(1);
		synchronizedAsStmt.comment();
		assertTrue(m6.getBody().getStatement(1) instanceof CtComment);
		assertEquals("synchronized(obj) {" + EOL  + 
				"java.lang.System.out.println(\"Executing\");" + EOL  + 
				"}", ((CtComment) m6.getBody().getStatement(1)).getContent());
	}
	
	@Test
	@ExtendWith(LineSeparatorExtension.class)
	public void testTryStatement(){
		// contract: test commenting of try-catch statement
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m3 =  allstmt.getMethod("m3");
		assertTrue(m3.getBody().getStatement(0) instanceof CtTry);
		CtTry tryStmt = (CtTry) m3.getBody().getStatement(0);
		tryStmt.comment();
		assertTrue(m3.getBody().getStatement(0) instanceof CtComment);
		CtComment tryAsComment = m3.getBody().getStatement(0);
		assertEquals("try {" + EOL  + 
				"throw new java.lang.Exception();" + EOL  + 
				"} catch (java.lang.Exception e) {" + EOL  + 
				"java.lang.System.out.println(e);" + EOL  + 
				"}", tryAsComment.getContent());
	}
	

	public void testCatchStatementFail(){
		// contract: test commenting of isolated catch leads to UnsupportedOperationException exception
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m3 =  allstmt.getMethod("m3");
		assertTrue(m3.getBody().getStatement(0) instanceof CtTry);
		CtTry tryStmt = (CtTry) m3.getBody().getStatement(0);
		Iterator<CtCatch> it = tryStmt.getCatchers().iterator();
		assertThrows(UnsupportedOperationException.class, () -> {
			while(it.hasNext()) {
			((CtCatchImpl) it.next()).comment();
		}
	});
	}
	
	@Test
	public void testUnaryOperatorStatement(){
		// contract: test commenting out of UnaryOperator statement
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m3 =  allstmt.getMethod("m3");
		assertTrue(m3.getBody().getStatement(2) instanceof CtUnaryOperator);
		CtUnaryOperator<?> incrementStmt = (CtUnaryOperator) m3.getBody().getStatement(2);
		incrementStmt.comment();
		CtComment incrementComment = m3.getBody().getStatement(2);
		assertEquals("r++;", incrementComment.getContent());
	}

	@Test
	public void testCodeSnippetStatement(){
		// contract: test creation and comment out of code snippet
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m6 =  allstmt.getMethod("m6");
		CtCodeSnippetStatement codeSnippet = getSpoonFactory().createCodeSnippetStatement("int j = 10");
		m6.getBody().insertEnd(codeSnippet);
		assertTrue(m6.getBody().getStatement(4) instanceof CtCodeSnippetStatement);
		codeSnippet.comment();
		assertTrue(m6.getBody().getStatement(4) instanceof CtComment);
		CtComment comment = (CtComment) m6.getBody().getStatement(4);
		assertEquals("int j = 10;", comment.getContent());
	}
}
