package spoon.smpl;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.smpl.*;
import static spoon.smpl.TestUtils.*;

public class EndToEndTests {
    @Test
    public void testAppendContextBranch() {
        // contract: a patch should be able to append elements below a context branch

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        if (true) {\n" +
                                               "            int x = 0;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "        if (true) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "        int y = 1;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "  if (true) {\n" +
                                         "      int x = 0;\n" +
                                         "  }\n" +
                                         "+ int y = 1;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testAppendToContext() {
        // contract: a patch should be able to append elements to a context statement

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        int x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m2() {\n" +
                                               "        int y;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "        int x;\n" +
                                                  "        int appended1;\n" +
                                                  "        int appended2;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m2() {\n" +
                                                  "        int y;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "  int x;\n" +
                                         "+ int appended1;\n" +
                                         "+ int appended2;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testBasicDots() {
        // contract: dots are able to match any number of arbitrary paths

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    int sgn(int input) {\n" +
                                               "        int x;\n" +
                                               "        \n" +
                                               "        if (input > 0) {\n" +
                                               "            x = 1;\n" +
                                               "        } else if (input == 0) {\n" +
                                               "            x = 0;\n" +
                                               "        } else {\n" +
                                               "            x = 2;\n" +
                                               "        }\n" +
                                               "        \n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    int sgn(int input) {\n" +
                                                  "        int x;\n" +
                                                  "        \n" +
                                                  "        if (input > 0) {\n" +
                                                  "            x = 1 + 1;\n" +
                                                  "        } else if (input == 0) {\n" +
                                                  "            x = 0 + 1;\n" +
                                                  "        } else {\n" +
                                                  "            x = 2 + 1;\n" +
                                                  "        }\n" +
                                                  "        \n" +
                                                  "        return x;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "identifier v1;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "  int v1;\n" +
                                         "  ...\n" +
                                         "- v1 = C;\n" +
                                         "+ v1 = C + 1;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testBasicPatternDisjunction() {
        // contract: matching of pattern disjunction including clause-order priority

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void a() {}\n" +
                                               "    void b() {}\n" +
                                               "    void c() {}\n" +
                                               "    \n" +
                                               "    void m1() {\n" +
                                               "        a();\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m2() {\n" +
                                               "        b();\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m3() {\n" +
                                               "        c();\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m4() {\n" +
                                               "        a();\n" +
                                               "        b();\n" +
                                               "        c();\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m5() {\n" +
                                               "        c();\n" +
                                               "        b();\n" +
                                               "        a();\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void a() {}\n" +
                                                  "    void b() {}\n" +
                                                  "    void c() {}\n" +
                                                  "    \n" +
                                                  "    void m1() {\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m2() {\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m3() {\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m4() {\n" +
                                                  "        b();\n" +
                                                  "        c();\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m5() {\n" +
                                                  "        c();\n" +
                                                  "        b();\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "(\n" +
                                         "- a();\n" +
                                         "|\n" +
                                         "- b();\n" +
                                         "|\n" +
                                         "- c();\n" +
                                         ")\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteBranch() {
        // contract: a patch should be able to delete a complete branch statement

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        if (true) {\n" +
                                               "            int x = 0;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- if (true) {\n" +
                                         "-     int x = 0;\n" +
                                         "- }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteBranchInBranch() {
        // contract: a patch should be able to delete a complete branch statement nested inside another branch

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void before() {}\n" +
                                               "    void after() {}\n" +
                                               "    \n" +
                                               "    void m1() {\n" +
                                               "        boolean somevariable = Math.random() < 0.5;\n" +
                                               "        \n" +
                                               "        if (somevariable) {\n" +
                                               "            before();\n" +
                                               "            \n" +
                                               "            if (true) {\n" +
                                               "                int x = 0;\n" +
                                               "            }\n" +
                                               "            \n" +
                                               "            after();\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void before() {}\n" +
                                                  "    void after() {}\n" +
                                                  "    \n" +
                                                  "    void m1() {\n" +
                                                  "        boolean somevariable = Math.random() < 0.5;\n" +
                                                  "        \n" +
                                                  "        if (somevariable) {\n" +
                                                  "            before();\n" +
                                                  "            after();\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- if (true) {\n" +
                                         "-     int x = 0;\n" +
                                         "- }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteEnclosingBranch() {
        // contract: a patch should be able to delete an enclosing branch statement while keeping inner context

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        if (true) {\n" +
                                               "            int x = 0;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m2() {\n" +
                                               "        if (true) {\n" +
                                               "            if (true) {\n" +
                                               "                int x = 0;\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m3() {\n" +
                                               "        if (false) {\n" +
                                               "            if (true) {\n" +
                                               "                int x = 0;\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "        int x = 0;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m2() {\n" +
                                                  "        if (true) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m3() {\n" +
                                                  "        if (false) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- if (true) {\n" +
                                         "      int x = 0;\n" +
                                         "- }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteEnclosingBranchDots() {
        // contract: a patch should be able to delete an enclosing branch statement while keeping inner context

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        if (true) {\n" +
                                               "            int x = 0;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m2() {\n" +
                                               "        if (true) {\n" +
                                               "            if (true) {\n" +
                                               "                int x = 0;\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m3() {\n" +
                                               "        if (false) {\n" +
                                               "            if (true) {\n" +
                                               "                int x = 0;\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m4() {\n" +
                                               "        if (true) {\n" +
                                               "            if (false) {\n" +
                                               "                int x = 0;\n" +
                                               "            }\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "        int x = 0;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m2() {\n" +
                                                  "        if (true) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m3() {\n" +
                                                  "        if (false) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m4() {\n" +
                                                  "        if (false) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- if (true) {\n" +
                                         "      ...\n" +
                                         "- }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteStmAfterBranch() {
        // contract: only the statement below the branch should be removed

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    int positive(int input) {\n" +
                                               "        int ans = 0;\n" +
                                               "        \n" +
                                               "        ans = 1;\n" +
                                               "        \n" +
                                               "        if (input > 0) {\n" +
                                               "            ans = 1;\n" +
                                               "        }\n" +
                                               "        \n" +
                                               "        ans = 1;\n" +
                                               "        \n" +
                                               "        return ans;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    int positive(int input) {\n" +
                                                  "        int ans = 0;\n" +
                                                  "        \n" +
                                                  "        ans = 1;\n" +
                                                  "        \n" +
                                                  "        if (input > 0) {\n" +
                                                  "            ans = 1;\n" +
                                                  "        }\n" +
                                                  "        \n" +
                                                  "        return ans;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "identifier v1;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "  if (input > 0) {\n" +
                                         "  ...\n" +
                                         "  }\n" +
                                         "- v1 = C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteStmBeforeBranch() {
        // contract: only the statement above the branch should be removed

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    int positive(int input) {\n" +
                                               "        int ans = 0;\n" +
                                               "        \n" +
                                               "        ans = 1;\n" +
                                               "        \n" +
                                               "        if (input > 0) {\n" +
                                               "            ans = 1;\n" +
                                               "        }\n" +
                                               "        \n" +
                                               "        ans = 1;\n" +
                                               "        \n" +
                                               "        return ans;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    int positive(int input) {\n" +
                                                  "        int ans = 0;\n" +
                                                  "        \n" +
                                                  "        if (input > 0) {\n" +
                                                  "            ans = 1;\n" +
                                                  "        }\n" +
                                                  "        \n" +
                                                  "        ans = 1;\n" +
                                                  "        \n" +
                                                  "        return ans;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "identifier v1;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- v1 = C;\n" +
                                         "  if (input > 0) {\n" +
                                         "  ...\n" +
                                         "  }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDeleteStmInBranch() {
        // contract: only the statement inside the branch should be removed

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    int positive(int input) {\n" +
                                               "        int ans = 0;\n" +
                                               "        \n" +
                                               "        ans = 1;\n" +
                                               "        \n" +
                                               "        if (input > 0) {\n" +
                                               "            ans = 1;\n" +
                                               "        }\n" +
                                               "        \n" +
                                               "        ans = 1;\n" +
                                               "        \n" +
                                               "        return ans;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    int positive(int input) {\n" +
                                                  "        int ans = 0;\n" +
                                                  "        \n" +
                                                  "        ans = 1;\n" +
                                                  "        \n" +
                                                  "        if (input > 0) {\n" +
                                                  "        }\n" +
                                                  "        \n" +
                                                  "        ans = 1;\n" +
                                                  "        \n" +
                                                  "        return ans;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "identifier v1;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "  if (input > 0) {\n" +
                                         "-     v1 = C;\n" +
                                         "  }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDotsShortestPath() {
        // contract: dots by default should only match the shortest path between enclosing anchors (if any)

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void foo(Object x) {}\n" +
                                               "    void bar(Object x) {}\n" +
                                               "    \n" +
                                               "    void m1(Object x) {\n" +
                                               "        foo(x);\n" +
                                               "        foo(x);\n" +
                                               "        bar(x);\n" +
                                               "        bar(x);\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void foo(Object x) {}\n" +
                                                  "    void bar(Object x) {}\n" +
                                                  "    \n" +
                                                  "    void m1(Object x) {\n" +
                                                  "        foo(x);\n" +
                                                  "        bar(x);\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- foo(x);\n" +
                                         "  ...\n" +
                                         "- bar(x);\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testDotsWhenAny() {
        // contract: dots shortest path restriction is lifted by using when any

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void foo(Object x) {}\n" +
                                               "    void bar(Object x) {}\n" +
                                               "    \n" +
                                               "    void m1(Object x) {\n" +
                                               "        foo(x);\n" +
                                               "        foo(x);\n" +
                                               "        bar(x);\n" +
                                               "        bar(x);\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void foo(Object x) {}\n" +
                                                  "    void bar(Object x) {}\n" +
                                                  "    \n" +
                                                  "    void m1(Object x) {\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- foo(x);\n" +
                                         "  ... when any\n" +
                                         "- bar(x);\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testEncloseInBranch() {
        // contract: a patch should be able to add a branch statement enclosing context

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void anchor() {}\n" +
                                               "    void foo() {}\n" +
                                               "    \n" +
                                               "    void m1() {\n" +
                                               "        anchor();\n" +
                                               "        foo();\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void anchor() {}\n" +
                                                  "    void foo() {}\n" +
                                                  "    \n" +
                                                  "    void m1() {\n" +
                                                  "        boolean debug = Math.random() < 0.5;\n" +
                                                  "        anchor();\n" +
                                                  "        if (debug) {\n" +
                                                  "            foo();\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "+ boolean debug = Math.random() < 0.5;\n" +
                                         "  anchor();\n" +
                                         "+ if (debug) {\n" +
                                         "      foo();\n" +
                                         "+ }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testHelloWorld() {
        // contract: hello world template test

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void foo() {\n" +
                                               "        int x = 1;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void foo() {\n" +
                                                  "        \n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "identifier v1;\n" +
                                         "@@\n" +
                                         "- int v1 = 1;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testMatchAnyType() {
        // contract: a 'type' metavariable should match any type

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    class ASpecificType {}\n" +
                                               "    \n" +
                                               "    void foo() {\n" +
                                               "        int x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void bar() {\n" +
                                               "        float x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void baz() {\n" +
                                               "        ASpecificType x;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    class ASpecificType {}\n" +
                                                  "    \n" +
                                                  "    void foo() {\n" +
                                                  "        \n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void bar() {\n" +
                                                  "        \n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void baz() {\n" +
                                                  "        \n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "@@\n" +
                                         "- T x;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testMatchSpecificType() {
        // contract: a concretely given type in SmPL should match that precise type

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    class ASpecificType {}\n" +
                                               "    \n" +
                                               "    void foo() {\n" +
                                               "        int x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void bar() {\n" +
                                               "        float x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void baz() {\n" +
                                               "        ASpecificType x;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    class ASpecificType {}\n" +
                                                  "    \n" +
                                                  "    void foo() {\n" +
                                                  "        int x;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void bar() {\n" +
                                                  "        float x;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void baz() {\n" +
                                                  "        \n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "- ASpecificType x;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testPrependContextBranch() {
        // contract: a patch should be able to prepend elements above a context branch

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        if (true) {\n" +
                                               "            int x = 0;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "        int y = 1;\n" +
                                                  "        if (true) {\n" +
                                                  "            int x = 0;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "+ int y = 1;\n" +
                                         "  if (true) {\n" +
                                         "      int x = 0;\n" +
                                         "  }\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testPrependToContext() {
        // contract: a patch should be able to prepend elements to a context statement

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    void m1() {\n" +
                                               "        int x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void m2() {\n" +
                                               "        int y;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    void m1() {\n" +
                                                  "        int prepended1;\n" +
                                                  "        int prepended2;\n" +
                                                  "        int x;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void m2() {\n" +
                                                  "        int y;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "@@\n" +
                                         "+ int prepended1;\n" +
                                         "+ int prepended2;\n" +
                                         "  int x;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstants001() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    float square(float x) { return x*x; }\n" +
                                               "    void print(Object x) { System.out.println(x); }\n" +
                                               "    \n" +
                                               "    int m1() {\n" +
                                               "        int x = 0;\n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    int m1b() {\n" +
                                               "        int x = 0;\n" +
                                               "        x = x + 1;\n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    float m2() {\n" +
                                               "        float x = 3.0f;\n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    float m2b() {\n" +
                                               "        float x = 3.0f;\n" +
                                               "        float y = square(x);\n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    String m3() {\n" +
                                               "        String x = \"Hello, World!\";\n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    String m3b() {\n" +
                                               "        String x = \"Hello, World!\";\n" +
                                               "        print(x);\n" +
                                               "        return x;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    float square(float x) { return x*x; }\n" +
                                                  "    void print(Object x) { System.out.println(x); }\n" +
                                                  "    \n" +
                                                  "    int m1() {\n" +
                                                  "        return 0;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    int m1b() {\n" +
                                                  "        int x = 0;\n" +
                                                  "        x = x + 1;\n" +
                                                  "        return x;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    float m2() {\n" +
                                                  "        return 3.0f;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    float m2b() {\n" +
                                                  "        float x = 3.0f;\n" +
                                                  "        float y = square(x);\n" +
                                                  "        return x;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    String m3() {\n" +
                                                  "        return \"Hello, World!\";\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    String m3b() {\n" +
                                                  "        String x = \"Hello, World!\";\n" +
                                                  "        print(x);\n" +
                                                  "        return x;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstantsBranch() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class input\n" +
                                               "{\n" +
                                               "    public int foo(boolean x)\n" +
                                               "    {\n" +
                                               "        int ret = 42;\n" +
                                               "        \n" +
                                               "        if (x == true)\n" +
                                               "        {\n" +
                                               "            return ret;\n" +
                                               "        }\n" +
                                               "        else\n" +
                                               "        {\n" +
                                               "            return ret;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class input\n" +
                                                  "{\n" +
                                                  "    public int foo(boolean x)\n" +
                                                  "    {\n" +
                                                  "        if (x == true)\n" +
                                                  "        {\n" +
                                                  "            return 42;\n" +
                                                  "        }\n" +
                                                  "        else\n" +
                                                  "        {\n" +
                                                  "            return 42;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstantsBranchMultiple() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class input\n" +
                                               "{\n" +
                                               "    public int foo(int n)\n" +
                                               "    {\n" +
                                               "        int a = 123;\n" +
                                               "        int b = 234;\n" +
                                               "        int c = 345;\n" +
                                               "        \n" +
                                               "        if (n == 0)\n" +
                                               "        {\n" +
                                               "            return a;\n" +
                                               "        }\n" +
                                               "        else if (n == 1)\n" +
                                               "        {\n" +
                                               "            return b;\n" +
                                               "        }\n" +
                                               "        else\n" +
                                               "        {\n" +
                                               "            return c;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class input\n" +
                                                  "{\n" +
                                                  "    public int foo(int n)\n" +
                                                  "    {\n" +
                                                  "        int a = 123;\n" +
                                                  "        int b = 234;\n" +
                                                  "        int c = 345;\n" +
                                                  "        \n" +
                                                  "        if (n == 0)\n" +
                                                  "        {\n" +
                                                  "            return a;\n" +
                                                  "        }\n" +
                                                  "        else if (n == 1)\n" +
                                                  "        {\n" +
                                                  "            return b;\n" +
                                                  "        }\n" +
                                                  "        else\n" +
                                                  "        {\n" +
                                                  "            return c;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstantsBranchMultipleWhenExists() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class input\n" +
                                               "{\n" +
                                               "    public int foo(int n)\n" +
                                               "    {\n" +
                                               "        int a = 123;\n" +
                                               "        int b = 234;\n" +
                                               "        int c = 345;\n" +
                                               "        \n" +
                                               "        if (n == 0)\n" +
                                               "        {\n" +
                                               "            return a;\n" +
                                               "        }\n" +
                                               "        else if (n == 1)\n" +
                                               "        {\n" +
                                               "            return b;\n" +
                                               "        }\n" +
                                               "        else\n" +
                                               "        {\n" +
                                               "            return c;\n" +
                                               "        }\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class input\n" +
                                                  "{\n" +
                                                  "    public int foo(int n)\n" +
                                                  "    {\n" +
                                                  "        if (n == 0)\n" +
                                                  "        {\n" +
                                                  "            return 123;\n" +
                                                  "        }\n" +
                                                  "        else if (n == 1)\n" +
                                                  "        {\n" +
                                                  "            return 234;\n" +
                                                  "        }\n" +
                                                  "        else\n" +
                                                  "        {\n" +
                                                  "            return 345;\n" +
                                                  "        }\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "      when exists\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstantsElselessBranch() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class input\n" +
                                               "{\n" +
                                               "    public int foo(boolean x)\n" +
                                               "    {\n" +
                                               "        int ret = 42;\n" +
                                               "        \n" +
                                               "        if (x == true)\n" +
                                               "        {\n" +
                                               "            return ret;\n" +
                                               "        }\n" +
                                               "        \n" +
                                               "        return ret;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class input\n" +
                                                  "{\n" +
                                                  "    public int foo(boolean x)\n" +
                                                  "    {\n" +
                                                  "        if (x == true)\n" +
                                                  "        {\n" +
                                                  "            return 42;\n" +
                                                  "        }\n" +
                                                  "        \n" +
                                                  "        return 42;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstantsExpressionlessReturnBug() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class input\n" +
                                               "{\n" +
                                               "    public void foo(boolean x)\n" +
                                               "    {\n" +
                                               "        int ret = 42;\n" +
                                               "        return;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class input\n" +
                                                  "{\n" +
                                                  "    public void foo(boolean x)\n" +
                                                  "    {\n" +
                                                  "        int ret = 42;\n" +
                                                  "        return;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testRemoveLocalsReturningConstantsRejectUsageInBranchCondition() {
        // contract: correct application of remove-locals-returning-constants patch example

        CtClass<?> input = Launcher.parseClass("class input\n" +
                                               "{\n" +
                                               "    public int foo()\n" +
                                               "    {\n" +
                                               "        int y = 42;\n" +
                                               "        \n" +
                                               "        if (y > 0)\n" +
                                               "        {\n" +
                                               "            return y;\n" +
                                               "        }\n" +
                                               "        \n" +
                                               "        return y;\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class input\n" +
                                                  "{\n" +
                                                  "    public int foo()\n" +
                                                  "    {\n" +
                                                  "        int y = 42;\n" +
                                                  "        \n" +
                                                  "        if (y > 0)\n" +
                                                  "        {\n" +
                                                  "            return y;\n" +
                                                  "        }\n" +
                                                  "        \n" +
                                                  "        return y;\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "type T;\n" +
                                         "identifier ret;\n" +
                                         "constant C;\n" +
                                         "@@\n" +
                                         "- T ret = C;\n" +
                                         "  ... when != ret\n" +
                                         "- return ret;\n" +
                                         "+ return C;\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testTypedIdentifierMetavariables1() {
        // contract: correct bindings of explicitly typed identifier metavariables

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    class ASpecificType {}\n" +
                                               "    void log(Object x) { System.out.println(x.toString()); }\n" +
                                               "    \n" +
                                               "    void foo() {\n" +
                                               "        int x = 0;\n" +
                                               "        log(x);\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void bar() {\n" +
                                               "        float x = 0.0f;\n" +
                                               "        log(x);\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void baz() {\n" +
                                               "        ASpecificType x = new ASpecificType();\n" +
                                               "        log(x);\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    class ASpecificType {}\n" +
                                                  "    void log(Object x) { System.out.println(x.toString()); }\n" +
                                                  "    \n" +
                                                  "    void foo() {\n" +
                                                  "        int x = 0;\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void bar() {\n" +
                                                  "        float x = 0.0f;\n" +
                                                  "        log(x);\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void baz() {\n" +
                                                  "        ASpecificType x = new ASpecificType();\n" +
                                                  "        log(x);\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "int x;\n" +
                                         "@@\n" +
                                         "- log(x);\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testTypedIdentifierMetavariables2() {
        // contract: correct bindings of explicitly typed identifier metavariables

        CtClass<?> input = Launcher.parseClass("class A {\n" +
                                               "    class ASpecificType {}\n" +
                                               "    void log(Object x) { System.out.println(x.toString()); }\n" +
                                               "    \n" +
                                               "    void foo() {\n" +
                                               "        int x = 0;\n" +
                                               "        log(x);\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void bar() {\n" +
                                               "        float x = 0.0f;\n" +
                                               "        log(x);\n" +
                                               "    }\n" +
                                               "    \n" +
                                               "    void baz() {\n" +
                                               "        ASpecificType x = new ASpecificType();\n" +
                                               "        log(x);\n" +
                                               "    }\n" +
                                               "}\n");
    
        CtClass<?> expected = Launcher.parseClass("class A {\n" +
                                                  "    class ASpecificType {}\n" +
                                                  "    void log(Object x) { System.out.println(x.toString()); }\n" +
                                                  "    \n" +
                                                  "    void foo() {\n" +
                                                  "        int x = 0;\n" +
                                                  "        log(x);\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void bar() {\n" +
                                                  "        float x = 0.0f;\n" +
                                                  "        log(x);\n" +
                                                  "    }\n" +
                                                  "    \n" +
                                                  "    void baz() {\n" +
                                                  "        ASpecificType x = new ASpecificType();\n" +
                                                  "    }\n" +
                                                  "}\n");
    
        SmPLRule rule = SmPLParser.parse("@@\n" +
                                         "ASpecificType x;\n" +
                                         "@@\n" +
                                         "- log(x);\n");
    
        input.getMethods().forEach((method) -> {
            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);
            Transformer.transform(model, checker.getResult().getAllWitnesses());
        });
    
        assertEquals(expected.toString(), input.toString());
    }
}
