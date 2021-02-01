package spoon.smpl.metavars;

import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.smpl.formula.MetavariableConstraint;

// TODO: remove special method header encoding case once we have a simpler approach for matching the method header

/**
 * A TypeConstraint restricts a metavariable binding to be a CtTypeReference. A special case exists for binding to
 * a newly created type reference indicated by the name of a CtFieldRead appearing as the single argument to an
 * invocation of the SmPL Java DSL meta element encoding a method header return type.
 */
public class TypeConstraint implements MetavariableConstraint {
    /**
     * Validate and potentially modify a value bound to a metavariable.
     *
     * @param value Value bound to metavariable
     * @return The Object that is a valid binding under the constraint, or null if the value does not match the constraint
     */
    @Override
    @SuppressWarnings("unchecked")
    public CtElement apply(CtElement value) {
        if (value instanceof CtTypeReference) {
            return value;
        } else if (value instanceof CtTypeAccess) {
            return ((CtTypeAccess<?>) value).getAccessedType();
        } else if (value instanceof CtFieldRead && isMethodHeaderTypeWrapper(value.getParent())) {
            CtTypeReference typeReference = value.getFactory().createTypeReference();
            typeReference.setSimpleName(((CtFieldRead) value).getVariable().getSimpleName());
            typeReference.setParent(value.getParent());
            return typeReference;
        }
        else {
            return null;
        }
    }

    // TODO: why isnt this in SmPLJavaDSL
    private static boolean isMethodHeaderTypeWrapper(CtElement e) {
        return e instanceof CtInvocation && ((CtInvocation<?>) e).getExecutable().getSimpleName().equals("__MethodHeaderReturnType__");
    }
}
