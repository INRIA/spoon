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

import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * An Expression Predicate contains a parameterized match pattern for an expression.
 */
public class Expression extends CodeElementPredicate {
	public Expression(CtElement codeElement) {
		super(codeElement);
	}

	/**
	 * Create a new Expression Predicate.
	 *
	 * @param codeElement Expression code element
	 * @param metavars    Metavariable names and their corresponding constraints
	 */
	public Expression(CtElement codeElement, Map<String, MetavariableConstraint> metavars) {
		super(codeElement, metavars);
	}

	/**
	 * Implements the Visitor pattern.
	 *
	 * @param visitor
	 */
	@Override
	public void accept(FormulaVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Expression(").append(getCodeElementStringRepresentation()).append(")");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this == other || (other instanceof Expression && other.hashCode() == hashCode());
	}
}
