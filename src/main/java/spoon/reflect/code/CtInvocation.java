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

import spoon.reflect.reference.CtActualTypeContainer;
import spoon.reflect.reference.CtTypeReference;

/**
 * This code element defines a concrete invocation.
 *
 * @param <T>
 * 		Return type of this invocation
 */
public interface CtInvocation<T> extends CtAbstractInvocation<T>, CtStatement, CtTargetedExpression<T, CtExpression<?>>, CtActualTypeContainer {
	/**
	 * Return the type returned by the invocation. If the invocation is to a
	 * method where the returned type is a generic type, this method returns
	 * the actual type bound to this particular invocation.
	 */
	@Override
	CtTypeReference<T> getType();
}
