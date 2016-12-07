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
import spoon.reflect.visitor.chain.ChainableFunction;
import spoon.reflect.visitor.chain.Consumer;
import spoon.reflect.visitor.chain.QueryStep;

/**
 * Gets all overridden method from the method given.
 */
public class OverriddenMethodFilter implements Filter<CtMethod<?>>, ChainableFunction<CtMethod<?>, CtMethod<?>> {
	private CtMethod<?> method;

	/**
	 * Creates a new overridden method filter, which will automatically scan correct scope for all overridden methods of the input element
	 * Use {@link QueryStep#map(ChainableFunction)} to run process this filter instance
	 *
	 * Note: the executable to be tested for being invoked, is this of CtElement, which invokes getElements method
	 */
	public OverriddenMethodFilter() {
	}
	/**
	 * Creates a new overridden method filter, which will scan input element for all overridden methods of the defined method
	 * Use {@link QueryStep#scan(spoon.reflect.visitor.chain.Predicate)} to run process this filter instance
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
			throw new SpoonException("Do not use parameterless constructor together with QueryStep#scan(). Use A) parameterized constructor, B) QueryStep#then()");
		}
		final CtType<?> expectedParent = method.getParent(CtType.class);
		final CtType<?> currentParent = element.getParent(CtType.class);
		return expectedParent.isSubtypeOf(currentParent.getReference()) //
				&& !currentParent.equals(expectedParent) //
				&& method.getReference().isOverriding(element.getReference());
	}

	@Override
	public void apply(CtMethod<?> input, Consumer<CtMethod<?>> output) {
		if (method != null) {
			throw new SpoonException("Do not use parameterized constructor together with QueryStep#then(). Use A) parameterless constructor, B) QueryStep#scan()");
		}
		method = input;
		try {
			method.getFactory().Package().getRootPackage().scan(this).forEach(output);
		} finally {
			method = null;
		}
	}
}
