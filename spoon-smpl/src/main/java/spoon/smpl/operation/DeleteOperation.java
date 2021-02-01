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
