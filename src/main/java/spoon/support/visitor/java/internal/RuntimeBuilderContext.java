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
