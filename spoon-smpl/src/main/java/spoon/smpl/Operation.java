package spoon.smpl;

import spoon.reflect.declaration.CtElement;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * An Operation is a (generally non-pure) function that takes a CtElement and a map of
 * metavariable bindings and then possibly inflicts some mutation on the CtElement.
 */
public interface Operation extends BiConsumer<CtElement, Map<String, Object>> {
    /**
     * Apply the operation.
     * @param element Element to operate on
     * @param bindings Metavariable bindings to use
     */
    @Override
    public void accept(CtElement element, Map<String, Object> bindings);
}
