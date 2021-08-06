package spoon.test.ctStatementList;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.factory.Factory;
import spoon.support.reflect.code.CtStatementListImpl;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CtStatementListImplTest {

    @Test
    void testInsertBeginWithListOfStatements() {
        // contract: insertBegin adds a list of statements at the beginning of the statementList

        // arrange
        Factory factory = new Launcher().getFactory();

        CtStatement initialStatement = factory.Code().createCodeSnippetStatement("int preexisting = 42;").compile();

        CtStatement firstStatementToBeInserted = factory.Code().createCodeSnippetStatement("int first = 1;").compile();
        CtStatement secondStatementToBeInserted = factory.Code().createCodeSnippetStatement("int second = 2;").compile();

        CtStatementList mainStatementList =  new CtStatementListImpl<CtStatement>();
        mainStatementList.addStatement(initialStatement);

        CtStatementList statementListToBeAddedToTheMainList =  new CtStatementListImpl<CtStatement>();
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
}
