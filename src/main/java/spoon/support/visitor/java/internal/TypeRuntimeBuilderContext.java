/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.java.internal;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TypeRuntimeBuilderContext extends AbstractRuntimeBuilderContext {
	protected CtType type;
	protected Type rtType;
	private Map<String, CtTypeParameter> mapTypeParameters;

	public TypeRuntimeBuilderContext(Type rtType, CtType type) {
		super(type);
		this.type = type;
		this.rtType = rtType;
		this.mapTypeParameters = new HashMap<>();
	}

	@Override
	public void addPackage(CtPackage ctPackage) {
		ctPackage.addType(type);
	}

	@Override
	public void addType(CtType<?> aType) {
		type.addNestedType(aType);
	}

	@Override
	public void addAnnotation(CtAnnotation<Annotation> ctAnnotation) {
		type.addAnnotation(ctAnnotation);
	}

	@Override
	public void addMethod(CtMethod<?> ctMethod) {
		type.addMethod(ctMethod);
	}

	@Override
	public void addField(CtField<?> ctField) {
		type.addField(ctField);
	}

	@Override
	public void addFormalType(CtTypeParameter parameterRef) {
		this.type.addFormalCtTypeParameter(parameterRef);
		this.mapTypeParameters.put(parameterRef.getSimpleName(), parameterRef);
	}

	@Override
	public void addTypeReference(CtRole role, CtTypeReference<?> typeReference) {
		switch (role) {
			case INTERFACE:
				type.addSuperInterface(typeReference);
				return;
			case SUPER_TYPE:
				if (type instanceof CtTypeParameter) {
					((CtTypeParameter) this.type).setSuperclass(typeReference);
					return;
				}
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public CtTypeParameter getTypeParameter(GenericDeclaration genericDeclaration, String string) {
		return rtType == genericDeclaration ? this.mapTypeParameters.get(string) : null;
	}
}
