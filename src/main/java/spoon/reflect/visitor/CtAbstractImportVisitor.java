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
package spoon.reflect.visitor;

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
}
