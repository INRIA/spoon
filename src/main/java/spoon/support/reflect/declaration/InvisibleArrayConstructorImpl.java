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
