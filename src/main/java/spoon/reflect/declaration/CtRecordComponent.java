/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;
/**
 * This element represents a record component.
 *
 * Example:
 * <pre>
 *    // x is a record component
 *    record Point(int x) {
 *    }
 * </pre>
 */
public interface CtRecordComponent extends CtTypedElement<Object>, CtNamedElement, CtShadowable {

	/**
	 * Converts the component to an implicit method. The returned method is a view and has no parent.
	 * This means that any modification on the returned method will not be reflected on the component.
	 * Also this element <strong>is not</strong> part of the model. A record already has the methods corresponding to its components.
	 * Use {@link CtRecord#getMethods()} to get the getter methods of a record.
	 *
	 * @return the method corresponding to the component (a getter) as a view.
	 */
	CtMethod<?> toMethod();

	/**
	 * Converts the component to an implicit field.The returned field is a view and has <b>no</b> parent.
	 * This means that any modification on the returned field will not be reflected on the component.
	 * Also this element <strong>is not</strong> part of the model. A record already has the field corresponding to its components.
	 * Use {@link CtRecord#getFields()} to get the fields of a record.
	 *
	 * @return the field corresponding to the component as a view.
	 */
	CtField<?> toField();

	@Override
	CtRecordComponent clone();
}
