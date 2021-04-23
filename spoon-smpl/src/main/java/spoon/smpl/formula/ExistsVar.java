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
 * ExistsVar represents the existentially quantified variable logical connective of CTL-V(W).
 * <p>
 * Semantically, "E(v, p)" selects the states that satisfy the formula "p" while also removing
 * the binding for the metavariable "v" from the environment, if such a binding exists.
 */
public class ExistsVar implements Formula {
	/**
	 * Create a new existentially quantified variable logical connective.
	 *
	 * @param varName      Variable name
	 * @param innerElement The Formula that should hold in some successor
	 */
	public ExistsVar(String varName, Formula innerElement) {
		this.varName = varName;
		this.innerElement = innerElement;
	}

	/**
	 * Get the name of the quantified variable.
	 *
	 * @return The name of the quantified variable
	 */
	public String getVarName() {
		return varName;
	}

	/**
	 * Get the inner formula.
	 *
	 * @return The inner formula
	 */
	public Formula getInnerElement() {
		return innerElement;
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
		return "E(" + varName + ", " + innerElement.toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof ExistsVar && other.hashCode() == hashCode());
	}

	/**
	 * Name of quantified variable.
	 */
	private String varName;

	/**
	 * Inner formula.
	 */
	private Formula innerElement;
}
