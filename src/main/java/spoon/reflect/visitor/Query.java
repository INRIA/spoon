/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.reference.CtReference;

/**
 * This class provides some useful methods to retrieve program elements and
 * reference through a {@link spoon.reflect.visitor.CtScanner}-based deep
 * search. It uses the {@link spoon.reflect.visitor.Filter} and
 * {@link spoon.reflect.visitor.ReferenceFilter} facitily to select the right
 * elements or references.
 */
public abstract class Query extends CtScanner {

	private Query() {
	}

	/**
	 * Within a given factory, returns all the program elements that match the
	 * filter.
	 * 
	 * @param <E>
	 *            the type of the seeked program elements
	 * @param factory
	 *            the factory that contains the elements where to recursive
	 *            search on
	 * @param filter
	 *            the filter which defines the matching criteria
	 */
	public static <E extends CtElement> List<E> getElements(Factory factory,
			Filter<E> filter) {
		List<E> e = new ArrayList<E>();
		for (CtPackage p : factory.Package().getAllRoots()) {
			e.addAll(getElements(p, filter));
		}
		return e;
	}

	/**
	 * Returns all the program elements that match the filter.
	 * 
	 * @param <E>
	 *            the type of the seeked program elements
	 * @param rootElement
	 *            the element to start the recursive search on
	 * @param filter
	 *            the filter which defines the matching criteria
	 */
	public static <E extends CtElement> List<E> getElements(
			CtElement rootElement, Filter<E> filter) {
		QueryVisitor<E> visitor = new QueryVisitor<E>(filter);
		visitor.scan(rootElement);
		return visitor.getResult();
	}

	/**
	 * Returns all the program element references that match the filter.
	 * 
	 * @param <T>
	 *            the type of the seeked program element references
	 * @param rootElement
	 *            the element to start the recursive search on
	 * @param filter
	 *            the filter which defines the matching criteria
	 */
	public static <T extends CtReference> List<T> getReferences(
			CtElement rootElement, ReferenceFilter<T> filter) {
		ReferenceQueryVisitor<T> visitor = new ReferenceQueryVisitor<T>(filter);
		visitor.scan(rootElement);
		return visitor.getResult();
	}

	/**
	 * Within a given factory, returns all the program element references that
	 * match the filter.
	 * 
	 * @param <R>
	 *            the type of the seeked program element references
	 * @param factory
	 *            the factory that contains the references where to recursive
	 *            search on
	 * @param filter
	 *            the filter which defines the matching criteria
	 */
	public static <R extends CtReference> List<R> getReferences(
			Factory factory, ReferenceFilter<R> filter) {
		List<R> r = new ArrayList<R>();
		for (CtPackage p : factory.Package().getAllRoots()) {
			r.addAll(getReferences(p, filter));
		}
		return r;
	}

}