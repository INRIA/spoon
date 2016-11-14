/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.UnsettableProperty;

import java.util.List;
import java.util.Set;

/**
 * This element defines an annotation method declared in an annotation type.
 */
public interface CtAnnotationMethod<T> extends CtMethod<T> {
	/**
	 * Gets the default expression assigned to the annotation method.
	 */
	CtExpression<T> getDefaultExpression();

	/**
	 * Sets the default expression assigned to the annotation method.
	 */
	<C extends CtAnnotationMethod<T>> C setDefaultExpression(CtExpression<T> assignedExpression);

	@Override
	CtAnnotationMethod<T> clone();

	@Override
	@UnsettableProperty
	<T1 extends CtBodyHolder> T1 setBody(CtStatement body);

	@Override
	@UnsettableProperty
	<T1 extends CtExecutable<T>> T1 setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes);

	@Override
	@UnsettableProperty
	<T extends CtFormalTypeDeclarer> T setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters);

	@Override
	@UnsettableProperty
	<T1 extends CtExecutable<T>> T1 setParameters(List<CtParameter<?>> parameters);
}
