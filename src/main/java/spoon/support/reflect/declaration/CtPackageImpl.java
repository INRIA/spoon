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

import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.visitor.CtVisitor;

/**
 * The implementation for {@link spoon.reflect.declaration.CtPackage}.
 * 
 * @author Renaud Pawlak
 */
public class CtPackageImpl extends CtNamedElementImpl implements CtPackage {
	private static final long serialVersionUID = 1L;

	Set<CtPackage> packs = new TreeSet<CtPackage>();

	Set<CtSimpleType<?>> types = new TreeSet<CtSimpleType<?>>();

	public CtPackageImpl() {
		super();
	}

	public void accept(CtVisitor v) {
		v.visitCtPackage(this);
	}

	public CtPackage getDeclaringPackage() {
		if (parent == null)
			return null;
		return getParent(CtPackage.class);
	}

	public CtPackage getPackage(String name) {
		for (CtPackage p : packs) {
			if (p.getSimpleName().equals(name))
				return p;
		}
		return null;
	}

	public Set<CtPackage> getPackages() {
		return packs;
	}

	public String getQualifiedName() {
		if (getDeclaringPackage() == null)
			return getSimpleName();
		return getDeclaringPackage().getQualifiedName() + "." + getSimpleName();
	}

	public CtSimpleType<?> getType(String simpleName) {
		for (CtSimpleType<?> t : types) {
			if (t.getSimpleName().equals(simpleName)) {
				return t;
			}
		}
		return null;
	}

	public Set<CtSimpleType<?>> getTypes() {
		return types;
	}

	public void setPackages(Set<CtPackage> packs) {
		this.packs = packs;
	}

	public void setTypes(Set<CtSimpleType<?>> types) {
		this.types = types;
	}

	@Override
	public CtPackageReference getReference() {
		return getFactory().Package().createReference(this);
	}
}
