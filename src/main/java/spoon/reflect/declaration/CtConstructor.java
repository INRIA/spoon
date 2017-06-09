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
package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.annotations.PropertyGetter;
import spoon.support.UnsettableProperty;

import static spoon.reflect.path.CtRole.NAME;

/**
 * This element defines a constructor declaration.
 */
public interface CtConstructor<T> extends CtExecutable<T>, CtTypeMember, CtFormalTypeDeclarer, CtShadowable {

	/**
	 * Always returns "&lt;init&gt;".
	 */
	@PropertyGetter(role = NAME)
	String getSimpleName();

	@Override
	CtConstructor<T> clone();

	@Override
	@UnsettableProperty
	<C extends CtTypedElement> C setType(CtTypeReference<T> type);
}
