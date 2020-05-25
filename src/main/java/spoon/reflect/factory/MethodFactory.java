/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.template.Substitution;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The {@link CtMethod} sub-factory.
 */
public class MethodFactory extends ExecutableFactory {

	public final Set<CtMethod<?>> OBJECT_METHODS = Collections.unmodifiableSet(factory.Class().get(Object.class).getMethods());

	/**
	 * Creates a new method sub-factory.
	 *
	 * @param factory
	 * 		the parent factory
	 */
	public MethodFactory(Factory factory) {
		super(factory);
	}

	/**
	 * Creates a method.
	 *
	 * @param target
	 * 		the class where the method is inserted
	 * @param modifiers
	 * 		the modifiers
	 * @param returnType
	 * 		the method's return type
	 * @param name
	 * 		the method's name
	 * @param parameters
	 * 		the parameters
	 * @param thrownTypes
	 * 		the thrown types
	 * @param body
	 * 		the method's body
	 */
	public <R, B extends R> CtMethod<R> create(CtClass<?> target, Set<ModifierKind> modifiers, CtTypeReference<R> returnType, String name, List<CtParameter<?>> parameters,
			Set<CtTypeReference<? extends Throwable>> thrownTypes, CtBlock<B> body) {
		CtMethod<R> method = create(target, modifiers, returnType, name, parameters, thrownTypes);
		method.setBody(body);
		return method;
	}

	/**
	 * Creates a method by copying an existing method.
	 *
	 * @param <T>
	 * 		the type of the method
	 * @param target
	 * 		the target type where the new method has to be inserted to
	 * @param source
	 * 		the source method to be copied
	 * @param redirectReferences
	 * 		tells if all the references to the owning type of the source
	 * 		method should be redirected to the target type (true is
	 * 		recommended for most uses)
	 * @return the newly created method
	 */
	public <T> CtMethod<T> create(CtType<?> target, CtMethod<T> source, boolean redirectReferences) {
		CtMethod<T> newMethod = source.clone();
		if (redirectReferences && (source.getDeclaringType() != null)) {
			Substitution.redirectTypeReferences(newMethod, source.getDeclaringType().getReference(), target.getReference());
		}
		target.addMethod(newMethod);
		return newMethod;
	}

	/**
	 * Creates an empty method.
	 *
	 * @param target
	 * 		the class where the method is inserted
	 * @param modifiers
	 * 		the modifiers
	 * @param returnType
	 * 		the method's return type
	 * @param name
	 * 		the method's name
	 * @param parameters
	 * 		the parameters
	 * @param thrownTypes
	 * 		the thrown types
	 */
	public <T> CtMethod<T> create(CtType<?> target, Set<ModifierKind> modifiers, CtTypeReference<T> returnType, String name, List<CtParameter<?>> parameters,
			Set<CtTypeReference<? extends Throwable>> thrownTypes) {
		CtMethod<T> method = factory.Core().createMethod();
		if (modifiers != null) {
			method.setModifiers(modifiers);
		}
		method.setType(returnType);
		method.setSimpleName(name);
		if (parameters != null) {
			method.setParameters(parameters);
		}
		if (thrownTypes != null) {
			method.setThrownTypes(thrownTypes);
		}
		target.addMethod(method);
		return method;
	}

	/**
	 * Creates a method reference.
	 */
	public <T> CtExecutableReference<T> createReference(CtMethod<T> m) {
		return factory.Executable().createReference(m);
	}

	/**
	 * Creates a method reference from an actual method.
	 */
	@SuppressWarnings("unchecked")
	public <T> CtExecutableReference<T> createReference(Method method) {
		return createReference(factory.Type().createReference(method.getDeclaringClass()), (CtTypeReference<T>) factory.Type().createReference(method.getReturnType()), method.getName(),
				factory.Type().createReferences(Arrays.asList(method.getParameterTypes())).toArray(new CtTypeReference<?>[0]));
	}

	/**
	 * Gets all the main methods stored in this factory.
	 */
	public Collection<CtMethod<Void>> getMainMethods() {
		Collection<CtMethod<Void>> methods = new ArrayList<>();
		for (CtType<?> t : factory.Type().getAll()) {
			if (t instanceof CtClass) {
				CtMethod<Void> m = ((CtClass<?>) t).getMethod(factory.Type().createReference(void.class), "main", factory.Type().createArrayReference(factory.Type().createReference(String.class)));
				if ((m != null) && m.getModifiers().contains(ModifierKind.STATIC)) {
					methods.add(m);
				}
			}
		}
		return methods;
	}

}
