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
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.chain.CtFunction;

import java.util.List;

/**
 * This class provides some useful methods to retrieve program elements and
 * reference through a {@link spoon.reflect.visitor.CtScanner}-based deep
 * search. It uses the {@link spoon.reflect.visitor.Filter} facility to select the right
 * elements or references.
 */
public abstract class Query {

	private Query() {
	}

	/**
	 * Within a given factory, returns all the program elements that match the
	 * filter.
	 *
	 * @param <E>
	 * 		the type of the sought program elements
	 * @param factory
	 * 		the factory that contains the elements where to recursive
	 * 		search on
	 * @param filter
	 * 		the filter which defines the matching criteria
	 */
	public static <E extends CtElement> List<E> getElements(Factory factory,
															Filter<E> filter) {
		return getElements(factory.Package().getRootPackage(), filter);
	}

	/**
	 * Returns all the program elements that match the filter starting from the given rootElement.
	 * Use {@link spoon.reflect.visitor.chain.CtQueryable#map(CtFunction)} if you need more control on the scanning context of the Filter.
	 *
	 * @param <E>
	 * 		the type of the sought program elements
	 * @param rootElement
	 * 		the element to start the recursive search on
	 * @param filter
	 * 		the filter which defines the matching criteria
	 */
	public static <E extends CtElement> List<E> getElements(
			CtElement rootElement, Filter<E> filter) {
		return rootElement.filterChildren(filter).list();
	}

	/**
	 * Returns all the program element references that match the filter.
	 *
	 * @param <T>
	 * 		the type of the sought program element references
	 * @param rootElement
	 * 		the element to start the recursive search on
	 * @param filter
	 * 		the filter which defines the matching criteria
	 *
	 * @deprecated use {@link #getElements(CtElement, Filter)} instead.
	 */
	@Deprecated
	public static <T extends CtReference> List<T> getReferences(
			CtElement rootElement, Filter<T> filter) {
		return getElements(rootElement, filter);
	}

	/**
	 * Within a given factory, returns all the program element references that
	 * match the filter.
	 *
	 * @param <R>
	 * 		the type of the sought program element references
	 * @param factory
	 * 		the factory that contains the references where to recursive
	 * 		search on
	 * @param filter
	 * 		the filter which defines the matching criteria
	 * @deprecated use {@link #getElements(CtElement, Filter)} instead.
	 */
	@Deprecated
	public static <R extends CtReference> List<R> getReferences(
			Factory factory, Filter<R> filter) {
		return getElements(factory, filter);
	}

}
