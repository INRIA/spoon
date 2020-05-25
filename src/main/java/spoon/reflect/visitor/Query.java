/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.chain.CtFunction;
import spoon.reflect.visitor.filter.TypeFilter;

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
	 */
	public static <T extends CtReference> List<T> getReferences(
			CtElement rootElement, Filter<T> filter) {
		// note that the new TypeFilter<>(CtReference.class) should not be necessary
		// thanks to using <T extends CtReference>
		// however, playing safe to satisfy contract in case of type erasure
		return rootElement.filterChildren(new TypeFilter<>(CtReference.class)).filterChildren(filter).list();
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
	 */
	public static <R extends CtReference> List<R> getReferences(
			Factory factory, Filter<R> filter) {
		return getReferences(factory.Package().getRootPackage(), filter);
	}

}
