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
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
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
		CtPackage rootPackage = input.getFactory().getModel().getRootPackage();
		CtElement parent = input;
		while (parent != null && parent != rootPackage && query.isTerminated() == false) {
			parent = parent.getParent();
			outputConsumer.accept(parent);
		}
	}

	@Override
	public void setQuery(CtQuery query) {
		this.query = query;
	}
}
