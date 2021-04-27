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

import spoon.reflect.declaration.CtMethod;
import spoon.smpl.MethodHeaderModel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A MethodHeaderPredicate contains an inner formula suitable for matching against a MethodHeaderModel.
 */
public class MethodHeaderPredicate extends ParameterizedPredicate {
	/**
	 * Create a new MethodHeaderPredicate given a method and a set of metavariable declarations.
	 *
	 * @param method   Method defining the header signature that is to be matched by this predicate
	 * @param metavars Metavariable names and their corresponding constraints
	 */
	public MethodHeaderPredicate(CtMethod<?> method, Map<String, MetavariableConstraint> metavars) {
		super(metavars);
		metavarsUsedInHeader = new HashSet<>();
		headerFormula = MethodHeaderModel.compileMethodHeaderFormula(method, metavars, metavarsUsedInHeader);
	}

	/**
	 * Get the header-matching inner formula.
	 *
	 * @return Formula for matching a MethodHeaderModel
	 */
	public Formula getHeaderFormula() {
		return headerFormula;
	}

	/**
	 * Get the set of metavariable names involved in the header-matching inner formula. An enclosing
	 * Formula should quantify these.
	 *
	 * @return Set of metavariable names involved in the header-matching inner formula
	 */
	public Set<String> getMetavarsUsedInHeader() {
		return metavarsUsedInHeader;
	}

	@Override
	public void accept(FormulaVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "MethodHeader(" + headerFormula.toString() + ")";
	}

	/**
	 * The header-matching inner formula.
	 */
	private final Formula headerFormula;

	/**
	 * Set of metavariable names involved in the header-matching inner formula.
	 */
	private final Set<String> metavarsUsedInHeader;
}
