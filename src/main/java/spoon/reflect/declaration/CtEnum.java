/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.support.UnsettableProperty;

import java.util.List;

import static spoon.reflect.path.CtRole.VALUE;

/**
 * This element represents an enumeration declaration.
 *
 * Example:
 * <pre>
 *    enum Boolean { TRUE, FALSE }
 * </pre>

 */
public interface CtEnum<T extends Enum<?>> extends CtClass<T> {
	/**
	 * Adds an enum value.
	 *
	 * @param enumValue
	 * 		An enum value.
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = VALUE)
	<C extends CtEnum<T>> C addEnumValue(CtEnumValue<?> enumValue);

	/**
	 * Removes en enum value.
	 *
	 * @param enumValue
	 * 		An enum value.
	 * @return <tt>true</tt> if this element changed as a result of the call
	 */
	@PropertySetter(role = VALUE)
	boolean removeEnumValue(CtEnumValue<?> enumValue);

	/**
	 * Gets an enum value by its name.
	 *
	 * @param name
	 * 		Name of the enum value.
	 * @return An enum value.
	 */
	@PropertyGetter(role = VALUE)
	CtEnumValue<?> getEnumValue(String name);

	/**
	 * Gets all enum values of the enumeration.
	 *
	 * @return All enum values.
	 */
	@PropertyGetter(role = VALUE)
	List<CtEnumValue<?>> getEnumValues();

	/**
	 *Sets all enum values of the enum.
	 */
	@PropertySetter(role = VALUE)
	<C extends CtEnum<T>> C setEnumValues(List<CtEnumValue<?>> enumValues);

	@Override
	CtEnum<T> clone();

	@Override
	@UnsettableProperty
	<T extends CtFormalTypeDeclarer> T setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters);

	@Override
	@UnsettableProperty
	<C extends CtType<T>> C setSuperclass(CtTypeReference<?> superClass);
}
