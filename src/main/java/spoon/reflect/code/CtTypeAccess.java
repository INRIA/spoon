/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

/**
 * This code element defines an access to a type.
 *
 * In Java, it is generally of the form: <code>Type</code>.
 *
 * For example, you can have:
 *
 * <pre>
 *     Type.staticField
 *     Type.staticMethod()
 *     Type::method
 *     t instanceof Type
 *     Type.class
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
	CtTypeReference<Void> getType();
}
