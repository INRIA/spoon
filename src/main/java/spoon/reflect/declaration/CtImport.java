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
