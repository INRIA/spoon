/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;

/**
 * Matches all CtType elements, which are sub type of {@link #superType}
 * Matches the input `superType` too.
 * Call {@link #includingSelf(boolean)} with value false, if instance of {@link #superType} should no match this {@link Filter}
 */
public class SubtypeFilter extends AbstractFilter<CtType<?>> {

	private CtTypeReference<?> superType;
	private String superTypeQualifiedName;

	public SubtypeFilter(CtTypeReference<?> superType) {
		this.superType = superType;
	}

	/**
	 * @param includingSelf if false then element which is equal to to #superType is not matching
	 */
	public SubtypeFilter includingSelf(boolean includingSelf) {
		if (includingSelf) {
			superTypeQualifiedName = null;
		} else {
			superTypeQualifiedName = superType.getQualifiedName();
		}
		return this;
	}

	@Override
	public boolean matches(CtType<?> mayBeSubType) {
		if (superTypeQualifiedName != null && superTypeQualifiedName.equals(mayBeSubType.getQualifiedName())) {
			//we should not accept superType
			return false;
		}
		return mayBeSubType.isSubtypeOf(superType);
	}
}
