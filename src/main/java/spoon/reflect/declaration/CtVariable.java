/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.code.CtExpression;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.DerivedProperty;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.DEFAULT_EXPRESSION;

/**
 * This abstract element defines a variable declaration.
 */
public interface CtVariable<T> extends CtNamedElement, CtTypedElement<T>, CtModifiable {
	/**
	 * Gets the initialization expression assigned to the variable (also known
	 * as the initializer), when declared.
	 */
	@PropertyGetter(role = DEFAULT_EXPRESSION)
	CtExpression<T> getDefaultExpression();

	/*
	 * (non-Javadoc)
	 *
	 * @see spoon.reflect.declaration.CtNamedElement#getReference()
	 */
	@Override
	@DerivedProperty
	CtVariableReference<T> getReference();

	/**
	 * Sets the initialization expression assigned to the variable, when
	 * declared.
	 */
	@PropertySetter(role = DEFAULT_EXPRESSION)
	<C extends CtVariable<T>> C setDefaultExpression(CtExpression<T> assignedExpression);

	/**
	 * Returns true it the variable (field, localvariable) is jointly declared with a share type
	 * Eg int a,b;
	 * Warning: is computed on demand
	 */
	@DerivedProperty
	boolean isPartOfJointDeclaration();

	/**
	 * Returns true if field or a local variable are part of the same joint declaration.
	 * Eg:
	 * ```
	 * int a,b;
	 * int c;
	 * ```
	 * a and b are part of the same joint declaration.
	 * a and c are not part of the same joint declaration.
	 */
	boolean isInSameJointDeclarationAs(CtVariable<?> variable);
}
