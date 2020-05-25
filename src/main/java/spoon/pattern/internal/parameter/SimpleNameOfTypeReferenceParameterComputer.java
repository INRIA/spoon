/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
