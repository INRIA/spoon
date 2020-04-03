package spoon.smpl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static spoon.smpl.TestUtils.*;

/**
 * The idea for this suite is to show the current status of the main features of the
 * implementation.
 */
public class MainFeaturesTest {
    @Before
    public void before() {
        resetControlFlowNodeCounter();
    }

    @Test
    public void test1() {

        // contract: the SmPL parser can parse a given SmPL patch and produce a checkable formula

        String smplString = "@@\n" +
                            "type T;\n" +
                            "identifier ret;\n" +
                            "constant C;\n" +
                            "@@\n" +
                            "T ret = 0;\n" +
                            "if (ret > C) {\n" +
                            "    ret = ret - 1;\n" +
                            "}\n" +
                            "return ret;\n";

        Model model = new CFGModel(methodCfg(parseMethod("int foo()\n" +
                                                         "{\n" +
                                                         "    int y = 0;\n" +
                                                         "\n" +
                                                         "    if (y > 5)\n" +
                                                         "    {\n" +
                                                         "        y = y - 1;\n" +
                                                         "    }\n" +
                                                         "\n" +
                                                         "    return y;\n" +
                                                         "}\n")));

        SmPLRule rule = SmPLParser.parse(smplString);
        ModelChecker modelChecker = new ModelChecker(model);

        rule.getFormula().accept(modelChecker);

        assertEquals("[(4, {C=5, T=int, ret=y}, [])]", sortedEnvs(modelChecker.getResult().toString()));
    }
}
