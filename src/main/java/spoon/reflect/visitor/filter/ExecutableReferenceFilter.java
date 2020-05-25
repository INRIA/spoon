/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.SpoonException;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This filter matches all the {@link CtExecutableReference} referencing defined one or more {@link CtExecutable}s.
 */
public class ExecutableReferenceFilter implements Filter<CtExecutableReference<?>> {

	private Map<CtExecutable<?>, CtExecutable<?>> executables = new IdentityHashMap<>();
	private Set<String> typeQualifiedNames = new HashSet<>();
	private Set<String> methodNames = new HashSet<>();

	/**
	 * Creates a new executable reference filter.
	 *
	 * Call {@link #addExecutable(CtExecutable)} to define executables
	 * whose references it matches.
	 */
	public ExecutableReferenceFilter() {
	}

	/**
	 * Creates a new executable reference filter.
	 *
	 * @param executable
	 * 		the executable whose references it matches
	 */
	public ExecutableReferenceFilter(CtExecutable<?> executable) {
		addExecutable(executable);
	}

	/**
	 * Add next {@link CtExecutable} whose {@link CtExecutableReference}s has to be matched
	 *
	 * @param executable searched {@link CtExecutable} instance
	 * @return this to support fluent API
	 */
	public ExecutableReferenceFilter addExecutable(CtExecutable<?> executable) {
		executables.put(executable, executable);
		if (executable instanceof CtTypeMember) {
			CtType<?> declType = ((CtTypeMember) executable).getDeclaringType();
			if (declType == null) {
				throw new SpoonException("Cannot search for executable reference, which has no declaring type");
			}
			typeQualifiedNames.add(declType.getQualifiedName());
			if (executable instanceof CtMethod<?>) {
				methodNames.add(((CtMethod<?>) executable).getSimpleName());
			}
		}
		return this;
	}

	@Override
	public boolean matches(CtExecutableReference<?> execRef) {
		if (execRef.getSimpleName().startsWith(CtExecutableReference.LAMBDA_NAME_PREFIX)) {
			//reference to lambda
			return executables.containsKey(execRef.getDeclaration());
		} else {
			//reference to constructor or method
			CtTypeReference<?> declaringType = execRef.getDeclaringType();
			if (declaringType != null && typeQualifiedNames.contains(declaringType.getQualifiedName())) {
				if (CtExecutableReference.CONSTRUCTOR_NAME.equals(execRef.getSimpleName()) || methodNames.contains(execRef.getSimpleName())) {
					return executables.containsKey(execRef.getDeclaration());
				}
			}
		}
		return false;
	}
}
