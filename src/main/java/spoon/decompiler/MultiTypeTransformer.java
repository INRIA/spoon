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
package spoon.decompiler;

import spoon.reflect.declaration.CtType;

import java.util.Collection;
import java.util.LinkedHashSet;

public class MultiTypeTransformer implements TypeTransformer {

	protected LinkedHashSet<TypeTransformer> transformers;

	public MultiTypeTransformer() {
		transformers = new LinkedHashSet<>();
	}

	public void addTransformer(TypeTransformer transformer) {
		transformers.add(transformer);
	}

	public void addTransformers(Collection<TypeTransformer> transformers) {
		this.transformers.addAll(transformers);
	}

	@Override
	public void transform(CtType type) {
		for (TypeTransformer transformer: transformers) {
			if (transformer.accept(type)) {
				transformer.transform(type);
			}
		}
	}

	@Override
	public boolean accept(CtType type) {
		for (TypeTransformer transformer: transformers) {
			if (transformer.accept(type)) {
				return true;
			}
		}
		return false;
	}
}
