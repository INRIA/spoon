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
import spoon.reflect.reference.CtModuleProvidedService;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.HashSet;
import java.util.Set;

public class CtModuleImpl extends CtNamedElementImpl implements CtModule {

	private boolean openModule;

	private Set<CtModuleExport> exportedPackages = CtElementImpl.emptySet();
	private Set<CtModuleExport> openedPackages = CtElementImpl.emptySet();
	private Set<CtModuleRequirement> requiredModules = CtElementImpl.emptySet();
	private Set<CtModuleProvidedService> providedServices = CtElementImpl.emptySet();
	private Set<CtTypeReference> consumedServices = CtElementImpl.emptySet();

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
	public Set<CtTypeReference> getConsumedServices() {
		return this.consumedServices;
	}

	@Override
	public <T extends CtModule> T setConsumedServices(Set<CtTypeReference> consumedServices) {
		if (consumedServices == null || consumedServices.isEmpty()) {
			this.consumedServices = CtElementImpl.emptySet();
			return (T) this;
		}

		if (this.consumedServices == CtElementImpl.<CtTypeReference>emptySet()) {
			this.consumedServices = new HashSet<>();
		}
		this.consumedServices.clear();
		this.consumedServices.addAll(consumedServices);
		return (T) this;
	}

	@Override
	public Set<CtModuleExport> getExportedPackages() {
		return this.exportedPackages;
	}

	@Override
	public <T extends CtModule> T setExportedPackages(Set<CtModuleExport> exportedPackages) {
		if (exportedPackages == null || exportedPackages.isEmpty()) {
			this.exportedPackages = CtElementImpl.emptySet();
			return (T) this;
		}

		if (this.exportedPackages == CtElementImpl.<CtModuleExport>emptySet()) {
			this.exportedPackages = new HashSet<>();
		}

		this.exportedPackages.clear();
		this.exportedPackages.addAll(exportedPackages);

		return (T) this;
	}

	@Override
	public Set<CtModuleExport> getOpenedPackages() {
		return this.openedPackages;
	}

	@Override
	public <T extends CtModule> T setOpenedPackages(Set<CtModuleExport> openedPackages) {
		if (openedPackages == null || openedPackages.isEmpty()) {
			this.openedPackages = CtElementImpl.emptySet();
			return (T) this;
		}

		if (this.openedPackages == CtElementImpl.<CtModuleExport>emptySet()) {
			this.openedPackages = new HashSet<>();
		}

		this.openedPackages.clear();
		this.openedPackages.addAll(openedPackages);

		return (T) this;
	}

	@Override
	public Set<CtModuleRequirement> getRequiredModules() {
		return this.requiredModules;
	}

	@Override
	public <T extends CtModule> T setRequiredModules(Set<CtModuleRequirement> requiredModules) {
		if (requiredModules == null || requiredModules.isEmpty()) {
			this.requiredModules = CtElementImpl.emptySet();
			return (T) this;
		}

		if (this.requiredModules == CtElementImpl.<CtModuleRequirement>emptySet()) {
			this.requiredModules = new HashSet<>();
		}

		this.requiredModules.clear();
		this.requiredModules.addAll(requiredModules);

		return (T) this;
	}

	@Override
	public Set<CtModuleProvidedService> getProvidedServices() {
		return this.getProvidedServices();
	}

	@Override
	public <T extends CtModule> T setProvidedServices(Set<CtModuleProvidedService> providedServices) {
		if (providedServices == null || providedServices.isEmpty()) {
			this.providedServices = CtElementImpl.emptySet();
			return (T) this;
		}

		if (this.providedServices == CtElementImpl.<CtModuleProvidedService>emptySet()) {
			this.providedServices = new HashSet<>();
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
