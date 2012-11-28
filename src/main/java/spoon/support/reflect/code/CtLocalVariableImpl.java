/*
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */

package spoon.support.reflect.code;

import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtLocalVariableImpl<T> extends CtStatementImpl implements
        CtLocalVariable<T> {
    private static final long serialVersionUID = 1L;

    CtExpression<T> defaultExpression;

    String docComment;

    Set<ModifierKind> modifiers = new TreeSet<ModifierKind>();

    String name;

    CtTypeReference<T> type;

    public void accept(CtVisitor visitor) {
        visitor.visitCtLocalVariable(this);
    }

    public CtExpression<T> getDefaultExpression() {
        return defaultExpression;
    }

    @Override
    public String getDocComment() {
        return docComment;
    }

    public Set<ModifierKind> getModifiers() {
        return modifiers;
    }

    public CtLocalVariableReference<T> getReference() {
        return getFactory().Code().createLocalVariableReference(this);
    }

    public String getSimpleName() {
        return name;
    }

    public CtTypeReference<T> getType() {
        return type;
    }

    public ModifierKind getVisibility() {
        if (getModifiers().contains(ModifierKind.PUBLIC)) {
            return ModifierKind.PUBLIC;
        }
        if (getModifiers().contains(ModifierKind.PROTECTED)) {
            return ModifierKind.PROTECTED;
        }
        if (getModifiers().contains(ModifierKind.PRIVATE)) {
            return ModifierKind.PRIVATE;
        }
        return null;
    }

    public boolean hasModifier(ModifierKind modifier) {
        return modifiers.contains(modifier);
    }

    public void setDefaultExpression(CtExpression<T> defaultExpression) {
        this.defaultExpression = defaultExpression;
		defaultExpression.setParent(this);
    }

    @Override
    public void setDocComment(String docComment) {
        this.docComment = docComment;
    }

    public void setModifiers(Set<ModifierKind> modifiers) {
        this.modifiers = modifiers;
    }

    public void setSimpleName(String simpleName) {
        this.name = simpleName;
    }

    public void setType(CtTypeReference<T> type) {
        this.type = type;
    }

    public void setVisibility(ModifierKind visibility) {
        getModifiers().remove(ModifierKind.PUBLIC);
        getModifiers().remove(ModifierKind.PROTECTED);
        getModifiers().remove(ModifierKind.PRIVATE);
        getModifiers().add(visibility);
    }

}
