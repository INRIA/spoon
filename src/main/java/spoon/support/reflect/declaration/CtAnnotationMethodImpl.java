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
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import static spoon.reflect.path.CtRole.DEFAULT_EXPRESSION;

import java.util.List;
import java.util.Set;

/**
 * The implementation for {@link spoon.reflect.declaration.CtAnnotationMethod}.
 */
public class CtAnnotationMethodImpl<T> extends CtMethodImpl<T> implements CtAnnotationMethod<T> {
	@MetamodelPropertyField(role = DEFAULT_EXPRESSION)
	CtExpression<T> defaultExpression;

	@Override
	public void accept(CtVisitor v) {
		v.visitCtAnnotationMethod(this);
	}

	@Override
	public CtExpression<T> getDefaultExpression() {
		return defaultExpression;
	}

	@Override
	public <C extends CtAnnotationMethod<T>> C setDefaultExpression(CtExpression<T> assignedExpression) {
		if (assignedExpression != null) {
			assignedExpression.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, DEFAULT_EXPRESSION, assignedExpression, this.defaultExpression);
		this.defaultExpression = assignedExpression;
		return (C) this;
	}

	@Override
	@DerivedProperty
	public CtBlock<T> getBody() {
		return null;
	}

	@Override
	@UnsettableProperty
	public <T extends CtBodyHolder> T setBody(CtStatement statement) {
		return (T) this;
	}

	@Override
	@DerivedProperty
	public Set<CtTypeReference<? extends Throwable>> getThrownTypes() {
		return emptySet();
	}

	@Override
	@UnsettableProperty
	public <U extends CtExecutable<T>> U setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes) {
		return (U) this;
	}

	@Override
	@DerivedProperty
	public List<CtTypeParameter> getFormalCtTypeParameters() {
		return emptyList();
	}

	@Override
	@UnsettableProperty
	public <C extends CtFormalTypeDeclarer> C setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters) {
		return (C) this;
	}

	@Override
	@DerivedProperty
	public List<CtParameter<?>> getParameters() {
		return emptyList();
	}

	@Override
	@UnsettableProperty
	public <U extends CtExecutable<T>> U setParameters(List<CtParameter<?>> parameters) {
		return (U) this;
	}

	@Override
	public CtAnnotationMethod<T> clone() {
		return (CtAnnotationMethod<T>) super.clone();
	}
}


