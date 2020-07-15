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

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtBFSIterator;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.statementComment.testclasses.AllStmtExtensions;

import java.lang.annotation.Annotation;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class StatementCommentTest {
	String EOL;
	
	public StatementCommentTest() {
		EOL = System.getProperty("line.separator");
	}
	
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
	public void testBlockStatementWithinBody(){
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m1 =  allstmt.getMethod("m1");
		assertTrue(m1.getBody().getStatement(4) instanceof CtBlock);
		CtBlock blockWithinBody = m1.getBody().getStatement(4);
		blockWithinBody.comment();
		assertTrue(m1.getBody().getStatement(4) instanceof CtComment);
		CtComment blockAsComment = (CtComment) m1.getBody().getStatement(4);
		assertEquals("{" + EOL  + 
				"int j = 10;" + EOL  + 
				"}", blockAsComment.getContent());
	}
	
	@Test
	public void testMethodBodyEmptyStatement(){
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m2 =  allstmt.getMethod("m2");
		m2.getBody().comment();
		assertEquals("{" + EOL +
				"}", m2.getBody().prettyprint());
	}
	
	@Test
	public void testMethodBodyNonEmptyStatement(){
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
	
	@Test
	public void testCaseStatement(){
		
	}
	
	@Test
	public void testFlowBreakStatement(){
		
	}
	
	@Test
	public void testClassStatement(){
		
	}
	
	@Test
	public void testCodeSnippetStatement(){
		
	}
	
	@Test
	public void testCommentStatement(){
		
	}
	
	@Test
	public void testConstructorCallStatement(){
		
	}
	
	@Test
	public void testIfStatement(){
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
	public void testInvocationStatement(){
		
	}
	
	@Test
	public void testLocalVariableStatement(){
		
	}
	
	@Test
	public void testLoopStatement(){
		
	}
	
	@Test
	public void testSwitchStatement(){
		
	}
	
	@Test
	public void testSynchronousStatement(){
		
	}
	
	@Test
	public void testTryStatement(){
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
	
	@Test
	public void testUnaryOperatorStatement(){
		Launcher launcher = setUpTest();
		CtClass<?> allstmt = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.statementComment.testclasses.AllStmtExtensions");
		CtMethod<?> m3 =  allstmt.getMethod("m3");
		assertTrue(m3.getBody().getStatement(2) instanceof CtUnaryOperator);
		CtUnaryOperator<?> incrementStmt = (CtUnaryOperator) m3.getBody().getStatement(2);
		incrementStmt.comment();
		CtComment incrementComment = m3.getBody().getStatement(2);
		assertEquals("r++;", incrementComment.getContent());
	}
}
