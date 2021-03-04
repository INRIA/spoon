package spoon.reflect.visitor;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spoon.Launcher;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultJavaPrettyPrinterTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "1 + 2 + 3",
            "1 + (2 + 3)",
            "\"Sum: \" + (1 + 2)",
            "\"Sum: \" + 1 + 2",
            "-(1 + 2 + 3)",
            "true || true && false",
            "(true || false) && false"
    })
    public void testParenOptimizationCorrectlyPrintsParenthesesForExpressions(String rawExpression) {
        Launcher launcher = createLauncherWithOptimizeParenthesesPrinter();
        CtExpression<?> expr = launcher.getFactory()
                .createCodeSnippetExpression(rawExpression).compile();
        assertThat(expr.toString(), equalTo(rawExpression));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "int sum = 1 + 2 + 3",
            "java.lang.String s = \"Sum: \" + (1 + 2)",
            "java.lang.String s = \"Sum: \" + 1 + 2"
    })
    public void testParenOptimizationCorrectlyPrintsParenthesesForStatements(String rawStatement) {
        Launcher launcher = createLauncherWithOptimizeParenthesesPrinter();
        CtStatement statement = launcher.getFactory()
                .createCodeSnippetStatement(rawStatement);
        assertThat(statement.toString(), equalTo(rawStatement));
    }

    private static Launcher createLauncherWithOptimizeParenthesesPrinter() {
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setPrettyPrinterCreator(() -> {
            DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(launcher.getEnvironment());
            printer.setOptimizeParentheses(true);
            return printer;
        });
        return launcher;
    }
}
