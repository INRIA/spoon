/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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

	/**
	 * Checks if a field with the given name conflicts with fields in the type's scope.
	 * @param name The name of the field to check for conflicts.
	 * @return true if a field with the same name exists in the type's scope, false otherwise.
	 */
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

	/**
	 * Checks if a nested type with the given name conflicts with nested types in the type's scope.
	 * @param name The name of the nested type to check for conflicts.
	 * @return true if a nested type with the same name exists in the type's scope, false otherwise.
	 */
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

	/**
	 * Gets the simple name of the referenced type.
	 * @return The simple name of the referenced type.
	 */
	public String getSimpleName() {
		return typeRef.getSimpleName();
	}

	/**
	 * Gets the package reference of the referenced type.
	 * @return The package reference of the referenced type.
	 */
	public CtPackageReference getPackage() {
		return typeRef.getPackage();
	}
}
