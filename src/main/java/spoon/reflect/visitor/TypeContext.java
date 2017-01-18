/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.reflect.visitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;

public class TypeContext {
	CtType<?> type;
	CtTypeReference<?> typeRef;
	Set<String> memberNames;

	TypeContext(CtType<?> p_type) {
		type = p_type;
		typeRef = type.getReference();
	}

	public boolean isNameConflict(String name) {
		if (memberNames == null) {
			Collection<CtFieldReference<?>> allFields = type.getAllFields();
			memberNames = new HashSet<>(allFields.size());
			for (CtFieldReference<?> field : allFields) {
				memberNames.add(field.getSimpleName());
			}
		}
		return memberNames.contains(name);
	}

	public String getSimpleName() {
		return typeRef.getSimpleName();
	}

	public CtPackageReference getPackage() {
		return typeRef.getPackage();
	}
}
