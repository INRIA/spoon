/**
 * Copyright (C) 2006-2021 INRIA and contributors
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
package spoon.test.ctStatementList;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.factory.Factory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CtStatementListTest {
    private static CtStatementList getStatementListInitializedWithOneStatement() {
        Factory factory = new Launcher().getFactory();
        CtStatementList statementList = factory.Core().createStatementList();
        statementList.addStatement(factory.createCodeSnippetStatement("int preExisting = 0;").compile());
        return statementList;
    }

    @Test
    void testInsertBeginWithListOfStatements() {
        // contract: insertBegin adds a list of statements at the beginning of the statementList

        // arrange
        CtStatementList mainStatementList = getStatementListInitializedWithOneStatement();
        Factory factory = mainStatementList.getFactory();
        CtStatement firstStatementToBeInserted = factory.Code().createCodeSnippetStatement("int first = 1;").compile();
        CtStatement secondStatementToBeInserted = factory.Code().createCodeSnippetStatement("int second = 2;").compile();

        CtStatementList statementListToBeAddedToTheMainList = factory.Core().createStatementList();
        statementListToBeAddedToTheMainList.addStatement(firstStatementToBeInserted);
        statementListToBeAddedToTheMainList.addStatement(secondStatementToBeInserted);

        // act
        mainStatementList.insertBegin(statementListToBeAddedToTheMainList);

        // assert
        CtStatement statementAtTheBeginningAfterInsertion = mainStatementList.getStatements().get(0);
        CtStatement secondStatementAtTheBeginningOfTheListAfterInsertion = mainStatementList.getStatements().get(1);

        assertEquals(firstStatementToBeInserted, statementAtTheBeginningAfterInsertion);
        assertEquals(secondStatementToBeInserted, secondStatementAtTheBeginningOfTheListAfterInsertion);

        assertThat(firstStatementToBeInserted.getParent(), is(mainStatementList));
        assertThat(secondStatementToBeInserted.getParent(), is(mainStatementList));
    }

    @Test
    void testInsertBeginWithSingleStatement() {
        // contract: insertBegin adds a statement at the beginning of the statementList

        CtStatementList statementList = getStatementListInitializedWithOneStatement();
        Factory factory = statementList.getFactory();
        CtStatement statementToBeInserted = factory.Code().createCodeSnippetStatement("int first = 1").compile();

        statementList.insertBegin(statementToBeInserted);

        CtStatement statementAtTheBeginningAfterInsertion = statementList.getStatements().get(0);
        assertThat(statementAtTheBeginningAfterInsertion, is(statementToBeInserted));
        assertThat(statementAtTheBeginningAfterInsertion.getParent(), is(statementList));
    }

    @Test
    void testRemoveStatement() {
        // contract: removeStatement removes a statement form a StatementList having a single statement

        CtStatementList statementList = getStatementListInitializedWithOneStatement();
        assertThat(statementList.getStatements().size(), is(1));

        statementList.removeStatement(statementList.getStatements().get(0));

        assertThat(statementList.getStatements().size(), is(0));
    }

    @Test
    void testInsertEndWithSingleStatement() {
        // contract: insertEnd adds a statement at the end of a StatementList, i.e below an already existing statement

        CtStatementList statementList = getStatementListInitializedWithOneStatement();
        Factory factory = statementList.getFactory();
        CtStatement statementToBeInserted = factory.Code().createCodeSnippetStatement("int first = 1;").compile();

        statementList.insertEnd(statementToBeInserted);

        int lastStatementIndex = statementList.getStatements().size() - 1;
        assertThat(statementList.getStatement(lastStatementIndex), is(statementToBeInserted));
        assertThat(statementToBeInserted.getParent(), is(statementList));
    }

    @Test
    void testInsertEndWithListOfStatements() {
        // contract: insertEnd adds a list of statements at the end of a statementList, i.e the list is added below an
        // existing statement, and the order of statements in the list remains the same

        // arrange
        CtStatementList mainStatementList = getStatementListInitializedWithOneStatement();
        Factory factory = mainStatementList.getFactory();
        CtStatement firstStatementToBeInserted = factory.createCodeSnippetStatement("int first = 1;").compile();
        CtStatement secondStatementToBeInserted = factory.createCodeSnippetStatement("int second = 2;").compile();

        CtStatementList statementList = factory.Core().createStatementList();
        statementList.addStatement(firstStatementToBeInserted);
        statementList.addStatement(secondStatementToBeInserted);

        // act
        mainStatementList.insertEnd(statementList);

        // assert
        int lastStatementIndex = mainStatementList.getStatements().size() - 1;
        int secondLastStatementIndex = lastStatementIndex - 1;
        assertThat(mainStatementList.getStatement(secondLastStatementIndex), is(firstStatementToBeInserted));
        assertThat(mainStatementList.getStatement(lastStatementIndex), is(secondStatementToBeInserted));
        assertThat(firstStatementToBeInserted.getParent(), is(mainStatementList));
        assertThat(secondStatementToBeInserted.getParent(), is(mainStatementList));
    }
}
