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
 * A VariableUsePredicate is a Predicate that should match code element labels whose code
 * elements either involve access to an explicitly given variable name or involve access
 * to a variable that can be bound to a metavariable specified by the predicate.
 * <p>
 * VariableUsePredicate("x", {}) should match all states in a model where a variable or
 * field "x" is accessed in any way.
 * <p>
 * VariableUsePredicate("v1", {v1: IdentifierConstraint}) should match all states in a
 * model where a variable is used that can be bound to the metavariable "v1" according to
 * its constraints.
 */
public class VariableUsePredicate extends ParameterizedPredicate {
	/**
	 * Create a new VariableUsePredicate using a given variable (or metavariable) name and
	 * metavariable spec.
	 *
	 * @param variable Variable (or metavariable) name
	 * @param metavars Metavariable names and their corresponding constraints
	 */
	public VariableUsePredicate(String variable, Map<String, MetavariableConstraint> metavars) {
		super(metavars);
		this.variable = variable;
	}

	/**
	 * Get the variable (or metavariable) name to search for.
	 *
	 * @return Variable (or metavariable) name
	 */
	public String getVariable() {
		return variable;
	}

	@Override
	public void accept(FormulaVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "Used(" + variable + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof VariableUsePredicate && other.hashCode() == hashCode());
	}

	/**
	 * Variable (or metavariable) name to match.
	 */
	private String variable;
}
