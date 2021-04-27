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
package spoon.smpl.metavars;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.formula.MetavariableConstraint;

/**
 * A ConstantConstraint restricts a metavariable binding to be a CtLiteral, potentially
 * by refining a given binding to a CtExpression if the expression consists of a single
 * CtLiteral.
 */
public class ConstantConstraint implements MetavariableConstraint {
	/**
	 * Validate and potentially modify a value bound to a metavariable.
	 *
	 * @param value Value bound to metavariable
	 * @return The Object that is a valid binding under the constraint, or null if the value does not match the constraint
	 */
	@Override
	public CtElement apply(CtElement value) {
		if (value instanceof CtLiteral) {
			return value;
		} else if (value instanceof CtExpression) {
			CtExpression<?> expr = (CtExpression<?>) value;

			if (expr.getDirectChildren().get(0) instanceof CtLiteral) {
				return expr.getDirectChildren().get(0);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
