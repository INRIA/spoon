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
package spoon.reflect.visitor.filter;

import spoon.reflect.reference.CtReference;

/**
 * This simple filter matches all the references of a given type.
 *
 * @param <T>
 * 		the type of the reference to be matched
 *
 */
public class ReferenceTypeFilter<T extends CtReference> extends TypeFilter<T> {

	/**
	 * Creates the filter.
	 *
	 * @param type
	 * 		the type that matches
	 */
	public ReferenceTypeFilter(Class<? super T> type) {
		super(type);
	}
}
