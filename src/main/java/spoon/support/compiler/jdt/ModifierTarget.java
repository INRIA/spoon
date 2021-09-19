/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

import spoon.reflect.declaration.ModifierKind;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

enum ModifierTarget {
	/**
	 * JLS $ 8.3.1
	 */
	FIELD(
			ModifierKind.PUBLIC,
			ModifierKind.PROTECTED,
			ModifierKind.PRIVATE,
			ModifierKind.STRICTFP,
			ModifierKind.FINAL,
			ModifierKind.TRANSIENT,
			ModifierKind.VOLATILE
	),
	/**
	 * JLS $ 8.4.1
	 */
	LOCAL_VARIABLE(ModifierKind.FINAL),
	/**
	 * JLS $ 8.4.3
	 */
	METHOD(
			ModifierKind.PUBLIC,
			ModifierKind.PROTECTED,
			ModifierKind.PRIVATE,
			ModifierKind.ABSTRACT,
			ModifierKind.STATIC,
			ModifierKind.FINAL,
			ModifierKind.SYNCHRONIZED,
			ModifierKind.NATIVE,
			ModifierKind.STRICTFP
	),
	/**
	 * JLS $ 8.8.3
	 */
	CONSTRUCTOR(
			ModifierKind.PUBLIC,
			ModifierKind.PROTECTED,
			ModifierKind.PRIVATE
	),
	/**
	 * JLS $ 8.1.1
	 */
	CLASS(
			ModifierKind.PUBLIC,
			ModifierKind.PROTECTED,
			ModifierKind.PRIVATE,
			ModifierKind.ABSTRACT,
			ModifierKind.STATIC,
			ModifierKind.FINAL,
			ModifierKind.STRICTFP
	),

	/**
	 * JLS $ 9.1.1
	 */
	INTERFACE(
			ModifierKind.PUBLIC,
			ModifierKind.PROTECTED,
			ModifierKind.PRIVATE,
			ModifierKind.ABSTRACT,
			ModifierKind.STATIC,
			ModifierKind.STRICTFP
	),
	/**
	 * JLS $ 8.4.1
	 */
	PARAMETER(ModifierKind.FINAL);

	private Set<ModifierTarget> singleton;
	private final Set<ModifierKind> allowedKinds;

	/**
	 * Two parameters to make easy use of {@link EnumSet#of(Enum, Enum[])}.
	 *
	 * @param firstAllowed the first kind
	 * @param allowedKinds the remaining kind
	 */
	ModifierTarget(ModifierKind firstAllowed, ModifierKind... allowedKinds) {
		this.allowedKinds = EnumSet.of(firstAllowed, allowedKinds);
	}

	public static final Set<ModifierTarget> NONE = Collections.unmodifiableSet(
			EnumSet.noneOf(ModifierTarget.class)
	);

	public static final Set<ModifierTarget> VARIABLE = Collections.unmodifiableSet(
			EnumSet.of(FIELD, LOCAL_VARIABLE)
	);

	public static final Set<ModifierTarget> EXECUTABLE = Collections.unmodifiableSet(
			EnumSet.of(METHOD, CONSTRUCTOR)
	);

	public static final Set<ModifierTarget> TYPE = Collections.unmodifiableSet(
			EnumSet.of(CLASS, INTERFACE)
	);

	public Set<ModifierTarget> asSingleton() {
		if (singleton == null) {
			singleton = Collections.singleton(this);
		}
		return singleton;
	}


	public Set<ModifierKind> getAllowedKinds() {
		return allowedKinds;
	}
}
