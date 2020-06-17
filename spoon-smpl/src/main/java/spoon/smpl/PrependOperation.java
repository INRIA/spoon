package spoon.smpl;

import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * Default implementation of PrependOperation.
 */
public class PrependOperation implements Operation {
    /**
     * Create a new PrependOperation given the AST element that should be prepended.
     * @param elementToPrepend Element to prepend
     */
    public PrependOperation(CtElement elementToPrepend) {
        this.elementToPrepend = elementToPrepend;
    }

    /**
     * Prepend the contained element to a given target element (by means of insertBefore).
     *
     * @param category Operation is applied when category is APPEND
     * @param targetElement AST element targeted by operation
     * @param bindings Metavariable bindings to use
     */
    @Override
    public void accept(OperationFilter category, CtElement targetElement, Map<String, Object> bindings) {
        if (category != OperationFilter.PREPEND) {
            return;
        }

        if (targetElement instanceof CtStatement) {
            CtStatement stmt = (CtStatement) targetElement;
            stmt.insertBefore((CtStatement) Substitutor.apply(elementToPrepend, bindings));
        } else {
            throw new IllegalArgumentException("cannot prepend to " + targetElement.getClass().toString());
        }
    }

    @Override
    public String toString() {
        return "Prepend(" + elementToPrepend.toString() + ")";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof PrependOperation && other.hashCode() == hashCode());
    }

    /**
     * Element to prepend.
     */
    public CtElement elementToPrepend;
}
