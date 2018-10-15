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
package spoon.test.intercession.insertBefore;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class InsertMethodsTest {

	Factory factory;
	private CtClass<?> assignmentClass;
	private CtClass<?> insertExampleClass;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		spoon.createCompiler(factory, SpoonResourceHelper.resources("./src/test/java/spoon/test/intercession/insertBefore/InsertBeforeExample.java")).build();
		assignmentClass = factory.Code().createCodeSnippetStatement(//
				"class X {" //
						+ "public void foo() {" //
						+ "  int x=0;" //
						+ "  int y=0;" //
						+ "  int z=x+y;" //
						+ " }" //
						+ "};").compile();
		insertExampleClass = factory.Package().get("spoon.test.intercession.insertBefore").getType("InsertBeforeExample");
	}

	@Test
	public void testInsertBefore() {
		CtMethod<Void> foo = (CtMethod<Void>) assignmentClass.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		assertEquals(3, body.getStatements().size());

		CtStatement s = body.getStatements().get(2);
		assertEquals("int z = x + y", s.toString());

		// adding a new statement;
		CtCodeSnippetStatement stmt = factory.Core().createCodeSnippetStatement();
		stmt.setValue("System.out.println(x);");
		s.insertBefore(stmt);
		assertEquals(4, body.getStatements().size());
		assertSame(stmt, body.getStatements().get(2));
		assertEquals(s.getParent(), stmt.getParent());
	}

	@Test
	public void testInsertAfter() {
		CtMethod<Void> foo = (CtMethod<Void>) assignmentClass.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		assertEquals(3, body.getStatements().size());

		CtStatement s = body.getStatements().get(2);
		assertEquals("int z = x + y", s.toString());

		// adding a new statement;
		CtCodeSnippetStatement stmt = factory.Core().createCodeSnippetStatement();
		stmt.setValue("System.out.println(x);");
		s.insertAfter(stmt);
		assertEquals(4, body.getStatements().size());
		assertSame(stmt, body.getStatements().get(3));
		assertEquals(body, stmt.getParent());
	}

	@Test
	public void testInsertBeforeWithoutBrace() {
		CtMethod<?> ifWithoutBraces_m = insertExampleClass.getElements(new NamedElementFilter<>(CtMethod.class,"ifWithoutBraces")).get(0);

		// replace the return
		CtCodeSnippetStatement s = factory.Code().createCodeSnippetStatement("return 2");

		CtIf ifWithoutBraces = ifWithoutBraces_m.getElements(new TypeFilter<>(CtIf.class)).get(0);

		// Inserts a s before the then statement
		ifWithoutBraces.getThenStatement().insertBefore(s);

		assertTrue(ifWithoutBraces.getThenStatement() instanceof CtBlock);
		assertEquals(s, ((CtBlock<?>) ifWithoutBraces.getThenStatement()).getStatement(0));
		assertEquals(ifWithoutBraces.getThenStatement(), s.getParent());
	}

	@Test
	public void testInsertBeforeWithBrace() {
		CtMethod<?> ifWithBraces_m = insertExampleClass.getElements(new NamedElementFilter<>(CtMethod.class,"ifWithBraces")).get(0);

		// replace the return
		CtCodeSnippetStatement s = factory.Code().createCodeSnippetStatement("return 2");

		CtIf ifWithBraces = ifWithBraces_m.getElements(new TypeFilter<>(CtIf.class)).get(0);

		// Inserts a s before the then statement
		ifWithBraces.getThenStatement().insertBefore(s);
		assertTrue(ifWithBraces.getThenStatement() instanceof CtBlock);
		assertEquals(s, ((CtBlock<?>) ifWithBraces.getThenStatement()).getStatement(0));
		assertEquals(ifWithBraces.getThenStatement(), s.getParent());
	}

	@Test
	public void testInsertAfterWithoutBrace() {
		CtMethod<?> ifWithoutBraces_m = insertExampleClass.getElements(new NamedElementFilter<>(CtMethod.class,"ifWithoutBraces")).get(0);

		// replace the return
		CtCodeSnippetStatement s = factory.Code().createCodeSnippetStatement("return 2");

		CtIf ifWithoutBraces = ifWithoutBraces_m.getElements(new TypeFilter<>(CtIf.class)).get(0);

		// Inserts a s before the then statement
		ifWithoutBraces.getThenStatement().insertAfter(s);

		assertTrue(ifWithoutBraces.getThenStatement() instanceof CtBlock);
		assertEquals(s, ((CtBlock<?>) ifWithoutBraces.getThenStatement()).getStatement(1));
		assertEquals(ifWithoutBraces.getThenStatement(), s.getParent());
	}

	@Test
	public void testInsertAfterWithBrace() {
		CtMethod<?> ifWithBraces_m = insertExampleClass.getElements(new NamedElementFilter<>(CtMethod.class,"ifWithBraces")).get(0);

		// replace the return
		CtCodeSnippetStatement s = factory.Code().createCodeSnippetStatement("return 2");

		CtIf ifWithBraces = ifWithBraces_m.getElements(new TypeFilter<>(CtIf.class)).get(0);

		// Inserts a s before the then statement
		ifWithBraces.getThenStatement().insertAfter(s);
		assertTrue(ifWithBraces.getThenStatement() instanceof CtBlock);
		assertEquals(s, ((CtBlock<?>) ifWithBraces.getThenStatement()).getStatement(1));
		assertEquals(ifWithBraces.getThenStatement(), s.getParent());
	}

	@Test
	public void testInsertBeforeSwitchCase() {
		CtMethod<?> sm = insertExampleClass.getElements(new NamedElementFilter<>(CtMethod.class,"switchMethod")).get(0);

		// Adds a new snippet in a case.
		CtSwitch<?> sw = sm.getElements(new TypeFilter<CtSwitch<?>>(CtSwitch.class)).get(0);

		CtCase<?> ctCase1 = sw.getCases().get(2);
		CtCase<?> ctCase2 = sw.getCases().get(3);
		CtCodeSnippetStatement snippet = factory.Code().createCodeSnippetStatement("System.out.println(\"foo\")");
		ctCase1.getStatements().get(0).insertBefore(snippet);
		assertEquals(snippet, ctCase1.getStatements().get(0));
		assertEquals(ctCase1, snippet.getParent());

		CtCodeSnippetStatement snippet2 = snippet.clone();
		ctCase2.getStatements().get(1).insertBefore(snippet2);
		assertEquals(snippet2, ctCase2.getStatements().get(1));
		assertEquals(ctCase2, snippet2.getParent());

		// Creates a new case.
		CtCase<Object> caseElem = factory.Core().createCase();
		CtLiteral<Object> literal = factory.Core().createLiteral();
		literal.setValue(1);
		caseElem.setCaseExpression(literal);
		// here we may call either insertBefore(CtStatement) or insertBefore(CtStatementList)
		// ctCase.insertBefore(caseElem);
		// so force the correct insert
		CtCase<?> ctCase = sw.getCases().get(1);
		ctCase.insertBefore((CtStatement) caseElem);

		assertEquals(5, sw.getCases().size());
		assertEquals(caseElem, sw.getCases().get(1));
		assertEquals(ctCase, sw.getCases().get(2));
		assertEquals(sw, caseElem.getParent());
	}

	@Test
	public void testInsertAfterSwitchCase() {
		CtMethod<?> sm = insertExampleClass.getElements(new NamedElementFilter<>(CtMethod.class,"switchMethod")).get(0);

		// Adds a new snippet in a case.
		CtSwitch<?> sw = sm.getElements(new TypeFilter<CtSwitch<?>>(CtSwitch.class)).get(0);

		CtCase<?> ctCase1 = sw.getCases().get(2);
		CtCase<?> ctCase2 = sw.getCases().get(3);
		CtCodeSnippetStatement snippet = factory.Code().createCodeSnippetStatement("System.out.println(\"foo\")");
		ctCase1.getStatements().get(0).insertAfter(snippet);
		assertEquals(snippet, ctCase1.getStatements().get(1));
		assertEquals(ctCase1, snippet.getParent());

		CtCodeSnippetStatement snippet2 = snippet.clone();
		ctCase2.getStatements().get(1).insertAfter(snippet2);
		assertEquals(snippet2, ctCase2.getStatements().get(2));
		assertEquals(ctCase2, snippet2.getParent());

		// Creates a new case.
		CtCase<Object> caseElem = factory.Core().createCase();
		CtLiteral<Object> literal = factory.Core().createLiteral();
		literal.setValue(1);
		caseElem.setCaseExpression(literal);
		// here we may call either insertAfter(CtStatement) or insertAfter(CtStatementList)
		// ctCase.insertAfter(caseElem);
		// so force the correct insert
		CtCase<?> ctCase = sw.getCases().get(1);
		ctCase.insertAfter((CtStatement) caseElem);

		assertEquals(5, sw.getCases().size());
		assertEquals(caseElem, sw.getCases().get(2));
		assertEquals(ctCase, sw.getCases().get(1));
		assertEquals(sw, caseElem.getParent());
	}

	@Test
	public void insertBeforeAndUpdateParent() throws Exception {
		/**
		 * if (condition)
		 *     while (loop_condition)
		 *
		 * In this case the 'while' is inside an implicit block, but
		 * when we insert a new statement
		 *
		 * if (condition) {
		 *     newStatement
		 *     while (loop_condition)
		 *     ...
		 * }
		 *
		 * Now the while is inside an explicit block.
		 */
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		spoon.createCompiler(factory, SpoonResourceHelper.resources("./src/test/resources/spoon/test/intercession/insertBefore/InsertBeforeExample2.java")).build();

		// Get the 'while'
		List<CtWhile> elements = Query.getElements(factory, new TypeFilter<>(CtWhile.class));
		assertTrue(1 == elements.size());
		CtWhile theWhile = elements.get(0);

		// We make sure the parent of the while is the CtIf and not the block
		CtElement parent = theWhile.getParent();
		assertTrue(parent instanceof CtBlock);
		assertTrue(parent.isImplicit());
		CtIf ifParent = (CtIf) parent.getParent();

		// Create a new statement to be inserted before the while
		CtStatement insert = factory.Code().createCodeSnippetStatement("System.out.println()");

		// Insertion of the new statement
		theWhile.insertBefore(insert);

		// We make sure the parent of the while is updated
		CtElement newParent = theWhile.getParent();
		assertNotSame(newParent, ifParent);
		assertTrue(newParent instanceof CtBlock);
		assertFalse(newParent.isImplicit());
	}
}
