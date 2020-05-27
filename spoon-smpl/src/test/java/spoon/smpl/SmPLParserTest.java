package spoon.smpl;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.fail;
import static spoon.smpl.SmPLParser.parse;
import static spoon.smpl.SmPLParser.rewrite;

public class SmPLParserTest {
    String implicitDotsBegin = "if (" + SmPLJavaDSL.getDotsWithOptionalMatchName() + ") {";
    String implicitDotsEnd = "}";

    @Test(expected = RuntimeException.class)
    public void testRewriteEmptyString() {

        // contract: asking SmPLParser to rewrite the empty string should cause exception

        rewrite("");
    }

    @Test(expected = RuntimeException.class)
    public void testRewriteBad01() {

        // contract: asking SmPLParser to rewrite nonsense string should cause exception

        rewrite("hello");
    }

    @Test
    public void testRewriteEmptyRule() {

        // contract: SmPLParser can handle an empty rule, producing an empty Java DSL class.

        String result = DebugUtils.prettify(rewrite("@@@@\n"));
        assertEquals(DebugUtils.prettify("class RewrittenSmPLRule {\n" +
                                         "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
                                         "}\n" +
                                         "}\n"), result);
    }

    @Test
    public void testRewriteSimpleRule() {

        // contract: SmPLParser correctly rewrites very basic SmPL rules

        String result = DebugUtils.prettify(rewrite(
                "@@\n" +
                "identifier x;\n" +
                "@@\n" +
                "int x = 1;\n" +
                "return x + 1;\n"));

        assertEquals(DebugUtils.prettify("class RewrittenSmPLRule {\n" +
                                         "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
                                         "identifier(x);\n" +
                                         "}\n" +
                                         SmPLJavaDSL.createUnspecifiedMethodHeaderString() + " {\n" +
                                         implicitDotsBegin + "\n" +
                                         "int x = 1;\n" +
                                         "return x + 1;\n" +
                                         implicitDotsEnd + "\n" +
                                         "}\n" +
                                         "}\n"), result);
    }

    @Test
    public void testRewriteMultipleIdentifiersSingleLine() {

        // contract: SmPLParser.rewrite correctly rewrites multiple metavariable declarations on a single line

        String result = DebugUtils.prettify(rewrite(
                "@@\n" +
                "identifier x,y;\n" +
                "@@\n" +
                "int x = 1;\n" +
                "int y = 2;\n" +
                "return x + y;\n"));

        assertEquals(DebugUtils.prettify("class RewrittenSmPLRule {\n" +
                                         "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
                                         "identifier(x);\n" +
                                         "identifier(y);\n" +
                                         "}\n" +
                                         SmPLJavaDSL.createUnspecifiedMethodHeaderString() + " {\n" +
                                         implicitDotsBegin + "\n" +
                                         "int x = 1;\n" +
                                         "int y = 2;\n" +
                                         "return x + y;\n" +
                                         implicitDotsEnd + "\n" +
                                         "}\n" +
                                         "}\n"), result);
    }

    @Test
    public void testRewriteMultipleIdentifiersMultipleLines() {

        // contract: SmPLParser.rewrite correctly rewrites multiple metavariable declarations on multiple lines

        String result = DebugUtils.prettify(rewrite(
                "@@\n" +
                "identifier x;\n" +
                "identifier y;\n" +
                "@@\n" +
                "int x = 1;\n" +
                "int y = 2;\n" +
                "return x + y;\n"));

        assertEquals(DebugUtils.prettify("class RewrittenSmPLRule {\n" +
                                         "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
                                         "identifier(x);\n" +
                                         "identifier(y);\n" +
                                         "}\n" +
                                         SmPLJavaDSL.createUnspecifiedMethodHeaderString() + " {\n" +
                                         implicitDotsBegin + "\n" +
                                         "int x = 1;\n" +
                                         "int y = 2;\n" +
                                         "return x + y;\n" +
                                         implicitDotsEnd + "\n" +
                                         "}\n" +
                                         "}\n"), result);
    }

    @Test
    public void testRewriteSimpleDots() {

        // contract: SmPLParser.rewrite corrently rewrites a dots statement with a simple constraint

        String result = DebugUtils.prettify(rewrite(
                "@@\n" +
                "identifier x;\n" +
                "@@\n" +
                "int x = 1;\n" +
                "... when != x\n" +
                "return x + 1;\n"));

        assertEquals(DebugUtils.prettify("class RewrittenSmPLRule {\n" +
                                         "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
                                         "identifier(x);\n" +
                                         "}\n" +
                                         SmPLJavaDSL.createUnspecifiedMethodHeaderString() + " {\n" +
                                         implicitDotsBegin + "\n" +
                                         "int x = 1;\n" +
                                         "__SmPLDots__(whenNotEqual(x));\n" +
                                         "return x + 1;\n" +
                                         implicitDotsEnd + "\n" +
                                         "}\n" +
                                         "}\n"), result);
    }

    @Test
    public void testRewriteReturningConstants() {

        // contract: SmPLParser.rewrite correctly rewrites the remove-locals-used-to-return-constants example

        String result = DebugUtils.prettify(rewrite(
                "@@\n" +
                "type T;\n" +
                "identifier ret;\n" +
                "constant C;\n" +
                "@@\n" +
                "T ret = C;\n" +
                "... when != ret\n" +
                "return ret;\n"));

        assertEquals(DebugUtils.prettify("class RewrittenSmPLRule {\n" +
                                         "void " + SmPLJavaDSL.getMetavarsMethodName() + "() {\n" +
                                         "type(T);\n" +
                                         "identifier(ret);\n" +
                                         "constant(C);\n" +
                                         "}\n" +
                                         SmPLJavaDSL.createUnspecifiedMethodHeaderString() + " {\n" +
                                         implicitDotsBegin + "\n" +
                                         "T ret = C;\n" +
                                         "__SmPLDots__(whenNotEqual(ret));\n" +
                                         "return ret;\n" +
                                         implicitDotsEnd + "\n" +
                                         "}\n" +
                                         "}\n"), result);
    }

    @Test
    public void testParsingAnonymousRule() {

        // contract: the parser sets null as name for anonymous rules

        SmPLRule rule = parse("@@\n" +
                              "identifier x;\n" +
                              "@@\n" +
                              "return x;\n");

        assertEquals(null, rule.getName());
    }

    @Test
    public void testParsingRuleName() {

        // contract: the parser correctly parses and sets the rule name

        SmPLRule rule;

        rule = parse("@ myrule @\n" +
                     "identifier x;\n" +
                     "@@\n" +
                     "return x;\n");

        assertEquals("myrule", rule.getName());
    }

    @Test
    public void testDisappearingFieldReadTargetBug() {

        // contract: the fieldread target of the expression "foo.x" with no further context for "foo" should not disappear when parsing a patch

        CtClass<?> ctClass = Launcher.parseClass(SmPLParser.rewrite("@@\n" +
                                                                    "@@\n" +
                                                                    "print(foo.x);\n"));

        if (!ctClass.toString().contains("print(foo.x)")) {
            fail(ctClass.toString() + " does not contain \"print(foo.x)\"");
        }
    }
}
