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
package spoon.reflect.declaration;

import spoon.reflect.code.CtRHSReceiver;
import spoon.reflect.reference.CtFieldReference;

/**
 * This element defines a field declaration.
 */
public interface CtField<T> extends CtVariable<T>, CtTypeMember, CtRHSReceiver<T> {

	/**
	 * The separator for a string representation of a field.
	 */
	String FIELD_SEPARATOR = "#";

	/*
	 * (non-Javadoc)
	 *
	 * @see spoon.reflect.declaration.CtNamedElement#getReference()
	 */
	CtFieldReference<T> getReference();

	/**
	 * Replaces this element by another one.
	 */
	<R extends T> void replace(CtField<R> element);
}
