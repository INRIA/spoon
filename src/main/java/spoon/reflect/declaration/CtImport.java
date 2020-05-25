/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtImportVisitor;
import spoon.support.DerivedProperty;

import static spoon.reflect.path.CtRole.IMPORT_REFERENCE;

/**
 * This element represents an import declaration.
 * The given reference should be of type {@link spoon.reflect.reference.CtTypeReference},
 * {@link spoon.reflect.reference.CtPackageReference}, {@link spoon.reflect.reference.CtExecutableReference},
 * {@link spoon.reflect.reference.CtFieldReference} or {@link spoon.support.reflect.reference.CtTypeMemberWildcardImportReferenceImpl}
 *
 * <pre>
 *     import static import static org.junit.Assert.*;
 * </pre>
 *
 * It will be ignored in all other cases.
 *
 * Example:
 * <pre>
 *     import java.io.File;
 * </pre>
 */
public interface CtImport extends CtElement {
	/**
	 * Returns the kind of import (see {@link CtImportKind})
	 */
	@DerivedProperty
	CtImportKind getImportKind();

	/**
	 * Returns the reference of the import.
	 */
	@PropertyGetter(role = IMPORT_REFERENCE)
	CtReference getReference();

	/**
	 * Sets the reference of the import.
	 * The import kind will be computed based on this reference.
	 */
	@PropertySetter(role = IMPORT_REFERENCE)
	<T extends CtImport> T setReference(CtReference reference);

	/**
	 * Accepts a {@link CtImportVisitor}
	 */
	void accept(CtImportVisitor visitor);

	@Override
	CtImport clone();
}
