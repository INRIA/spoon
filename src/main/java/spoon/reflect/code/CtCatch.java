/**
 * Copyright (C) 2006-2018 INRIA and contributors
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

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.BODY;
import static spoon.reflect.path.CtRole.PARAMETER;


/**
 * This code element defines a <code>catch</code> of a <code>try</code>.
 *
 * @see spoon.reflect.code.CtTry
 */
public interface CtCatch extends CtCodeElement, CtBodyHolder {

	/**
	 * Gets the catch's parameter (a throwable).
	 */
	@PropertyGetter(role = PARAMETER)
	CtCatchVariable<? extends Throwable> getParameter();

	/**
	 * Sets the catch's parameter (a throwable).
	 */
	@PropertySetter(role = PARAMETER)
	<T extends CtCatch> T setParameter(CtCatchVariable<? extends Throwable> parameter);

	/**
	 * Gets the catch's body.
	 */
	@Override
	@PropertyGetter(role = BODY)
	CtBlock<?> getBody();

	@Override
	CtCatch clone();
}
