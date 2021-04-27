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

import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtVariableReference;
import spoon.smpl.formula.MetavariableConstraint;

/**
 * An IdentifierConstraint restricts a metavariable binding to be CtVariableReference, potentially
 * by refining a given binding to a CtVariableAccess
 */
public class IdentifierConstraint implements MetavariableConstraint {
	/**
	 * Validate and potentially modify a value bound to a metavariable.
	 *
	 * @param value Value bound to metavariable
	 * @return The Object that is a valid binding under the constraint, or null if the value does not match the constraint
	 */
	@Override
	public CtElement apply(CtElement value) {
		if (value instanceof CtVariableReference) {
			return value;
		} else if (value instanceof CtVariableAccess) {
			return ((CtVariableAccess<?>) value).getVariable();
		} else {
			return null;
		}
	}
}
