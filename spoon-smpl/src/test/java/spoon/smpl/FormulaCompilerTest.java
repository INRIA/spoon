package spoon.smpl;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import static spoon.smpl.TestUtils.*;

/**
 * This suite is intentionally left very sparse as the current idea is that FormulaCompiler
 * will be thoroughly tested by the end-to-end SmPL patch application tests.
 *
 * Tests for bugs specific to the FormulaCompiler should go in this suite.
 */
public class FormulaCompilerTest {
    @Test
    public void testEmpty() {

        // contract: the formula compiler should return null for an empty CFG

        CFGModel model = new CFGModel(methodCfg(parseMethod("void foo() { }")));
        FormulaCompiler compiler = new FormulaCompiler();

        assertEquals(null, compiler.compileFormula(model.getCfg().getExitNode()));
    }
}
