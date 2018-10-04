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
