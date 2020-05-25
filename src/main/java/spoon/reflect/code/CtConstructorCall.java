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
import spoon.support.DefaultCoreFactory;
import spoon.support.DerivedProperty;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.List;

import static spoon.reflect.path.CtRole.TYPE;
import static spoon.reflect.path.CtRole.TYPE_ARGUMENT;

/**
 * This code element represents a constructor call.
 *
 * Example:<pre>
 *     new Object();
 * </pre>
 *
 * To build a constructor call, see {@link DefaultCoreFactory#createConstructorCall()}.
 *
 * @param <T>
 * 		created type
 */
public interface CtConstructorCall<T> extends CtTargetedExpression<T, CtExpression<?>>, CtAbstractInvocation<T>, CtStatement, CtActualTypeContainer {
	/**
	 * Delegate to the executable reference of the constructor call.
	 *
	 * @see CtExecutableReference#getActualTypeArguments()
	 */
	@Override
	@DerivedProperty
	@PropertyGetter(role = TYPE_ARGUMENT)
	List<CtTypeReference<?>> getActualTypeArguments();

	/**
	 * Delegate to the executable reference of the constructor call.
	 *
	 * @see CtExecutableReference#getActualTypeArguments()
	 */
	@Override
	@PropertySetter(role = TYPE_ARGUMENT)
	<T extends CtActualTypeContainer> T setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments);

	/**
	 * Delegate to the executable reference of the constructor call.
	 *
	 * @see CtExecutableReference#getActualTypeArguments()
	 */
	@Override
	@PropertySetter(role = TYPE_ARGUMENT)
	<T extends CtActualTypeContainer> T addActualTypeArgument(CtTypeReference<?> actualTypeArgument);

	@Override
	CtConstructorCall<T> clone();

	@Override
	@DerivedProperty
	@PropertyGetter(role = TYPE)
	CtTypeReference<T> getType();
}
