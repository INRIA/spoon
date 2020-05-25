/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleDirective;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.DerivedProperty;
import spoon.support.comparator.CtLineElementComparator;
import spoon.support.util.SortedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CtModuleImpl extends CtNamedElementImpl implements CtModule {
	@MetamodelPropertyField(role = CtRole.MODIFIER)
	private boolean openModule;

	@MetamodelPropertyField(role = CtRole.MODULE_DIRECTIVE)
	private List<CtModuleDirective> moduleDirectives = CtElementImpl.emptyList();

	@MetamodelPropertyField(role = CtRole.SUB_PACKAGE)
	private CtPackage rootPackage;

	public CtModuleImpl() {
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
	public <T extends CtModule> T setModuleDirectives(List<CtModuleDirective> moduleDirectives) {
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.MODULE_DIRECTIVE, this.moduleDirectives, new ArrayList<>(this.moduleDirectives));
		if (moduleDirectives == null || moduleDirectives.isEmpty()) {
			this.moduleDirectives = CtElementImpl.emptyList();
			return (T) this;
		}
		if (this.moduleDirectives == CtElementImpl.<CtModuleDirective>emptyList()) {
			this.moduleDirectives = new SortedList<>(new CtLineElementComparator());
		}
		this.moduleDirectives.clear();

		for (CtModuleDirective moduleDirective : moduleDirectives) {
			this.addModuleDirective(moduleDirective);
		}

		return (T) this;
	}

	@Override
	public <T extends CtModule> T addModuleDirective(CtModuleDirective moduleDirective) {
		if (moduleDirective == null) {
			return (T) this;
		}

		if (this.moduleDirectives == CtElementImpl.<CtModuleDirective>emptyList()) {
			this.moduleDirectives = new SortedList<>(new CtLineElementComparator());
		}
		if (!this.moduleDirectives.contains(moduleDirective)) {
			moduleDirective.setParent(this);
			CtRole role = CtRole.MODULE_DIRECTIVE.getMatchingSubRoleFor(moduleDirective);

			getFactory().getEnvironment().getModelChangeListener().onListAdd(this, role, this.moduleDirectives, moduleDirective);
			this.moduleDirectives.add(moduleDirective);
		}

		return (T) this;
	}

	@Override
	public <T extends CtModule> T addModuleDirectiveAt(int position, CtModuleDirective moduleDirective) {
		if (moduleDirective == null) {
			return (T) this;
		}

		if (this.moduleDirectives == CtElementImpl.<CtModuleDirective>emptyList()) {
			this.moduleDirectives = new SortedList<>(new CtLineElementComparator());
		}
		if (!this.moduleDirectives.contains(moduleDirective)) {
			moduleDirective.setParent(this);
			CtRole role = CtRole.MODULE_DIRECTIVE.getMatchingSubRoleFor(moduleDirective);

			getFactory().getEnvironment().getModelChangeListener().onListAdd(this, role, this.moduleDirectives, position, moduleDirective);
			this.moduleDirectives.add(position, moduleDirective);
		}

		return (T) this;
	}

	@Override
	public List<CtModuleDirective> getModuleDirectives() {
		return Collections.unmodifiableList(this.moduleDirectives);
	}

	@Override
	public <T extends CtModule> T removeModuleDirective(CtModuleDirective moduleDirective) {
		if (moduleDirective == null || this.moduleDirectives.isEmpty()) {
			return (T) this;
		}
		if (this.moduleDirectives.contains(moduleDirective)) {
			getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.MODULE_DIRECTIVE.getMatchingSubRoleFor(moduleDirective), this.moduleDirectives, this.moduleDirectives.indexOf(moduleDirective), moduleDirective);
			if (this.moduleDirectives.size() == 1) {
				this.moduleDirectives = CtElementImpl.emptyList();
			} else {
				this.moduleDirectives.remove(moduleDirective);
			}
		}

		return (T) this;
	}

	@Override
	public <T extends CtModule> T setIsOpenModule(boolean openModule) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.MODIFIER, openModule, this.openModule);
		this.openModule = openModule;
		return (T) this;
	}

	@Override
	public List<CtUsedService> getUsedServices() {
		if (this.moduleDirectives.isEmpty()) {
			return CtElementImpl.emptyList();
		} else {
			List<CtUsedService> usedServices = new ArrayList<>();
			for (CtModuleDirective moduleDirective : this.moduleDirectives) {
				if (moduleDirective instanceof CtUsedService) {
					usedServices.add((CtUsedService) moduleDirective);
				}
			}
			return Collections.unmodifiableList(usedServices);
		}
	}

	@Override
	public <T extends CtModule> T setUsedServices(List<CtUsedService> consumedServices) {
		if (consumedServices == null || consumedServices.isEmpty()) {
			return (T) this;
		}
		List<CtUsedService> usedServices = getUsedServices();
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.SERVICE_TYPE, this.moduleDirectives, new ArrayList<>(usedServices));
		this.moduleDirectives.removeAll(usedServices);

		for (CtUsedService consumedService : consumedServices) {
			this.addModuleDirective(consumedService);
		}
		return (T) this;
	}

	@Override
	public <T extends CtModule> T addUsedService(CtUsedService consumedService) {
		if (consumedService == null) {
			return (T) this;
		}

		this.addModuleDirective(consumedService);
		return (T) this;
	}

	@Override
	public <T extends CtModule> T removeUsedService(CtUsedService usedService) {
		if (usedService == null) {
			return (T) this;
		}
		return this.removeModuleDirective(usedService);
	}

	@Override
	public List<CtPackageExport> getExportedPackages() {
		if (this.moduleDirectives.isEmpty()) {
			return CtElementImpl.emptyList();
		} else {
			List<CtPackageExport> exportedPackages = new ArrayList<>();
			for (CtModuleDirective moduleDirective : this.moduleDirectives) {
				if (moduleDirective instanceof CtPackageExport) {
					CtPackageExport exportedPackage = (CtPackageExport) moduleDirective;
					if (!exportedPackage.isOpenedPackage()) {
						exportedPackages.add(exportedPackage);
					}
				}
			}
			return Collections.unmodifiableList(exportedPackages);
		}
	}

	@Override
	public <T extends CtModule> T setExportedPackages(List<CtPackageExport> exportedPackages) {
		if (exportedPackages == null || exportedPackages.isEmpty()) {
			return (T) this;
		}

		List<CtPackageExport> oldExportedPackages = getExportedPackages();
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.EXPORTED_PACKAGE, this.moduleDirectives, new ArrayList<>(oldExportedPackages));
		this.moduleDirectives.removeAll(oldExportedPackages);

		for (CtPackageExport exportedPackage : exportedPackages) {
			exportedPackage.setOpenedPackage(false);
			this.addModuleDirective(exportedPackage);
		}

		return (T) this;
	}

	@Override
	public <T extends CtModule> T addExportedPackage(CtPackageExport exportedPackage) {
		if (exportedPackage == null) {
			return (T) this;
		}
		exportedPackage.setOpenedPackage(false);
		this.addModuleDirective(exportedPackage);
		return (T) this;
	}

	@Override
	public <T extends CtModule> T removeExportedPackage(CtPackageExport exportedPackage) {
		if (exportedPackage == null) {
			return (T) this;
		}
		return this.removeModuleDirective(exportedPackage);
	}

	@Override
	public List<CtPackageExport> getOpenedPackages() {
		if (this.moduleDirectives.isEmpty()) {
			return CtElementImpl.emptyList();
		} else {
			List<CtPackageExport> openedPackages = new ArrayList<>();
			for (CtModuleDirective moduleDirective : this.moduleDirectives) {
				if (moduleDirective instanceof CtPackageExport) {
					CtPackageExport exportedPackage = (CtPackageExport) moduleDirective;
					if (exportedPackage.isOpenedPackage()) {
						openedPackages.add(exportedPackage);
					}
				}
			}
			return Collections.unmodifiableList(openedPackages);
		}
	}

	@Override
	public <T extends CtModule> T setOpenedPackages(List<CtPackageExport> openedPackages) {
		if (openedPackages == null || openedPackages.isEmpty()) {
			return (T) this;
		}

		List<CtPackageExport> oldOpenedPackages = getOpenedPackages();
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.OPENED_PACKAGE, this.moduleDirectives, new ArrayList<>(oldOpenedPackages));
		this.moduleDirectives.removeAll(oldOpenedPackages);

		for (CtPackageExport exportedPackage : openedPackages) {
			exportedPackage.setOpenedPackage(true);
			this.addModuleDirective(exportedPackage);
		}

		return (T) this;
	}

	@Override
	public <T extends CtModule> T addOpenedPackage(CtPackageExport openedPackage) {
		if (openedPackage == null) {
			return (T) this;
		}
		openedPackage.setOpenedPackage(true);
		this.addModuleDirective(openedPackage);
		return (T) this;
	}

	@Override
	public <T extends CtModule> T removeOpenedPackage(CtPackageExport openedPackage) {
		if (openedPackage == null) {
			return (T) this;
		}
		return this.removeModuleDirective(openedPackage);
	}

	@Override
	public List<CtModuleRequirement> getRequiredModules() {
		if (this.moduleDirectives.isEmpty()) {
			return CtElementImpl.emptyList();
		} else {
			List<CtModuleRequirement> moduleRequirements = new ArrayList<>();
			for (CtModuleDirective moduleDirective : this.moduleDirectives) {
				if (moduleDirective instanceof CtModuleRequirement) {
					moduleRequirements.add((CtModuleRequirement) moduleDirective);
				}
			}
			return Collections.unmodifiableList(moduleRequirements);
		}
	}

	@Override
	public <T extends CtModule> T setRequiredModules(List<CtModuleRequirement> requiredModules) {
		if (requiredModules == null || requiredModules.isEmpty()) {
			return (T) this;
		}

		List<CtModuleRequirement> oldRequiredModules = getRequiredModules();
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.REQUIRED_MODULE, this.moduleDirectives, new ArrayList<>(oldRequiredModules));
		this.moduleDirectives.removeAll(oldRequiredModules);

		for (CtModuleRequirement moduleRequirement : requiredModules) {
			this.addModuleDirective(moduleRequirement);
		}
		return (T) this;
	}

	@Override
	public <T extends CtModule> T addRequiredModule(CtModuleRequirement requiredModule) {
		if (requiredModule == null) {
			return (T) this;
		}

		this.addModuleDirective(requiredModule);
		return (T) this;
	}

	@Override
	public <T extends CtModule> T removeRequiredModule(CtModuleRequirement requiredModule) {
		if (requiredModule == null) {
			return (T) this;
		}
		return this.removeModuleDirective(requiredModule);
	}

	@Override
	public List<CtProvidedService> getProvidedServices() {
		if (this.moduleDirectives.isEmpty()) {
			return CtElementImpl.emptyList();
		} else {
			List<CtProvidedService> providedServices = new ArrayList<>();
			for (CtModuleDirective moduleDirective : this.moduleDirectives) {
				if (moduleDirective instanceof CtProvidedService) {
					providedServices.add((CtProvidedService) moduleDirective);
				}
			}
			return Collections.unmodifiableList(providedServices);
		}
	}

	@Override
	public <T extends CtModule> T setProvidedServices(List<CtProvidedService> providedServices) {
		if (providedServices == null || providedServices.isEmpty()) {
			return (T) this;
		}

		List<CtProvidedService> oldProvidedServices = getProvidedServices();
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.PROVIDED_SERVICE, this.moduleDirectives, new ArrayList<>(oldProvidedServices));
		this.moduleDirectives.removeAll(oldProvidedServices);

		for (CtProvidedService providedService : providedServices) {
			this.addModuleDirective(providedService);
		}

		return (T) this;
	}

	@Override
	public <T extends CtModule> T addProvidedService(CtProvidedService providedService) {
		if (providedService == null) {
			return (T) this;
		}

		this.addModuleDirective(providedService);
		return (T) this;
	}

	@Override
	public <T extends CtModule> T removeProvidedService(CtProvidedService providedService) {
		if (providedService == null) {
			return (T) this;
		}
		return this.removeModuleDirective(providedService);
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
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.SUB_PACKAGE, rootPackage, this.rootPackage);
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

	@Override
	@DerivedProperty
	public <T extends CtElement> T setParent(T parent) {
		return (T) this;
	}

	@Override
	@DerivedProperty
	public CtElement getParent() {
		return getFactory().getModel().getUnnamedModule();
	}
}
