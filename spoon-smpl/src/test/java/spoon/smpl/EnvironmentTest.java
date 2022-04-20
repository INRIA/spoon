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
import static spoon.smpl.TestUtils.*;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnvironmentTest {
	@Test
	public void testJoinPositives() {

		// contract: non-conflicting positive bindings should be joinable into an env containing all of them

		Environment e1 = new Environment();
		e1.put("x", 1);

		Environment e2 = new Environment();
		e2.put("y", 2);

		assertEquals(env("x", 1, "y", 2), Environment.join(e1, e2));
	}

	@Test
	public void testJoinNegatives() {

		// contract: arbitrary sets of neg. bindings for the same variable name should be joinable into an env containing all of the bindings

		Environment e1 = new Environment();
		e1.put("x", new Environment.NegativeBinding(1));

		Environment e2 = new Environment();
		e2.put("x", new Environment.NegativeBinding(2));

		assertEquals(env("x", envNeg(1, 2)), Environment.join(e1, e2));
	}

	@Test
	public void testJoinMixed() {

		// contract: joining envs containing both non-conflicting positive bindings on arbitrary variables and neg. bindings on the same variable should produce an env containing all the bindings

		Environment e1 = new Environment();
		e1.put("x", new Environment.NegativeBinding(1));
		e1.put("y", 3);

		Environment e2 = new Environment();
		e2.put("x", new Environment.NegativeBinding(2));
		e2.put("z", 4);

		assertEquals(env("x", envNeg(1, 2), "y", 3, "z", 4), Environment.join(e1, e2));
	}

	@Test
	public void testNegationOfPositives() {

		// contract: negating an env containing positive bindings should produce an env of identical domain with each positive binding replaced by a neg. binding

		Environment e1 = new Environment();
		e1.put("x", 1);
		e1.put("y", 2);

		assertEquals(envSet(env("x", envNeg(1)), env("y", envNeg(2))), Environment.negate(e1));
	}

	@Test
	public void testNegationOfSingularNegatives() {

		// contract: negating a neg. binding over a singular value should produce a singular positive binding, so negating an env containing only singular neg. bindings should produce a single env with the singular neg. bindings replaced by positive bindings

		Environment e1 = new Environment();
		e1.put("x", new Environment.NegativeBinding(1));
		e1.put("y", new Environment.NegativeBinding(2));

		assertEquals(envSet(env("x", 1, "y", 2)), Environment.negate(e1));
	}

	@Test
	public void testNegationOfNonSingularNegatives() {

		// contract: negating a neg. binding over multiple values should produce a set of envs covering each possible positive binding, so negating an env containing multiple such neg. bindings should produce the cartesian product of these sets

		Environment e1 = new Environment();
		e1.put("x", new Environment.NegativeBinding(1, 2));
		e1.put("y", new Environment.NegativeBinding(3, 4));

		assertEquals(envSet(env("x", 1, "y", 3),
							env("x", 2, "y", 3),
							env("x", 1, "y", 4),
							env("x", 2, "y", 4)), Environment.negate(e1));
	}

	@Test
	public void testNegationOfBottom() {

		// contract: the negation of the bottom/conflicting environment is the any-environment

		assertEquals(new HashSet<>(Arrays.asList(new Environment())), Environment.negate(null));
	}

	@Test
	public void testJoinBottom() {

		// contract: if either of the two environments being joined is conflicting, the result is conflicting

		Environment environment = new Environment();

		assertEquals(null, Environment.join(environment, null));
		assertEquals(null, Environment.join(null, environment));
		assertEquals(null, Environment.join(null, null));
	}

	@Test
	public void testRejectDueToNegativeBinding() {

		// contract: the join of two envs. where a negative binding in one matches a positive binding in the other is conflicting

		Environment e1 = new Environment();
		Environment e2 = new Environment();

		e1.put("x", 1);
		e2.put("x", new Environment.NegativeBinding(1));

		assertEquals(null, Environment.join(e1, e2));
		assertEquals(null, Environment.join(e2, e1));
	}

	@Test
	public void testJoinDoesNotMutateInputs() {

		// contract: Environment.join(a,b) should not mutate a or b

		Environment e1 = new Environment();
		Environment e2 = new Environment();

		e1.put("x", new Environment.NegativeBinding("y"));
		e2.put("x", new Environment.NegativeBinding("z"));

		int e1hash = e1.hashCode();
		int e2hash = e2.hashCode();

		Environment e3 = Environment.join(e1, e2);

		assertEquals(e1hash, e1.hashCode());
		assertEquals(e2hash, e2.hashCode());
	}

	@Test
	public void testMMultipleAlternativesPositiveBinding() {

		// contract: negating a MultipleAlternativesPositiveBinding should create a negative binding containing all the alternatives

		Environment e = new Environment();
		e.put("x", Environment.MultipleAlternativesPositiveBinding.create(1, 2, 3));
		assertEquals(Environment.negate(e).iterator().next().get("x"), new Environment.NegativeBinding(1, 2, 3));
	}
}
