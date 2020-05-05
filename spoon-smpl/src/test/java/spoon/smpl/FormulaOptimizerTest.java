package spoon.smpl;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import spoon.smpl.formula.*;

import java.util.ArrayList;

public class FormulaOptimizerTest {
    @Test
    public void testRemoveEmptyOperations() {

        // contract: the formula optimizer should remove empty operation elements

        Formula phi = new And(new Proposition("foo"),
                              new ExistsVar("_v", new SetEnv("_v", new ArrayList<>())));

        assertEquals(new Proposition("foo"), FormulaOptimizer.optimizeFully(phi));
    }
}
