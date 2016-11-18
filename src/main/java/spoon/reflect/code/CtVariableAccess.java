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
import spoon.reflect.reference.CtVariableReference;
import spoon.support.DerivedProperty;

/**
 * This code element defines an access to a variable (read and write).
 *
 * If you process this element, keep in mind that you will process var++ too.
 *
 * @param <T>
 * 		type of the variable
 */
public interface CtVariableAccess<T> extends CtExpression<T> {
	/**
	 * Gets the reference to the accessed variable.
	 */
	CtVariableReference<T> getVariable();

	/**
	 * Sets the reference to the accessed variable.
	 */
	<C extends CtVariableAccess<T>> C setVariable(CtVariableReference<T> variable);

	@Override
	CtVariableAccess<T> clone();

	@Override
	@DerivedProperty
	CtTypeReference<T> getType();
}
