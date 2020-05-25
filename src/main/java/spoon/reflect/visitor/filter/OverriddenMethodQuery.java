/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;

/**
 * Gets all overridden method from the method given.
 */
public class OverriddenMethodQuery implements CtConsumableFunction<CtMethod<?>> {
	@Override
	public void apply(CtMethod<?> input, CtConsumer<Object> outputConsumer) {
		CtPackage searchScope = input.getFactory().Package().getRootPackage();
		searchScope.filterChildren(new OverriddenMethodFilter(input)).forEach(outputConsumer);
	}
}
