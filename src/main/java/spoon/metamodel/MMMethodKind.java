/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.metamodel;

import java.util.function.Predicate;

import spoon.SpoonException;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

/**
 * Represents the type of metamodel method.
 * eg {@link spoon.reflect.declaration.CtType#addField(CtField)} has MMMethodKind{@link #ADD_FIRST}.
 */
public enum MMMethodKind {
	/**
	 * Getter.
	 * T get()
	 */
	GET(-1, false, 1, m -> m.getParameters().isEmpty() && (m.getSimpleName().startsWith("get") || m.getSimpleName().startsWith("is"))),
	/**
	 * Setter
	 * void set(T)
	 */
	SET(0, false, 1, m -> m.getParameters().size() == 1 && m.getSimpleName().startsWith("set")),
	/**
	 * void addFirst(T)
	 */
	ADD_FIRST(0, true, 10, m -> {
		if (m.getParameters().size() == 1) {
			if (m.getSimpleName().startsWith("add") || m.getSimpleName().startsWith("insert")) {
				return m.getSimpleName().endsWith("AtTop") || m.getSimpleName().endsWith("Begin");
			}
		}
		return false;
	}),
	/**
	 * void add(T)
	 */
	ADD_LAST(0, true,  1, m -> {
		if (m.getParameters().size() == 1) {
			return m.getSimpleName().startsWith("add") || m.getSimpleName().startsWith("insert");
		}
		return false;
	}),
	/**
	 * void addOn(int, T)
	 */
	ADD_ON(1, true, 1, m -> {
		if (m.getParameters().size() == 2 && "int".equals(m.getParameters().get(0).getType().getSimpleName())) {
			return m.getSimpleName().startsWith("add") || m.getSimpleName().startsWith("insert");
		}
		return false;
	}),
	/**
	 * void remove(T)
	 */
	REMOVE(0, true, 1, m -> m.getParameters().size() == 1 && m.getSimpleName().startsWith("remove")),

	/**
	 * Return element by its name
	 * T get(String)
	 */
	GET_BY(-1, true, 1, m -> m.getSimpleName().startsWith("get")
			&& m.getParameters().size() == 1  && m.getParameters().get(0).getType().getQualifiedName().equals(String.class.getName())),

	/**
	 * The not matching method
	 */
	OTHER(-2, false, 0, m -> true);

	private final Predicate<CtMethod<?>> detector;
	private final int level;
	private final boolean multi;
	private final int valueParameterIndex;

	MMMethodKind(int valueParameterIndex, boolean multi, int level, Predicate<CtMethod<?>> detector) {
		this.multi = multi;
		this.level = level;
		this.detector = detector;
		this.valueParameterIndex = valueParameterIndex;
	}

	/**
	 * @return true if this accessor provides access to elements of a collection.
	 * 		 false if it accessed full value of attribute
	 */
	public boolean isMulti() {
		return multi;
	}

	/**
	 * Detect kind of method
	 * @param method to be check method
	 * @return detected {@link MMMethodKind}, which fits to the `method`
	 */
	public static MMMethodKind kindOf(CtMethod<?> method) {
		MMMethodKind result = OTHER;
		for (MMMethodKind k : values()) {
			if (k.detector.test(method) && result.level <= k.level) {
				if (result.level == k.level && k != OTHER) {
					throw new SpoonException("Ambiguous method kinds " + result.name() + " X " + k.name() + " for method " + method.getSignature());
				}
				result = k;
			}
		}
		return result;
	}
}
