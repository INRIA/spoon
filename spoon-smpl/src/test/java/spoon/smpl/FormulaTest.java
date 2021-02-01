package spoon.smpl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import spoon.smpl.formula.*;

public class FormulaTest {
	@Test
	public void testToString() {

		// contract: formula elements should have a nice structurally recursive string representation

		Formula phi = new AllNext(new AllUntil(new And(new ExistsNext(new Not(new True())),
													   new ExistsUntil(new Or(new ExistsVar("x", new Proposition("Proposition")),
																			  new SetEnv("y", 1)), new True())), new True()));
		assertEquals("AX(AU(And(EX(Not(T)), EU(Or(E(x, Proposition), SetEnv(y = 1)), T)), T))",
					 phi.toString());
	}
}
