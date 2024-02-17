/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtReceiverParameter;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.CtModifierHandler;

import java.util.Set;

public class CtReceiverParameterImpl extends CtNamedElementImpl implements CtReceiverParameter   {

    @MetamodelPropertyField(role = CtRole.TYPE)
    private CtTypeReference<Object> type;
    @MetamodelPropertyField(role = CtRole.MODIFIER)
    private final CtModifierHandler modifierHandler = new CtModifierHandler(this);
    @MetamodelPropertyField(role = CtRole.IS_SHADOW)
    boolean isShadow;
    @Override
    public Set<ModifierKind> getModifiers() {
        return modifierHandler.getModifiers();
    }

    @Override
    public boolean hasModifier(ModifierKind modifier) {
        return modifierHandler.hasModifier(modifier);
    }

    @Override
    public <C extends CtModifiable> C setModifiers(Set<ModifierKind> modifiers) {
        modifierHandler.setModifiers(modifiers);
        return (C) this;
    }

    @Override
    public <C extends CtModifiable> C addModifier(ModifierKind modifier) {
        modifierHandler.addModifier(modifier);
        return (C) this;
    }

    @Override
    public <C extends CtModifiable> C removeModifier(ModifierKind modifier) {
        modifierHandler.removeModifier(modifier);
        return (C) this;
    }

    @Override
    public <C extends CtModifiable> C setVisibility(ModifierKind visibility) {
        // CtReceiverParameter has no visibility
        return (C) this;
    }

    @Override
    public ModifierKind getVisibility() {
        return null;
    }

    @Override
    public Set<CtExtendedModifier> getExtendedModifiers() {
        return null;
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return modifierHandler.isFinal();
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public boolean isVolatile() {
        return false;
    }

    @Override
    public boolean isSynchronized() {
        return false;
    }

    @Override
    public boolean isNative() {
        return false;
    }

    @Override
    public boolean isStrictfp() {
        return false;
    }

    @Override
    public CtExpression<Object> getDefaultExpression() {
        return null;
    }

    @Override
    public <C extends CtVariable<Object>> C setDefaultExpression(CtExpression<Object> assignedExpression) {
        return (C) this;
    }

    @Override
    public boolean isPartOfJointDeclaration() {
        return false;
    }

    @Override
    public <T extends CtModifiable> T setExtendedModifiers(Set<CtExtendedModifier> extendedModifiers) {
        this.modifierHandler.setExtendedModifiers(extendedModifiers);
        return (T) this;
    }

    public boolean isShadow() {
        return isShadow;
    }


    @Override
    public <C extends CtShadowable> C setShadow(boolean isShadow) {
        getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_SHADOW, isShadow, this.isShadow);
        this.isShadow = isShadow;
        return (C) this;
    }
    @Override
    public CtTypeReference<Object> getType() {
        return type;
    }

    @Override
    public <C extends CtTypedElement> C setType(CtTypeReference type) {
        if (type != null) {
            type.setParent(this);
        }
        getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.TYPE, type, this.type);
        this.type = type;
        return (C) this;
    }

    @Override
    public void accept(CtVisitor visitor) {
    //TODO: implement
    }

    @Override
    public CtVariableReference<Object> getReference() {

        //TODO: implement
        return null;
    }
}
