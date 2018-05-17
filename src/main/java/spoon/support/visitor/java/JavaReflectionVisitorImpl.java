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
			visitClassReference(clazz.getGenericSuperclass());
		}
		for (Type anInterface : clazz.getGenericInterfaces()) {
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
			visitClassReference(annotation.annotationType());
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
			if (method.getReturnType().isArray() && method.getReturnType().getComponentType() != null) {
				visitArrayReference(method.getReturnType().getComponentType());
			} else if (method.getGenericReturnType() instanceof Class) {
				visitClassReference(method.getReturnType());
			} else if (method.getGenericReturnType() instanceof ParameterizedType) {
				visitTypeReference((ParameterizedType) method.getGenericReturnType());
			} else {
				visitTypeParameterReference((TypeVariable) method.getGenericReturnType());
			}
		}
		visitMethodExceptionTypes(method);
	}

	protected void visitMethodExceptionTypes(RtMethod method) {
		for (Class<?> exceptionType : method.getExceptionTypes()) {
			visitTypeReference(exceptionType);
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
				visitClassReference(field.getGenericType());
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
			} else if (parameter.getGenericType() instanceof Class) {
				visitClassReference(parameter.getType());
			} else if (parameter.getGenericType() instanceof ParameterizedType) {
				visitTypeReference((ParameterizedType) parameter.getGenericType());
			} else {
				visitTypeParameterReference((TypeVariable) parameter.getGenericType());
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
			visitTypeReference(type);
		}
	}

	@Override
	public <T extends GenericDeclaration> void visitTypeParameterReference(TypeVariable<T> parameter) {
		for (Type type : parameter.getBounds()) {
			if (type == Object.class) {
				// we bypass Object.class: if a generic type extends Object we don't put it in the model, it's implicit
				// we do the same thing in ReferenceBuilder
				continue;
			}
			visitTypeReference(type);
		}
	}

	@Override
	public void visitTypeReference(Type type) {
	}

	@Override
	public void visitTypeReference(ParameterizedType type) {
		if (type.getRawType() != null) {
			visitClassReference((Class) type.getRawType());
		}
		for (Type actualType : type.getActualTypeArguments()) {
			visitTypeReference(actualType);
		}
	}

	@Override
	public void visitTypeReference(WildcardType type) {
		if (!type.getUpperBounds()[0].equals(Object.class)) {
			for (Type upper : type.getUpperBounds()) {
				visitTypeReference(upper);
			}
		}
		for (Type lower : type.getLowerBounds()) {
			if (lower instanceof ParameterizedType) {
				visitTypeReference((ParameterizedType) lower);
			} else if (lower instanceof WildcardType) {
				visitTypeReference((WildcardType) lower);
			} else {
				visitTypeReference(lower);
			}
		}
	}

	@Override
	public <T> void visitTypeReference(Class<T> clazz) {
		if (clazz.getPackage() != null && clazz.getEnclosingClass() == null) {
			visitPackage(clazz.getPackage());
		}
		if (clazz.getEnclosingClass() != null) {
			visitClassReference(clazz.getEnclosingClass());
		}
//
//		for (TypeVariable<Class<T>> generic : clazz.getTypeParameters()) {
//			visitTypeParameter(generic);
//		}
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
	public <T> void visitClassReference(ParameterizedType type) {
		Type rawType = type.getRawType();

		if (!(rawType instanceof Class)) {
			throw new UnsupportedOperationException("Rawtype of the parameterized type should be a class.");
		}

		@SuppressWarnings("unchecked")
		Class<T> classRaw = (Class<T>) rawType;
		if (classRaw.getPackage() != null) {
			visitPackage(classRaw.getPackage());
		}
		if (classRaw.getEnclosingClass() != null) {
			visitClassReference(classRaw.getEnclosingClass());
		}

		for (Type generic : type.getActualTypeArguments()) {
			visitTypeReference(generic);
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
	public final <T> void visitClassReference(Type type) {
		if (type instanceof Class) {
			visitClassReference((Class) type);
		} else if (type instanceof ParameterizedType) {
			visitClassReference((ParameterizedType) type);
		} else {
			throw new UnsupportedOperationException("Only type with class or ParameterizedType are supported.");
		}
	}

	@Override
	public <T> void visitInterfaceReference(Class<T> type) {
		if (type.getPackage() != null && type.getEnclosingClass() == null) {
			visitPackage(type.getPackage());
		}
		if (type.getEnclosingClass() != null) {
			visitClassReference(type.getEnclosingClass());
		}

		for (TypeVariable<Class<T>> generic : type.getTypeParameters()) {
			visitTypeParameter(generic);
		}
	}

	@Override
	public <T> void visitInterfaceReference(ParameterizedType type) {
		Type rawType = type.getRawType();

		if (!(rawType instanceof Class)) {
			throw new UnsupportedOperationException("Rawtype of the parameterized type should be a class.");
		}

		@SuppressWarnings("unchecked")
		Class<T> classRaw = (Class<T>) rawType;

		if (classRaw.getPackage() != null) {
			visitPackage(classRaw.getPackage());
		}
		if (classRaw.getEnclosingClass() != null) {
			visitClassReference(classRaw.getEnclosingClass());
		}

		for (Type generic : type.getActualTypeArguments()) {
			visitTypeReference(generic);
		}
	}

	@Override
	public final <T> void visitInterfaceReference(Type type) {
		if (type instanceof Class) {
			visitInterfaceReference((Class) type);
		} else if (type instanceof ParameterizedType) {
			visitInterfaceReference((ParameterizedType) type);
		} else {
			throw new UnsupportedOperationException("Only type with class or ParameterizedType are supported.");
		}
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
