/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.code;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;

import java.util.List;

// TODO (440) metamodel

/**
 * This code element defines a record pattern, introduced in Java 21
 * by <a href=https://openjdk.java.net/jeps/440>JEP 440</a>.
 * <p>
 * Example:
 * <pre>
 *     Object obj = null;
 *     boolean longerThanTwo = false;
 *     record MyRecord(String value) {}
 *     // MyRecord(var string) is the record pattern
 *     if (obj instanceof MyRecord(var string)) {
 *         longerThanTwo = string.length() > 2;
 *     }
 * </pre>
 */
public interface CtRecordPattern extends CtPattern, CtExpression<Void> {

	/**
	 * {@return the type of the deconstructed record}
	 */
	@PropertyGetter(role = CtRole.TYPE_REF)
	CtTypeReference<?> getRecordType();

	/**
	 * Sets the type of the deconstructed record.
	 * @param recordType the record type.
	 * @return this pattern
	 */
	@PropertySetter(role = CtRole.TYPE_REF)
	CtRecordPattern setRecordType(CtTypeReference<?> recordType);

	/**
	 * {@return the inner patterns of this record pattern}
	 */
	@PropertyGetter(role = CtRole.PATTERN)
	List<CtPattern> getPatternList();

	/**
	 * Sets the inner patterns of this record pattern.
	 * @param patternList the list of inner patterns.
	 * @return this pattern
	 */
	@PropertySetter(role = CtRole.PATTERN)
	CtRecordPattern setPatternList(List<CtPattern> patternList);

	/**
	 * Adds an inner patterns to the list of inner patterns of this record.
	 * @param pattern the inner pattern.
	 * @return this pattern
	 */
	@PropertySetter(role = CtRole.PATTERN)
	CtRecordPattern addPattern(CtPattern pattern);

	@Override
	CtRecordPattern clone();

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
