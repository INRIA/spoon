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
        // contract: a patch should be able to append elements to a context branch

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
        // contract: a patch should be able to prepend elements to a context branch

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
}
