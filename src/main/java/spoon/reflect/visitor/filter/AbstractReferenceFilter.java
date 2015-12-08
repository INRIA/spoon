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
package spoon.reflect.visitor.filter;

import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.ReferenceFilter;

/**
 * This class defines an abstract reference filter that needs to be subclassed
 * in order to define the matching criteria.
 *
 * @param <T>
 * 		the type of the reference to be matched
 * @see spoon.reflect.visitor.ReferenceFilter#matches(CtReference)
 */
public abstract class AbstractReferenceFilter<T extends CtReference>
		implements ReferenceFilter<T> {

	Class<T> type;

	/**
	 * Creates a reference filter with the type of the potentially matching
	 * references.
	 */
	// TODO: INFER TYPE BY INTROSPECTION
	@SuppressWarnings("unchecked")
	public AbstractReferenceFilter(Class<?> type) {
		this.type = (Class<T>) type;
	}

	public Class<T> getType() {
		return type;
	}

}
