/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.List;

import static spoon.reflect.path.CtRole.TYPE;

/**
 * Defined an element with several types.
 */
public interface CtMultiTypedElement extends CtElement {
	/**
	 * Adds a type for the element.
	 */
	@PropertySetter(role = TYPE)
	<T extends CtMultiTypedElement> T addMultiType(CtTypeReference<?> ref);

	/**
	 * Removes a type for the element.
	 */
	@PropertySetter(role = TYPE)
	boolean removeMultiType(CtTypeReference<?> ref);

	/**
	 * Gets all types of the element.
	 */
	@PropertyGetter(role = TYPE)
	List<CtTypeReference<?>> getMultiTypes();

	/**
	 * Adds a type for the element.
	 */
	@PropertySetter(role = TYPE)
	<T extends CtMultiTypedElement> T setMultiTypes(List<CtTypeReference<?>> types);
}
