package spoon.smpl;

import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * Default implementation of DeleteOperation.
 */
public class DeleteOperationImpl implements DeleteOperation {
    /**
     * Delete the target element from its surrounding AST context.
     * @param targetElement Element to delete
     * @param bindings Irrelevant
     */
    @Override
    public void accept(CtElement targetElement, Map<String, Object> bindings) {
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
