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

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

/**
 * Defines an abstract filter based on matching on the element types.
 *
 * Not necessary in simple cases thanks to the use of runtime reflection.
 */
public abstract class AbstractFilter<T extends CtElement> implements Filter<T> {

	private Class<T> type;

	/**
	 * Creates a filter with the type of the potentially matching elements.
	 */
	@SuppressWarnings("unchecked")
	public AbstractFilter(Class<? super T> type) {
		this.type = (Class<T>) type;
	}

	@Override
	public boolean matches(T element) {
		return type.isAssignableFrom(element.getClass());
	}

}
