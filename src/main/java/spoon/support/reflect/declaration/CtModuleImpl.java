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

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.factory.ModuleFactory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CtModuleImpl extends CtNamedElementImpl implements CtModule {
	@MetamodelPropertyField(role = CtRole.MODIFIER)
	private boolean openModule;

	@MetamodelPropertyField(role = CtRole.EXPORTED_PACKAGE)
	private List<CtPackageExport> exportedPackages = CtElementImpl.emptyList();

	@MetamodelPropertyField(role = CtRole.OPENED_PACKAGE)
	private List<CtPackageExport> openedPackages = CtElementImpl.emptyList();

	@MetamodelPropertyField(role = CtRole.REQUIRED_MODULE)
	private List<CtModuleRequirement> requiredModules = CtElementImpl.emptyList();

	@MetamodelPropertyField(role = CtRole.PROVIDED_SERVICE)
	private List<CtProvidedService> providedServices = CtElementImpl.emptyList();

	@MetamodelPropertyField(role = CtRole.SERVICE_TYPE)
	private List<CtUsedService> consumedServices = CtElementImpl.emptyList();

	@MetamodelPropertyField(role = CtRole.SUB_PACKAGE)
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
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.MODIFIER, openModule, this.openModule);
		return (T) this;
	}

	@Override
	public List<CtUsedService> getUsedServices() {
		return Collections.unmodifiableList(this.consumedServices);
	}

	@Override
	public <T extends CtModule> T setUsedServices(List<CtUsedService> consumedServices) {
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.SERVICE_TYPE, this.consumedServices, new ArrayList<>(this.consumedServices));
		if (consumedServices == null || consumedServices.isEmpty()) {
			this.consumedServices = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.consumedServices == CtElementImpl.<CtUsedService>emptyList()) {
			this.consumedServices = new ArrayList<>();
		}
		this.consumedServices.clear();
		for (CtUsedService consumedService : consumedServices) {
			this.addUsedService(consumedService);
		}
		return (T) this;
	}

	@Override
	public <T extends CtModule> T addUsedService(CtUsedService consumedService) {
		if (consumedService == null) {
			return (T) this;
		}
		if (this.consumedServices == CtElementImpl.<CtUsedService>emptyList()) {
			this.consumedServices = new ArrayList<>();
		}

		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.SERVICE_TYPE, this.consumedServices, consumedService);
		consumedService.setParent(this);
		this.consumedServices.add(consumedService);
		return (T) this;
	}

	@Override
	public List<CtPackageExport> getExportedPackages() {
		return Collections.unmodifiableList(this.exportedPackages);
	}

	@Override
	public <T extends CtModule> T setExportedPackages(List<CtPackageExport> exportedPackages) {
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.EXPORTED_PACKAGE, this.exportedPackages, new ArrayList<>(this.exportedPackages));
		if (exportedPackages == null || exportedPackages.isEmpty()) {
			this.exportedPackages = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.exportedPackages == CtElementImpl.<CtPackageExport>emptyList()) {
			this.exportedPackages = new ArrayList<>();
		}

		this.exportedPackages.clear();
		for (CtPackageExport moduleExport : exportedPackages) {
			this.addExportedPackage(moduleExport);
		}

		return (T) this;
	}

	@Override
	public <T extends CtModule> T addExportedPackage(CtPackageExport exportedPackage) {
		if (exportedPackage == null) {
			return (T) this;
		}
		if (this.exportedPackages == CtElementImpl.<CtPackageExport>emptyList()) {
			this.exportedPackages = new ArrayList<>();
		}
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.EXPORTED_PACKAGE, this.exportedPackages, exportedPackage);
		exportedPackage.setParent(this);
		this.exportedPackages.add(exportedPackage);
		return (T) this;
	}

	@Override
	public List<CtPackageExport> getOpenedPackages() {
		return Collections.unmodifiableList(this.openedPackages);
	}

	@Override
	public <T extends CtModule> T setOpenedPackages(List<CtPackageExport> openedPackages) {
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.OPENED_PACKAGE, this.openedPackages, new ArrayList<>(this.openedPackages));
		if (openedPackages == null || openedPackages.isEmpty()) {
			this.openedPackages = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.openedPackages == CtElementImpl.<CtPackageExport>emptyList()) {
			this.openedPackages = new ArrayList<>();
		}

		this.openedPackages.clear();
		for (CtPackageExport openedPackage : openedPackages) {
			this.addOpenedPackage(openedPackage);
		}
		return (T) this;
	}

	@Override
	public <T extends CtModule> T addOpenedPackage(CtPackageExport openedPackage) {
		if (openedPackage == null) {
			return (T) this;
		}
		if (this.openedPackages == CtElementImpl.<CtPackageExport>emptyList()) {
			this.openedPackages = new ArrayList<>();
		}
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.OPENED_PACKAGE, this.openedPackages, openedPackage);
		openedPackage.setParent(this);
		this.openedPackages.add(openedPackage);
		return (T) this;
	}

	@Override
	public List<CtModuleRequirement> getRequiredModules() {
		return Collections.unmodifiableList(this.requiredModules);
	}

	@Override
	public <T extends CtModule> T setRequiredModules(List<CtModuleRequirement> requiredModules) {
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.REQUIRED_MODULE, this.requiredModules, new ArrayList<>(this.requiredModules));
		if (requiredModules == null || requiredModules.isEmpty()) {
			this.requiredModules = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.requiredModules == CtElementImpl.<CtModuleRequirement>emptyList()) {
			this.requiredModules = new ArrayList<>();
		}

		this.requiredModules.clear();
		for (CtModuleRequirement moduleRequirement : requiredModules) {
			this.addRequiredModule(moduleRequirement);
		}
		return (T) this;
	}

	@Override
	public <T extends CtModule> T addRequiredModule(CtModuleRequirement requiredModule) {
		if (requiredModule == null) {
			return (T) this;
		}
		if (this.requiredModules == CtElementImpl.<CtModuleRequirement>emptyList()) {
			this.requiredModules = new ArrayList<>();
		}

		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.REQUIRED_MODULE, this.requiredModules, requiredModule);
		requiredModule.setParent(this);
		this.requiredModules.add(requiredModule);
		return (T) this;
	}

	@Override
	public List<CtProvidedService> getProvidedServices() {
		return Collections.unmodifiableList(this.providedServices);
	}

	@Override
	public <T extends CtModule> T setProvidedServices(List<CtProvidedService> providedServices) {
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.PROVIDED_SERVICE, this.providedServices, new ArrayList<>(providedServices));
		if (providedServices == null || providedServices.isEmpty()) {
			this.providedServices = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.providedServices == CtElementImpl.<CtProvidedService>emptyList()) {
			this.providedServices = new ArrayList<>();
		}

		this.providedServices.clear();
		for (CtProvidedService providedService : providedServices) {
			this.addProvidedService(providedService);
		}

		return (T) this;
	}

	@Override
	public <T extends CtModule> T addProvidedService(CtProvidedService providedService) {
		if (providedService == null) {
			return (T) this;
		}
		if (this.providedServices == CtElementImpl.<CtProvidedService>emptyList()) {
			this.providedServices = new ArrayList<>();
		}
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.PROVIDED_SERVICE, this.providedServices, providedService);
		providedService.setParent(this);
		this.providedServices.add(providedService);
		return (T) this;
	}

	@Override
	public CtPackage getRootPackage() {
		return this.rootPackage;
	}

	@Override
	public <T extends CtModule> T setRootPackage(CtPackage rootPackage) {
		if (rootPackage != null) {
			rootPackage.setParent(this);
		}
		if (!(this instanceof ModuleFactory.CtUnnamedModule)) {
			getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.SUB_PACKAGE, rootPackage, this.rootPackage);
		}
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

	@Override
	public CtModule clone() {
		return (CtModule) super.clone();
	}
}
