package spoon.smpl.operation;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.smpl.Substitutor;

import java.util.Map;

/**
 * A MethodHeaderReplaceOperation is an Operation that replaces the method header (return type,
 * name and parameter list).
 */
public class MethodHeaderReplaceOperation implements Operation {
    /**
     * Create a new MethodHeaderReplaceOperation given the method element having the target header.
     *
     * @param replacementElement Replacement for target element
     */
    public MethodHeaderReplaceOperation(CtMethod<?> replacementElement) {
        this.replacementElement = replacementElement;
    }

    /**
     * Replace the appropriate header sub-elements of the given method element.
     *
     * @param category Operation is applied when category is DELETE
     * @param targetElement Method targeted by operation
     * @param bindings Metavariable bindings to use
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public void accept(OperationFilter category, CtElement targetElement, Map<String, Object> bindings) {
        if (category != OperationFilter.DELETE) {
            return;
        }

        if (!(targetElement instanceof CtMethod)) {
            throw new IllegalArgumentException("cannot apply a MethodHeaderReplaceOperation to " + targetElement.getClass().toString());
        }

        CtMethod<?> method = (CtMethod<?>) targetElement;

        // TODO: reenable this once we can match on access modifiers
        // method.setModifiers(replacementElement.getModifiers());

        method.setType((CtTypeReference) Substitutor.apply(replacementElement.getType(), bindings));
        method.setSimpleName(replacementElement.getSimpleName());

        // TODO: metavar substitutions for method parameters
        method.setParameters(replacementElement.getParameters());
    }

    @Override
    public String toString() {
        return "SetHeader(" + replacementElement.toString().substring(0, replacementElement.toString().indexOf('{')).strip() + ")";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof MethodHeaderReplaceOperation && other.hashCode() == hashCode());
    }

    /**
     * Method equipped with header that should replace the header of a target element.
     */
    public CtMethod<?> replacementElement;
}
