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
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;

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
	public void addInterfaceReference(CtTypeReference<?> typeReference) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addClassReference(CtTypeReference<?> typeReference) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addArrayReference(CtArrayTypeReference<?> arrayTypeReference) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addFormalType(CtTypeParameter parameterRef) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addTypeName(CtTypeReference<?> ctTypeReference) {
		throw new UnsupportedOperationException();
	}
}
