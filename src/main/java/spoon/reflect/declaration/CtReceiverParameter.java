package spoon.reflect.declaration;


import spoon.reflect.code.CtExpression;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.CtExtendedModifier;

import java.util.Set;

public interface CtReceiverParameter extends CtVariable<Object>, CtShadowable {

    /**
     * Gets the executable that is the parent declaration of this receiver parameter declaration.
     *
     * (Overriding the return type)
     */
    @Override
    @DerivedProperty
    CtExecutable<?> getParent();

    @Override
    @UnsettableProperty
    <C extends CtVariable<Object>> C setDefaultExpression(CtExpression<Object> assignedExpression);

    @Override
    @UnsettableProperty
    <C extends CtModifiable> C setExtendedModifiers(Set<CtExtendedModifier> extendedModifiers);


    @Override
    @UnsettableProperty
    <T extends CtNamedElement> T setSimpleName(String simpleName);
}
