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
package spoon.reflect.factory;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import static spoon.reflect.ModelElementContainerDefaultCapacities.PARAMETERS_CONTAINER_DEFAULT_CAPACITY;

/**
 * The {@link CtExecutable} sub-factory.
 */
public class ExecutableFactory extends SubFactory {

	/**
	 * Creates a new executable sub-factory.
	 *
	 * @param factory
	 * 		the parent factory
	 */
	public ExecutableFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates an anonymous executable (initializer block) in a target class).
	 */
	public CtAnonymousExecutable createAnonymous(CtClass<?> target, CtBlock<Void> body) {
		CtAnonymousExecutable a = factory.Core().createAnonymousExecutable();
		target.addAnonymousExecutable(a);
		a.setBody(body);
		return a;
	}

	/**
	 * Creates a new parameter.
	 */
	public <T> CtParameter<T> createParameter(CtExecutable<?> parent, CtTypeReference<T> type, String name) {
		CtParameter<T> parameter = factory.Core().createParameter();
		parameter.setType(type);
		parameter.setSimpleName(name);
		if (parent != null) {
			parent.addParameter(parameter);
		}
		return parameter;
	}

	/**
	 * Creates a parameter reference from an existing parameter.
	 *
	 * @param <T>
	 * 		the parameter's type
	 * @param parameter
	 * 		the parameter
	 */
	public <T> CtParameterReference<T> createParameterReference(CtParameter<T> parameter) {
		CtParameterReference<T> ref = factory.Core().createParameterReference();
		ref.setSimpleName(parameter.getSimpleName());
		ref.setType(parameter.getType());
		return ref;
	}

	/**
	 * Creates an executable reference from an existing executable.
	 */
	public <T> CtExecutableReference<T> createReference(CtExecutable<T> e) {
		CtExecutableReference<T> er = createReferenceInternal(e);
		er.setParent(e);
		return er;
	}

	private <T> CtExecutableReference<T> createReferenceInternal(CtExecutable<T> e) {
		CtTypeReference<?> refs[] = new CtTypeReference[e.getParameters().size()];
		int i = 0;
		for (CtParameter<?> param : e.getParameters()) {
			refs[i++] = param.getType() != null
					? param.getType().clone()
					// With a lambda and in noclasspath (when the type of
					// parameters isn't specified), we assume Object.
					: factory.Type().OBJECT.clone();
		}
		String executableName = e.getSimpleName();
		if (e instanceof CtMethod) {
			boolean isStatic = ((CtMethod) e).hasModifier(ModifierKind.STATIC);
			return createReference(((CtMethod<T>) e).getDeclaringType().getReference(), isStatic, ((CtMethod<T>) e).getType().clone(), executableName, refs);
		} else if (e instanceof CtLambda) {
			CtMethod<T> lambdaMethod = ((CtLambda) e).getOverriddenMethod();
			return createReference(e.getParent(CtType.class).getReference(), lambdaMethod == null ? null : lambdaMethod.getType().clone(), executableName, refs);
		} else if (e instanceof CtAnonymousExecutable) {
			return createReference(((CtAnonymousExecutable) e).getDeclaringType().getReference(), e.getType().clone(), executableName);
		}
		// constructor
		return createReference(((CtConstructor<T>) e).getDeclaringType().getReference(), ((CtConstructor<T>) e).getType().clone(), CtExecutableReference.CONSTRUCTOR_NAME, refs);
	}

	/**
	 * Creates an executable reference.
	 *
	 * @param declaringType
	 * 		reference to the declaring type
	 * @param type
	 * 		the executable's type
	 * @param methodName
	 * 		simple name
	 * @param parameterTypes
	 * 		list of parameter's types
	 */
	public <T> CtExecutableReference<T> createReference(CtTypeReference<?> declaringType, CtTypeReference<T> type, String methodName, CtTypeReference<?>... parameterTypes) {
		return createReference(declaringType, false, type, methodName, parameterTypes);
	}

	/**
	 * Creates an executable reference.
	 *
	 * @param declaringType
	 * 		reference to the declaring type
	 * @param isStatic
	 * 		if this reference references a static executable
	 * @param type
	 * 		the return type of the executable
	 * @param methodName
	 * 		simple name
	 * @param parameterTypes
	 * 		list of parameter's types
	 */
	public <T> CtExecutableReference<T> createReference(CtTypeReference<?> declaringType, boolean isStatic, CtTypeReference<T> type, String methodName, CtTypeReference<?>... parameterTypes) {
		return createReference(declaringType, isStatic, type, methodName, Arrays.asList(parameterTypes));
	}

	/**
	 * Creates an executable reference.
	 *
	 * @param declaringType
	 * 		reference to the declaring type
	 * @param isStatic
	 * 		if this reference references a static executable
	 * @param type
	 * 		the return type of the executable
	 * @param methodName
	 * 		simple name
	 * @param parameterTypes
	 * 		list of parameter's types
	 */
	public <T> CtExecutableReference<T> createReference(CtTypeReference<?> declaringType, boolean isStatic, CtTypeReference<T> type, String methodName, List<CtTypeReference<?>> parameterTypes) {
		CtExecutableReference<T> methodRef = factory.Core().createExecutableReference();
		methodRef.setStatic(isStatic);
		methodRef.setDeclaringType(declaringType);
		methodRef.setSimpleName(methodName);
		methodRef.setType(type);
		List<CtTypeReference<?>> l = new ArrayList<>(parameterTypes);
		methodRef.setParameters(l);
		return methodRef;
	}

	/**
	 * Creates an executable reference.
	 *
	 * @param declaringType
	 * 		reference to the declaring type
	 * @param type
	 * 		the return type of the executable
	 * @param methodName
	 * 		simple name
	 * @param parameterTypes
	 * 		list of parameter's types
	 */
	public <T> CtExecutableReference<T> createReference(CtTypeReference<?> declaringType, CtTypeReference<T> type, String methodName, List<CtTypeReference<?>> parameterTypes) {
		CtExecutableReference<T> methodRef = factory.Core().createExecutableReference();
		methodRef.setDeclaringType(declaringType);
		methodRef.setSimpleName(methodName);
		methodRef.setType(type);
		List<CtTypeReference<?>> l = new ArrayList<>(parameterTypes);
		methodRef.setParameters(l);
		return methodRef;
	}

	/**
	 * Creates an executable reference from its signature, as defined by the
	 * executable reference's toString.
	 */
	public <T> CtExecutableReference<T> createReference(String signature) {
		CtExecutableReference<T> executableRef = factory.Core().createExecutableReference();
		String type = signature.substring(0, signature.indexOf(" "));
		String declaringType = signature.substring(signature.indexOf(" ") + 1, signature.indexOf(CtExecutable.EXECUTABLE_SEPARATOR));
		String executableName = signature.substring(signature.indexOf(CtExecutable.EXECUTABLE_SEPARATOR) + 1, signature.indexOf("("));
		executableRef.setSimpleName(executableName);
		executableRef.setDeclaringType(factory.Type().createReference(declaringType));
		CtTypeReference<T> typeRef = factory.Type().createReference(type);
		executableRef.setType(typeRef);
		String parameters = signature.substring(signature.indexOf("(") + 1, signature.indexOf(")"));
		List<CtTypeReference<?>> params = new ArrayList<>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		StringTokenizer t = new StringTokenizer(parameters, ",");
		while (t.hasMoreTokens()) {
			String paramType = t.nextToken();
			params.add(factory.Type().createReference(paramType));
		}
		executableRef.setParameters(params);
		return executableRef;
	}

}
