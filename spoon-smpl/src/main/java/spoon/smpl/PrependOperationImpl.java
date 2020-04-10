package spoon.smpl;

import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * Default implementation of PrependOperation.
 */
public class PrependOperationImpl implements PrependOperation {
    /**
     * Create a new PrependOperation given the AST element that should be prepended.
     * @param elementToPrepend Element to prepend
     */
    public PrependOperationImpl(CtElement elementToPrepend) {
        this.elementToPrepend = elementToPrepend;
    }

    /**
     * Prepend the contained element to a given target element (by means of insertBefore).
     * @param targetElement Target element to append to
     * @param bindings Metavariable bindings to use
     */
    @Override
    public void accept(CtElement targetElement, Map<String, Object> bindings) {
        if (targetElement instanceof CtStatement) {
            CtStatement stmt = (CtStatement) targetElement;
            stmt.insertBefore((CtStatement) Substitutor.apply(elementToPrepend, bindings));
        } else {
            throw new IllegalArgumentException("cannot prepend to " + targetElement.getClass().toString());
        }
    }

    @Override
    public String toString() {
        return "Append(" + elementToPrepend.toString() + ")";
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
