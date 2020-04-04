package spoon.smpl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import spoon.smpl.formula.*;

public class FormulaTest {
    @Test
    public void testToString() {

        // contract: formula elements should have a nice structurally recursive string representation

        Formula phi = new AllNext(new AllUntil(new And(new ExistsNext(new Neg(new True())),
                                                       new ExistsUntil(new Or(new ExistsVar("x", new Proposition("Proposition")),
                                                                              new SetEnv("y", 1)), new True())), new True()));

        assertEquals("AX(A[(EX(-T) && E[(Ex(Proposition) || SetEnv(y = 1)) U T]) U T])",
                     phi.toString());
    }
}
