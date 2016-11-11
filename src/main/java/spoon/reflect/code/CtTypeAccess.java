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

import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;

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
	CtTypeReference<A> getAccessedType();

	/**
	 * Set the accessed type.
	 *
	 * @param accessedType
	 * 		CtTypeReference.
	 */
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
	CtTypeAccess<A> clone();
}
