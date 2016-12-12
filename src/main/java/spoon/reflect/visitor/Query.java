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

import java.util.List;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.chain.ChainableFunction;
import spoon.reflect.visitor.chain.Function;
import spoon.reflect.visitor.chain.QueryStepImpl;
import spoon.reflect.visitor.chain.CtQueryable;
import spoon.reflect.visitor.chain.QueryStep;

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
	 * Returns all the program elements that match the filter starting from defined rootElement.
	 * Use {@link CtElement#map(ChainableFunction)} if you want to let Filter automatically decide correct scanning context
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
		return rootElement.scan(filter).list();
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

	/**
	 * @return a {@link QueryStep} which maps input to zero one or more output elements which are produced by code
	 *<br><br>
	 * Note: Use methods of {@link CtQueryable} implemented by {@link CtElement} to create a query, which starts on the CtElement.
	 * This method is utility method to create building parts of the query chain
	 */
	@SuppressWarnings("unchecked")
	public static <P> QueryStep<P> map(ChainableFunction<?, P> code) {
		if (code instanceof QueryStep) {
			//the code is already a QueryStep. Just return it. Do not add useless wrapper.
			return (QueryStep<P>) code;
		}
		return new QueryStepImpl<P>().map(code);
	}

	/**
	 * returns a QueryStep which behaves depending on the type of returned value like this:
	 * <table>
	 * <tr><td><b>Return type</b><td><b>Behavior</b>
	 * <tr><td>{@link Boolean}<td>Sends input to the next step if returned value is true
	 * <tr><td>{@link Iterable}<td>Sends each item of Iterable to the next step
	 * <tr><td>{@link Object[]}<td>Sends each item of Array to the next step
	 * <tr><td>? extends {@link Object}<td>Sends returned value to the next step
	 * </table><br>
	 *
	 * @param code a Function with one parameter of type I returning value of type R
	 *<br><br>
	 * Note: Use methods of {@link CtQueryable} implemented by {@link CtElement} to create a query which starts on the element.
	 * This method is utility method to create building parts of the query chain
	 */
	public static <I, R> QueryStep<R> map(Function<I, R> code) {
		return new QueryStepImpl<R>().map(code);
	}

	/**
	 * creates a query which scans all children of the input and sends matching children to the output
	 * The if input element is matching then it is sent to output too.
	 */
	public static <T extends CtElement> QueryStep<T> scan(Filter<T> filter) {
		return new QueryStepImpl<T>().scan(filter);
	}
}
