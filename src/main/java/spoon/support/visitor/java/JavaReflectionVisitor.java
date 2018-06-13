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
package spoon.support.visitor.java;

import spoon.reflect.path.CtRole;
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

/**
 * Internal, package-visible interface for building shadow classes.
 * Client code should not rely on it.
 */
interface JavaReflectionVisitor {
	/** Visits a {@link java.lang.Package} */
	void visitPackage(Package aPackage);

	/** Visits a {@link java.lang.Class} */
	<T> void visitClass(Class<T> clazz);

	/** Visits a {@link java.lang.Class} representing an interface, see {@link Class#isInterface()} ()} */
	<T> void visitInterface(Class<T> clazz);

	/** Visits a {@link java.lang.Class} representing an enum, see {@link Class#isEnum()} */
	<T> void visitEnum(Class<T> clazz);

	/** Visits a {@link java.lang.Class} representing an enum, see {@link Class#isAnnotation()} */
	<T extends Annotation> void visitAnnotationClass(Class<T> clazz);

	/** Visits an {@link Annotation} instance */
	void visitAnnotation(Annotation annotation);

	/** Visits a {@link Constructor} */
	<T> void visitConstructor(Constructor<T> constructor);

	/** Visits a {@link RtMethod} (spoon wrapper) */
	void visitMethod(RtMethod method);

	/** Visits a {@link Field} */
	void visitField(Field field);

	/** Visits a {@link Field} from an enum */
	void visitEnumValue(Field field);

	/** Visits a {@link RtParameter} (spoon wrapper) */
	void visitParameter(RtParameter parameter);

	/** Visits a {@link TypeVariable} */
	<T extends GenericDeclaration> void visitTypeParameter(TypeVariable<T> parameter);

	/** Visits a {@link TypeVariable}
	 * @param role {@link CtRole} which this reference plays*/
	<T extends GenericDeclaration> void visitTypeParameterReference(CtRole role, TypeVariable<T> parameter);

	/** Visits a {@link Type}
	 * @param role {@link CtRole} which this reference plays*/
	void visitTypeReference(CtRole role, Type type);

	/** Visits a {@link ParameterizedType}
	 * @param role {@link CtRole} which this reference plays*/
	void visitTypeReference(CtRole role, ParameterizedType type);

	/** Visits a {@link WildcardType}
	 * @param role {@link CtRole} which this reference plays*/
	void visitTypeReference(CtRole role, WildcardType type);

	/** Visits a {@link Class} in generic parameters
	 * @param role {@link CtRole} which this reference plays*/
	<T> void visitTypeReference(CtRole role, Class<T> clazz);

	/** Visits a class as an array reference
	 * @param role {@link CtRole} which this reference plays*/
	<T> void visitArrayReference(CtRole role, Type typeArray);
}
