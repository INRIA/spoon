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
package spoon.support.reflect.declaration;

import java.util.Set;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.QualifiedNameBasedSortedSet;

/**
 * The implementation for {@link spoon.reflect.declaration.CtPackage}.
 *
 * @author Renaud Pawlak
 */
public class CtPackageImpl extends CtNamedElementImpl implements CtPackage {
	private static final long serialVersionUID = 1L;

	protected Set<CtPackage> packs = orderedPackageSet();

	private Set<CtType<?>> types = orderedTypeSet();

	public CtPackageImpl() {
		super();
	}

	@Override
	public void accept(CtVisitor v) {
		v.visitCtPackage(this);
	}

	@Override
	public <T extends CtPackage> T addPackage(CtPackage pack) {
		if (pack == null) {
			return (T) this;
		}
		if (packs == CtElementImpl.<CtPackage>emptySet()) {
			this.packs = orderedPackageSet();
		}
		// they are the same
		if (this.getQualifiedName().equals(pack.getQualifiedName())) {
			addAllTypes(pack, this);
			addAllPackages(pack, this);
			return (T) this;
		}

		// it already exists
		for (CtPackage p1 : packs) {
			if (p1.getQualifiedName().equals(pack.getQualifiedName())) {
				addAllTypes(pack, p1);
				addAllPackages(pack, p1);
				return (T) this;
			}
		}

		pack.setParent(this);
		this.packs.add(pack);

		return (T) this;
	}

	private Set<CtPackage> orderedPackageSet() {
		return new QualifiedNameBasedSortedSet<>();
	}

	private Set<CtType<?>> orderedTypeSet() {
		return new QualifiedNameBasedSortedSet<>();
	}

	/** add all types of "from" in "to" */
	private void addAllTypes(CtPackage from, CtPackage to) {
		for (CtType t : from.getTypes()) {
			for (CtType t2: to.getTypes()) {
				if (t2.getQualifiedName().equals(t.getQualifiedName()) && !t2.equals(t)) {
					throw new IllegalStateException("types with same qualified names and different code cannot be merged");
				}
			}
			to.addType(t);
		}
	}

	/** add all packages of "from" in "to" */
	private void addAllPackages(CtPackage from, CtPackage to) {
		for (CtPackage p : from.getPackages()) {
			to.addPackage(p);
		}
	}

	@Override
	public boolean removePackage(CtPackage pack) {
		return packs.remove(pack);
	}

	@Override
	public CtPackage getDeclaringPackage() {
		try {
			return getParent(CtPackage.class);
		} catch (ParentNotInitializedException e) {
			return null;
		}
	}

	@Override
	public CtPackage getPackage(String name) {
		for (CtPackage p : packs) {
			if (p.getSimpleName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	@Override
	public Set<CtPackage> getPackages() {
		return packs;
	}

	@Override
	public String getQualifiedName() {
		if (getDeclaringPackage() == null || getDeclaringPackage().isUnnamedPackage()) {
			return getSimpleName();
		} else {
			return getDeclaringPackage().getQualifiedName() + "." + getSimpleName();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends CtType<?>> T getType(String simpleName) {
		for (CtType<?> t : types) {
			if (t.getSimpleName().equals(simpleName)) {
				return (T) t;
			}
		}
		return null;
	}

	@Override
	public Set<CtType<?>> getTypes() {
		return types;
	}

	@Override
	public <T extends CtPackage> T setPackages(Set<CtPackage> packs) {
		if (packs == null || packs.isEmpty()) {
			this.packs = CtElementImpl.emptySet();
			return (T) this;
		}
		this.packs.clear();
		for (CtPackage p : packs) {
			addPackage(p);
		}
		return (T) this;
	}

	@Override
	public <T extends CtPackage> T setTypes(Set<CtType<?>> types) {
		if (types == null || types.isEmpty()) {
			this.types = CtElementImpl.emptySet();
			return (T) this;
		}
		this.types.clear();
		for (CtType<?> t : types) {
			addType(t);
		}
		return (T) this;
	}

	@Override
	public CtPackageReference getReference() {
		return getFactory().Package().createReference(this);
	}

	@Override
	public <T extends CtPackage> T addType(CtType<?> type) {
		if (type == null) {
			return (T) this;
		}
		if (types == CtElementImpl.<CtType<?>>emptySet()) {
			this.types = orderedTypeSet();
		}
		type.setParent(this);
		types.add(type);
		return (T) this;
	}

	@Override
	public void removeType(CtType<?> type) {
		types.remove(type);
	}

	@Override
	public SourcePosition getPosition() {
		/*
		 * The super.getPosition() method returns the own position
		 * or if it's null the position of the parent element,
		 * but that isn't possible for packages.
		 * A package should return the position of the package-info file
		 * if it exists. The parent of a package is another package which
		 * needs to have an own package-info file.
		 */
		return this.position;
	}

	@Override
	public String toString() {
		return getQualifiedName();
	}

	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public <E extends CtShadowable> E setShadow(boolean isShadow) {
		this.isShadow = isShadow;
		return (E) this;
	}

	@Override
	public CtPackage clone() {
		return (CtPackage) super.clone();
	}

	@Override
	public boolean isUnnamedPackage() {
		return TOP_LEVEL_PACKAGE_NAME.equals(getSimpleName());
	}

}
