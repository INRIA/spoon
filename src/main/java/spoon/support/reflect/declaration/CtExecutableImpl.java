/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * The implementation for {@link spoon.reflect.declaration.CtExecutable}.
 * 
 * @author Renaud Pawlak
 */
public abstract class CtExecutableImpl<R> extends CtNamedElementImpl implements
		CtExecutable<R> {

	CtBlock<?> body;

	List<CtTypeReference<?>> formalTypeParameters = new ArrayList<CtTypeReference<?>>();

	List<CtParameter<?>> parameters = new Vector<CtParameter<?>>();

	Set<CtTypeReference<? extends Throwable>> thrownTypes = new TreeSet<CtTypeReference<? extends Throwable>>();

	public CtExecutableImpl() {
		super();
	}

	@SuppressWarnings("unchecked")
	public <B extends R> CtBlock<B> getBody() {
		return (CtBlock<B>)body;
	}

	public List<CtTypeReference<?>> getFormalTypeParameters() {
		return formalTypeParameters;
	}

	public List<CtParameter<?>> getParameters() {
		return parameters;
	}

	public Set<CtTypeReference<? extends Throwable>> getThrownTypes() {
		return thrownTypes;
	}

	@SuppressWarnings("unchecked")
	public <B extends R> void setBody(CtBlock<B> body) {
		this.body = body;
	}

	public void setFormalTypeParameters(
			List<CtTypeReference<?>> formalTypeParameters) {
		this.formalTypeParameters = formalTypeParameters;
	}

	public void setParameters(List<CtParameter<?>> parameters) {
		this.parameters = parameters;
	}

	public void setThrownTypes(
			Set<CtTypeReference<? extends Throwable>> thrownTypes) {
		this.thrownTypes = thrownTypes;
	}

	@Override
	public CtExecutableReference<R> getReference() {
		return getFactory().Executable().createReference(this);
	}

}
