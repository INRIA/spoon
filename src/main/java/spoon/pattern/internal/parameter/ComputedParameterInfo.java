/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.parameter;

import java.util.List;
import java.util.function.Function;

import spoon.SpoonException;
import spoon.pattern.internal.ResultHolder;
import spoon.reflect.factory.Factory;
import spoon.support.util.ImmutableMap;

/**
 * Represents a parameter which is related to a value of another parameter.
 * For example parameter which represents CtTypeReference has a value `abc.sample.AType`
 * And computed parameter which represents name of type referenced by CtTypeReference
 * has a computed String value `AType`
 */
public class ComputedParameterInfo extends AbstractParameterInfo {

	private final ParameterComputer computer;

	public ComputedParameterInfo(ParameterComputer computer, ParameterInfo next) {
		super(next);
		this.computer = computer;
	}

	@Override
	protected String getPlainName() {
		return getWrappedName(getContainerName());
	}

	@Override
	protected String getWrappedName(String containerName) {
		return containerName + "$" + computer.getName();
	}

	@Override
	protected Object addValueAs(Object container, Function<Object, Object> merger) {
		//do not try to match on computed value
		return container;
	}

	@Override
	protected List<Object> getEmptyContainer() {
		throw new SpoonException("ComputedParameterInfo#getEmptyContainer should not be used");
	}
	@Override
	public <T> void getValueAs(Factory factory, ResultHolder<T> result, ImmutableMap parameters) {
		ResultHolder<?> inputHolder = computer.createInputHolder();
		super.getValueAs(factory, inputHolder, parameters);
		computer.computeValue((ResultHolder) result, inputHolder);
	}
}
