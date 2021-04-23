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

import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.Substitutor;

import java.util.Map;

/**
 * An Operation that appends an element to a given anchor element.
 */
public class AppendOperation implements Operation {
	/**
	 * Create a new AppendOperation given the AST element that should be appended.
	 *
	 * @param elementToAppend Element to append
	 */
	public AppendOperation(CtElement elementToAppend) {
		this.elementToAppend = elementToAppend;
	}

	/**
	 * Append the contained element to a given target element (by means of insertAfter).
	 *
	 * @param category      Operation is applied when category is APPEND
	 * @param targetElement AST element targeted by operation
	 * @param bindings      Metavariable bindings to use
	 */
	@Override
	public void accept(OperationCategory category, CtElement targetElement, Map<String, Object> bindings) {
		if (category != OperationCategory.APPEND) {
			return;
		}

		if (targetElement instanceof CtStatement) {
			CtStatement stmt = (CtStatement) targetElement;
			stmt.insertAfter((CtStatement) Substitutor.apply(elementToAppend, bindings));
		} else {
			throw new IllegalArgumentException("cannot append to " + targetElement.getClass().toString());
		}
	}

	@Override
	public String toString() {
		return "Append(" + elementToAppend.toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof AppendOperation && other.hashCode() == hashCode());
	}

	/**
	 * Element to append.
	 */
	public CtElement elementToAppend;
}
