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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import spoon.reflect.Factory;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * The {@link CtConstructor} sub-factory.
 */
public class ConstructorFactory extends ExecutableFactory {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new constructor sub-factory.
	 *
	 * @param factory
	 *            the parent factory
	 */
	public ConstructorFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Copies a constructor into a target class.
	 *
	 * @param target
	 *            the target class
	 * @param source
	 *            the constructor to be copied
	 * @return the new constructor
	 */
	@SuppressWarnings("unchecked")
	public <T> CtConstructor<T> create(CtClass<T> target,
			CtConstructor<?> source) {
		CtConstructor<T> newConstructor = factory.Core().clone(
				(CtConstructor<T>) source);
		target.getConstructors().add(newConstructor);
		newConstructor.setParent(target);
		return newConstructor;
	}

	/**
	 * Creates a constructor into a target class by copying it from a source
	 * method.
	 *
	 * @param target
	 *            the target class
	 * @param source
	 *            the method to be copied
	 * @return the new constructor
	 */
	@SuppressWarnings("unchecked")
	public <T> CtConstructor<T> create(CtClass<T> target, CtMethod<?> source) {
		CtMethod<T> method = factory.Core().clone((CtMethod<T>) source);
		CtConstructor<T> newConstructor = factory.Core().createConstructor();
		newConstructor.setAnnotations(method.getAnnotations());
		newConstructor.setBody(method.getBody());
		newConstructor.setDocComment(method.getDocComment());
		newConstructor
				.setFormalTypeParameters(method.getFormalTypeParameters());
		newConstructor.setModifiers(method.getModifiers());
		newConstructor.setParameters(method.getParameters());
		setParent(newConstructor, method.getAnnotations(), method.getBody(),
				method.getParameters(), method.getFormalTypeParameters());
		target.getConstructors().add(newConstructor);
		newConstructor.setParent(target);
		return newConstructor;
	}

	/**
	 * Creates an empty constructor.
	 *
	 * @param modifiers
	 *            the modifiers
	 * @param parameters
	 *            the parameters
	 * @param thrownTypes
	 *            the thrown types
	 */
	public <T> CtConstructor<T> create(CtClass<T> target,
			Set<ModifierKind> modifiers, List<CtParameter<?>> parameters,
			Set<CtTypeReference<? extends Throwable>> thrownTypes) {
		CtConstructor<T> constructor = factory.Core().createConstructor();
		constructor.setModifiers(modifiers);
		constructor.setParent(target);
		constructor.setParameters(parameters);
		constructor.setThrownTypes(thrownTypes);
		setParent(constructor, parameters);
		target.getConstructors().add(constructor);
		return constructor;
	}

	/**
	 * Creates a constructor.
	 *
	 * @param modifiers
	 *            the modifiers
	 * @param parameters
	 *            the parameters
	 * @param thrownTypes
	 *            the thrown types
	 * @param body
	 *            the body
	 */
	public <T> CtConstructor<T> create(CtClass<T> target,
			Set<ModifierKind> modifiers, List<CtParameter<?>> parameters,
			Set<CtTypeReference<? extends Throwable>> thrownTypes,
			CtBlock<T> body) {
		CtConstructor<T> constructor = create(target, modifiers, parameters,
				thrownTypes);
		constructor.setBody(body);
		body.setParent(constructor);
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
		CtTypeReference<T> type=factory.Type().createReference(constructor.getDeclaringClass());
		return createReference(type, type, CtExecutableReference.CONSTRUCTOR_NAME,
				factory.Type().createReferences(
						(List<Class<?>>) Arrays.asList(constructor.getParameterTypes())));
	}

}
