package spoon.smpl;

import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * Default implementation of AppendOperation.
 */
public class AppendOperationImpl implements AppendOperation {
    /**
     * Create a new AppendOperation given the AST element that should be appended.
     * @param elementToAppend Element to append
     */
    public AppendOperationImpl(CtElement elementToAppend) {
        this.elementToAppend = elementToAppend;
    }

    /**
     * Append the contained element to a given target element (by means of insertAfter).
     * @param targetElement Target element to append to
     * @param bindings Metavariable bindings to use
     */
    @Override
    public void accept(CtElement targetElement, Map<String, Object> bindings) {
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

    /**
     * Element to append.
     */
    public CtElement elementToAppend;
}
