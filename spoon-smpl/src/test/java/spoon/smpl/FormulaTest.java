/**
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package spoon.smpl;

import org.junit.jupiter.api.Test;
import spoon.smpl.formula.*;


import static org.junit.jupiter.api.Assertions.assertEquals;

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
