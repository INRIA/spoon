/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
