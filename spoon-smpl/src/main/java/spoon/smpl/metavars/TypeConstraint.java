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

import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.smpl.formula.MetavariableConstraint;

// TODO: remove special method header encoding case once we have a simpler approach for matching the method header

/**
 * A TypeConstraint restricts a metavariable binding to be a CtTypeReference. A special case exists for binding to
 * a newly created type reference indicated by the name of a CtFieldRead appearing as the single argument to an
 * invocation of the SmPL Java DSL meta element encoding a method header return type.
 */
public class TypeConstraint implements MetavariableConstraint {
	/**
	 * Validate and potentially modify a value bound to a metavariable.
	 *
	 * @param value Value bound to metavariable
	 * @return The Object that is a valid binding under the constraint, or null if the value does not match the constraint
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CtElement apply(CtElement value) {
		if (value instanceof CtTypeReference) {
			return value;
		} else if (value instanceof CtTypeAccess) {
			return ((CtTypeAccess<?>) value).getAccessedType();
		} else if (value instanceof CtFieldRead && isMethodHeaderTypeWrapper(value.getParent())) {
			CtTypeReference typeReference = value.getFactory().createTypeReference();
			typeReference.setSimpleName(((CtFieldRead) value).getVariable().getSimpleName());
			typeReference.setParent(value.getParent());
			return typeReference;
		} else {
			return null;
		}
	}

	// TODO: why isnt this in SmPLJavaDSL
	private static boolean isMethodHeaderTypeWrapper(CtElement e) {
		return e instanceof CtInvocation && ((CtInvocation<?>) e).getExecutable().getSimpleName().equals("__MethodHeaderReturnType__");
	}
}
