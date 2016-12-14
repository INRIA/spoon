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
package spoon.reflect.visitor.filter;

import spoon.SpoonException;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtQueryStep;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;

/**
 * Gets all overridden method from the method given.
 */
public class OverriddenMethodFilter implements Filter<CtMethod<?>>, CtQueryStep<CtMethod<?>, CtMethod<?>> {
	private CtMethod<?> method;

	/**
	 * Creates a new overridden method filter, which will automatically scan correct scope for all overridden methods of the input element
	 * Use {@link CtQuery#map(CtQueryStep)} to add query step using this filter
	 *
	 * Note: the executable to be tested for being invoked, comes as output from previous query step
	 */
	public OverriddenMethodFilter() {
	}
	/**
	 * Creates a new overridden method filter, which will scan input element for all overridden methods of the defined method
	 * Use {@link CtQuery#filterChildren(Filter)} to filter child elements produced by previous step by this filter
	 *
	 * @param method
	 * 		the executable to be tested for being invoked
	 */
	public OverriddenMethodFilter(CtMethod<?> method) {
		this.method = method;
	}

	@Override
	public boolean matches(CtMethod<?> element) {
		if (method == null) {
			/**
			 * Do not use parameterless constructor together with QueryStep#scan().
			 * Use:
			 * A) parameterized constructor and QueryStep#scan()
			 * B) parameterless constructor and QueryStep#map()
			 */
			throw new SpoonException("Missing Filter context parameter 'method'");
		}
		final CtType<?> expectedParent = method.getParent(CtType.class);
		final CtType<?> currentParent = element.getParent(CtType.class);
		return expectedParent.isSubtypeOf(currentParent.getReference()) //
				&& !currentParent.equals(expectedParent) //
				&& method.getReference().isOverriding(element.getReference());
	}

	/**
	 * This method is used when this Filter is used in {@link CtQuery#map(CtQueryStep)}
	 * In such case Filter can automatically use correct scanning scope - root package
	 */
	@Override
	public void process(CtMethod<?> input, CtConsumer<CtMethod<?>> output) {
		if (method != null) {
			/**
			 * Do not use parameterized constructor together with QueryStep#map().
			 * Use:
			 * A) parameterized constructor and QueryStep#scan()
			 * B) parameterless constructor and QueryStep#map()
			 */
			throw new SpoonException("Do not use parameterized constructor together with QueryStep#map()");
		}
		method = input;
		try {
			method.getFactory().Package().getRootPackage().filterChildren(this).forEach(output);
		} finally {
			method = null;
		}
	}
}
