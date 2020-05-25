/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.java.internal;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.GenericDeclaration;
import java.util.HashMap;
import java.util.Map;

public class ExecutableRuntimeBuilderContext extends AbstractRuntimeBuilderContext {
	private CtExecutable<?> ctExecutable;
	private Executable executable;
	private Map<String, CtTypeParameter> mapTypeParameters;

	public ExecutableRuntimeBuilderContext(Executable executable, CtMethod<?> ctMethod) {
		super(ctMethod);
		this.ctExecutable = ctMethod;
		this.executable = executable;
		this.mapTypeParameters = new HashMap<>();
	}

	public ExecutableRuntimeBuilderContext(Executable executable, CtConstructor<?> ctConstructor) {
		super(ctConstructor);
		this.ctExecutable = ctConstructor;
		this.executable = executable;
		this.mapTypeParameters = new HashMap<>();
	}

	@Override
	public void addAnnotation(CtAnnotation<Annotation> ctAnnotation) {
		ctExecutable.addAnnotation(ctAnnotation);
	}

	@Override
	public void addParameter(CtParameter ctParameter) {
		ctExecutable.addParameter(ctParameter);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addTypeReference(CtRole role, CtTypeReference<?> typeReference) {
		switch (role) {
		case THROWN:
			ctExecutable.addThrownType((CtTypeReference) typeReference);
			return;
		case TYPE:
			ctExecutable.setType((CtTypeReference) typeReference);
			return;
		}
		super.addTypeReference(role, typeReference);
	}

	@Override
	public void addFormalType(CtTypeParameter parameterRef) {
		if (ctExecutable instanceof CtFormalTypeDeclarer) {
			((CtFormalTypeDeclarer) ctExecutable).addFormalCtTypeParameter(parameterRef);
			this.mapTypeParameters.put(parameterRef.getSimpleName(), parameterRef);
			return;
		}
		super.addFormalType(parameterRef);
	}

	@Override
	public CtTypeParameter getTypeParameter(GenericDeclaration genericDeclaration, String string) {
		return executable == genericDeclaration ? this.mapTypeParameters.get(string) : null;
	}
}
