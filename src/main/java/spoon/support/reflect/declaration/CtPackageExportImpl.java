/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CtPackageExportImpl extends CtElementImpl implements CtPackageExport {
	@MetamodelPropertyField(role = CtRole.PACKAGE_REF)
	private CtPackageReference packageReference;

	@MetamodelPropertyField(role = CtRole.MODULE_REF)
	private List<CtModuleReference> targets = CtElementImpl.emptyList();

	@MetamodelPropertyField(role = CtRole.OPENED_PACKAGE)
	private boolean isOpen;

	public CtPackageExportImpl() {
	}

	@Override
	public <T extends CtPackageExport> T setOpenedPackage(boolean openedPackage) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.OPENED_PACKAGE, openedPackage, this.isOpen);
		this.isOpen = openedPackage;
		return (T) this;
	}

	@Override
	public boolean isOpenedPackage() {
		return this.isOpen;
	}

	@Override
	public CtPackageReference getPackageReference() {
		return this.packageReference;
	}

	@Override
	public <T extends CtPackageExport> T setPackageReference(CtPackageReference packageReference) {
		if (packageReference != null) {
			packageReference.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.PACKAGE_REF, packageReference, this.packageReference);
		this.packageReference = packageReference;
		return (T) this;
	}

	@Override
	public List<CtModuleReference> getTargetExport() {
		return Collections.unmodifiableList(targets);
	}

	@Override
	public <T extends CtPackageExport> T setTargetExport(List<CtModuleReference> targetExports) {
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.MODULE_REF, this.targets, new ArrayList<>(this.targets));
		if (targetExports == null || targetExports.isEmpty()) {
			this.targets = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.targets == CtElementImpl.<CtModuleReference>emptyList()) {
			this.targets = new ArrayList<>();
		}
		this.targets.clear();
		for (CtModuleReference targetExport : targetExports) {
			this.addTargetExport(targetExport);
		}

		return (T) this;
	}

	@Override
	public <T extends CtPackageExport> T addTargetExport(CtModuleReference targetExport) {
		if (targetExport == null) {
			return (T) this;
		}
		if (this.targets == CtElementImpl.<CtModuleReference>emptyList()) {
			this.targets = new ArrayList<>();
		}
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.MODULE_REF, this.targets, targetExport);
		targetExport.setParent(this);
		this.targets.add(targetExport);
		return (T) this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtPackageExport(this);
	}

	@Override
	public CtPackageExport clone() {
		return (CtPackageExport) super.clone();
	}
}
