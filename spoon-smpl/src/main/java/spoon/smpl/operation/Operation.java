package spoon.smpl.operation;

import spoon.reflect.declaration.CtElement;
import spoon.smpl.TriConsumer;

import java.util.Map;


/**
 * An Operation is a (generally non-pure) function that takes a CtElement and a map of
 * metavariable bindings and then possibly inflicts some mutation on the CtElement
 * or its parent structure / environment.
 */
public interface Operation extends TriConsumer<OperationFilter, CtElement, Map<String, Object>> {
    /**
     * Apply the operation.
     *
     * The Operation should inspect the 'category' parameter to validate whether or not
     * the operation should be applied. For example, an Operation that appends elements
     * to an anchor element should probably only apply their effect if the call comes
     * with the OperationFilter.APPEND category value.
     *
     * @param category Category to match
     * @param targetElement AST element targeted by operation
     * @param bindings Metavariable bindings to use
     */
    @Override
    public void accept(OperationFilter category, CtElement targetElement, Map<String, Object> bindings);
}
