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
