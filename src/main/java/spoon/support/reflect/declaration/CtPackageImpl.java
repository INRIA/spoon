/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.util.internal.ElementNameMap;

/**
 * The implementation for {@link spoon.reflect.declaration.CtPackage}.
 *
 * @author Renaud Pawlak
 */
public class CtPackageImpl extends CtNamedElementImpl implements CtPackage {
	private static final long serialVersionUID = 1L;
	private final Packages packs;
	private final Types types;
	private final CtModule declaringModule;
	public CtPackageImpl(CtModule declaringModule) {
		this.types = new Types();
		this.packs = new Packages();
		this.declaringModule = declaringModule;
		this.setFactory(declaringModule.getFactory());
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
		this.packs.put(pack.getSimpleName(), pack);
		return (T) this;
	}

	@Override
	public boolean removePackage(CtPackage pack) {
		return packs.remove(pack.getSimpleName()) != null;
	}

	@Override
	public CtModule getDeclaringModule() {
		return declaringModule;
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
	public <T extends CtNamedElement> T setSimpleName(String simpleName) {
		String oldName = getSimpleName();
		super.setSimpleName(simpleName);

		if (parent instanceof CtPackageImpl) {
			((CtPackageImpl) parent).updatePackageName(this, oldName);
		}

		return (T) this;
	}

	@Override
	public String getQualifiedName() {
		if (!isParentInitialized() || parent instanceof RootPackage || parent instanceof CtModule) {
			return getSimpleName();
		}

		return ((CtPackage) parent).getQualifiedName() + CtPackage.PACKAGE_SEPARATOR_CHAR + getSimpleName();
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
			this.packs.put(pack.getSimpleName(), pack);
		}
		return (T) this;
	}

	@Override
	public <T extends CtPackage> T setTypes(Set<CtType<?>> types) {
		this.types.clear();
		for (CtType<?> type : types) {
			this.types.put(type.getSimpleName(), type);
		}
		return (T) this;
	}

	@Override
	public CtPackageReference getReference() {
		return getFactory().Package().createReference(this);
	}

	public <T extends CtPackage> T addType(String simpleName, CtType<?> type) {
		if (type == null) {
			return (T) this;
		}
		// type map will take care of setting the parent
		types.put(simpleName, type);
		return (T) this;
	}

	@Override
	public <T extends CtPackage> T addType(CtType<?> type) {
		if (type == null) {
			return (T) this;
		}

		return addType(type.getSimpleName(), type);
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
		return false;
	}

	@Override
	public boolean hasPackageInfo() {
		return !(getPosition() instanceof NoSourcePosition);
	}

	@Override
	public boolean isEmpty() {
		return getPackages().isEmpty() && getTypes().isEmpty();
	}

	protected void updateTypeName(CtType<?> newType, String oldName) {
		types.updateKey(oldName, newType.getSimpleName());
	}

	protected void updatePackageName(CtPackage newPackage, String oldName) {
		packs.updateKey(oldName, newPackage.getSimpleName());
	}

	private class Packages extends ElementNameMap<CtPackage> {
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
		public CtPackage put(String simpleName, CtPackage pack) {
			if (pack == null) {
				return null;
			}

			if (CtPackageImpl.this.getQualifiedName().equals(pack.getQualifiedName())) {
				addAllTypes(pack, CtPackageImpl.this);
				addAllPackages(pack, CtPackageImpl.this);
				return null;
			}

			CtPackage ctPackage = get(simpleName);
			if (ctPackage != null) {
				addAllTypes(pack, ctPackage);
				addAllPackages(pack, ctPackage);
				return null;
			}

			return super.put(simpleName, pack);
		}

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

		private void addAllPackages(CtPackage from, CtPackage to) {
			for (CtPackage p : from.getPackages()) {
				to.addPackage(p);
			}
		}
	}

	private class Types extends ElementNameMap<CtType<?>> {
		private static final long serialVersionUID = 1L;

		@Override
		protected CtElement getOwner() {
			return CtPackageImpl.this;
		}

		@Override
		protected CtRole getRole() {
			return CtRole.CONTAINED_TYPE;
		}
	}

	public static class RootPackage extends CtPackageImpl {
		public RootPackage(CtModule module) {
			super(module);
			this.setSimpleName(CtPackage.TOP_LEVEL_PACKAGE_NAME);
			this.setParent(module);
		}

		@Override
		public boolean isUnnamedPackage() {
			return true;
		}

		@Override
		public String getQualifiedName() {
			return "";
		}

		@Override
		public <T extends CtNamedElement> T setSimpleName(String name) {
			return Objects.equals(name, CtPackage.TOP_LEVEL_PACKAGE_NAME) ? super.setSimpleName(name) : (T) this;
		}

		@Override
		public String toString() {
			return this.getSimpleName();
		}

		@Override
		public void accept(CtVisitor visitor) {
			visitor.visitCtPackage(this);
		}

		@Override
		public CtPackageImpl clone() {
			return (CtPackageImpl) super.clone();
		}
	}
}
