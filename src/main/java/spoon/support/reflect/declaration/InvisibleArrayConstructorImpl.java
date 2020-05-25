/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;


/**
 * This class is used to represent the constructor of an array when calling with an expression like
 * <pre>
 *     String[]::new
 * </pre>
 *
 * Warning: this type is never present in the Spoon model.
 * It is created dynamically when calling the executable of an expression such as the one in the example.
 */
public class InvisibleArrayConstructorImpl<T> extends CtConstructorImpl<T> {

	@MetamodelPropertyField(role = CtRole.TYPE)
	private CtTypeReference<T> type;

	@Override
	public CtTypeReference<T> getType() {
		return this.type;
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference<T> type) {
		if (type == null) {
			return (C) this;
		}

		this.type = type;
		return (C) this;
	}

	@Override
	public String toString() {
		return this.getType().toString() + "::new";
	}

	@Override
	public CtType<T> getDeclaringType() {
		return this.getType().getTypeDeclaration();
	}
}
