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
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;

public interface RuntimeBuilderContext {
	void addPackage(CtPackage ctPackage);

	void addType(CtType<?> aType);

	void addAnnotation(CtAnnotation<Annotation> ctAnnotation);

	void addConstructor(CtConstructor<?> ctConstructor);

	void addMethod(CtMethod<?> ctMethod);

	void addField(CtField<?> ctField);

	void addEnumValue(CtEnumValue<?> ctEnumValue);

	void addParameter(CtParameter ctParameter);

	void addTypeReference(CtRole role, CtTypeReference<?> ctTypeReference);

	void addFormalType(CtTypeParameter parameterRef);


	CtTypeParameter getTypeParameter(GenericDeclaration genericDeclaration, String string);
}
