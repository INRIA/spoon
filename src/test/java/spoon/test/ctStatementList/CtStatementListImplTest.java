package spoon.test.ctStatementList;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.factory.Factory;
import spoon.support.reflect.code.CtStatementListImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CtStatementListImplTest {

    @Test
    void testInsertBeginWithListOfStatements() {
        // contract: insertBegin adds a list of statements at the beginning of the statementList

        // arrange
        Factory factory = new Launcher().getFactory();

        CtStatement initialStatement = factory.Code().createCodeSnippetStatement("initialStatement");

        CtStatement firstStatementToBeInserted = factory.Code().createCodeSnippetStatement("firstStatement");
        CtStatement secondStatementToBeInserted = factory.Code().createCodeSnippetStatement("secondStatement");

        CtStatementList mainStatementList =  new CtStatementListImpl<CtStatement>() {
            { addStatement(initialStatement); }
        };

        CtStatementList statementListToBeAddedToTheMainList =  new CtStatementListImpl<CtStatement>() {
            {
                addStatement(firstStatementToBeInserted);
                addStatement(secondStatementToBeInserted);
            }
        };

        // act
        mainStatementList.insertBegin(statementListToBeAddedToTheMainList);

        // assert
        CtStatement statementAtTheBeginningAfterInsertion = mainStatementList.getStatements().get(0);
        CtStatement secondStatementAtTheBeginningOfTheListAfterInsertion = mainStatementList.getStatements().get(1);
        assertEquals(firstStatementToBeInserted, statementAtTheBeginningAfterInsertion);
        assertEquals(secondStatementToBeInserted, secondStatementAtTheBeginningOfTheListAfterInsertion);
    }
}
