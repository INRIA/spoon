/*
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
package spoon.smpl.formula;

import java.util.Map;

/**
 * A Proposition is a constant, unparameterized predicate. The actual proposition is represented
 * by a given String.
 */
public class Proposition implements Predicate {
	/**
	 * Create a new Proposition.
	 *
	 * @param proposition The proposition String
	 */
	public Proposition(String proposition) {
		this.proposition = proposition;
	}

	/**
	 * Implements the Visitor pattern.
	 *
	 * @param visitor Visitor to accept
	 */
	@Override
	public void accept(FormulaVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Propositions do not support metavariables.
	 *
	 * @return null
	 */
	@Override
	public Map<String, MetavariableConstraint> getMetavariables() {
		return null;
	}

	/**
	 * Propositions do not support metavariables.
	 *
	 * @return true
	 */
	@Override
	public boolean processMetavariableBindings(Map<String, Object> parameters) {
		return true;
	}

	/**
	 * Get the proposition String.
	 *
	 * @return The proposition String
	 */
	public String getProposition() {
		return proposition;
	}

	@Override
	public String toString() {
		return proposition;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof Proposition && other.hashCode() == hashCode());
	}

	/**
	 * The proposition String.
	 */
	private String proposition;
}
