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
package spoon.support.reflect.reference;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.UnsettableProperty;
import spoon.support.reflect.declaration.CtElementImpl;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Objects;

import static spoon.reflect.path.CtRole.NAME;

public abstract class CtReferenceImpl extends CtElementImpl implements CtReference, Serializable {

	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = NAME)
	protected String simplename = "";

	public CtReferenceImpl() {
	}

	protected abstract AnnotatedElement getActualAnnotatedElement();

	@Override
	public String getSimpleName() {
		return simplename;
	}

	@Override
	public <T extends CtReference> T setSimpleName(String simplename) {
		Factory factory = getFactory();
		if (factory == null) {
			this.simplename = simplename;
			return (T) this;
		}
		if (factory instanceof FactoryImpl) {
			simplename = ((FactoryImpl) factory).dedup(simplename);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, NAME, simplename, this.simplename);
		this.simplename = simplename;
		return (T) this;
	}

	@UnsettableProperty
	@Override
	public <E extends CtElement> E setComments(List<CtComment> comments) {
		return (E) this;
	}

	@Override
	public abstract void accept(CtVisitor visitor);

	@Override
	public CtReference clone() {
		return (CtReference) super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CtReference) {
			CtReference ref = (CtReference) o;
			if (!Objects.equals(getSimpleName(), ref.getSimpleName())) {
				//fast fallback when simple names are not equal
				//it is much faster then EqualsVisitor
				return false;
			}
			return super.equals(o);
		}
		return false;
	}
}
