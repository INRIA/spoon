/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.List;

import static spoon.reflect.path.CtRole.TYPE;
import static spoon.reflect.path.CtRole.TYPE_ARGUMENT;

/**
 * This code element defines a concrete invocation.
 *
 * Example:
 * <pre>
 *     // invocation of method println
 *     // the target is "System.out"
 *     System.out.println("foo");
 * </pre>
 *
 * @param <T>
 * 		Return type of this invocation
 */
public interface CtInvocation<T> extends CtAbstractInvocation<T>, CtStatement, CtTargetedExpression<T, CtExpression<?>>, CtActualTypeContainer {
	/**
	 * Delegate to the executable reference of the invocation.
	 *
	 * @see CtExecutableReference#getActualTypeArguments()
	 */
	@Override
	@DerivedProperty
	@PropertyGetter(role = TYPE_ARGUMENT)
	List<CtTypeReference<?>> getActualTypeArguments();

	/**
	 * Delegate to the executable reference of the invocation.
	 *
	 * @see CtExecutableReference#getActualTypeArguments()
	 */
	@Override
	@PropertySetter(role = TYPE_ARGUMENT)
	<T extends CtActualTypeContainer> T setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments);

	/**
	 * Delegate to the executable reference of the invocation.
	 *
	 * @see CtExecutableReference#getActualTypeArguments()
	 */
	@Override
	@PropertySetter(role = TYPE_ARGUMENT)
	<T extends CtActualTypeContainer> T addActualTypeArgument(CtTypeReference<?> actualTypeArgument);

	/**
	 * Return the type returned by the invocation. If the invocation is to a
	 * method where the returned type is a generic type, this method returns
	 * the actual type bound to this particular invocation.
	 */
	@Override
	@DerivedProperty
	@PropertyGetter(role = TYPE)
	CtTypeReference<T> getType();

	@Override
	CtInvocation<T> clone();
}
