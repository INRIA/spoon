package spoon.reflect.visitor;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtType;
import spoon.test.SpoonTestHelpers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
                .getFactory().createCodeSnippetStatement(rawStatement).compile();
        assertThat(statement.toString(), equalTo(rawStatement));
    }

    private static Launcher createLauncherWithOptimizeParenthesesPrinter() {
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setPrettyPrinterCreator(() -> {
            DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(launcher.getEnvironment());
            printer.setMinimizeRoundBrackets(true);
            return printer;
        });
        return launcher;
    }


    @Test
    void testAutoImportPrinterDoesNotImportFunctionalInterfaceTargetedInLambda() {
        // contract: The auto-import printer should not import functional interfaces that are
        // targeted in lambdas, but are not explicitly referenced anywhere
        Launcher launcher = new Launcher();
        launcher.addInputResource("src/test/resources/target-functional-interface-in-lambda");
        launcher.buildModel();
        CtCompilationUnit cu = launcher.getFactory().Type().get("TargetsFunctionalInterface")
                .getPosition().getCompilationUnit();

        PrettyPrinter autoImportPrettyPrinter = launcher.getEnvironment().createPrettyPrinterAutoImport();
        String output = autoImportPrettyPrinter.prettyprint(cu);

        assertThat(output, not(containsString("import java.util.function.IntFunction;")));
    }

    @Test
    void testLocalTypesPrintedWithoutLeadingDigits() {
        // contract: leading digits should be stripped from simple names when printing
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(16);
        launcher.addInputResource("src/test/resources/localtypes");
        launcher.buildModel();

        CtCompilationUnit cu = launcher.getFactory().Type().get("LocalTypesHolder")
                .getPosition().getCompilationUnit();

        String output = cu.prettyprint();
        // local classes will always have a leading space, without leading digits
        assertThat(output, containsString(" MyClass"));
        assertThat(output, containsString(" MyEnum"));
        assertThat(output, containsString(" MyInterface"));
        // the code does not contain a 1, which would be the prefix of the local types' binary names
        // in the given code snippet
        assertThat(output, not(containsString("1")));
    }

    @Test
    void testEmptyIfBlocksArePrintedWithoutError() {
        // contract: empty if blocks don't crash the DJJP
        Launcher launcher = new Launcher();
        var ctIf = launcher.getFactory().createIf()
                .setThenStatement(launcher.getFactory().createBlock())
                .setElseStatement(launcher.getFactory().createBlock());
        assertDoesNotThrow(() -> ctIf.toString());
    }
    @Test
    void testEmptyIfBlocksWithCommentsArePrintedWithoutError() {
        // contract: empty if blocks don't crash the DJJP
        Launcher launcher = new Launcher();
        CtBlock<Object> thenBlock = launcher.getFactory().createBlock();
        CtBlock<Object> elseBlock = launcher.getFactory().createBlock();
        thenBlock.addComment(launcher.getFactory().createComment().setContent("then"));
        elseBlock.addComment(launcher.getFactory().createComment().setContent("else"));
        var ctIf = launcher.getFactory().createIf()
                .setThenStatement(thenBlock)
                .setElseStatement(elseBlock);
        assertDoesNotThrow(() -> ctIf.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "int a = (new int[10])[1];",
            "java.lang.Object b = (new java.lang.Object[6])[4];",
            "double c = (new double[1][1][1])[0][0][0];",
            "boolean d = new boolean[]{ true, false }[0];",
            "float e = (new float[3][])[0][0];", // compiles, but throws a NPE on execution
            "(new byte[8])[3] = 64;",
            "(new byte[8][])[3] = new byte[16];",
    })
    void testPrintNewArrayWithImmediateAccess(String line) {
        // contract: immediate access to a new array must be surrounded by parentheses if
        // no elements are passed directly
        String code = SpoonTestHelpers.wrapLocal(line);
        CtModel model = SpoonTestHelpers.createModelFromString(code, 8);
        CtType<?> first = model.getAllTypes().iterator().next();
        assertThat(first.toString(), containsString(line));
    }
}
