package spoon.smpl.metavars;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtVariableReference;
import spoon.smpl.formula.MetavariableConstraint;

/**
 * An TypedIdentifierConstraint behaves like an IdentifierConstraint extended with the
 * additional constraint on the data type of the bound variable.
 */
public class TypedIdentifierConstraint implements MetavariableConstraint {
    public TypedIdentifierConstraint(String requiredType) {
        this.innerConstraint = new IdentifierConstraint();
        this.requiredType = requiredType;
    }

    @Override
    public CtElement apply(CtElement value) {
        CtElement validIdentifier = innerConstraint.apply(value);

        if (validIdentifier instanceof CtVariableReference
            && ((CtVariableReference<?>) validIdentifier).getType().getSimpleName().equals(requiredType)) {
            return validIdentifier;
        } else {
            return null;
        }
    }

    private IdentifierConstraint innerConstraint;
    private String requiredType;
}
