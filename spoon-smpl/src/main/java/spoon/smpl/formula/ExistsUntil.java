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

/**
 * ExistsUntil represents the EU logical connective of CTL.
 * <p>
 * Semantically, "E[p U q]" (or "p EU q") selects the states for which there is at least
 * one path where "p" holds on every step until "q" eventually holds.
 */
public class ExistsUntil extends BinaryConnective {
	/**
	 * Create a new EU logical connective.
	 *
	 * @param lhs The Formula that must hold until the second one does
	 * @param rhs The Formula that must eventually hold
	 */
	public ExistsUntil(Formula lhs, Formula rhs) {
		super(lhs, rhs);
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

	@Override
	public String toString() {
		return "EU(" + getLhs().toString() + ", " + getRhs().toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof ExistsUntil && other.hashCode() == hashCode());
	}
}
