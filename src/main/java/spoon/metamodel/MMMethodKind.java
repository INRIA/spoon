/**
 * Copyright (C) 2006-2018 INRIA and contributors
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
			if (k.detector.test(method) && result.level < k.level) {
				if (result.level == k.level) {
					throw new SpoonException("Ambiguous method kinds " + result.name() + " X " + k.name() + " for method " + method.getSignature());
				}
				result = k;
			}
		}
		return result;
	}
}
