/*
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.reflect.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import spoon.reflect.Factory;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * The {@link CtExecutable} sub-factory.
 */
public class ExecutableFactory extends SubFactory {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new executable sub-factory.
	 *
	 * @param factory
	 *            the parent factory
	 */
	public ExecutableFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates an anonymous executable (initializer block) in a target class).
	 */
	public CtAnonymousExecutable createAnonymous(CtClass<?> target,
			CtBlock<?> body) {
		CtAnonymousExecutable a = factory.Core().createAnonymousExecutable();
		target.getAnonymousExecutables().add(a);
		a.setParent(target);
		a.setBody(body);
		body.setParent(a);
		return a;
	}

	/**
	 * Creates a new parameter.
	 */
	public <T> CtParameter<T> createParameter(CtExecutable<?> parent,
			CtTypeReference<T> type, String name) {
		CtParameter<T> parameter = factory.Core().createParameter();
		parameter.setType(type);
		parameter.setSimpleName(name);
		if (parent != null) {
			parent.getParameters().add(parameter);
			parameter.setParent(parent);
		}
		return parameter;
	}

	/**
	 * Creates a parameter reference from an existing parameter.
	 *
	 * @param <T>
	 *            the parameter's type
	 * @param parameter
	 *            the parameter
	 */
	@SuppressWarnings("unchecked")
	public <T> CtParameterReference<T> createParameterReference(
			CtParameter<T> parameter) {
		CtParameterReference<T> ref = factory.Core().createParameterReference();
		if (parameter.getParent() != null) {
			ref.setDeclaringExecutable(factory.Executable().createReference(
					(CtExecutable) parameter.getParent()));
		}
		ref.setSimpleName(parameter.getSimpleName());
		ref.setType(parameter.getType());
		return ref;
	}

	/**
	 * Creates an executable reference from an existing executable.
	 */
	public <T> CtExecutableReference<T> createReference(CtExecutable<T> e) {
		CtTypeReference<?> refs[] = new CtTypeReference[e.getParameters()
				.size()];
		int i = 0;
		for (CtParameter<?> param : e.getParameters()) {
			refs[i++] = param.getType();
		}
		if (e instanceof CtMethod) {
			return createReference(e.getDeclaringType().getReference(),
					((CtMethod<T>) e).getType(), e.getSimpleName(), refs);
		}
		return createReference(e.getDeclaringType().getReference(),
				((CtConstructor<T>) e).getType(), CtExecutableReference.CONSTRUCTOR_NAME, refs);
	}

	/**
	 * Creates an executable reference.
	 *
	 * @param declaringType
	 *            reference to the declaring type
	 * @param type
	 *            the executable's type
	 * @param methodName
	 *            simple name
	 * @param parameterTypes
	 *            list of parameter's types
	 */
	public <T> CtExecutableReference<T> createReference(
			CtTypeReference<?> declaringType, CtTypeReference<T> type,
			String methodName, CtTypeReference<?>... parameterTypes) {
		CtExecutableReference<T> methodRef = factory.Core()
				.createExecutableReference();
		methodRef.setDeclaringType(declaringType);
		methodRef.setSimpleName(methodName);
		methodRef.setType(type);
		List<CtTypeReference<?>> l = new ArrayList<CtTypeReference<?>>();
		for (CtTypeReference<?> ref : parameterTypes) {
			l.add(ref);
		}
		methodRef.setParameterTypes(l);
		return methodRef;
	}

	/**
	 * Creates an executable reference.
	 *
	 * @param declaringType
	 *            reference to the declaring type
	 * @param isStatic
	 *            if this reference references a static executable
	 * @param type
	 *            the return type of the executable
	 * @param methodName
	 *            simple name
	 * @param parameterTypes
	 *            list of parameter's types
	 */
	public <T> CtExecutableReference<T> createReference(
			CtTypeReference<?> declaringType, boolean isStatic,
			CtTypeReference<T> type, String methodName,
			CtTypeReference<?>... parameterTypes) {
		CtExecutableReference<T> methodRef = factory.Core()
				.createExecutableReference();
		methodRef.setStatic(isStatic);
		methodRef.setDeclaringType(declaringType);
		methodRef.setSimpleName(methodName);
		methodRef.setType(type);
		List<CtTypeReference<?>> l = new ArrayList<CtTypeReference<?>>();
		for (CtTypeReference<?> ref : parameterTypes) {
			l.add(ref);
		}
		methodRef.setParameterTypes(l);
		return methodRef;
	}

	/**
	 * Creates an executable reference.
	 *
	 * @param declaringType
	 *            reference to the declaring type
	 * @param isStatic
	 *            if this reference references a static executable
	 * @param type
	 *            the return type of the executable
	 * @param methodName
	 *            simple name
	 * @param parameterTypes
	 *            list of parameter's types
	 */
	public <T> CtExecutableReference<T> createReference(
			CtTypeReference<?> declaringType, boolean isStatic,
			CtTypeReference<T> type, String methodName,
			List<CtTypeReference<?>> parameterTypes) {
		CtExecutableReference<T> methodRef = factory.Core()
				.createExecutableReference();
		methodRef.setStatic(isStatic);
		methodRef.setDeclaringType(declaringType);
		methodRef.setSimpleName(methodName);
		methodRef.setType(type);
		List<CtTypeReference<?>> l = new ArrayList<CtTypeReference<?>>();
		for (CtTypeReference<?> ref : parameterTypes) {
			l.add(ref);
		}
		methodRef.setParameterTypes(l);
		return methodRef;
	}

	/**
	 * Creates an executable reference.
	 *
	 * @param declaringType
	 *            reference to the declaring type
	 * @param type
	 *            the return type of the executable
	 * @param methodName
	 *            simple name
	 * @param parameterTypes
	 *            list of parameter's types
	 */
	public <T> CtExecutableReference<T> createReference(
			CtTypeReference<?> declaringType, CtTypeReference<T> type,
			String methodName, List<CtTypeReference<?>> parameterTypes) {
		CtExecutableReference<T> methodRef = factory.Core()
				.createExecutableReference();
		methodRef.setDeclaringType(declaringType);
		methodRef.setSimpleName(methodName);
		methodRef.setType(type);
		List<CtTypeReference<?>> l = new ArrayList<CtTypeReference<?>>();
		for (CtTypeReference<?> ref : parameterTypes) {
			l.add(ref);
		}
		methodRef.setParameterTypes(l);
		return methodRef;
	}

	/**
	 * Creates an executable reference from its signature, as defined by the
	 * executable reference's toString.
	 */
	public <T> CtExecutableReference<T> createReference(String signature) {
		CtExecutableReference<T> executableRef = factory.Core()
				.createExecutableReference();
		String type = signature.substring(0, signature.indexOf(" "));
		String declaringType = signature.substring(signature.indexOf(" ") + 1,
				signature.indexOf(CtExecutable.EXECUTABLE_SEPARATOR));
		String executableName = signature.substring(signature
				.indexOf(CtExecutable.EXECUTABLE_SEPARATOR) + 1, signature
				.indexOf("("));
		executableRef.setSimpleName(executableName);
		executableRef.setDeclaringType(factory.Type().createReference(
				declaringType));
		CtTypeReference<T> typeRef = factory.Type().createReference(type);
		executableRef.setType(typeRef);
		String parameters = signature.substring(signature.indexOf("(") + 1,
				signature.indexOf(")"));
		StringTokenizer t = new StringTokenizer(parameters, ",");
		while (t.hasMoreTokens()) {
			String paramType = t.nextToken();
			executableRef.getParameterTypes().add(
					factory.Type().createReference(paramType));
		}
		return executableRef;
	}

}
