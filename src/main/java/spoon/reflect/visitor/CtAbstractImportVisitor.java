/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.experimental.CtUnresolvedImport;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;

/**
 * Provides an empty implementation of {@link CtImportVisitor}.
 */
public class CtAbstractImportVisitor implements CtImportVisitor {

	@Override
	public <T> void visitTypeImport(CtTypeReference<T> typeReference) {
	}

	@Override
	public <T> void visitMethodImport(CtExecutableReference<T> executableReference) {
	}

	@Override
	public <T> void visitFieldImport(CtFieldReference<T> fieldReference) {
	}

	@Override
	public void visitAllTypesImport(CtPackageReference packageReference) {
	}

	@Override
	public <T> void visitAllStaticMembersImport(CtTypeMemberWildcardImportReference typeReference) {
	}
	@Override
	public <T> void visitUnresolvedImport(CtUnresolvedImport unresolvedImport) {
	}
}
