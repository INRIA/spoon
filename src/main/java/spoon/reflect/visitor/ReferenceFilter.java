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
package spoon.reflect.visitor;

import spoon.reflect.reference.CtReference;

/**
 * This interface defines a filter for program element references.
 *
 * @param <T>
 * 		the type of the filtered references (an reference belonging to the
 * 		filtered element must be assignable from <code>T</code>).
 */
public interface ReferenceFilter<T extends CtReference> {
	/**
	 * Tells if the given reference matches.
	 */
	boolean matches(T reference);

	/**
	 * Gets the runtime type that corresponds to the <code>T</code> parameter
	 * (the type of the filtered references). Any reference assignable from this
	 * type is a potential match and is tested using the
	 * {@link #matches(CtReference)} method, while other references are never a
	 * match.
	 */
	Class<T> getType();
}
