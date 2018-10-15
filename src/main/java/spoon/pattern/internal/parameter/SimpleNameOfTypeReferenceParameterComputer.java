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

import spoon.pattern.internal.ResultHolder;
import spoon.reflect.reference.CtTypeReference;

/**
 * a {@link ParameterComputer} which computes simpleName of {@link CtTypeReference}
 */
public class SimpleNameOfTypeReferenceParameterComputer implements ParameterComputer {

	public static final SimpleNameOfTypeReferenceParameterComputer INSTANCE = new SimpleNameOfTypeReferenceParameterComputer();

	@Override
	public String getName() {
		return "simpleName";
	}

	@Override
	public ResultHolder<?> createInputHolder() {
		return new ResultHolder.Single<>(CtTypeReference.class);
	}

	@Override
	public void computeValue(ResultHolder<Object> outputHolder, ResultHolder<?> inputHolder) {
		String name = null;
		CtTypeReference<?> typeRef = ((ResultHolder.Single<CtTypeReference<?>>) inputHolder).getResult();
		if (typeRef != null) {
			name = typeRef.getSimpleName();
		}
		outputHolder.addResult(name);
	}
}
