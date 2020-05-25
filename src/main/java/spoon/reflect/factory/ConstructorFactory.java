/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * The {@link CtConstructor} sub-factory.
 */
public class ConstructorFactory extends ExecutableFactory {

	/**
	 * Creates a new constructor sub-factory.
	 *
	 * @param factory
	 * 		the parent factory
	 */
	public ConstructorFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Copies a constructor into a target class.
	 *
	 * @param target
	 * 		the target class
	 * @param source
	 * 		the constructor to be copied
	 * @return the new constructor
	 */
	@SuppressWarnings("unchecked")
	public <T> CtConstructor<T> create(CtClass<T> target, CtConstructor<?> source) {
		CtConstructor<T> newConstructor = (CtConstructor<T>) source.clone();
		target.addConstructor(newConstructor);
		return newConstructor;
	}

	/**
	 * Creates a constructor into a target class by copying it from a source
	 * method.
	 *
	 * @param target
	 * 		the target class
	 * @param source
	 * 		the method to be copied
	 * @return the new constructor
	 */
	@SuppressWarnings("unchecked")
	public <T> CtConstructor<T> create(CtClass<T> target, CtMethod<?> source) {
		CtMethod<T> method = (CtMethod<T>) source.clone();
		CtConstructor<T> newConstructor = factory.Core().createConstructor();
		newConstructor.setAnnotations(method.getAnnotations());
		newConstructor.setBody(method.getBody());
		newConstructor.setDocComment(method.getDocComment());
		newConstructor.setFormalCtTypeParameters(method.getFormalCtTypeParameters());
		newConstructor.setModifiers(method.getModifiers());
		newConstructor.setParameters(method.getParameters());
		target.addConstructor(newConstructor);
		return newConstructor;
	}

	/**
	 * Creates an empty constructor.
	 *
	 * @param modifiers
	 * 		the modifiers
	 * @param parameters
	 * 		the parameters
	 * @param thrownTypes
	 * 		the thrown types
	 */
	public <T> CtConstructor<T> create(CtClass<T> target, Set<ModifierKind> modifiers, List<CtParameter<?>> parameters,
			Set<CtTypeReference<? extends Throwable>> thrownTypes) {
		CtConstructor<T> constructor = factory.Core().createConstructor();
		constructor.setModifiers(modifiers);
		constructor.setParameters(parameters);
		constructor.setThrownTypes(thrownTypes);
		target.addConstructor(constructor);
		return constructor;
	}

	/**
	 * Create the default empty constructor.
	 *
	 * @param target
	 * 		the class to insert the constructor into
	 * @return the created constructor
	 */
	public <T> CtConstructor<T> createDefault(CtClass<T> target) {
		CtConstructor<T> constructor = factory.Core().createConstructor();
		constructor.addModifier(ModifierKind.PUBLIC);
		target.addConstructor(constructor);
		return constructor;
	}

	/**
	 * Creates a constructor.
	 *
	 * @param modifiers
	 * 		the modifiers
	 * @param parameters
	 * 		the parameters
	 * @param thrownTypes
	 * 		the thrown types
	 * @param body
	 * 		the body
	 */
	public <T> CtConstructor<T> create(CtClass<T> target, Set<ModifierKind> modifiers, List<CtParameter<?>> parameters,
			Set<CtTypeReference<? extends Throwable>> thrownTypes, CtBlock<T> body) {
		CtConstructor<T> constructor = create(target, modifiers, parameters, thrownTypes);
		constructor.setBody(body);
		return constructor;
	}

	/**
	 * Creates a constructor reference from an existing constructor.
	 */
	public <T> CtExecutableReference<T> createReference(CtConstructor<T> c) {
		return factory.Executable().createReference(c);
	}

	/**
	 * Creates a constructor reference from an actual constructor.
	 */
	public <T> CtExecutableReference<T> createReference(Constructor<T> constructor) {
		CtTypeReference<T> type = factory.Type().createReference(constructor.getDeclaringClass());
		return createReference(type, type.clone(), CtExecutableReference.CONSTRUCTOR_NAME,
				factory.Type().createReferences(Arrays.asList(constructor.getParameterTypes())));
	}

	/**
	 * Creates a constructor reference.
	 * @param type Declaring type of the constructor.
	 * @param parameters Constructor parameters.
	 * @param <T> Infered type of the constructor.
	 * @return CtExecutablereference if a constructor.
	 */
	public <T> CtExecutableReference<T> createReference(CtTypeReference<T> type, CtExpression<?>...parameters) {
		final CtExecutableReference<T> executableReference = factory.Core().createExecutableReference();
		executableReference.setType(type);
		executableReference.setDeclaringType(type == null ? null : type.clone());
		executableReference.setSimpleName(CtExecutableReference.CONSTRUCTOR_NAME);
		List<CtTypeReference<?>> typeReferences = new ArrayList<>();
		for (CtExpression<?> parameter : parameters) {
			typeReferences.add(parameter.getType() == null ? null : parameter.getType().clone());
		}
		executableReference.setParameters(typeReferences);
		return executableReference;
	}

}
