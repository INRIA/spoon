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
import spoon.reflect.visitor.Filter;

/**
 * This class defines an abstract reference filter that needs to be subclassed
 * in order to define the matching criteria.
 *
 * @param <T>
 * 		the type of the reference to be matched
 */
public abstract class AbstractReferenceFilter<T extends CtReference> extends AbstractFilter<T> implements Filter<T> {

	/**
	 * Creates a reference filter with the type of the potentially matching
	 * references.
	 */
	@SuppressWarnings("unchecked")
	public AbstractReferenceFilter(Class<? super T> type) {
		super(type);
	}

	/**
	 * Creates a filter with the type computed by reflection from the matches method parameter
	 */
	public AbstractReferenceFilter() {
	}
}
