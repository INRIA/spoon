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
package spoon.reflect.declaration;

import spoon.reflect.reference.CtReference;
import spoon.support.DerivedProperty;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import static spoon.reflect.path.CtRole.NAME;

/**
 * Declares an element that has a name (a class, a method, a variable, etc).
 * Hence, all subclasses of CtNamedElement are in package "declaration".
 * Note that references don't define elements, hence are not under CtNamedElement
 * even if they also have methods set/getSimpleName.
 */
public interface CtNamedElement extends CtElement {
	/**
	 * Returns the simple (unqualified) name of this element.
	 */
	@PropertyGetter(role = NAME)
	String getSimpleName();

	/**
	 * Sets the simple (unqualified) name of this element.
	 */
	@PropertySetter(role = NAME)
	<T extends CtNamedElement> T setSimpleName(String simpleName);

	/**
	 * Returns the corresponding reference.
	 */
	@DerivedProperty
	CtReference getReference();

	@Override
	CtNamedElement clone();
}
