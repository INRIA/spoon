/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryAware;

/**
 * This Function expects a {@link CtElement} as input
 * and returns all parents of this element.
 *
 * By default input is not returned,
 * but this behavior can be changed by call of {@link #includingSelf(boolean)} with value true
 */
public class ParentFunction implements CtConsumableFunction<CtElement>, CtQueryAware {

	private boolean includingSelf = false;
	private CtQuery query;

	public ParentFunction() {
	}

	/**
	 * @param includingSelf if true then input element is sent to output too. By default it is false.
	 */
	public ParentFunction includingSelf(boolean includingSelf) {
		this.includingSelf = includingSelf;
		return this;
	}

	@Override
	public void apply(CtElement input, CtConsumer<Object> outputConsumer) {
		if (input == null) {
			return;
		}
		if (includingSelf) {
			outputConsumer.accept(input);
		}
		CtElement parent = input;
		CtModule topLevel = input.getFactory().getModel().getUnnamedModule();
		while (parent != null && parent != topLevel && query.isTerminated() == false && parent.isParentInitialized()) {
			parent = parent.getParent();
			outputConsumer.accept(parent);
		}
	}

	@Override
	public void setQuery(CtQuery query) {
		this.query = query;
	}
}
