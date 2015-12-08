/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

public class CtEnumImpl<T extends Enum<?>> extends CtClassImpl<T>
		implements CtEnum<T> {
	private static final long serialVersionUID = 1L;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtEnum(this);
	}

	@Override
	public Set<CtMethod<?>> getAllMethods() {
		return getMethods();
	}

	@Override
	public boolean isSubtypeOf(CtTypeReference<?> type) {
		for (CtTypeReference<?> ref : getSuperInterfaces()) {
			if (ref.isSubtypeOf(type)) {
				return true;
			}
		}
		return false;
	}

	public List<CtField<?>> getValues() {
		List<CtField<?>> result = new ArrayList<CtField<?>>();
		for (CtField<?> field : getFields()) {
			if (field.getType() == null // this is null for enum values
					) {
				result.add(field);
			}
		}
		return Collections.unmodifiableList(result);
	}

}
