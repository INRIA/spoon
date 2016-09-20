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
package spoon.support.visitor.java.internal;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;

public class ExecutableRuntimeBuilderContext extends AbstractRuntimeBuilderContext {
	private CtExecutable<?> ctExecutable;

	public ExecutableRuntimeBuilderContext(CtMethod<?> ctMethod) {
		super(ctMethod);
		this.ctExecutable = ctMethod;
	}

	public ExecutableRuntimeBuilderContext(CtConstructor<?> ctConstructor) {
		super(ctConstructor);
		this.ctExecutable = ctConstructor;
	}

	@Override
	public void addAnnotation(CtAnnotation<Annotation> ctAnnotation) {
		ctExecutable.addAnnotation(ctAnnotation);
	}

	@Override
	public void addParameter(CtParameter ctParameter) {
		ctExecutable.addParameter(ctParameter);
	}

	@Override
	public void addArrayReference(CtArrayTypeReference<?> arrayTypeReference) {
		if (ctExecutable instanceof CtMethod) {
			final CtArrayTypeReference ref = arrayTypeReference;
			ctExecutable.setType(ref);
			return;
		}
		super.addArrayReference(arrayTypeReference);
	}

	@Override
	public void addClassReference(CtTypeReference<?> typeReference) {
		if (ctExecutable instanceof CtMethod) {
			final CtTypeReference ref = typeReference;
			ctExecutable.setType(ref);
			return;
		}
		super.addClassReference(typeReference);
	}

	@Override
	public void addFormalType(CtTypeParameter parameterRef) {
		if (ctExecutable instanceof CtFormalTypeDeclarer) {
			((CtFormalTypeDeclarer) ctExecutable).addFormalCtTypeParameter(parameterRef);
			return;
		}
		super.addFormalType(parameterRef);
	}
}
