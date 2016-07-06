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
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class JavaReflectionVisitorImpl implements JavaReflectionVisitor {
	@Override
	public void visitPackage(Package aPackage) {
	}

	@Override
	public <T> void visitClass(Class<T> clazz) {
		if (clazz.getPackage() != null) {
			clazz.getPackage();
		}
		if (clazz.getSuperclass() != null) {
			visitClassReference(clazz.getSuperclass());
		}
		for (Class<?> anInterface : clazz.getInterfaces()) {
			visitInterfaceReference(anInterface);
		}
		for (Annotation annotation : clazz.getDeclaredAnnotations()) {
			visitAnnotation(annotation);
		}
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			visitConstructor(constructor);
		}
		for (RtMethod method : getDeclaredMethods(clazz)) {
			visitMethod(method);
		}
		for (Field field : clazz.getDeclaredFields()) {
			visitField(field);
		}
		for (Class<?> aClass : clazz.getDeclaredClasses()) {
			visitClass(aClass);
		}
		for (TypeVariable<Class<T>> generic : clazz.getTypeParameters()) {
			visitTypeParameter(generic);
		}
	}

	@Override
	public <T> void visitInterface(Class<T> clazz) {
		assert clazz.isInterface();
		if (clazz.getPackage() != null) {
			clazz.getPackage();
		}
		for (Class<?> anInterface : clazz.getInterfaces()) {
			visitInterfaceReference(anInterface);
		}
		for (Annotation annotation : clazz.getDeclaredAnnotations()) {
			visitAnnotation(annotation);
		}
		for (RtMethod method : getDeclaredMethods(clazz)) {
			visitMethod(method);
		}
		for (Field field : clazz.getDeclaredFields()) {
			visitField(field);
		}
		for (Class<?> aClass : clazz.getDeclaredClasses()) {
			visitClass(aClass);
		}
		for (TypeVariable<Class<T>> generic : clazz.getTypeParameters()) {
			visitTypeParameter(generic);
		}
	}

	@Override
	public <T> void visitEnum(Class<T> clazz) {
		assert clazz.isEnum();
		if (clazz.getPackage() != null) {
			clazz.getPackage();
		}
		for (Class<?> anInterface : clazz.getInterfaces()) {
			visitInterfaceReference(anInterface);
		}
		for (Annotation annotation : clazz.getDeclaredAnnotations()) {
			visitAnnotation(annotation);
		}
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			visitConstructor(constructor);
		}
		for (RtMethod method : getDeclaredMethods(clazz)) {
			if (("valueOf".equals(method.getName()) && method.getParameterTypes().length == 1 && String.class.equals(method.getParameterTypes()[0])) || "values".equals(method.getName())) {
				continue;
			}
			visitMethod(method);
		}
		for (Field field : clazz.getDeclaredFields()) {
			if ("$VALUES".equals(field.getName())) {
				continue;
			}
			if (field.isEnumConstant()) {
				visitEnumValue(field);
			} else {
				visitField(field);
			}
		}
		for (Class<?> aClass : clazz.getDeclaredClasses()) {
			visitClass(aClass);
		}
	}

	@Override
	public <T extends Annotation> void visitAnnotationClass(Class<T> clazz) {
		assert clazz.isAnnotation();
		if (clazz.getPackage() != null) {
			clazz.getPackage();
		}
		for (Annotation annotation : clazz.getDeclaredAnnotations()) {
			visitAnnotation(annotation);
		}
		for (RtMethod method : getDeclaredMethods(clazz)) {
			visitMethod(method);
		}
		for (Field field : clazz.getDeclaredFields()) {
			visitField(field);
		}
		for (Class<?> aClass : clazz.getDeclaredClasses()) {
			visitClass(aClass);
		}
	}

	@Override
	public void visitAnnotation(Annotation annotation) {
		if (annotation.annotationType() != null) {
			visitClassReference(annotation.annotationType());
		}
	}

	@Override
	public <T> void visitConstructor(Constructor<T> constructor) {
		for (Annotation annotation : constructor.getDeclaredAnnotations()) {
			visitAnnotation(annotation);
		}
		for (RtParameter parameter : RtParameter.parametersOf(constructor)) {
			visitParameter(parameter);
		}
		for (TypeVariable<Constructor<T>> aTypeParameter : constructor.getTypeParameters()) {
			visitTypeParameter(aTypeParameter);
		}
	}

	@Override
	public void visitMethod(RtMethod method) {
		for (Annotation annotation : method.getDeclaredAnnotations()) {
			visitAnnotation(annotation);
		}
		for (RtParameter parameter : RtParameter.parametersOf(method)) {
			visitParameter(parameter);
		}
		for (TypeVariable<Method> aTypeParameter : method.getTypeParameters()) {
			visitTypeParameter(aTypeParameter);
		}
		if (method.getReturnType() != null) {
			if (method.getReturnType().isArray() && method.getReturnType().getComponentType() != null) {
				visitArrayReference(method.getReturnType().getComponentType());
			} else {
				visitClassReference(method.getReturnType());
			}
		}
	}

	@Override
	public void visitField(Field field) {
		for (Annotation annotation : field.getDeclaredAnnotations()) {
			visitAnnotation(annotation);
		}
		if (field.getType() != null) {
			if (field.getType().isArray() && field.getType().getComponentType() != null) {
				visitArrayReference(field.getType().getComponentType());
			} else {
				visitClassReference(field.getType());
			}
		}
	}

	@Override
	public void visitEnumValue(Field field) {
		for (Annotation annotation : field.getDeclaredAnnotations()) {
			visitAnnotation(annotation);
		}
		if (field.getType() != null) {
			visitClassReference(field.getType());
		}
	}

	@Override
	public void visitParameter(RtParameter parameter) {
		for (Annotation annotation : parameter.getDeclaredAnnotations()) {
			visitAnnotation(annotation);
		}
		if (parameter.getType() != null) {
			if ((parameter.isVarArgs() || parameter.getType().isArray()) && parameter.getType().getComponentType() != null) {
				visitArrayReference(parameter.getType().getComponentType());
			} else {
				visitClassReference(parameter.getType());
			}
		}
	}

	@Override
	public <T extends GenericDeclaration> void visitTypeParameter(TypeVariable<T> parameter) {
		for (Type type : parameter.getBounds()) {
			if (type instanceof ParameterizedType) {
				visitType((ParameterizedType) type);
			} else if (type instanceof WildcardType) {
				visitType((WildcardType) type);
			} else {
				visitType(type);
			}
		}
	}

	@Override
	public void visitType(Type type) {
	}

	@Override
	public void visitType(ParameterizedType type) {
		if (type.getRawType() != null) {
			visitClassReference((Class) type.getRawType());
		}
		for (Type actualType : type.getActualTypeArguments()) {
			if (actualType instanceof ParameterizedType) {
				visitType((ParameterizedType) actualType);
			} else if (actualType instanceof WildcardType) {
				visitType((WildcardType) actualType);
			} else {
				visitType(actualType);
			}
		}
	}

	@Override
	public void visitType(WildcardType type) {
		if (!type.getUpperBounds()[0].equals(Object.class)) {
			for (Type upper : type.getUpperBounds()) {
				if (upper instanceof ParameterizedType) {
					visitType((ParameterizedType) upper);
				} else if (upper instanceof WildcardType) {
					visitType((WildcardType) upper);
				} else {
					visitType(upper);
				}
			}
		}
		for (Type lower : type.getLowerBounds()) {
			if (lower instanceof ParameterizedType) {
				visitType((ParameterizedType) lower);
			} else if (lower instanceof WildcardType) {
				visitType((WildcardType) lower);
			} else {
				visitType(lower);
			}
		}
	}

	@Override
	public <T> void visitArrayReference(Class<T> typeArray) {
		if (typeArray.isArray() && typeArray.getComponentType() != null) {
			visitArrayReference(typeArray.getComponentType());
		} else {
			visitClassReference(typeArray);
		}
	}

	@Override
	public <T> void visitClassReference(Class<T> clazz) {
		if (clazz.getPackage() != null && clazz.getEnclosingClass() == null) {
			visitPackage(clazz.getPackage());
		}
		if (clazz.getEnclosingClass() != null) {
			visitClassReference(clazz.getEnclosingClass());
		}
	}

	@Override
	public <T> void visitInterfaceReference(Class<T> type) {
		if (type.getPackage() != null) {
			visitPackage(type.getPackage());
		}
		if (type.getEnclosingClass() != null) {
			visitClassReference(type.getEnclosingClass());
		}
	}

	private <T> List<RtMethod> getDeclaredMethods(Class<T> clazz) {
		final List<RtMethod> methods = new ArrayList<>();
		methods.addAll(Arrays.asList(RtMethod.methodsOf(clazz)));
		final Class<?> superclass = clazz.getSuperclass();
		if (superclass != null) {
			methods.removeAll(Arrays.asList(RtMethod.sameMethodsWithDifferentTypeOf(superclass, methods)));
		}
		return methods;
	}
}
