/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.EXECUTABLE_REF;

/**
 * This abstract code element defines an expression which represents an executable reference.
 *
 * * Example:
 * <pre>
 *     java.util.function.Supplier p =
 *       Object::new;
 * </pre>
 *
 * @param <T>
 * 		Each executable references are typed by an interface with one method. This generic type
 * 		correspond to this concept.
 * @param <E>
 * 		Correspond of <code>Type</code> in <code>Type::method</code>.
 */
public interface CtExecutableReferenceExpression<T, E extends CtExpression<?>> extends CtTargetedExpression<T, E> {
	/**
	 * Gets the executable referenced by the expression.
	 */
	@PropertyGetter(role = EXECUTABLE_REF)
	CtExecutableReference<T> getExecutable();

	/**
	 * Sets the executable will be referenced by the expression.
	 */
	@PropertySetter(role = EXECUTABLE_REF)
	<C extends CtExecutableReferenceExpression<T, E>> C setExecutable(CtExecutableReference<T> executable);

	@Override
	CtExecutableReferenceExpression<T, E> clone();
}
