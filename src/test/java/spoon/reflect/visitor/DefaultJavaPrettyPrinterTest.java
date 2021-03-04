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
            "1 + 2 + -3",
            "1 + 2 + -(2 + 3)",
            "\"Sum: \" + (1 + 2)",
            "\"Sum: \" + 1 + 2",
            "-(1 + 2 + 3)",
            "true || true && false",
            "(true || false) && false",
            "1 | 2 | 3",
            "1 | (2 | 3)",
            "1 | 2 & 3",
            "(1 | 2) & 3",
            "1 | 2 ^ 3",
            "(1 | 2) ^ 3"
    })
    public void testParenOptimizationCorrectlyPrintsParenthesesForExpressions(String rawExpression) {
        // contract: When input expressions are minimally parenthesized, pretty-printed output
        // should match the input
        CtExpression<?> expr = createLauncherWithOptimizeParenthesesPrinter()
                .getFactory().createCodeSnippetExpression(rawExpression).compile();
        assertThat(expr.toString(), equalTo(rawExpression));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "int sum = 1 + 2 + 3",
            "java.lang.String s = \"Sum: \" + (1 + 2)",
            "java.lang.String s = \"Sum: \" + 1 + 2"
    })
    public void testParenOptimizationCorrectlyPrintsParenthesesForStatements(String rawStatement) {
        // contract: When input expressions as part of statements are minimally parenthesized,
        // pretty-printed output should match the input
        CtStatement statement = createLauncherWithOptimizeParenthesesPrinter()
                .getFactory().createCodeSnippetStatement(rawStatement);
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
