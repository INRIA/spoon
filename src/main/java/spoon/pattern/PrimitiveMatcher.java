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
package spoon.pattern;

/**
 * Defines API of a primitive matcher - matcher for single target object
 */
public interface PrimitiveMatcher extends RepeatableMatcher {

	/**
	 * @param target - to be matched element
	 * @param parameters will receive the matching parameter values
	 * @return true if `element` matches with pattern of this matcher
	 */
	ParameterValueProvider matchTarget(Object target, ParameterValueProvider parameters);
}
