package spoon.reflect.visitor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtBinaryOperator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultJavaPrettyPrinterTest {

    @Test
    public void testPrintsNoParenthesesWhenRedundantInArithmeticExpression() {
        // contract: The pretty-printer should not print parentheses when they are not needed

        Launcher launcher = createLauncherWithOptimizeParenthesesPrinter();
        String originalExpression = "1 + 2 + 3";
        CtBinaryOperator<?> binop = (CtBinaryOperator<?>) launcher.getFactory()
                .createCodeSnippetExpression(originalExpression).compile();

        assertThat(binop.toString(), equalTo(originalExpression));
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
