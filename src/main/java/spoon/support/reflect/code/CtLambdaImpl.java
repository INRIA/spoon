/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.support.reflect.code;

import spoon.SpoonException;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static spoon.reflect.ModelElementContainerDefaultCapacities.PARAMETERS_CONTAINER_DEFAULT_CAPACITY;

public class CtLambdaImpl<T> extends CtExpressionImpl<T> implements CtLambda<T> {
	String simpleName;
	CtExpression<T> expression;
	CtBlock<?> body;
	List<CtParameter<?>> parameters = emptyList();
	Set<CtTypeReference<? extends Throwable>> thrownTypes = emptySet();

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtLambda(this);
	}

	@Override
	public String getSimpleName() {
		return simpleName;
	}

	@Override
	public <C extends CtNamedElement> C setSimpleName(String simpleName) {
		this.simpleName = simpleName;
		return (C) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <B extends T> CtBlock<B> getBody() {
		return (CtBlock<B>) body;
	}

	@Override
	public <B extends T, C extends CtExecutable<T>> C setBody(CtBlock<B> body) {
		if (expression != null) {
			throw new SpoonException("A lambda can't have two bodys.");
		}
		if (body != null) {
			body.setParent(this);
		}
		this.body = body;
		return (C) this;
	}

	@Override
	public List<CtParameter<?>> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	@Override
	public <C extends CtExecutable<T>> C setParameters(List<CtParameter<?>> params) {
		if (this.parameters == CtElementImpl.<CtParameter<?>>emptyList()) {
			this.parameters = new ArrayList<CtParameter<?>>(
					PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		this.parameters.clear();
		for (CtParameter<?> p : params) {
			addParameter(p);
		}
		return (C) this;
	}

	@Override
	public <C extends CtExecutable<T>> C addParameter(CtParameter<?> parameter) {
		if (parameters == CtElementImpl.<CtParameter<?>>emptyList()) {
			parameters = new ArrayList<CtParameter<?>>(
					PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		parameter.setParent(this);
		parameters.add(parameter);
		return (C) this;
	}

	@Override
	public boolean removeParameter(CtParameter<?> parameter) {
		return parameters != CtElementImpl.<CtParameter<?>>emptyList()
				&& parameters.remove(parameter);
	}

	@Override
	public Set<CtTypeReference<? extends Throwable>> getThrownTypes() {
		return thrownTypes;
	}

	@Override
	public <C extends CtExecutable<T>> C setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes) {
		if (this.thrownTypes == CtElementImpl.<CtTypeReference<? extends Throwable>>emptySet()) {
			this.thrownTypes = new TreeSet<CtTypeReference<? extends Throwable>>();
		}
		this.thrownTypes.clear();
		for (CtTypeReference<? extends Throwable> thrownType : thrownTypes) {
			addThrownType(thrownType);
		}
		return (C) this;
	}

	@Override
	public <C extends CtExecutable<T>> C addThrownType(CtTypeReference<? extends Throwable> throwType) {
		if (thrownTypes == CtElementImpl.<CtTypeReference<? extends Throwable>>emptySet()) {
			thrownTypes = new TreeSet<CtTypeReference<? extends Throwable>>();
		}
		throwType.setParent(this);
		thrownTypes.add(throwType);
		return (C) this;
	}

	@Override
	public boolean removeThrownType(CtTypeReference<? extends Throwable> throwType) {
		return thrownTypes.remove(throwType);
	}

	@Override
	public CtExecutableReference<T> getReference() {
		return getFactory().Executable().createReference(this);
	}

	@Override
	public CtExpression<T> getExpression() {
		return expression;
	}

	@Override
	public <C extends CtLambda<T>> C setExpression(CtExpression<T> expression) {
		if (body != null) {
			throw new SpoonException("A lambda can't have two bodys.");
		}
		if (expression != null) {
			expression.setParent(this);
		}
		this.expression = expression;
		return (C) this;
	}
}
