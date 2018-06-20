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
package spoon.reflect.reference;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtRole;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import java.util.List;


/**
 * This is the root interface for named program element references. References
 * can point to program element built in the model or not. In the latter case,
 * introspection methods fall back on runtime reflection (
 * <code>java.lang.reflect</code>) to access the program information, as long as
 * available in the classpath.
 *
 * @see spoon.reflect.declaration.CtElement
 */
public interface CtReference extends CtElement {

	/**
	 * Gets the simple name of referenced element.
	 */
	@PropertyGetter(role = CtRole.NAME)
	String getSimpleName();

	/**
	 * Sets the name of referenced element.
	 */
	@PropertySetter(role = CtRole.NAME)
	<T extends CtReference> T setSimpleName(String simpleName);

	/**
	 * Returns the declaration that corresponds to the referenced element only
	 * if the declaration is in the analyzed source files.
	 * It is strongly advised to use the more robust {@link CtTypeReference#getTypeDeclaration()} and {@link CtExecutableReference#getExecutableDeclaration()} that never return null.
	 * @return referenced element or null if element is not in the source path (aka input resource).
	 */
	@DerivedProperty
	CtElement getDeclaration();

	@Override
	CtReference clone();

	/** comments are not possible for references */
	@Override
	@UnsettableProperty
	<E extends CtElement> E setComments(List<CtComment> comments);
}
