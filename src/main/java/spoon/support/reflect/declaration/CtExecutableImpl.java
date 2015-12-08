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
package spoon.support.reflect.declaration;

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static spoon.reflect.ModelElementContainerDefaultCapacities.PARAMETERS_CONTAINER_DEFAULT_CAPACITY;

/**
 * The implementation for {@link spoon.reflect.declaration.CtExecutable}.
 *
 * @author Renaud Pawlak
 */
public abstract class CtExecutableImpl<R> extends CtNamedElementImpl implements CtExecutable<R> {
	private static final long serialVersionUID = 1L;

	CtBlock<?> body;

	List<CtParameter<?>> parameters = emptyList();

	Set<CtTypeReference<? extends Throwable>> thrownTypes = emptySet();

	public CtExecutableImpl() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <B extends R> CtBlock<B> getBody() {
		return (CtBlock<B>) body;
	}

	@Override
	public <B extends R, T extends CtExecutable<R>> T setBody(CtBlock<B> body) {
		if (body != null) {
			body.setParent(this);
		}
		this.body = body;
		return (T) this;
	}

	@Override
	public List<CtParameter<?>> getParameters() {
		return parameters;
	}

	@Override
	public <T extends CtExecutable<R>> T setParameters(List<CtParameter<?>> parameters) {
		if (this.parameters == CtElementImpl.<CtParameter<?>>emptyList()) {
			this.parameters = new ArrayList<CtParameter<?>>(
					PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		this.parameters.clear();
		for (CtParameter<?> p : parameters) {
			addParameter(p);
		}
		return (T) this;
	}

	@Override
	public <T extends CtExecutable<R>> T addParameter(CtParameter<?> parameter) {
		if (parameters == CtElementImpl.<CtParameter<?>>emptyList()) {
			parameters = new ArrayList<CtParameter<?>>(
					PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		parameter.setParent(this);
		parameters.add(parameter);
		return (T) this;
	}

	@Override
	public boolean removeParameter(CtParameter<?> parameter) {
		return parameters != CtElementImpl.<CtParameter<?>>emptyList() && parameters.remove(parameter);
	}

	@Override
	public Set<CtTypeReference<? extends Throwable>> getThrownTypes() {
		return thrownTypes;
	}

	@Override
	public <T extends CtExecutable<R>> T setThrownTypes(Set<CtTypeReference<? extends Throwable>> thrownTypes) {
		if (this.thrownTypes == CtElementImpl.<CtTypeReference<? extends Throwable>>emptySet()) {
			this.thrownTypes = new TreeSet<CtTypeReference<? extends Throwable>>();
		}
		this.thrownTypes.clear();
		for (CtTypeReference<? extends Throwable> thrownType : thrownTypes) {
			addThrownType(thrownType);
		}
		return (T) this;
	}

	@Override
	public <T extends CtExecutable<R>> T addThrownType(CtTypeReference<? extends Throwable> throwType) {
		if (thrownTypes == CtElementImpl.<CtTypeReference<? extends Throwable>>emptySet()) {
			thrownTypes = new TreeSet<CtTypeReference<? extends Throwable>>();
		}
		throwType.setParent(this);
		thrownTypes.add(throwType);
		return (T) this;
	}

	@Override
	public boolean removeThrownType(CtTypeReference<? extends Throwable> throwType) {
		return thrownTypes.remove(throwType);
	}

	@Override
	public CtExecutableReference<R> getReference() {
		return getFactory().Executable().createReference(this);
	}
}
