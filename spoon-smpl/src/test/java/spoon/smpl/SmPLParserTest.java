package spoon.smpl;

import org.junit.Test;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static spoon.smpl.SmPLParser.parse;
import static spoon.smpl.SmPLParser.rewrite;
import static spoon.smpl.SmPLParser.prettify;

public class SmPLParserTest {
    @Test(expected = RuntimeException.class)
    public void testRewriteEmptyString() {
        rewrite("");
    }

    @Test(expected = RuntimeException.class)
    public void testRewriteBad01() {
        rewrite("hello");
    }

    @Test
    public void testRewriteEmptyRule() {
        String result = prettify(rewrite("@@\n@@\n"));
        assertEquals("class SmPLRule {\n" +
                "    void __SmPLMetavars__() {\n" +
                "    }\n" +
                "}\n", result);
    }

    @Test
    public void testRewriteSimpleRule() {
        String result = prettify(rewrite(
                "@@\n" +
                "identifier x;\n" +
                "@@\n" +
                "int x = 1;\n" +
                "return x + 1;\n"));

        assertEquals(
                "class SmPLRule {\n" +
                "    void __SmPLMetavars__() {\n" +
                "        identifier(x);\n" +
                "    }\n" +
                "    __SmPLUndeclared__ method() {\n" +
                "        int x = 1;\n" +
                "        return x + 1;\n" +
                "    }\n" +
                "}\n", result);
    }

    @Test
    public void testRewriteMultipleIdentifiersSingleLine() {
        String result = prettify(rewrite(
                "@@\n" +
                "identifier x,y;\n" +
                "@@\n" +
                "int x = 1;\n" +
                "int y = 2;\n" +
                "return x + y;\n"));

        assertEquals(
                "class SmPLRule {\n" +
                "    void __SmPLMetavars__() {\n" +
                "        identifier(x);\n" +
                "        identifier(y);\n" +
                "    }\n" +
                "    __SmPLUndeclared__ method() {\n" +
                "        int x = 1;\n" +
                "        int y = 2;\n" +
                "        return x + y;\n" +
                "    }\n" +
                "}\n", result);
    }

    @Test
    public void testRewriteMultipleIdentifiersMultipleLines() {
        String result = prettify(rewrite(
                "@@\n" +
                "identifier x;\n" +
                "identifier y;\n" +
                "@@\n" +
                "int x = 1;\n" +
                "int y = 2;\n" +
                "return x + y;\n"));

        assertEquals(
                "class SmPLRule {\n" +
                "    void __SmPLMetavars__() {\n" +
                "        identifier(x);\n" +
                "        identifier(y);\n" +
                "    }\n" +
                "    __SmPLUndeclared__ method() {\n" +
                "        int x = 1;\n" +
                "        int y = 2;\n" +
                "        return x + y;\n" +
                "    }\n" +
                "}\n", result);
    }

    @Test
    public void testRewriteSimpleDots() {
        String result = prettify(rewrite(
                "@@\n" +
                "identifier x;\n" +
                "@@\n" +
                "int x = 1;\n" +
                "... when != x\n" +
                "return x + 1;\n"));

        assertEquals(
                "class SmPLRule {\n" +
                "    void __SmPLMetavars__() {\n" +
                "        identifier(x);\n" +
                "    }\n" +
                "    __SmPLUndeclared__ method() {\n" +
                "        int x = 1;\n" +
                "        __SmPLDots__(whenNotEqual(x));\n" +
                "        return x + 1;\n" +
                "    }\n" +
                "}\n", result);
    }

    @Test
    public void testRewriteReturningConstants() {
        String result = prettify(rewrite(
                "@@\n" +
                "type T;\n" +
                "identifier ret;\n" +
                "constant C;\n" +
                "@@\n" +
                "T ret = C;\n" +
                "... when != ret\n" +
                "return ret;\n"));

        assertEquals(
                "class SmPLRule {\n" +
                "    void __SmPLMetavars__() {\n" +
                "        type(T);\n" +
                "        identifier(ret);\n" +
                "        constant(C);\n" +
                "    }\n" +
                "    __SmPLUndeclared__ method() {\n" +
                "        T ret = C;\n" +
                "        __SmPLDots__(whenNotEqual(ret));\n" +
                "        return ret;\n" +
                "    }\n" +
                "}\n", result);
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
}
