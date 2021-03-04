package spoon.reflect.visitor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultJavaPrettyPrinterTest {

    @Test
    public void testParenOptimizationPrintsNoParenthesesWhenRedundantInArithmeticExpression() {
        // contract: The pretty-printer should not print parentheses when they are not needed

        Launcher launcher = createLauncherWithOptimizeParenthesesPrinter();
        String originalExpression = "1 + 2 + 3";
        CtBinaryOperator<?> binop = (CtBinaryOperator<?>) launcher.getFactory()
                .createCodeSnippetExpression(originalExpression).compile();

        assertThat(binop.toString(), equalTo(originalExpression));
    }

    @Test
    public void testParenOptimizationPreservesRedundantParenthesesToPreserveASTStructure() {
        // contract: Even if parentheses are semantically redundant, the AST structure should be
        // preserved when optimizing parentheses.

        Launcher launcher = createLauncherWithOptimizeParenthesesPrinter();
        // note that + is left-associative, so the below gives a different AST than `1 + 2 + 3`
        String originalExpression = "1 + (2 + 3)";

        CtExpression<?> expr = launcher.getFactory()
                .createCodeSnippetExpression(originalExpression).compile();

        assertThat(expr.toString(), equalTo(originalExpression));
    }

    @Test
    public void testParenOptimizationPreservesSemanticsOfStringConcatenation() {
        // contract: Parentheses that are redundant for arithmetic operations aren't necessarily
        // when it comes to string concatenation, and so we want to ensure that we preserve
        // parentheses when necessary.

        Launcher launcher = createLauncherWithOptimizeParenthesesPrinter();
        String originalStatement = "java.lang.String s = \"Sum: \" + (1 + 2)";

        CtStatement statement = launcher.getFactory()
                .createCodeSnippetStatement(originalStatement).compile();

        assertThat(statement.toString(), equalTo(originalStatement));
    }

    @Test
    public void testParenOptimizationPreservesSemanticsOfUnaryOperator() {
        // contract: Unary operator parentheses should be preserved when required

        Launcher launcher = createLauncherWithOptimizeParenthesesPrinter();
        String originalExpression = "-(1 + 2 + 3)";

        CtExpression<?> expr = launcher.getFactory()
                .createCodeSnippetExpression(originalExpression).compile();

        assertThat(expr.toString(), equalTo(originalExpression));
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
