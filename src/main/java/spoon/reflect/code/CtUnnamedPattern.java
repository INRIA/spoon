/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;

import java.util.List;

/**
 * This code element defines an unnamed pattern, introduced in Java 2
 * by <a href=https://openjdk.java.net/jeps/394>JEP 456</a>.
 * <p>
 * Example:
 * <pre>
 *     Object obj = new Object();
 *     record X(int i) {}
 *     int i = switch (obj) {
 *         case X(_) -> 0; // an unnamed pattern does neither mention a type nor a name
 *         case null, default -> -1;
 *     };
 * </pre>
 */
public interface CtUnnamedPattern extends CtPattern, CtExpression<Void> {

	@Override
	CtUnnamedPattern clone();

	@Override
	@UnsettableProperty
	List<CtTypeReference<?>> getTypeCasts();

	@Override
	@UnsettableProperty
	<C extends CtExpression<Void>> C setTypeCasts(List<CtTypeReference<?>> types);

	@Override
	@UnsettableProperty
	<C extends CtExpression<Void>> C addTypeCast(CtTypeReference<?> type);

}
