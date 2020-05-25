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
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;

abstract class AbstractRuntimeBuilderContext implements RuntimeBuilderContext {

	protected AbstractRuntimeBuilderContext(CtShadowable element) {
		element.setShadow(true);
	}

	@Override
	public void addPackage(CtPackage ctPackage) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addType(CtType<?> aType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addAnnotation(CtAnnotation<Annotation> ctAnnotation) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addConstructor(CtConstructor<?> ctConstructor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addMethod(CtMethod<?> ctMethod) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addField(CtField<?> ctField) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addEnumValue(CtEnumValue<?> ctEnumValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addParameter(CtParameter ctParameter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addFormalType(CtTypeParameter parameterRef) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTypeReference(CtRole role, CtTypeReference<?> ctTypeReference) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CtTypeParameter getTypeParameter(GenericDeclaration genericDeclaration, String string) {
		return null;
	}
}
