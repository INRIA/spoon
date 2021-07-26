package spoon.test.ctCase;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.FileSystemFile;
import spoon.support.reflect.code.CtStatementListImpl;
import spoon.test.ctCase.testclasses.ClassWithSwitchExample;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        testCase.insertBegin(ctStatementList);

        // assert
        int firstStatementIndex = 0;
        int secondStatementIndex = 1;
        assertEquals(firstStatementToBeInserted, testCase.getStatement(firstStatementIndex));
        assertEquals(secondStatementToBeInserted, testCase.getStatement(secondStatementIndex));
    }

    private static CtClass getClassWithSwitchExample() {
        Launcher spoon = new Launcher();
        String pathToClassWithSwitchExample = "./src/test/java/spoon/test/ctCase/testclasses/ClassWithSwitchExample.java";
        spoon.addInputResource(new FileSystemFile(pathToClassWithSwitchExample));
        spoon.buildModel();

        return spoon.getFactory().Class().get(ClassWithSwitchExample.class);
    }

    public static CtStatement createStatement(CtClass ctClass, String statement) {
        return ctClass.getFactory().Code().createCodeSnippetStatement(Statement).compile();
    }
}
