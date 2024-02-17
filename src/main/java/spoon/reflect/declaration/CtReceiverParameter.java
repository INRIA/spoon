/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;


import spoon.reflect.code.CtExpression;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.CtExtendedModifier;

import java.util.Set;

public interface CtReceiverParameter extends CtVariable<Object>, CtShadowable {

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
