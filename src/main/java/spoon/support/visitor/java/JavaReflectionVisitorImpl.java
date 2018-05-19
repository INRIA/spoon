/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import spoon.SpoonException;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.visitor.java.reflect.RtMethod;
import spoon.support.visitor.java.reflect.RtParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
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
		for (TypeVariable<Class<T>> generic : clazz.getTypeParameters()) {
			visitTypeParameter(generic);
		}
		if (clazz.getGenericSuperclass() != null && clazz.getGenericSuperclass() != Object.class) {
			visitTypeReference(CtRole.SUPER_TYPE, clazz.getGenericSuperclass());
		}
		for (Type anInterface : clazz.getGenericInterfaces()) {
			visitTypeReference(CtRole.INTERFACE, anInterface);
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
			visitType(aClass);
		}
	}

	protected final <T> void visitType(Class<T> aClass) {
		if (aClass.isAnnotation()) {
			visitAnnotationClass((Class<Annotation>) aClass);
		} else if (aClass.isInterface()) {
			visitInterface(aClass);
		} else if (aClass.isEnum()) {
			visitEnum(aClass);
		} else {
			visitClass(aClass);
		}
	}

	@Override
	public <T> void visitInterface(Class<T> clazz) {
		assert clazz.isInterface();
		if (clazz.getPackage() != null) {
			clazz.getPackage();
		}
		for (Type anInterface : clazz.getGenericInterfaces()) {
			visitTypeReference(CtRole.INTERFACE, anInterface);
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
			visitType(aClass);
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
		for (Type anInterface : clazz.getGenericInterfaces()) {
			visitTypeReference(CtRole.INTERFACE, anInterface);
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
			visitType(aClass);
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
			visitType(aClass);
		}
	}

	@Override
	public void visitAnnotation(Annotation annotation) {
		if (annotation.annotationType() != null) {
			visitTypeReference(CtRole.ANNOTATION_TYPE, annotation.annotationType());
			List<RtMethod> methods = getDeclaredMethods(annotation.annotationType());
			for (RtMethod method : methods) {
				visitMethod(method, annotation);
			}
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
	public final void visitMethod(RtMethod method) {
		this.visitMethod(method, null);
	}

	protected void visitMethod(RtMethod method, Annotation parent) {
		for (Annotation annotation : method.getDeclaredAnnotations()) {
			if (parent == null || !annotation.annotationType().equals(parent.annotationType())) {
				visitAnnotation(annotation);
			}
		}
		for (TypeVariable<Method> aTypeParameter : method.getTypeParameters()) {
			visitTypeParameter(aTypeParameter);
		}
		for (RtParameter parameter : RtParameter.parametersOf(method)) {
			visitParameter(parameter);
		}

		if (method.getReturnType() != null) {
			visitTypeReference(CtRole.TYPE, method.getGenericReturnType());
		}
		visitMethodExceptionTypes(method);
	}

	protected void visitMethodExceptionTypes(RtMethod method) {
		for (Class<?> exceptionType : method.getExceptionTypes()) {
			visitTypeReference(CtRole.THROWN, exceptionType);
		}
	}

	@Override
	public void visitField(Field field) {
		for (Annotation annotation : field.getDeclaredAnnotations()) {

			visitAnnotation(annotation);
		}
		if (field.getGenericType() != null) {
			visitTypeReference(CtRole.TYPE, field.getGenericType());
		}
	}

	@Override
	public void visitEnumValue(Field field) {
		for (Annotation annotation : field.getDeclaredAnnotations()) {
			visitAnnotation(annotation);
		}
		if (field.getType() != null) {
			visitTypeReference(CtRole.TYPE, field.getType());
		}
	}

	@Override
	public void visitParameter(RtParameter parameter) {
		for (Annotation annotation : parameter.getDeclaredAnnotations()) {
			visitAnnotation(annotation);
		}
		if (parameter.getGenericType() != null) {
			if (parameter.isVarArgs()) {
				//TODO check if it is OK or there belonss parameter.getType().getComponentType() - as before
				visitArrayReference(CtRole.TYPE, parameter.getGenericType());
			} else {
				visitTypeReference(CtRole.TYPE, parameter.getGenericType());
			}
		}
	}

	@Override
	public <T extends GenericDeclaration> void visitTypeParameter(TypeVariable<T> parameter) {
		for (Type type : parameter.getBounds()) {
			if (type == Object.class) {
				// we want to ignore Object to avoid <T extends Object>
				continue;
			}
			visitTypeReference(CtRole.SUPER_TYPE, type);
		}
	}

	@Override
	public <T extends GenericDeclaration> void visitTypeParameterReference(CtRole role, TypeVariable<T> parameter) {
		for (Type type : parameter.getBounds()) {
			if (type == Object.class) {
				// we bypass Object.class: if a generic type extends Object we don't put it in the model, it's implicit
				// we do the same thing in ReferenceBuilder
				continue;
			}
			visitTypeReference(CtRole.SUPER_TYPE, type);
		}
	}

	@Override
	public final void visitTypeReference(CtRole role, Type type) {
		CtTypeReference<?> ctTypeReference;
		if (type instanceof TypeVariable) {
			this.visitTypeParameterReference(role, (TypeVariable<?>) type);
			return;
		}
		if (type instanceof ParameterizedType) {
			this.visitTypeReference(role, (ParameterizedType) type);
			return;
		}
		if (type instanceof WildcardType) {
			this.visitTypeReference(role, (WildcardType) type);
			return;
		}
		if (type instanceof GenericArrayType) {
			visitArrayReference(role, ((GenericArrayType) type).getGenericComponentType());
			return;
		}
		if (type instanceof Class) {
			Class<?> clazz = (Class<?>) type;
			if (clazz.isArray()) {
				visitArrayReference(role, clazz.getComponentType());
				return;
			}
			//do not call visitClassReference because it call addClassReference, but we need to call addTypeName
			this.visitTypeReference(role, clazz);
			return;
		}
		throw new SpoonException("Unexpected java reflection type: " + type.getClass().getName());
	}

	@Override
	public void visitTypeReference(CtRole role, ParameterizedType type) {
		Type rawType = type.getRawType();

		if (!(rawType instanceof Class)) {
			throw new UnsupportedOperationException("Rawtype of the parameterized type should be a class.");
		}

		Class<?> classRaw = (Class<?>) rawType;

		if (classRaw.getPackage() != null) {
			visitPackage(classRaw.getPackage());
		}
		if (classRaw.getEnclosingClass() != null) {
			visitTypeReference(CtRole.DECLARING_TYPE, classRaw.getEnclosingClass());
		}

		for (Type generic : type.getActualTypeArguments()) {
			visitTypeReference(CtRole.TYPE_ARGUMENT, generic);
		}
	}

	@Override
	public void visitTypeReference(CtRole role, WildcardType type) {
		if (!type.getUpperBounds()[0].equals(Object.class)) {
			for (Type upper : type.getUpperBounds()) {
				visitTypeReference(CtRole.BOUNDING_TYPE, upper);
			}
		}
		for (Type lower : type.getLowerBounds()) {
			visitTypeReference(CtRole.BOUNDING_TYPE, lower);
		}
	}

	@Override
	public <T> void visitArrayReference(CtRole role, Type typeArray) {
		visitTypeReference(role, typeArray);
	}

	@Override
	public <T> void visitTypeReference(CtRole role, Class<T> clazz) {
		if (clazz.getPackage() != null && clazz.getEnclosingClass() == null) {
			visitPackage(clazz.getPackage());
		}
		if (clazz.getEnclosingClass() != null) {
			visitTypeReference(CtRole.DECLARING_TYPE, clazz.getEnclosingClass());
		}
// TODO in case of interfaces this was used!! But not in case of class?
//		if (clazz.isInterface()) {
//			for (TypeVariable<Class<T>> generic : clazz.getTypeParameters()) {
//				visitTypeParameter(generic);
//			}
//		}
	}

	private <T> List<RtMethod> getDeclaredMethods(Class<T> clazz) {
		Method[] javaMethods = clazz.getDeclaredMethods();
		List<RtMethod> rtMethods = new ArrayList<>();
		for (Method method : javaMethods) {
			if (method.isSynthetic()) {
				//ignore synthetic methods.
				continue;
			}
			rtMethods.add(RtMethod.create(method));
		}
		return rtMethods;
	}
}
