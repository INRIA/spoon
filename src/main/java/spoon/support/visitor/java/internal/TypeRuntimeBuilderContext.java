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
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;

public class TypeRuntimeBuilderContext extends AbstractRuntimeBuilderContext {
	protected CtType type;

	public TypeRuntimeBuilderContext(CtType type) {
		super(type);
		this.type = type;
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
	public void addInterfaceReference(CtTypeReference<?> typeReference) {
		type.addSuperInterface(typeReference);
	}

	@Override
	public void addFormalType(CtTypeParameter parameterRef) {
		this.type.addFormalCtTypeParameter(parameterRef);
	}

	@Override
	public void addTypeName(CtTypeReference<?> ctTypeReference) {
		if (type instanceof CtTypeParameter) {
			((CtTypeParameter) this.type).setSuperclass(ctTypeReference);
		}
	}
}
