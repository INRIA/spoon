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

import spoon.reflect.reference.CtExecutableReference;

/**
 * This abstract code element defines an expression which represents an executable reference.
 *
 * In Java, it is generally of the form: <code>Type::method</code>.
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
	CtExecutableReference<T> getExecutable();

	/**
	 * Sets the executable will be referenced by the expression.
	 */
	<C extends CtExecutableReferenceExpression<T, E>> C setExecutable(CtExecutableReference<T> executable);
}
