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

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * An Operation that deletes a given element.
 */
public class DeleteOperation implements Operation {
	/**
	 * Delete the target element from its surrounding AST context.
	 *
	 * @param category      Operation is applied when category is DELETE
	 * @param targetElement AST element targeted by operation
	 * @param bindings      Irrelevant
	 */
	@Override
	public void accept(OperationCategory category, CtElement targetElement, Map<String, Object> bindings) {
		if (category != OperationCategory.DELETE) {
			return;
		}

		// Move inner elements out from branches of if-then-else-statements
		if (targetElement instanceof CtIf) {
			CtIf ifstmt = (CtIf) targetElement;

			if (ifstmt.getThenStatement() instanceof CtBlock<?>) {
				ifstmt.insertBefore((CtStatementList) ifstmt.getThenStatement());
			} else {
				ifstmt.insertBefore((CtStatement) ifstmt.getThenStatement());
			}

			if (ifstmt.getElseStatement() != null) {
				if (ifstmt.getElseStatement() instanceof CtBlock<?>) {
					ifstmt.insertBefore((CtStatementList) ifstmt.getElseStatement());
				} else {
					ifstmt.insertBefore((CtStatement) ifstmt.getElseStatement());
				}
			}
		}

		targetElement.delete();
	}

	@Override
	public String toString() {
		return "Delete";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof DeleteOperation && other.hashCode() == hashCode());
	}
}
