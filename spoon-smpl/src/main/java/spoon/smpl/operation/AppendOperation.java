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
     * @param elementToAppend Element to append
     */
    public AppendOperation(CtElement elementToAppend) {
        this.elementToAppend = elementToAppend;
    }

    /**
     * Append the contained element to a given target element (by means of insertAfter).
     * @param category Operation is applied when category is APPEND
     * @param targetElement AST element targeted by operation
     * @param bindings Metavariable bindings to use
     */
    @Override
    public void accept(OperationFilter category, CtElement targetElement, Map<String, Object> bindings) {
        if (category != OperationFilter.APPEND) {
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
