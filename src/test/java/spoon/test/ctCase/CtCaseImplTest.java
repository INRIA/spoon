package spoon.test.ctCase;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.factory.Factory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.CoreMatchers.is;

public class CtCaseImplTest {

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
}