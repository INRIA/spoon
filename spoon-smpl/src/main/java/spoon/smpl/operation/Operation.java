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
import spoon.smpl.TriConsumer;

import java.util.Map;


/**
 * An Operation is a (generally non-pure) function that takes a CtElement and a map of
 * metavariable bindings and then possibly inflicts some mutation on the CtElement
 * or its parent structure / environment.
 */
public interface Operation extends TriConsumer<OperationCategory, CtElement, Map<String, Object>> {
	/**
	 * Apply the operation.
	 * <p>
	 * The Operation should inspect the 'category' parameter to validate whether or not
	 * the operation should be applied. For example, an Operation that appends elements
	 * to an anchor element should probably only apply their effect if the call comes
	 * with the OperationCategory.APPEND category value.
	 *
	 * @param category      Category to match
	 * @param targetElement AST element targeted by operation
	 * @param bindings      Metavariable bindings to use
	 */
	@Override
	void accept(OperationCategory category, CtElement targetElement, Map<String, Object> bindings);
}
