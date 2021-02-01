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
