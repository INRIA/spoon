/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import java.util.LinkedHashSet;
import java.util.Set;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.KeyedModelSet;

/**
 * The implementation for {@link spoon.reflect.declaration.CtPackage}.
 *
 * @author Renaud Pawlak
 */
public class CtPackageImpl extends CtNamedElementImpl implements CtPackage {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.SUB_PACKAGE)
	protected KeyedModelSet<String, CtPackage> packs = new KeyedModelSet<String, CtPackage>() {
		private static final long serialVersionUID = 1L;
		@Override
		protected CtElement getOwner() {
			return CtPackageImpl.this;
		}

		@Override
		protected CtRole getRole() {
			return CtRole.SUB_PACKAGE;
		}

		@Override
		public boolean add(String simpleName, CtPackage pack) {
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
			CtPackage ctPackage = get(simpleName);
			if (ctPackage != null) {
				addAllTypes(pack, ctPackage);
				addAllPackages(pack, ctPackage);
				return false;
			}

			return super.add(simpleName, pack);
		}
	};

	@MetamodelPropertyField(role = CtRole.CONTAINED_TYPE)
	private final KeyedModelSet<String, CtType<?>> types = new KeyedModelSet<String, CtType<?>>() {
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
		if (pack == null) {
			return (T) this;
		}
		this.packs.add(pack.getSimpleName(), pack);
		return (T) this;
	}

	/** add all types of "from" in "to" */
	private void addAllTypes(CtPackage from, CtPackage to) {
		for (CtType<?> t : from.getTypes()) {
			for (CtType<?> t2: to.getTypes()) {
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
		return packs.remove(pack.getSimpleName()) != null;
	}

	@Override
	public CtModule getDeclaringModule() {
		return getParent(CtModule.class);
	}

	@Override
	public CtPackage getDeclaringPackage() {
		return getParent(CtPackage.class);
	}

	@Override
	public CtPackage getPackage(String simpleName) {
		return this.packs.get(simpleName);
	}

	@Override
	public Set<CtPackage> getPackages() {
		return new LinkedHashSet<>(packs.values());
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
		return (T) types.get(simpleName);
	}

	@Override
	public Set<CtType<?>> getTypes() {
		return new LinkedHashSet<>(types.values());
	}

	@Override
	public <T extends CtPackage> T setPackages(Set<CtPackage> packs) {
		this.packs.clear();
		for (CtPackage pack : packs) {
			this.packs.add(pack.getSimpleName(), pack);
		}
		return (T) this;
	}

	@Override
	public <T extends CtPackage> T setTypes(Set<CtType<?>> types) {
		this.types.clear();
		for (CtType<?> type : types) {
			this.types.add(type.getSimpleName(), type);
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
		// ModelSet of types will take care of setting the parent
		types.add(type.getSimpleName(), type);
		return (T) this;
	}

	@Override
	public void removeType(CtType<?> type) {
		types.remove(type.getSimpleName());
	}

	@Override
	public String toString() {
		return getQualifiedName();
	}

	@MetamodelPropertyField(role = CtRole.IS_SHADOW)
	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public <E extends CtShadowable> E setShadow(boolean isShadow) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_SHADOW, isShadow, this.isShadow);
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

	@Override
	public boolean hasPackageInfo() {
		return !(getPosition() instanceof NoSourcePosition);
	}

	@Override
	public boolean isEmpty() {
		return getPackages().isEmpty() && getTypes().isEmpty();
	}

	public void updateTypeName(CtType<?> newType, String oldName) {
		types.updateKey(oldName, newType.getSimpleName());
	}

	public void updatePackageName(CtPackage newPackage, String oldName) {
		packs.updateKey(oldName, newPackage.getSimpleName());
	}
}
