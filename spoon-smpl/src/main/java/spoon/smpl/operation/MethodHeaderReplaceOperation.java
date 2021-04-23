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
package spoon.smpl.operation;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.smpl.Substitutor;

import java.util.Map;

/**
 * A MethodHeaderReplaceOperation is an Operation that replaces the method header (return type,
 * name and parameter list).
 */
public class MethodHeaderReplaceOperation implements Operation {
	/**
	 * Create a new MethodHeaderReplaceOperation given the method element having the target header.
	 *
	 * @param replacementElement Replacement for target element
	 */
	public MethodHeaderReplaceOperation(CtMethod<?> replacementElement) {
		this.replacementElement = replacementElement;
	}

	/**
	 * Replace the appropriate header sub-elements of the given method element.
	 *
	 * @param category      Operation is applied when category is DELETE
	 * @param targetElement Method targeted by operation
	 * @param bindings      Metavariable bindings to use
	 */
	@Override
	@SuppressWarnings({"unchecked"})
	public void accept(OperationCategory category, CtElement targetElement, Map<String, Object> bindings) {
		if (category != OperationCategory.DELETE) {
			return;
		}

		if (!(targetElement instanceof CtMethod)) {
			throw new IllegalArgumentException("cannot apply a MethodHeaderReplaceOperation to " + targetElement.getClass().toString());
		}

		CtMethod<?> method = (CtMethod<?>) targetElement;

		// TODO: reenable this once we can match on access modifiers
		// method.setModifiers(replacementElement.getModifiers());

		method.setType((CtTypeReference) Substitutor.apply(replacementElement.getType(), bindings));
		method.setSimpleName(replacementElement.getSimpleName());

		// TODO: metavar substitutions for method parameters
		method.setParameters(replacementElement.getParameters());
	}

	@Override
	public String toString() {
		return "SetHeader(" + replacementElement.toString().substring(0, replacementElement.toString().indexOf('{')).strip() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof MethodHeaderReplaceOperation && other.hashCode() == hashCode());
	}

	/**
	 * Method equipped with header that should replace the header of a target element.
	 */
	public CtMethod<?> replacementElement;
}
