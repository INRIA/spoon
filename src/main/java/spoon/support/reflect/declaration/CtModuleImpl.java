/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtModuleExport;
import spoon.reflect.declaration.CtModuleProvidedService;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CtModuleImpl extends CtNamedElementImpl implements CtModule {

	private boolean openModule;

	private List<CtModuleExport> exportedPackages = CtElementImpl.emptyList();
	private List<CtModuleExport> openedPackages = CtElementImpl.emptyList();
	private List<CtModuleRequirement> requiredModules = CtElementImpl.emptyList();
	private List<CtModuleProvidedService> providedServices = CtElementImpl.emptyList();
	private List<CtTypeReference> consumedServices = CtElementImpl.emptyList();

	private CtPackage rootPackage;

	public CtModuleImpl() {
		super();
	}

	@Override
	public boolean isUnnamedModule() {
		return TOP_LEVEL_MODULE_NAME.equals(this.getSimpleName());
	}

	@Override
	public boolean isOpenModule() {
		return this.openModule;
	}

	@Override
	public <T extends CtModule> T setIsOpenModule(boolean openModule) {
		this.openModule = openModule;
		return (T) this;
	}

	@Override
	public List<CtTypeReference> getConsumedServices() {
		return Collections.unmodifiableList(this.consumedServices);
	}

	@Override
	public <T extends CtModule> T setConsumedServices(List<CtTypeReference> consumedServices) {
		if (consumedServices == null || consumedServices.isEmpty()) {
			this.consumedServices = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.consumedServices == CtElementImpl.<CtTypeReference>emptyList()) {
			this.consumedServices = new ArrayList<>();
		}
		this.consumedServices.clear();
		this.consumedServices.addAll(consumedServices);
		return (T) this;
	}

	@Override
	public List<CtModuleExport> getExportedPackages() {
		return Collections.unmodifiableList(this.exportedPackages);
	}

	@Override
	public <T extends CtModule> T setExportedPackages(List<CtModuleExport> exportedPackages) {
		if (exportedPackages == null || exportedPackages.isEmpty()) {
			this.exportedPackages = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.exportedPackages == CtElementImpl.<CtModuleExport>emptyList()) {
			this.exportedPackages = new ArrayList<>();
		}

		this.exportedPackages.clear();
		this.exportedPackages.addAll(exportedPackages);

		return (T) this;
	}

	@Override
	public List<CtModuleExport> getOpenedPackages() {
		return Collections.unmodifiableList(this.openedPackages);
	}

	@Override
	public <T extends CtModule> T setOpenedPackages(List<CtModuleExport> openedPackages) {
		if (openedPackages == null || openedPackages.isEmpty()) {
			this.openedPackages = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.openedPackages == CtElementImpl.<CtModuleExport>emptyList()) {
			this.openedPackages = new ArrayList<>();
		}

		this.openedPackages.clear();
		this.openedPackages.addAll(openedPackages);

		return (T) this;
	}

	@Override
	public List<CtModuleRequirement> getRequiredModules() {
		return Collections.unmodifiableList(this.requiredModules);
	}

	@Override
	public <T extends CtModule> T setRequiredModules(List<CtModuleRequirement> requiredModules) {
		if (requiredModules == null || requiredModules.isEmpty()) {
			this.requiredModules = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.requiredModules == CtElementImpl.<CtModuleRequirement>emptyList()) {
			this.requiredModules = new ArrayList<>();
		}

		this.requiredModules.clear();
		this.requiredModules.addAll(requiredModules);

		return (T) this;
	}

	@Override
	public List<CtModuleProvidedService> getProvidedServices() {
		return Collections.unmodifiableList(this.providedServices);
	}

	@Override
	public <T extends CtModule> T setProvidedServices(List<CtModuleProvidedService> providedServices) {
		if (providedServices == null || providedServices.isEmpty()) {
			this.providedServices = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.providedServices == CtElementImpl.<CtModuleProvidedService>emptyList()) {
			this.providedServices = new ArrayList<>();
		}

		this.providedServices.clear();
		this.providedServices.addAll(providedServices);

		return (T) this;
	}

	@Override
	public CtPackage getRootPackage() {
		return this.rootPackage;
	}

	@Override
	public <T extends CtModule> T setRootPackage(CtPackage rootPackage) {
		this.rootPackage = rootPackage;
		return (T) this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtModule(this);
	}

	@Override
	public CtModuleReference getReference() {
		return this.getFactory().Module().createReference(this);
	}
}
