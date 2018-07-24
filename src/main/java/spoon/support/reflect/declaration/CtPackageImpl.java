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
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.comparator.QualifiedNameComparator;
import spoon.support.util.ModelSet;

import java.util.Set;

import static spoon.reflect.path.CtRole.IS_SHADOW;
import static spoon.reflect.path.CtRole.SUB_PACKAGE;
import static spoon.reflect.path.CtRole.CONTAINED_TYPE;

/**
 * The implementation for {@link spoon.reflect.declaration.CtPackage}.
 *
 * @author Renaud Pawlak
 */
public class CtPackageImpl extends CtNamedElementImpl implements CtPackage {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = SUB_PACKAGE)
	protected ModelSet<CtPackage> packs = new ModelSet<CtPackage>(QualifiedNameComparator.INSTANCE) {
		private static final long serialVersionUID = 1L;
		@Override
		protected CtElement getOwner() {
			return CtPackageImpl.this;
		}

		@Override
		protected CtRole getRole() {
			return SUB_PACKAGE;
		}

		@Override
		public boolean add(CtPackage pack) {
			if (pack == null) {
				return false;
			}
			// they are the same
			if (CtPackageImpl.this.getQualifiedName().equals(pack.getQualifiedName())) {
				addAllTypes(pack, CtPackageImpl.this);
				addAllPackages(pack, CtPackageImpl.this);
				return false;
			}

			// it already exists
			for (CtPackage p1 : packs) {
				if (p1.getQualifiedName().equals(pack.getQualifiedName())) {
					addAllTypes(pack, p1);
					addAllPackages(pack, p1);
						return false;
				}
			}
			return super.add(pack);
		}
	};

	@MetamodelPropertyField(role = CONTAINED_TYPE)
	private ModelSet<CtType<?>> types = new ModelSet<CtType<?>>(QualifiedNameComparator.INSTANCE) {
		private static final long serialVersionUID = 1L;
		@Override
		protected CtElement getOwner() {
			return CtPackageImpl.this;
		}
		@Override
		protected CtRole getRole() {
			return CtRole.CONTAINED_TYPE;
		}
	};

	public CtPackageImpl() {
	}

	@Override
	public void accept(CtVisitor v) {
		v.visitCtPackage(this);
	}

	@Override
	public <T extends CtPackage> T addPackage(CtPackage pack) {
		this.packs.add(pack);
		return (T) this;
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
	public CtModule getDeclaringModule() {
		try {
			return getParent(CtModule.class);
		} catch (ParentNotInitializedException e) {
			return null;
		}
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
		this.packs.set(packs);
			return (T) this;
		}

	@Override
	public <T extends CtPackage> T setTypes(Set<CtType<?>> types) {
		this.types.set(types);
			return (T) this;
		}

	@Override
	public CtPackageReference getReference() {
		return getFactory().Package().createReference(this);
	}

	@Override
	public <T extends CtPackage> T addType(CtType<?> type) {
		types.add(type);
		return (T) this;
	}

	@Override
	public void removeType(CtType<?> type) {
		types.remove(type);
	}

	@Override
	public String toString() {
		return getQualifiedName();
	}

	@MetamodelPropertyField(role = IS_SHADOW)
	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public <E extends CtShadowable> E setShadow(boolean isShadow) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, IS_SHADOW, isShadow, this.isShadow);
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
