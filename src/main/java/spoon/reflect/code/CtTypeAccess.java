/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import static spoon.reflect.path.CtRole.ACCESSED_TYPE;

/**
 * This code element represents a type reference usable as an expression.
 * It is used in particular for static accesses, Java 8 method references, instanceof binary expressions and ".class".
 * <pre>
 *     // access to static field
 *     java.io.PrintStream ps = System.out;
 * </pre>
 * <pre>
 *     // call to static method
 *     Class.forName("Foo")
 * </pre>
 * <pre>
 *     // method reference
 *     java.util.function.Supplier p =
 *       Object::new;
 * </pre>
 * <pre>
 *     // instanceof test
 *     boolean x = new Object() instanceof Integer // Integer is represented as an access to type Integer
 * </pre>
 * <pre>
 *     // fake field "class"
 *     Class x = Number.class
 * </pre>
 *
 * @param <A>
 * 		Access type of the expression.
 */
public interface CtTypeAccess<A> extends CtExpression<Void> {
	/**
	 * Returns type represented and contained in the type access.
	 *
	 * @return CtTypeReference.
	 */
	@PropertyGetter(role = ACCESSED_TYPE)
	CtTypeReference<A> getAccessedType();

	/**
	 * Set the accessed type.
	 *
	 * @param accessedType
	 * 		CtTypeReference.
	 */
	@PropertySetter(role = ACCESSED_TYPE)
	<C extends CtTypeAccess<A>> C setAccessedType(CtTypeReference<A> accessedType);

	/**
	 * Returns always VOID.
	 *
	 * @see #getAccessedType() to get the accessed type.
	 */
	@Override
	@DerivedProperty
	CtTypeReference<Void> getType();

	@Override
	@UnsettableProperty
	<C extends CtTypedElement> C setType(CtTypeReference<Void> type);

	/**
	 * @return {@link #getAccessedType()}.isImplicit()
	 */
	@Override
	@DerivedProperty
	boolean isImplicit();

	/**
	 * Calls {@link #getAccessedType()}.setImplicit()
	 */
	@Override
	@DerivedProperty
	<E extends CtElement> E setImplicit(boolean implicit);

	@Override
	CtTypeAccess<A> clone();
}
