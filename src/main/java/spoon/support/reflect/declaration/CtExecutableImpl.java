/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.util.QualifiedNameBasedSortedSet;
import spoon.support.visitor.SignaturePrinter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static spoon.reflect.ModelElementContainerDefaultCapacities.PARAMETERS_CONTAINER_DEFAULT_CAPACITY;
import static spoon.reflect.path.CtRole.BODY;
import static spoon.reflect.path.CtRole.PARAMETER;
import static spoon.reflect.path.CtRole.THROWN;


/**
 * The implementation for {@link spoon.reflect.declaration.CtExecutable}.
 *
 * @author Renaud Pawlak
 */
public abstract class CtExecutableImpl<R> extends CtNamedElementImpl implements CtExecutable<R> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = BODY)
	CtBlock<?> body;

	@MetamodelPropertyField(role = PARAMETER)
	List<CtParameter<?>> parameters = emptyList();

	@MetamodelPropertyField(role = THROWN)
	Set<CtTypeReference<? extends Throwable>> thrownTypes = emptySet();

	public CtExecutableImpl() {
	}

	public CtType<?> getDeclaringType() {
		return (CtType<?>) parent;
	}

	public <T> CtType<T> getTopLevelType() {
		return getDeclaringType().getTopLevelType();
	}

	@Override
	@SuppressWarnings("unchecked")
	public CtBlock<R> getBody() {
		return (CtBlock<R>) body;
	}

	@Override
	public <T extends CtBodyHolder> T setBody(CtStatement statement) {
		if (statement != null) {
			CtBlock<?> body = getFactory().Code().getOrCreateCtBlock(statement);
			getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, BODY, body, this.body);
			if (body != null) {
				body.setParent(this);
			}
			this.body = body;
		} else {
			getFactory().getEnvironment().getModelChangeListener().onObjectDelete(this, BODY, this.body);
			this.body = null;
		}
		return (T) this;
	}

	@Override
	public List<CtParameter<?>> getParameters() {
		return parameters;
	}

	@Override
	public <T extends CtExecutable<R>> T setParameters(List<CtParameter<?>> parameters) {
		if (parameters == null || parameters.isEmpty()) {
			this.parameters = CtElementImpl.emptyList();
			return (T) this;
		}
		if (this.parameters == CtElementImpl.<CtParameter<?>>emptyList()) {
			this.parameters = new ArrayList<>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, PARAMETER, this.parameters, new ArrayList<>(this.parameters));
		this.parameters.clear();
		for (CtParameter<?> p : parameters) {
			addParameter(p);
		}
		return (T) this;
	}

	@Override
	public <T extends CtExecutable<R>> T addParameter(CtParameter<?> parameter) {
		if (parameter == null) {
			return (T) this;
		}
		if (parameters == CtElementImpl.<CtParameter<?>>emptyList()) {
			parameters = new ArrayList<>(PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		parameter.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, PARAMETER, this.parameters, parameter);
		parameters.add(parameter);
		return (T) this;
	}

	@Override
	public boolean removeParameter(CtParameter<?> parameter) {
		if (parameters == CtElementImpl.<CtParameter<?>>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, PARAMETER, parameters, parameters.indexOf(parameter), parameter);
		return parameters.remove(parameter);
	}

	@Override
	public Set<CtTypeReference<? extends Throwable>> getThrownTypes() {
		return thrownTypes;
	}

	@Override
	public <T extends CtExecutable<R>> T setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes) {
		if (thrownTypes == null || thrownTypes.isEmpty()) {
			this.thrownTypes = CtElementImpl.emptySet();
			return (T) this;
		}
		if (this.thrownTypes == CtElementImpl.<CtTypeReference<? extends Throwable>>emptySet()) {
			this.thrownTypes = new QualifiedNameBasedSortedSet<>();
		}
		getFactory().getEnvironment().getModelChangeListener().onSetDeleteAll(this, THROWN, this.thrownTypes, new HashSet<Object>(this.thrownTypes));
		this.thrownTypes.clear();
		for (CtTypeReference<? extends Throwable> thrownType : thrownTypes) {
			addThrownType(thrownType);
		}
		return (T) this;
	}

	@Override
	public <T extends CtExecutable<R>> T addThrownType(CtTypeReference<? extends Throwable> throwType) {
		if (throwType == null) {
			return (T) this;
		}
		if (thrownTypes == CtElementImpl.<CtTypeReference<? extends Throwable>>emptySet()) {
			thrownTypes = new QualifiedNameBasedSortedSet<>();
		}
		throwType.setParent(this);
		getFactory().getEnvironment().getModelChangeListener().onSetAdd(this, THROWN, this.thrownTypes, throwType);
		thrownTypes.add(throwType);
		return (T) this;
	}

	@Override
	public boolean removeThrownType(CtTypeReference<? extends Throwable> throwType) {
		if (thrownTypes == CtElementImpl.<CtTypeReference<? extends Throwable>>emptySet()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onSetDelete(this, THROWN, thrownTypes, throwType);
		return thrownTypes.remove(throwType);
	}

	@Override
	public String getSignature() {
		final SignaturePrinter pr = new SignaturePrinter();
		pr.scan(this);
		return pr.getSignature();
	}

	@Override
	public CtExecutableReference<R> getReference() {
		return getFactory().Executable().createReference(this);
	}

	@Override
	public CtExecutable<R> clone() {
		return (CtExecutable<R>) super.clone();
	}
}
