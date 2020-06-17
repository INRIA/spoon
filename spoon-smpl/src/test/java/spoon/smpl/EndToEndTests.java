package spoon.smpl;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.smpl.*;
import static spoon.smpl.TestUtils.*;

public class EndToEndTests {
    private void runSingleTest(String smpl, String inputCode, String expectedCode) {
        SmPLRule rule = SmPLParser.parse(smpl);
        CtClass<?> input = Launcher.parseClass(inputCode);
        CtClass<?> expected = Launcher.parseClass(expectedCode);

        input.getMethods().forEach((method) -> {
            if (method.getComments().stream().anyMatch(x -> x.getContent().toLowerCase().equals("skip"))) {
                return;
            }

            CFGModel model = new CFGModel(methodCfg(method));
            ModelChecker checker = new ModelChecker(model);
            rule.getFormula().accept(checker);

            ModelChecker.ResultSet results = checker.getResult();
            Transformer.transform(model, results.getAllWitnesses());

            if (results.size() > 0 && rule.getMethodsAdded().size() > 0) {
                Transformer.copyAddedMethods(model, rule);
            }
            model.getCfg().restoreUnsupportedElements();
        });

        assertEquals(expected.toString(), input.toString());
    }
    @Test
    public void testAnchorAfterDotsBug() {
        // contract: an addition must not be anchored to an element on the opposite side of an intermediate dots operator

        String inputCode = "class A {\n" +
                           "    void a() {}\n" +
                           "    void b() {}\n" +
                           "    void somethingElse() {}\n" +
                           "    \n" +
                           "    void foo() {\n" +
                           "        a();\n" +
                           "        somethingElse();\n" +
                           "        b();\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void a() {}\n" +
                              "    void b() {}\n" +
                              "    void somethingElse() {}\n" +
                              "    \n" +
                              "    void foo() {\n" +
                              "        a();\n" +
                              "        somethingElse();\n" +
                              "        c();\n" +
                              "        b();\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "a();\n" +
                      "...\n" +
                      "+ c();\n" +
                      "b();\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testAppendContextBranch() {
        // contract: a patch should be able to append elements below a context branch

        String inputCode = "class A {\n" +
                           "    void m1() {\n" +
                           "        if (true) {\n" +
                           "            int x = 0;\n" +
                           "        }\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void m1() {\n" +
                              "        if (true) {\n" +
                              "            int x = 0;\n" +
                              "        }\n" +
                              "        int y = 1;\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "  if (true) {\n" +
                      "      int x = 0;\n" +
                      "  }\n" +
                      "+ int y = 1;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testAppendToContext() {
        // contract: a patch should be able to append elements to a context statement

        String inputCode = "class A {\n" +
                           "    void m1() {\n" +
                           "        int x;\n" +
                           "    }\n" +
                           "    \n" +
                           "    void m2() {\n" +
                           "        int y;\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void m1() {\n" +
                              "        int x;\n" +
                              "        int appended1;\n" +
                              "        int appended2;\n" +
                              "    }\n" +
                              "    \n" +
                              "    void m2() {\n" +
                              "        int y;\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "  int x;\n" +
                      "+ int appended1;\n" +
                      "+ int appended2;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testBasicDots() {
        // contract: dots are able to match any number of arbitrary paths

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "identifier v1;\n" +
                      "constant C;\n" +
                      "@@\n" +
                      "  int v1;\n" +
                      "  ...\n" +
                      "- v1 = C;\n" +
                      "+ v1 = C + 1;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testBasicPatternDisjunction() {
        // contract: matching of pattern disjunction including clause-order priority

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "        b();\n" +
                              "        a();\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "identifier fn;\n" +
                      "@@\n" +
                      "void fn() {\n" +
                      "(\n" +
                      "- a();\n" +
                      "|\n" +
                      "- b();\n" +
                      "|\n" +
                      "- c();\n" +
                      ")\n" +
                      "...\n" +
                      "}\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDotsLeavingScopeBug01() {
        // contract: dots should be prevented from traversing out of the enclosing scope (when forall version)

        String inputCode = "class A {\n" +
                           "  void a() {}\n" +
                           "  boolean whatever;\n" +
                           "  \n" +
                           "  void m1() {\n" +
                           "    if (true) {\n" +
                           "    }\n" +
                           "    a();\n" +
                           "    if (true) {\n" +
                           "    }\n" +
                           "  }\n" +
                           "  void m2() {\n" +
                           "    if (true) {\n" +
                           "      a();\n" +
                           "    }\n" +
                           "    a();\n" +
                           "    if (true) {\n" +
                           "    }\n" +
                           "  }\n" +
                           "  void m3() {\n" +
                           "    if (true) {\n" +
                           "    }\n" +
                           "    a();\n" +
                           "    if (true) {\n" +
                           "      a();\n" +
                           "    }\n" +
                           "  }\n" +
                           "  void m4() {\n" +
                           "    if (true) {\n" +
                           "      a();\n" +
                           "    }\n" +
                           "    a();\n" +
                           "    if (true) {\n" +
                           "      a();\n" +
                           "    }\n" +
                           "  }\n" +
                           "  void m5() {\n" +
                           "    if (true) {\n" +
                           "      if (true) {\n" +
                           "        a();\n" +
                           "      }\n" +
                           "    }\n" +
                           "    a();\n" +
                           "    if (true) {\n" +
                           "    }\n" +
                           "  }\n" +
                           "  void m6() {\n" +
                           "    if (true) {\n" +
                           "      if (whatever) {\n" +
                           "        a();\n" +
                           "      }\n" +
                           "    }\n" +
                           "    a();\n" +
                           "    if (true) {\n" +
                           "    }\n" +
                           "  }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void a() {}\n" +
                              "    boolean whatever;\n" +
                              "    void m1() {\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "        a();\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "    void m2() {\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "        a();\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "    void m3() {\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "        a();\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "    void m4() {\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "        a();\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "    void m5() {\n" +
                              "        if (true) {\n" +
                              "            if (true) {\n" +
                              "            }\n" +
                              "        }\n" +
                              "        a();\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "    void m6() {\n" +
                              "        if (true) {\n" +
                              "            if (whatever) {\n" +
                              "                a();\n" +
                              "            }\n" +
                              "        }\n" +
                              "        a();\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "if (true) {\n" +
                      "  ...\n" +
                      "- a();\n" +
                      "  ...\n" +
                      "}\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDotsLeavingScopeBug02() {
        // contract: dots should be prevented from traversing out of the enclosing scope (when exists version)

        String inputCode = "class A {\n" +
                           "  void a() {}\n" +
                           "  boolean whatever;\n" +
                           "  \n" +
                           "  void m1() {\n" +
                           "    if (true) {\n" +
                           "    }\n" +
                           "    a();\n" +
                           "    if (true) {\n" +
                           "    }\n" +
                           "  }\n" +
                           "  void m2() {\n" +
                           "    if (true) {\n" +
                           "      a();\n" +
                           "    }\n" +
                           "    a();\n" +
                           "    if (true) {\n" +
                           "    }\n" +
                           "  }\n" +
                           "  void m3() {\n" +
                           "    if (true) {\n" +
                           "    }\n" +
                           "    a();\n" +
                           "    if (true) {\n" +
                           "      a();\n" +
                           "    }\n" +
                           "  }\n" +
                           "  void m4() {\n" +
                           "    if (true) {\n" +
                           "      a();\n" +
                           "    }\n" +
                           "    a();\n" +
                           "    if (true) {\n" +
                           "      a();\n" +
                           "    }\n" +
                           "  }\n" +
                           "  void m5() {\n" +
                           "    if (true) {\n" +
                           "      if (true) {\n" +
                           "        a();\n" +
                           "      }\n" +
                           "    }\n" +
                           "    a();\n" +
                           "    if (true) {\n" +
                           "    }\n" +
                           "  }\n" +
                           "  void m6() {\n" +
                           "    if (true) {\n" +
                           "      if (whatever) {\n" +
                           "        a();\n" +
                           "      }\n" +
                           "    }\n" +
                           "    a();\n" +
                           "    if (true) {\n" +
                           "    }\n" +
                           "  }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void a() {}\n" +
                              "    boolean whatever;\n" +
                              "    void m1() {\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "        a();\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "    void m2() {\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "        a();\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "    void m3() {\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "        a();\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "    void m4() {\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "        a();\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "    void m5() {\n" +
                              "        if (true) {\n" +
                              "            if (true) {\n" +
                              "            }\n" +
                              "        }\n" +
                              "        a();\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "    void m6() {\n" +
                              "        if (true) {\n" +
                              "            if (whatever) {\n" +
                              "            }\n" +
                              "        }\n" +
                              "        a();\n" +
                              "        if (true) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "if (true) {\n" +
                      "  ... when exists\n" +
                      "- a();\n" +
                      "  ...\n" +
                      "}\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDotsWhenExistsBug() {
        // contract: the when-exists dots modifier should successfully match in the following example

        String inputCode = "class A {\n" +
                           "  void a() {}\n" +
                           "  void b() {}\n" +
                           "  \n" +
                           "  void m() {\n" +
                           "    try {\n" +
                           "      a();\n" +
                           "    }\n" +
                           "    catch (Exception e) {\n" +
                           "      b();\n" +
                           "    }\n" +
                           "  }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void a() {}\n" +
                              "    void b() {}\n" +
                              "    void m() {\n" +
                              "        try {\n" +
                              "            a();\n" +
                              "        } catch (Exception e) {\n" +
                              "        }\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "  a();\n" +
                      "  ... when exists\n" +
                      "- b();\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testEnvironmentNegationBug() {
        // contract: the bug where the environments (Tv1=int, v1=x) and (Tv1=(int), v1=(y)) could not be joined should be fixed

        String inputCode = "class A {\n" +
                           "    /* skip */ void a(int x) {}\n" +
                           "    /* skip */ int f(int x, int y) { return 1; }\n" +
                           "    int m1(int x, int y) {\n" +
                           "        return f(x, y);\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    /* skip */ void a(int x) {}\n" +
                              "    /* skip */ int f(int x, int y) { return 1; }\n" +
                              "    int m1(int x, int y) {\n" +
                              "        a(x);\n" +
                              "        return f(x, y);\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ type Tf, Tv1; identifier v1, f; @@\n" +
                      "Tf f(Tv1 v1, ...) {\n" +
                      "+ a(v1);\n" +
                      "...\n" +
                      "}\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDeleteBranch() {
        // contract: a patch should be able to delete a complete branch statement

        String inputCode = "class A {\n" +
                           "    void m1() {\n" +
                           "        if (true) {\n" +
                           "            int x = 0;\n" +
                           "        }\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void m1() {\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "- if (true) {\n" +
                      "-     int x = 0;\n" +
                      "- }\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDeleteBranchInBranch() {
        // contract: a patch should be able to delete a complete branch statement nested inside another branch

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "- if (true) {\n" +
                      "-     int x = 0;\n" +
                      "- }\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDeleteEnclosingBranch() {
        // contract: a patch should be able to delete an enclosing branch statement while keeping inner context

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "- if (true) {\n" +
                      "      int x = 0;\n" +
                      "- }\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDeleteEnclosingBranchDots() {
        // contract: a patch should be able to delete an enclosing branch statement while keeping inner context

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "- if (true) {\n" +
                      "      ...\n" +
                      "- }\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDeleteStmAfterBranch() {
        // contract: only the statement below the branch should be removed

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "identifier v1;\n" +
                      "constant C;\n" +
                      "@@\n" +
                      "  if (input > 0) {\n" +
                      "  ...\n" +
                      "  }\n" +
                      "- v1 = C;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDeleteStmBeforeBranch() {
        // contract: only the statement above the branch should be removed

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "identifier v1;\n" +
                      "constant C;\n" +
                      "@@\n" +
                      "- v1 = C;\n" +
                      "  if (input > 0) {\n" +
                      "  ...\n" +
                      "  }\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDeleteStmInBranch() {
        // contract: only the statement inside the branch should be removed

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "identifier v1;\n" +
                      "constant C;\n" +
                      "@@\n" +
                      "  if (input > 0) {\n" +
                      "-     v1 = C;\n" +
                      "  }\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDotsShortestPath() {
        // contract: dots by default should only match the shortest path between enclosing anchors (if any)

        String inputCode = "class A {\n" +
                           "    void foo(Object x) {}\n" +
                           "    void bar(Object x) {}\n" +
                           "    \n" +
                           "    void m1(Object x) {\n" +
                           "        foo(x);\n" +
                           "        foo(x);\n" +
                           "        bar(x);\n" +
                           "        bar(x);\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void foo(Object x) {}\n" +
                              "    void bar(Object x) {}\n" +
                              "    \n" +
                              "    void m1(Object x) {\n" +
                              "        foo(x);\n" +
                              "        bar(x);\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "- foo(x);\n" +
                      "  ...\n" +
                      "- bar(x);\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDotsWhenAny() {
        // contract: dots shortest path restriction is lifted by using when any

        String inputCode = "class A {\n" +
                           "    void foo(Object x) {}\n" +
                           "    void bar(Object x) {}\n" +
                           "    \n" +
                           "    void m1(Object x) {\n" +
                           "        foo(x);\n" +
                           "        foo(x);\n" +
                           "        bar(x);\n" +
                           "        bar(x);\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void foo(Object x) {}\n" +
                              "    void bar(Object x) {}\n" +
                              "    \n" +
                              "    void m1(Object x) {\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "- foo(x);\n" +
                      "  ... when any\n" +
                      "- bar(x);\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDotsWithOptionalMatchShortestPath() {
        // contract: optdots should match the shortest path between surrounding context elements

        String inputCode = "class A {\n" +
                           "  void pre() {}\n" +
                           "  void post() {}\n" +
                           "  void a() {}\n" +
                           "  \n" +
                           "  void test() {\n" +
                           "    pre();\n" +
                           "    a();\n" +
                           "    pre();\n" +
                           "    a();\n" +
                           "    a();\n" +
                           "    post();\n" +
                           "  }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void pre() {}\n" +
                              "    void post() {}\n" +
                              "    void a() {}\n" +
                              "    \n" +
                              "    void test() {\n" +
                              "        pre();\n" +
                              "        a();\n" +
                              "        pre();\n" +
                              "        post();\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "void test() {\n" +
                      "  ... when any\n" +
                      "  pre();\n" +
                      "<...\n" +
                      "- a();\n" +
                      "...>\n" +
                      "  post();\n" +
                      "}\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDotsWithOptionalMatchSingle() {
        // contract: using the <... P ...> dots alternative to include an optional matching of P

        String inputCode = "class A {\n" +
                           "  /* skip */ void a(Object ... xs) {}\n" +
                           "  /* skip */ void b(Object ... xs) {}\n" +
                           "  /* skip */ void c(Object ... xs) {}\n" +
                           "  /* skip */ void d(Object ... xs) {}\n" +
                           "  Object x;\n" +
                           "  Object y;\n" +
                           "  Object z;\n" +
                           "  void m1() {\n" +
                           "    a();\n" +
                           "    b(x);\n" +
                           "    b(y);\n" +
                           "    b(z);\n" +
                           "    c();\n" +
                           "    d();\n" +
                           "  }\n" +
                           "  void m2() {\n" +
                           "    a();\n" +
                           "    c();\n" +
                           "    // call to c should be removed as patch should match even without the presence of any calls to b\n" +
                           "    d();\n" +
                           "  }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "  /* skip */ void a(Object ... xs) {}\n" +
                              "  /* skip */ void b(Object ... xs) {}\n" +
                              "  /* skip */ void c(Object ... xs) {}\n" +
                              "  /* skip */ void d(Object ... xs) {}\n" +
                              "  Object x;\n" +
                              "  Object y;\n" +
                              "  Object z;\n" +
                              "  void m1() {\n" +
                              "    a();\n" +
                              "    log(x);\n" +
                              "    log(y);\n" +
                              "    log(z);\n" +
                              "    d();\n" +
                              "  }\n" +
                              "  void m2() {\n" +
                              "    a();\n" +
                              "    // call to c should be removed as patch should match even without the presence of any calls to b\n" +
                              "    d();\n" +
                              "  }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "identifier x;\n" +
                      "@@\n" +
                      "a();\n" +
                      "<...\n" +
                      "- b(x);\n" +
                      "+ log(x);\n" +
                      "...>\n" +
                      "- c();\n" +
                      "d();\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testEncloseInBranch() {
        // contract: a patch should be able to add a branch statement enclosing context

        String inputCode = "class A {\n" +
                           "    void anchor() {}\n" +
                           "    void foo() {}\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "        anchor();\n" +
                           "        foo();\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "+ boolean debug = Math.random() < 0.5;\n" +
                      "  anchor();\n" +
                      "+ if (debug) {\n" +
                      "      foo();\n" +
                      "+ }\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDotsEnteringTryBlock() {
        // contract: dots should be able to traverse into try blocks

        String inputCode = "class A {\n" +
                           "  void a() {}\n" +
                           "  void b() {}\n" +
                           "  void c() {}\n" +
                           "  void d() {}\n" +
                           "  \n" +
                           "  void m() {\n" +
                           "    a();\n" +
                           "    try {\n" +
                           "      b();\n" +
                           "    }\n" +
                           "    catch (Exception e) {\n" +
                           "      c();\n" +
                           "    }\n" +
                           "    d();\n" +
                           "  }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void a() {}\n" +
                              "    void b() {}\n" +
                              "    void c() {}\n" +
                              "    void d() {}\n" +
                              "    \n" +
                              "    void m() {\n" +
                              "        a();\n" +
                              "        try {\n" +
                              "        } catch (Exception e) {\n" +
                              "            c();\n" +
                              "        }\n" +
                              "        d();\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "  a();\n" +
                      "  ...\n" +
                      "- b();\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testDotsTraversingTryCatch() {
        // contract: dots should be able to traverse over a try-catch

        String inputCode = "class A {\n" +
                           "  void a() {}\n" +
                           "  void b() {}\n" +
                           "  void c() {}\n" +
                           "  void d() {}\n" +
                           "  \n" +
                           "  void m() {\n" +
                           "    a();\n" +
                           "    try {\n" +
                           "      b();\n" +
                           "    }\n" +
                           "    catch (Exception e) {\n" +
                           "      c();\n" +
                           "    }\n" +
                           "    d();\n" +
                           "  }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void a() {}\n" +
                              "    void b() {}\n" +
                              "    void c() {}\n" +
                              "    void d() {}\n" +
                              "    \n" +
                              "    void m() {\n" +
                              "        a();\n" +
                              "        try {\n" +
                              "            b();\n" +
                              "        } catch (Exception e) {\n" +
                              "            c();\n" +
                              "        }\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "  a();\n" +
                      "  ...\n" +
                      "- d();\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testExistsDotsPatchingCatchBlock() {
        // contract: dots in exists mode should patch a statement only found in the catch block

        String inputCode = "class A {\n" +
                           "  void a() {}\n" +
                           "  void b() {}\n" +
                           "  void c() {}\n" +
                           "  void d() {}\n" +
                           "  \n" +
                           "  void m() {\n" +
                           "    a();\n" +
                           "    try {\n" +
                           "      b();\n" +
                           "    }\n" +
                           "    catch (Exception e) {\n" +
                           "      c();\n" +
                           "    }\n" +
                           "    d();\n" +
                           "  }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void a() {}\n" +
                              "    void b() {}\n" +
                              "    void c() {}\n" +
                              "    void d() {}\n" +
                              "    \n" +
                              "    void m() {\n" +
                              "        a();\n" +
                              "        try {\n" +
                              "            b();\n" +
                              "        } catch (Exception e) {\n" +
                              "        }\n" +
                              "        d();\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "  a();\n" +
                      "  ... when exists\n" +
                      "- c();\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testForallDotsNotPatchingCatchBlock() {
        // contract: dots in forall mode should not patch a statement only found in the catch block

        String inputCode = "class A {\n" +
                           "  void a() {}\n" +
                           "  void b() {}\n" +
                           "  void c() {}\n" +
                           "  void d() {}\n" +
                           "  \n" +
                           "  void m() {\n" +
                           "    a();\n" +
                           "    try {\n" +
                           "      b();\n" +
                           "    }\n" +
                           "    catch (Exception e) {\n" +
                           "      c();\n" +
                           "    }\n" +
                           "    d();\n" +
                           "  }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void a() {}\n" +
                              "    void b() {}\n" +
                              "    void c() {}\n" +
                              "    void d() {}\n" +
                              "    \n" +
                              "    void m() {\n" +
                              "        a();\n" +
                              "        try {\n" +
                              "            b();\n" +
                              "        } catch (Exception e) {\n" +
                              "            c();\n" +
                              "        }\n" +
                              "        d();\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "  a();\n" +
                      "  ...\n" +
                      "- c();\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testFieldReads() {
        // contract: correct matching of field reads

        String inputCode = "class A {\n" +
                           "    class Point { Integer x,y; public Point(Integer x, Integer y) {} }\n" +
                           "    class Logger { public void log(Object x) {} }\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "        Point point = new Point(1,2);\n" +
                           "        Logger logger = new Logger();\n" +
                           "        logger.log(point);\n" +
                           "    }\n" +
                           "    \n" +
                           "    void m2() {\n" +
                           "        Point point = new Point(1,2);\n" +
                           "        Logger logger = new Logger();\n" +
                           "        logger.log(point.x);\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    class Point { Integer x,y; public Point(Integer x, Integer y) {} }\n" +
                              "    class Logger { public void log(Object x) {} }\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        Point point = new Point(1,2);\n" +
                              "        Logger logger = new Logger();\n" +
                              "        logger.log(point);\n" +
                              "    }\n" +
                              "    \n" +
                              "    void m2() {\n" +
                              "        Point point = new Point(1,2);\n" +
                              "        Logger logger = new Logger();\n" +
                              "        logger.log(\"The X coordinate is \" + point.x.toString());\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "Point p;\n" +
                      "@@\n" +
                      "- logger.log(p.x);\n" +
                      "+ logger.log(\"The X coordinate is \" + p.x.toString());\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testHelloWorld() {
        // contract: hello world template test

        String inputCode = "class A {\n" +
                           "    void foo() {\n" +
                           "        int x = 1;\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void foo() {\n" +
                              "        \n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "identifier v1;\n" +
                      "@@\n" +
                      "- int v1 = 1;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMatchAnyType() {
        // contract: a 'type' metavariable should match any type

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "type T;\n" +
                      "@@\n" +
                      "- T x;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMatchSpecificType() {
        // contract: a concretely given type in SmPL should match that precise type

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "- ASpecificType x;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodHeaderBinding() {
        // contract: binding metavariables on the method header

        String inputCode = "class A {\n" +
                           "    int square(int x) {\n" +
                           "        return x*x;\n" +
                           "    }\n" +
                           "    float square(float x) {\n" +
                           "        return x*x;\n" +
                           "    }\n" +
                           "    double square(Float x) {\n" +
                           "        return x*x;\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    int square(int x) {\n" +
                              "        int y = 0;\n" +
                              "        return x*x;\n" +
                              "    }\n" +
                              "    float square(float x) {\n" +
                              "        float y = 0;\n" +
                              "        return x*x;\n" +
                              "    }\n" +
                              "    double square(Float x) {\n" +
                              "        return x*x;\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "type T1;\n" +
                      "expression E;\n" +
                      "@@\n" +
                      "  T1 square(T1 x) {\n" +
                      "+     T1 y = 0;\n" +
                      "      return E;\n" +
                      "  }\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodHeaderDots() {
        // contract: using dots to match arbitrary sequences of parameters in method header

        String inputCode = "class A {\n" +
                           "    static class Point {\n" +
                           "        public Integer x;\n" +
                           "        public Integer y;\n" +
                           "    }\n" +
                           "    /* skip */ void log(String message) {}\n" +
                           "    void drawCircle(Point origin, float radius) {\n" +
                           "        log(\"Coordinates: \" + origin.x.toString() + \", \" + origin.y.toString());\n" +
                           "    }\n" +
                           "    void drawRectangle(float width, float height, Point topLeftCorner) {\n" +
                           "        log(\"Coordinates: \" + topLeftCorner.x.toString() + \", \" + topLeftCorner.y.toString());\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    static class Point {\n" +
                              "        public Integer x;\n" +
                              "        public Integer y;\n" +
                              "    }\n" +
                              "    /* skip */ void log(String message) {}\n" +
                              "    void drawCircle(Point origin, float radius) {\n" +
                              "        log(\"Point: \" + origin.toString());\n" +
                              "    }\n" +
                              "    void drawRectangle(float width, float height, Point topLeftCorner) {\n" +
                              "        log(\"Point: \" + topLeftCorner.toString());\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "type T;\n" +
                      "identifier fn, pt;\n" +
                      "@@\n" +
                      "  T fn(..., Point pt, ...) {\n" +
                      "      ...\n" +
                      "-     log(\"Coordinates: \" + pt.x.toString() + \", \" + pt.y.toString());\n" +
                      "+     log(\"Point: \" + pt.toString());\n" +
                      "  }\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodHeaderLiteralMatch() {
        // contract: literal matching on the method header

        String inputCode = "class A {\n" +
                           "    int square(int x) {\n" +
                           "        return x*x;\n" +
                           "    }\n" +
                           "    \n" +
                           "    int cube(int x) {\n" +
                           "        return x*x*x;\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    int square(int x) {\n" +
                              "        log(\"square called\");\n" +
                              "        return x*x;\n" +
                              "    }\n" +
                              "    \n" +
                              "    int cube(int x) {\n" +
                              "        return x*x*x;\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "expression E;\n" +
                      "@@\n" +
                      "  int square(int x) {\n" +
                      "+     log(\"square called\");\n" +
                      "      return E;\n" +
                      "  }\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testAddToEmptyMethod() {
        // contract: a patch should be able to add statements to an empty method body

        String inputCode = "class A {\n" +
                           "    /* skip */ void a() {}\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "    }\n" +
                           "    void m2() {\n" +
                           "        a();\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    /* skip */ void a() {}\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        foo();\n" +
                              "        bar();\n" +
                              "    }\n" +
                              "    void m2() {\n" +
                              "        a();\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ identifier fn; @@\n" +
                      "void fn() {\n" +
                      "+ foo();\n" +
                      "+ bar();\n" +
                      "}\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testAddToMethodBottom() {
        // contract: a patch should be able to add statements at the bottom of methods

        String inputCode = "class A {\n" +
                           "    /* skip */ void a() {}\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "    }\n" +
                           "    void m2() {\n" +
                           "        a();\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    /* skip */ void a() {}\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        foo();\n" +
                              "        bar();\n" +
                              "    }\n" +
                              "    void m2() {\n" +
                              "        a();\n" +
                              "        foo();\n" +
                              "        bar();\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ identifier fn; @@\n" +
                      "void fn() {\n" +
                      "  ...\n" +
                      "+ foo();\n" +
                      "+ bar();\n" +
                      "}\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testAddToMethodTop() {
        // contract: a patch should be able to add statements at the top of methods

        String inputCode = "class A {\n" +
                           "    // Skip\n" +
                           "    void a() {}\n" +
                           "    void m1() {\n" +
                           "    }\n" +
                           "    void m2() {\n" +
                           "        a();\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    // Skip\n" +
                              "    void a() {}\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        foo();\n" +
                              "        bar();\n" +
                              "    }\n" +
                              "    void m2() {\n" +
                              "        foo();\n" +
                              "        bar();\n" +
                              "        a();\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ identifier fn; @@\n" +
                      "void fn() {\n" +
                      "+ foo();\n" +
                      "+ bar();\n" +
                      "  ...\n" +
                      "}\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodCallArgDotsMatchAny() {
        // contract: the expression 'f(...)' should match any method call to 'f' regardless of argument list

        String inputCode = "class A {\n" +
                           "    // Skip\n" +
                           "    int f() { return 0; }\n" +
                           "    \n" +
                           "    // Skip\n" +
                           "    int f(int x) { return 0; }\n" +
                           "    \n" +
                           "    // Skip\n" +
                           "    int f(int x, int y) { return 0; }\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "        f();\n" +
                           "        f(1);\n" +
                           "        f(2, 3);\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    // Skip\n" +
                              "    int f() { return 0; }\n" +
                              "    \n" +
                              "    // Skip\n" +
                              "    int f(int x) { return 0; }\n" +
                              "    \n" +
                              "    // Skip\n" +
                              "    int f(int x, int y) { return 0; }\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "- f(...);\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodCallArgDotsMatchSingle() {
        // contract: the expression 'f(..., E, ...)' should match any method call to 'f' where the expression E appears anywhere in the argument list

        String inputCode = "class A {\n" +
                           "    int f(int ... xs) { return 0; }\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "        f();\n" +
                           "        f(1);\n" +
                           "        f(2, 3);\n" +
                           "        f(1, 2, 3);\n" +
                           "        f(2, 1, 3);\n" +
                           "        f(2, 3, 1);\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    int f(int ... xs) { return 0; }\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        f();\n" +
                              "        f(2, 3);\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "- f(..., 1, ...);\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodCallArgDotsMatchSingleAtEnd() {
        // contract: the expression 'f(..., E)' should match any method call to 'f' where the expression E appears as the last argument

        String inputCode = "class A {\n" +
                           "    int f(int ... xs) { return 0; }\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "        f();\n" +
                           "        f(1);\n" +
                           "        f(2, 3);\n" +
                           "        f(1, 2, 3);\n" +
                           "        f(2, 1, 3);\n" +
                           "        f(2, 3, 1);\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    int f(int ... xs) { return 0; }\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        f();\n" +
                              "        f(2, 3);\n" +
                              "        f(1, 2, 3);\n" +
                              "        f(2, 1, 3);\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "- f(..., 1);\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodCallArgDotsMatchSingleAtStart() {
        // contract: the expression 'f(E, ...)' should match any method call to 'f' where the expression E appears as the first argument

        String inputCode = "class A {\n" +
                           "    /* skip */ int f(int ... xs) { return 0; }\n" +
                           "    void m1() {\n" +
                           "        f();\n" +
                           "        f(1);\n" +
                           "        f(2, 3);\n" +
                           "        f(1, 2, 3);\n" +
                           "        f(2, 1, 3);\n" +
                           "        f(2, 3, 1);\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    /* skip */ int f(int ... xs) { return 0; }\n" +
                              "    void m1() {\n" +
                              "        f();\n" +
                              "        f(2, 3);\n" +
                              "        f(2, 1, 3);\n" +
                              "        f(2, 3, 1);\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "- f(1, ...);\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodCallArgDotsNested1() {
        // contract: the expression 'f(..., g(...), ...)' should match any method call to 'f' with any argument list where a call to 'g' occurs (with any argument list for 'g')

        String inputCode = "class A {\n" +
                           "    int f(int ... xs) { return 0; }\n" +
                           "    int g(int ... xs) { return 0; }\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "        f();\n" +
                           "        f(1);\n" +
                           "        f(2, 3);\n" +
                           "        f(g());\n" +
                           "        f(g(), 1);\n" +
                           "        f(2, g(), 3);\n" +
                           "        f(1, 2, g());\n" +
                           "        f(g(1, 2));\n" +
                           "        f(g(2, 1), 1);\n" +
                           "        f(2, g(1, 3), 3);\n" +
                           "        f(1, 2, g(2, 3));\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    int f(int ... xs) { return 0; }\n" +
                              "    int g(int ... xs) { return 0; }\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        f();\n" +
                              "        f(1);\n" +
                              "        f(2, 3);\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "- f(..., g(...), ...);\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodCallArgDotsNested2() {
        // contract: the expression 'f(..., g(..., 1, ...), ...)' should match any method call to 'f' with any argument list where a call to 'g' occurs containing the argument 1 in its argument list

        String inputCode = "class A {\n" +
                           "    int f(int ... xs) { return 0; }\n" +
                           "    int g(int ... xs) { return 0; }\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "        f();\n" +
                           "        f(1);\n" +
                           "        f(2, 3);\n" +
                           "        f(g());\n" +
                           "        f(g(), 1);\n" +
                           "        f(2, g(), 3);\n" +
                           "        f(1, 2, g());\n" +
                           "        f(g(1, 2));\n" +
                           "        f(g(2, 1), 1);\n" +
                           "        f(2, g(1, 3), 3);\n" +
                           "        f(1, 2, g(2, 3));\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    int f(int ... xs) { return 0; }\n" +
                              "    int g(int ... xs) { return 0; }\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        f();\n" +
                              "        f(1);\n" +
                              "        f(2, 3);\n" +
                              "        f(g());\n" +
                              "        f(g(), 1);\n" +
                              "        f(2, g(), 3);\n" +
                              "        f(1, 2, g());\n" +
                              "        f(1, 2, g(2, 3));\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "- f(..., g(..., 1, ...), ...);\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodCallArgDotsNested3() {
        // contract: the expression 'f(..., g(1, ...), ...)' should match any method call to 'f' with any argument list where a call to 'g' occurs containing the argument 1 as its first argument

        String inputCode = "class A {\n" +
                           "    int f(int ... xs) { return 0; }\n" +
                           "    int g(int ... xs) { return 0; }\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "        f();\n" +
                           "        f(1);\n" +
                           "        f(2, 3);\n" +
                           "        f(g());\n" +
                           "        f(g(), 1);\n" +
                           "        f(2, g(), 3);\n" +
                           "        f(1, 2, g());\n" +
                           "        f(g(1, 2));\n" +
                           "        f(g(2, 1), 1);\n" +
                           "        f(2, g(1, 3), 3);\n" +
                           "        f(1, 2, g(2, 3));\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    int f(int ... xs) { return 0; }\n" +
                              "    int g(int ... xs) { return 0; }\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        f();\n" +
                              "        f(1);\n" +
                              "        f(2, 3);\n" +
                              "        f(g());\n" +
                              "        f(g(), 1);\n" +
                              "        f(2, g(), 3);\n" +
                              "        f(1, 2, g());\n" +
                              "        f(g(2, 1), 1);\n" +
                              "        f(1, 2, g(2, 3));\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "- f(..., g(1, ...), ...);\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodCallArgDotsNested4() {
        // contract: the expression 'f(..., g(..., 1), ...)' should match any method call to 'f' with any argument list where a call to 'g' occurs containing the argument 1 as its last argument

        String inputCode = "class A {\n" +
                           "    int f(int ... xs) { return 0; }\n" +
                           "    int g(int ... xs) { return 0; }\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "        f();\n" +
                           "        f(1);\n" +
                           "        f(2, 3);\n" +
                           "        f(g());\n" +
                           "        f(g(), 1);\n" +
                           "        f(2, g(), 3);\n" +
                           "        f(1, 2, g());\n" +
                           "        f(g(1, 2));\n" +
                           "        f(g(2, 1), 1);\n" +
                           "        f(2, g(1, 3), 3);\n" +
                           "        f(1, 2, g(2, 3));\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    int f(int ... xs) { return 0; }\n" +
                              "    int g(int ... xs) { return 0; }\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        f();\n" +
                              "        f(1);\n" +
                              "        f(2, 3);\n" +
                              "        f(g());\n" +
                              "        f(g(), 1);\n" +
                              "        f(2, g(), 3);\n" +
                              "        f(1, 2, g());\n" +
                              "        f(g(1, 2));\n" +
                              "        f(2, g(1, 3), 3);\n" +
                              "        f(1, 2, g(2, 3));\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "- f(..., g(..., 1), ...);\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodCallArgDotsNested5() {
        // contract: the expression 'f(..., g(..., 1))' should match any method call to 'f' with last argument a call to 'g' containing the argument 1 as its last argument

        String inputCode = "class A {\n" +
                           "    int f(int ... xs) { return 0; }\n" +
                           "    int g(int ... xs) { return 0; }\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "        f();\n" +
                           "        f(1);\n" +
                           "        f(2, 3);\n" +
                           "        f(g());\n" +
                           "        f(g(), 1);\n" +
                           "        f(2, g(), 3);\n" +
                           "        f(1, 2, g());\n" +
                           "        f(g(1, 2));\n" +
                           "        f(g(2, 1), 1);\n" +
                           "        f(2, g(1, 3), 3);\n" +
                           "        f(1, 2, g(2, 3));\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    int f(int ... xs) { return 0; }\n" +
                              "    int g(int ... xs) { return 0; }\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        f();\n" +
                              "        f(1);\n" +
                              "        f(2, 3);\n" +
                              "        f(g());\n" +
                              "        f(g(), 1);\n" +
                              "        f(2, g(), 3);\n" +
                              "        f(1, 2, g());\n" +
                              "        f(g(1, 2));\n" +
                              "        f(g(2, 1), 1);\n" +
                              "        f(2, g(1, 3), 3);\n" +
                              "        f(1, 2, g(2, 3));\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "- f(..., g(..., 1));\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodCallArgDotsNested6() {
        // contract: the expression 'f(..., g(1, ...))' should match any method call to 'f' with last argument a call to 'g' containing the argument 1 as its first argument

        String inputCode = "class A {\n" +
                           "    int f(int ... xs) { return 0; }\n" +
                           "    int g(int ... xs) { return 0; }\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "        f();\n" +
                           "        f(1);\n" +
                           "        f(2, 3);\n" +
                           "        f(g());\n" +
                           "        f(g(), 1);\n" +
                           "        f(2, g(), 3);\n" +
                           "        f(1, 2, g());\n" +
                           "        f(g(1, 2));\n" +
                           "        f(g(2, 1), 1);\n" +
                           "        f(2, g(1, 3), 3);\n" +
                           "        f(1, 2, g(2, 3));\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    int f(int ... xs) { return 0; }\n" +
                              "    int g(int ... xs) { return 0; }\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        f();\n" +
                              "        f(1);\n" +
                              "        f(2, 3);\n" +
                              "        f(g());\n" +
                              "        f(g(), 1);\n" +
                              "        f(2, g(), 3);\n" +
                              "        f(1, 2, g());\n" +
                              "        f(g(2, 1), 1);\n" +
                              "        f(2, g(1, 3), 3);\n" +
                              "        f(1, 2, g(2, 3));\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "- f(..., g(1, ...));\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodCallArgDotsNested7() {
        // contract: the expression 'f(1, ..., g(..., 3))' should match any method call to 'f' with first argument 1 and last argument a call to 'g' containing the argument 3 as its last argument

        String inputCode = "class A {\n" +
                           "    int f(int ... xs) { return 0; }\n" +
                           "    int g(int ... xs) { return 0; }\n" +
                           "    \n" +
                           "    void m1() {\n" +
                           "        f();\n" +
                           "        f(1);\n" +
                           "        f(2, 3);\n" +
                           "        f(g());\n" +
                           "        f(g(), 1);\n" +
                           "        f(2, g(), 3);\n" +
                           "        f(1, 2, g());\n" +
                           "        f(g(1, 2));\n" +
                           "        f(g(2, 1), 1);\n" +
                           "        f(2, g(1, 3), 3);\n" +
                           "        f(1, 2, g(2, 3));\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    int f(int ... xs) { return 0; }\n" +
                              "    int g(int ... xs) { return 0; }\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        f();\n" +
                              "        f(1);\n" +
                              "        f(2, 3);\n" +
                              "        f(g());\n" +
                              "        f(g(), 1);\n" +
                              "        f(2, g(), 3);\n" +
                              "        f(1, 2, g());\n" +
                              "        f(g(1, 2));\n" +
                              "        f(g(2, 1), 1);\n" +
                              "        f(2, g(1, 3), 3);\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "- f(1, ..., g(..., 3));\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testMethodsAddedToClass() {
        // contract: a patch should be able to add entire methods to the parent class of a patch-context-matching method

        String inputCode = "class A {\n" +
                           "  void a() {}\n" +
                           "  \n" +
                           "  void m1() {\n" +
                           "    a();\n" +
                           "  }\n" +
                           "  void m2() {\n" +
                           "    a();\n" +
                           "  }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void a() {}\n" +
                              "    \n" +
                              "    void m1() {\n" +
                              "        b();\n" +
                              "    }\n" +
                              "    void m2() {\n" +
                              "        b();\n" +
                              "    }\n" +
                              "    void b() {\n" +
                              "        System.out.println(\"Hello, World!\");\n" +
                              "        logCallToB();\n" +
                              "    }\n" +
                              "    void logCallToB() {\n" +
                              "        logger.log(\"b called\");\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ identifier fn; @@\n" +
                      "+ void b() {\n" +
                      "+   System.out.println(\"Hello, World!\");\n" +
                      "+   logCallToB();\n" +
                      "+ }\n" +
                      "void fn() {\n" +
                      "- a();\n" +
                      "+ b();\n" +
                      "}\n" +
                      "+ void logCallToB() {\n" +
                      "+   logger.log(\"b called\");\n" +
                      "+ }\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testPrependContextBranch() {
        // contract: a patch should be able to prepend elements above a context branch

        String inputCode = "class A {\n" +
                           "    void m1() {\n" +
                           "        if (true) {\n" +
                           "            int x = 0;\n" +
                           "        }\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void m1() {\n" +
                              "        int y = 1;\n" +
                              "        if (true) {\n" +
                              "            int x = 0;\n" +
                              "        }\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "+ int y = 1;\n" +
                      "  if (true) {\n" +
                      "      int x = 0;\n" +
                      "  }\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testPrependToContext() {
        // contract: a patch should be able to prepend elements to a context statement

        String inputCode = "class A {\n" +
                           "    void m1() {\n" +
                           "        int x;\n" +
                           "    }\n" +
                           "    \n" +
                           "    void m2() {\n" +
                           "        int y;\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    void m1() {\n" +
                              "        int prepended1;\n" +
                              "        int prepended2;\n" +
                              "        int x;\n" +
                              "    }\n" +
                              "    \n" +
                              "    void m2() {\n" +
                              "        int y;\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "+ int prepended1;\n" +
                      "+ int prepended2;\n" +
                      "  int x;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testRemoveLocalsReturningConstants001() {
        // contract: correct application of remove-locals-returning-constants patch example

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "type T;\n" +
                      "identifier ret;\n" +
                      "constant C;\n" +
                      "@@\n" +
                      "- T ret = C;\n" +
                      "  ... when != ret\n" +
                      "- return ret;\n" +
                      "+ return C;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testRemoveLocalsReturningConstantsBranch() {
        // contract: correct application of remove-locals-returning-constants patch example

        String inputCode = "class input\n" +
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
                           "}\n";
    
        String expectedCode = "class input\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "type T;\n" +
                      "identifier ret;\n" +
                      "constant C;\n" +
                      "@@\n" +
                      "- T ret = C;\n" +
                      "  ... when != ret\n" +
                      "- return ret;\n" +
                      "+ return C;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testRemoveLocalsReturningConstantsBranchMultiple() {
        // contract: correct application of remove-locals-returning-constants patch example

        String inputCode = "class input\n" +
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
                           "}\n";
    
        String expectedCode = "class input\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "type T;\n" +
                      "identifier ret;\n" +
                      "constant C;\n" +
                      "@@\n" +
                      "- T ret = C;\n" +
                      "  ... when != ret\n" +
                      "- return ret;\n" +
                      "+ return C;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testRemoveLocalsReturningConstantsBranchMultipleWhenExists() {
        // contract: correct application of remove-locals-returning-constants patch example

        String inputCode = "class input\n" +
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
                           "}\n";
    
        String expectedCode = "class input\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "type T;\n" +
                      "identifier ret;\n" +
                      "constant C;\n" +
                      "@@\n" +
                      "- T ret = C;\n" +
                      "  ... when != ret\n" +
                      "      when exists\n" +
                      "- return ret;\n" +
                      "+ return C;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testRemoveLocalsReturningConstantsElselessBranch() {
        // contract: correct application of remove-locals-returning-constants patch example

        String inputCode = "class input\n" +
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
                           "}\n";
    
        String expectedCode = "class input\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "type T;\n" +
                      "identifier ret;\n" +
                      "constant C;\n" +
                      "@@\n" +
                      "- T ret = C;\n" +
                      "  ... when != ret\n" +
                      "- return ret;\n" +
                      "+ return C;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testRemoveLocalsReturningConstantsExpressionlessReturnBug() {
        // contract: correct application of remove-locals-returning-constants patch example

        String inputCode = "class input\n" +
                           "{\n" +
                           "    public void foo(boolean x)\n" +
                           "    {\n" +
                           "        int ret = 42;\n" +
                           "        return;\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class input\n" +
                              "{\n" +
                              "    public void foo(boolean x)\n" +
                              "    {\n" +
                              "        int ret = 42;\n" +
                              "        return;\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "type T;\n" +
                      "identifier ret;\n" +
                      "constant C;\n" +
                      "@@\n" +
                      "- T ret = C;\n" +
                      "  ... when != ret\n" +
                      "- return ret;\n" +
                      "+ return C;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testRemoveLocalsReturningConstantsRejectUsageInBranchCondition() {
        // contract: correct application of remove-locals-returning-constants patch example

        String inputCode = "class input\n" +
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
                           "}\n";
    
        String expectedCode = "class input\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "type T;\n" +
                      "identifier ret;\n" +
                      "constant C;\n" +
                      "@@\n" +
                      "- T ret = C;\n" +
                      "  ... when != ret\n" +
                      "- return ret;\n" +
                      "+ return C;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testTernaryExpression() {
        // contract: patches should be able to match on ternary expressions

        String inputCode = "class A {\n" +
                           "    int sgn(int x) {\n" +
                           "        int result = (x > 0) ? 1 : 0;\n" +
                           "        return result;\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    int sgn(int x) {\n" +
                              "        int result = (x > 0) ? 1 : 0;\n" +
                              "        log(result);\n" +
                              "        return result;\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@ @@\n" +
                      "+ log(result);\n" +
                      "  return result;\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testTypedIdentifierMetavariables1() {
        // contract: correct bindings of explicitly typed identifier metavariables

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "int x;\n" +
                      "@@\n" +
                      "- log(x);\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testTypedIdentifierMetavariables2() {
        // contract: correct bindings of explicitly typed identifier metavariables

        String inputCode = "class A {\n" +
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
                           "}\n";
    
        String expectedCode = "class A {\n" +
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
                              "}\n";
    
        String smpl = "@@\n" +
                      "ASpecificType x;\n" +
                      "@@\n" +
                      "- log(x);\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testUnsupportedElementsDotsWhenExists() {
        // contract: dots in \"when exists\" mode should be allowed to traverse over unsupported elements when there exists a path that avoids them

        String inputCode = "class A {\n" +
                           "    /* skip */ void a() {}\n" +
                           "    /* skip */ void b() {}\n" +
                           "    float random;\n" +
                           "    boolean loopsNotSupported;\n" +
                           "    \n" +
                           "    void foo() {\n" +
                           "        a();\n" +
                           "        \n" +
                           "        if (random > 0.5f) {\n" +
                           "            while (loopsNotSupported) {\n" +
                           "              break;\n" +
                           "            }\n" +
                           "        }\n" +
                           "        \n" +
                           "        b();\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    /* skip */ void a() {}\n" +
                              "    /* skip */ void b() {}\n" +
                              "    float random;\n" +
                              "    boolean loopsNotSupported;\n" +
                              "    \n" +
                              "    void foo() {\n" +
                              "        a();\n" +
                              "        \n" +
                              "        if (random > 0.5f) {\n" +
                              "            while (loopsNotSupported) {\n" +
                              "              break;\n" +
                              "            }\n" +
                              "        }\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "a();\n" +
                      "... when exists\n" +
                      "- b();\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testUnsupportedElementsMatchAfter() {
        // contract: should be able to match and transform things surrounding an unsupported element

        String inputCode = "class A {\n" +
                           "    /* skip */ void a() {}\n" +
                           "    /* skip */ void b() {}\n" +
                           "    boolean loopsNotSupported;\n" +
                           "    \n" +
                           "    void foo() {\n" +
                           "        a();\n" +
                           "        while (loopsNotSupported) {\n" +
                           "          break;\n" +
                           "        }\n" +
                           "        b();\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    /* skip */ void a() {}\n" +
                              "    /* skip */ void b() {}\n" +
                              "    boolean loopsNotSupported;\n" +
                              "    \n" +
                              "    void foo() {\n" +
                              "        a();\n" +
                              "        while (loopsNotSupported) {\n" +
                              "            break;\n" +
                              "        }\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "- b();\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testUnsupportedElementsMatchBefore() {
        // contract: should be able to match and transform things surrounding an unsupported element

        String inputCode = "class A {\n" +
                           "    /* skip */ void a() {}\n" +
                           "    /* skip */ void b() {}\n" +
                           "    boolean loopsNotSupported;\n" +
                           "    \n" +
                           "    void foo() {\n" +
                           "        a();\n" +
                           "        while (loopsNotSupported) {\n" +
                           "          break;\n" +
                           "        }\n" +
                           "        b();\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    /* skip */ void a() {}\n" +
                              "    /* skip */ void b() {}\n" +
                              "    boolean loopsNotSupported;\n" +
                              "    \n" +
                              "    void foo() {\n" +
                              "        while (loopsNotSupported) {\n" +
                              "            break;\n" +
                              "        }\n" +
                              "        b();\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "- a();\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testUnsupportedElementsMatchSurrounding() {
        // contract: should be able to match and transform things surrounding an unsupported element

        String inputCode = "class A {\n" +
                           "    /* skip */ void a() {}\n" +
                           "    /* skip */ void b() {}\n" +
                           "    boolean loopsNotSupported;\n" +
                           "    \n" +
                           "    void foo() {\n" +
                           "        a();\n" +
                           "        while (loopsNotSupported) {\n" +
                           "          break;\n" +
                           "        }\n" +
                           "        b();\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    /* skip */ void a() {}\n" +
                              "    /* skip */ void b() {}\n" +
                              "    boolean loopsNotSupported;\n" +
                              "    \n" +
                              "    void foo() {\n" +
                              "        while (loopsNotSupported) {\n" +
                              "            break;\n" +
                              "        }\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "(\n" +
                      "- a();\n" +
                      "|\n" +
                      "- b();\n" +
                      ")\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testUnsupportedElementsRejectDots() {
        // contract: dots with post-context should not be allowed to traverse across unsupported elements

        String inputCode = "class A {\n" +
                           "    /* skip */ void a() {}\n" +
                           "    /* skip */ void b() {}\n" +
                           "    boolean loopsNotSupported;\n" +
                           "    \n" +
                           "    void foo() {\n" +
                           "        a();\n" +
                           "        while (loopsNotSupported) {\n" +
                           "          break;\n" +
                           "        }\n" +
                           "        b();\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    /* skip */ void a() {}\n" +
                              "    /* skip */ void b() {}\n" +
                              "    boolean loopsNotSupported;\n" +
                              "    \n" +
                              "    void foo() {\n" +
                              "        a();\n" +
                              "        while (loopsNotSupported) {\n" +
                              "            break;\n" +
                              "        }\n" +
                              "        b();\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "a();\n" +
                      "...\n" +
                      "- b();\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
    @Test
    public void testUnsupportedElementsRejectDotsWhenNotEquals() {
        // contract: dots constrained by \"when != x\" should not be allowed to traverse across unsupported elements even if there is no post-context

        String inputCode = "class A {\n" +
                           "    /* skip */ void a() {}\n" +
                           "    /* skip */ void b() {}\n" +
                           "    boolean loopsNotSupported;\n" +
                           "    \n" +
                           "    void foo() {\n" +
                           "        a();\n" +
                           "        while (loopsNotSupported) {\n" +
                           "          break;\n" +
                           "        }\n" +
                           "    }\n" +
                           "}\n";
    
        String expectedCode = "class A {\n" +
                              "    /* skip */ void a() {}\n" +
                              "    /* skip */ void b() {}\n" +
                              "    boolean loopsNotSupported;\n" +
                              "    \n" +
                              "    void foo() {\n" +
                              "        a();\n" +
                              "        while (loopsNotSupported) {\n" +
                              "            break;\n" +
                              "        }\n" +
                              "    }\n" +
                              "}\n";
    
        String smpl = "@@\n" +
                      "@@\n" +
                      "- a();\n" +
                      "... when != x\n";
    
        runSingleTest(smpl, inputCode, expectedCode);
    }
}
