/**
 * Copyright (C) 2006-2016 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
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
package spoon.reflect.code;

import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;

import java.util.List;

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
	List<CtTypeReference<?>> getActualTypeArguments();

	/**
	 * Delegate to the executable reference of the invocation.
	 *
	 * @see CtExecutableReference#getActualTypeArguments()
	 */
	@Override
	<T extends CtActualTypeContainer> T setActualTypeArguments(List<? extends CtTypeReference<?>> actualTypeArguments);

	/**
	 * Delegate to the executable reference of the invocation.
	 *
	 * @see CtExecutableReference#getActualTypeArguments()
	 */
	@Override
	<T extends CtActualTypeContainer> T addActualTypeArgument(CtTypeReference<?> actualTypeArgument);

	/**
	 * Return the type returned by the invocation. If the invocation is to a
	 * method where the returned type is a generic type, this method returns
	 * the actual type bound to this particular invocation.
	 */
	@Override
	@DerivedProperty
	CtTypeReference<T> getType();

	@Override
	CtInvocation<T> clone();
}
