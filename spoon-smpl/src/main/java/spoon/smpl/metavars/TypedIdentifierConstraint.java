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

import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtVariableReference;
import spoon.smpl.formula.MetavariableConstraint;

// TODO: replace with TypedExpressionConstraint to more closely mimic Coccinelle

/**
 * An TypedIdentifierConstraint behaves like an IdentifierConstraint extended with the
 * additional constraint on the data type of the bound variable.
 */
public class TypedIdentifierConstraint implements MetavariableConstraint {
	public TypedIdentifierConstraint(String requiredType) {
		this.innerConstraint = new IdentifierConstraint();
		this.requiredType = requiredType;
	}

	@Override
	public CtElement apply(CtElement value) {
		CtElement validIdentifier = innerConstraint.apply(value);

		if (validIdentifier instanceof CtVariableReference
			&& ((CtVariableReference<?>) validIdentifier).getType().getSimpleName().equals(requiredType)) {
			return validIdentifier;
		} else {
			return null;
		}
	}

	private IdentifierConstraint innerConstraint;
	private String requiredType;
}
