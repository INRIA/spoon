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
package spoon.test.ctCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static spoon.testing.utils.ModelUtils.build;

import java.util.List;

import org.junit.jupiter.api.Test;

import spoon.Launcher;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

@SuppressWarnings({"unchecked", "rawtypes"})
public class CtCaseTest {

	@Test
	public void insertAfterStatementInSwitchCaseWithoutException() throws Exception {
		String packageName = "spoon.test.ctCase.testclasses";
		String className = "ClassWithSwitchExample";
		Factory factory = factoryFor(packageName, className);
		List<CtCase> elements = elementsOfType(CtCase.class, factory);
		assertEquals(3, elements.size());
		CtCase firstCase = elements.get(0);
		List<CtStatement> statements = firstCase.getStatements();
		assertEquals(2, statements.size());
		CtStatement newStatement = factory.Code().createCodeSnippetStatement("result = 0");
		statements.get(0).insertAfter(newStatement);
		statements = firstCase.getStatements();
		assertEquals(3, statements.size());
		assertSame(statements.get(1), newStatement);
	}

	@Test
	public void insertBeforeStatementInSwitchCaseWithoutException() throws Exception {
		String packageName = "spoon.test.ctCase.testclasses";
		String className = "ClassWithSwitchExample";
		Factory factory = factoryFor(packageName, className);
		List<CtCase> elements = elementsOfType(CtCase.class, factory);
		assertEquals(3, elements.size());
		CtCase firstCase = elements.get(0);
		List<CtStatement> statements = firstCase.getStatements();
		assertEquals(2, statements.size());
		CtStatement newStatement = factory.Code().createCodeSnippetStatement("result = 0");
		statements.get(0).insertBefore(newStatement);
		statements = firstCase.getStatements();
		assertEquals(3, statements.size());
		assertSame(statements.get(0), newStatement);
	}

	@Test
	void testInsertBeginWithListOfStatements() {
		// contract: insertBegin adds a list of statements at the beginning of a case, i.e the list is added above two
		// exiting statements, and the order of statements in the list remains the same

		// arrange
		Factory factory = new Launcher().getFactory();
		CtCase<?> testCase = factory.createCase();
		testCase.addStatement(factory.createCodeSnippetStatement("int preexisting = 42;").compile());

		CtStatement firstStatementToBeInserted = factory.createCodeSnippetStatement("int first = 1;").compile();
		CtStatement secondStatementToBeInserted = factory.createCodeSnippetStatement("int second = 2;").compile();

		CtStatementList statementList = factory.createStatementList();
		statementList.addStatement(firstStatementToBeInserted);
		statementList.addStatement(secondStatementToBeInserted);

		// act
		testCase.insertBegin(statementList);

		// assert
		assertEquals(firstStatementToBeInserted, testCase.getStatement(0));
		assertEquals(secondStatementToBeInserted, testCase.getStatement(1));
		assertThat(firstStatementToBeInserted.getParent(), is(testCase));
		assertThat(secondStatementToBeInserted.getParent(), is(testCase));
	}

	@Test
	void testRemoveStatement() {
		// contract: removeStatement removes a statement form a case having a single statement

		Factory factory = new Launcher().getFactory();
		CtStatement testStatement = factory.Code().createCodeSnippetStatement("int hello = 1;").compile();
		CtCase<?> testCase = factory.createCase();
		testCase.addStatement(testStatement);
		assertThat(testCase.getStatements().size(), is(1));

		testCase.removeStatement(testStatement);

		assertThat(testCase.getStatements().size(), is(0));
	}

	@Test
	void testInsertBeginWithSingleStatement() {
		// contract: insertBegin adds a statement at the beginning of a case, i.e above an already existing statement

		Factory factory = new Launcher().getFactory();
		CtCase<?> testCase = factory.createCase();
		testCase.addStatement(factory.createCodeSnippetStatement("int preExisting = 0;").compile());
		CtStatement statementToBeInserted = factory.Code().createCodeSnippetStatement("int first = 1;").compile();

		testCase.insertBegin(statementToBeInserted);

		assertThat(testCase.getStatement(0), is(statementToBeInserted));
		assertThat(statementToBeInserted.getParent(), is(testCase));
	}

	@Test
	void testInsertEndWithSingleStatement() {
		// contract: insertEnd adds a statement at the end of a case, i.e below an already existing statement

		Factory factory = new Launcher().getFactory();
		CtCase<?> testCase = factory.createCase();
		testCase.addStatement(factory.createCodeSnippetStatement("int preExisting = 0;").compile());
		CtStatement statementToBeInserted = factory.Code().createCodeSnippetStatement("int first = 1;").compile();

		testCase.insertEnd(statementToBeInserted);

		int lastStatementIndex = testCase.getStatements().size() - 1;
		assertThat(testCase.getStatement(lastStatementIndex), is(statementToBeInserted));
		assertThat(statementToBeInserted.getParent(), is(testCase));
	}

	@Test
	void testInsertEndWithListOfStatements() {
		// contract: insertEnd adds a list of statements at the end of a case, i.e the list is added below an
		// existing statement, and the order of statements in the list remains the same

		Factory factory = new Launcher().getFactory();
		CtCase<?> testCase = factory.createCase();
		testCase.addStatement(factory.createCodeSnippetStatement("int preExisting = 0;").compile());

		CtStatement firstStatementToBeInserted = factory.createCodeSnippetStatement("int first = 1;").compile();
		CtStatement secondStatementToBeInserted = factory.createCodeSnippetStatement("int second = 2;").compile();

		CtStatementList statementList = factory.Core().createStatementList();
		statementList.addStatement(firstStatementToBeInserted);
		statementList.addStatement(secondStatementToBeInserted);

		// act
		testCase.insertEnd(statementList);

		// assert
		int lastStatementIndex = testCase.getStatements().size() - 1;
		int secondLastStatementIndex = lastStatementIndex - 1;
		assertEquals(firstStatementToBeInserted, testCase.getStatement(secondLastStatementIndex));
		assertEquals(secondStatementToBeInserted, testCase.getStatement(lastStatementIndex));
		assertThat(firstStatementToBeInserted.getParent(), is(testCase));
		assertThat(secondStatementToBeInserted.getParent(), is(testCase));
	}

	private <T extends CtElement> List<T> elementsOfType(Class<T> type, Factory factory) {
		return Query.getElements(factory, new TypeFilter<>(type));
	}

	private Factory factoryFor(String packageName, String className) throws Exception {
		return build(packageName, className).getFactory();
	}
}
