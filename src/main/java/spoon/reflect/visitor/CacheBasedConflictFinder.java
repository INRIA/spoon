/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;

/** Caches some field and nested type names */
public class CacheBasedConflictFinder {
	CtType<?> type;
	CtTypeReference<?> typeRef;
	Set<String> cachedFieldNames;
	Set<String> cachedNestedTypeNames;

	CacheBasedConflictFinder(CtType<?> p_type) {
		type = p_type;
		typeRef = type.getReference();
	}

	/** returns true if the given name is a field name */
	public boolean hasFieldConflict(String name) {
		if (cachedFieldNames == null) {
			Collection<CtFieldReference<?>> allFields = type.getAllFields();
			cachedFieldNames = new HashSet<>(allFields.size());
			for (CtFieldReference<?> field : allFields) {
				cachedFieldNames.add(field.getSimpleName());
			}
		}
		return cachedFieldNames.contains(name);
	}

	/** returns true if the given name is a nested type name */
	public boolean hasNestedTypeConflict(String name) {
		if (cachedNestedTypeNames == null) {
			Collection<CtType<?>> allTypes = type.getNestedTypes();
			cachedNestedTypeNames = new HashSet<>(allTypes.size());
			for (CtType<?> t : allTypes) {
				cachedNestedTypeNames.add(t.getSimpleName());
			}
		}
		return cachedNestedTypeNames.contains(name);
	}

	public String getSimpleName() {
		return typeRef.getSimpleName();
	}

	public CtPackageReference getPackage() {
		return typeRef.getPackage();
	}
}
