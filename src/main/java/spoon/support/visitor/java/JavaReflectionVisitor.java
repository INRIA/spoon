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
package spoon.support.visitor.java;

import spoon.support.visitor.java.reflect.RtMethod;
import spoon.support.visitor.java.reflect.RtParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

interface JavaReflectionVisitor {
	void visitPackage(Package aPackage);

	<T> void visitClass(Class<T> clazz);

	<T> void visitInterface(Class<T> clazz);

	<T> void visitEnum(Class<T> clazz);

	<T extends Annotation> void visitAnnotationClass(Class<T> clazz);

	void visitAnnotation(Annotation annotation);

	<T> void visitConstructor(Constructor<T> constructor);

	void visitMethod(RtMethod method);

	void visitField(Field field);

	void visitEnumValue(Field field);

	void visitParameter(RtParameter parameter);

	<T extends GenericDeclaration> void visitTypeParameter(TypeVariable<T> parameter);

	void visitType(Type type);

	void visitType(ParameterizedType type);

	void visitType(WildcardType type);

	<T> void visitArrayReference(Class<T> typeArray);

	<T> void visitClassReference(Class<T> clazz);

	<T> void visitInterfaceReference(Class<T> anInterface);
}
