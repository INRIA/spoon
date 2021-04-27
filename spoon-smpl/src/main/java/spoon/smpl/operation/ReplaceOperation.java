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
import spoon.smpl.Substitutor;

import java.util.Map;

/**
 * A ReplaceOperation is an Operation that replaces a target element.
 */
public class ReplaceOperation implements Operation {
	/**
	 * Create a new ReplacementOperation given the AST element that should replace the target element.
	 *
	 * @param replacementElement Replacement for target element
	 */
	public ReplaceOperation(CtElement replacementElement) {
		this.replacementElement = replacementElement;
	}

	/**
	 * Replace the target element.
	 *
	 * @param category      Operation is applied when category is DELETE
	 * @param targetElement AST element targeted by operation
	 * @param bindings      Metavariable bindings to use
	 */
	@Override
	public void accept(OperationCategory category, CtElement targetElement, Map<String, Object> bindings) {
		if (category != OperationCategory.DELETE) {
			return;
		}

		targetElement.replace(Substitutor.apply(replacementElement, bindings));
	}

	@Override
	public String toString() {
		return "Replace(" + replacementElement.toString() + ")";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof ReplaceOperation && other.hashCode() == hashCode());
	}

	/**
	 * Element that should replace the target element.
	 */
	public CtElement replacementElement;
}
