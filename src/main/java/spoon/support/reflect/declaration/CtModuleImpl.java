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
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleMember;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.factory.ModuleFactory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.comparator.CtLineElementComparator;
import spoon.support.util.SortedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CtModuleImpl extends CtNamedElementImpl implements CtModule {
	@MetamodelPropertyField(role = CtRole.MODIFIER)
	private boolean openModule;

	@MetamodelPropertyField(role = CtRole.MODULE_MEMBER)
	private List<CtModuleMember> moduleMembers = CtElementImpl.emptyList();

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
	public <T extends CtModule> T setModuleMembers(List<CtModuleMember> moduleMembers) {
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.MODULE_MEMBER, this.moduleMembers, new ArrayList<>(this.moduleMembers));
		if (moduleMembers == null || moduleMembers.isEmpty()) {
			this.moduleMembers = CtElementImpl.emptyList();
			return (T) this;
		}
		if (this.moduleMembers == CtElementImpl.<CtModuleMember>emptyList()) {
			this.moduleMembers = new SortedList<>(new CtLineElementComparator());
		}
		this.moduleMembers.clear();

		for (CtModuleMember moduleMember : moduleMembers) {
			this.addModuleMember(moduleMember);
		}

		return (T) this;
	}

	@Override
	public <T extends CtModule> T addModuleMember(CtModuleMember moduleMember) {
		if (moduleMember == null) {
			return (T) this;
		}

		return this.addModuleMemberAt(this.moduleMembers.size(), moduleMember);
	}

	private CtRole computeRoleFromModuleMember(CtModuleMember moduleMember) {
		CtRole role;
		if (moduleMember instanceof CtModuleRequirement) {
			role = CtRole.REQUIRED_MODULE;
		} else if (moduleMember instanceof CtUsedService) {
			role = CtRole.SERVICE_TYPE;
		} else if (moduleMember instanceof CtProvidedService) {
			role = CtRole.PROVIDED_SERVICE;
		} else if (moduleMember instanceof CtPackageExport) {
			CtPackageExport packageExport = (CtPackageExport) moduleMember;
			if (packageExport.isOpenedPackage()) {
				role = CtRole.OPENED_PACKAGE;
			} else {
				role = CtRole.EXPORTED_PACKAGE;
			}
		} else {
			role = CtRole.MODULE_MEMBER;
		}
		return role;
	}

	@Override
	public <T extends CtModule> T addModuleMemberAt(int position, CtModuleMember moduleMember) {
		if (moduleMember == null) {
			return (T) this;
		}

		if (this.moduleMembers == CtElementImpl.<CtModuleMember>emptyList()) {
			this.moduleMembers = new SortedList<>(new CtLineElementComparator());
		}
		if (!this.moduleMembers.contains(moduleMember)) {
			moduleMember.setParent(this);
			CtRole role = this.computeRoleFromModuleMember(moduleMember);

			getFactory().getEnvironment().getModelChangeListener().onListAdd(this, role, this.moduleMembers, position, moduleMember);
			this.moduleMembers.add(position, moduleMember);
		}

		return (T) this;
	}

	@Override
	public List<CtModuleMember> getModuleMembers() {
		return Collections.unmodifiableList(this.moduleMembers);
	}

	@Override
	public <T extends CtModule> T removeModuleMember(CtModuleMember moduleMember) {
		if (moduleMember == null || this.moduleMembers.size() == 0) {
			return (T) this;
		}
		if (this.moduleMembers.contains(moduleMember)) {
			getFactory().getEnvironment().getModelChangeListener().onListDelete(this, this.computeRoleFromModuleMember(moduleMember), this.moduleMembers, this.moduleMembers.indexOf(moduleMember), moduleMember);
			if (this.moduleMembers.size() == 1) {
				this.moduleMembers = CtElementImpl.emptyList();
			} else {
				this.moduleMembers.remove(moduleMember);
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
		if (this.moduleMembers.isEmpty()) {
			return CtElementImpl.emptyList();
		} else {
			List<CtUsedService> usedServices = new ArrayList<>();
			for (CtModuleMember moduleMember : this.moduleMembers) {
				if (moduleMember instanceof CtUsedService) {
					usedServices.add((CtUsedService) moduleMember);
				}
			}
			return usedServices;
		}
	}

	@Override
	public <T extends CtModule> T setUsedServices(List<CtUsedService> consumedServices) {
		if (consumedServices == null || consumedServices.isEmpty()) {
			return (T) this;
		}

		for (CtUsedService consumedService : consumedServices) {
			this.addModuleMember(consumedService);
		}
		return (T) this;
	}

	@Override
	public <T extends CtModule> T addUsedService(CtUsedService consumedService) {
		if (consumedService == null) {
			return (T) this;
		}

		this.addModuleMember(consumedService);
		return (T) this;
	}

	@Override
	public <T extends CtModule> T removeUsedService(CtUsedService usedService) {
		if (usedService == null) {
			return (T) this;
		}
		return this.removeModuleMember(usedService);
	}

	@Override
	public List<CtPackageExport> getExportedPackages() {
		if (this.moduleMembers.isEmpty()) {
			return CtElementImpl.emptyList();
		} else {
			List<CtPackageExport> exportedPackages = new ArrayList<>();
			for (CtModuleMember moduleMember : this.moduleMembers) {
				if (moduleMember instanceof CtPackageExport) {
					CtPackageExport exportedPackage = (CtPackageExport) moduleMember;
					if (!exportedPackage.isOpenedPackage()) {
						exportedPackages.add(exportedPackage);
					}
				}
			}
			return exportedPackages;
		}
	}

	@Override
	public <T extends CtModule> T setExportedPackages(List<CtPackageExport> exportedPackages) {
		if (exportedPackages == null || exportedPackages.isEmpty()) {
			return (T) this;
		}

		for (CtPackageExport exportedPackage : exportedPackages) {
			exportedPackage.setOpenedPackage(false);
			this.addModuleMember(exportedPackage);
		}

		return (T) this;
	}

	@Override
	public <T extends CtModule> T addExportedPackage(CtPackageExport exportedPackage) {
		if (exportedPackage == null) {
			return (T) this;
		}
		exportedPackage.setOpenedPackage(false);
		this.addModuleMember(exportedPackage);
		return (T) this;
	}

	@Override
	public <T extends CtModule> T removeExportedPackage(CtPackageExport exportedPackage) {
		if (exportedPackage == null) {
			return (T) this;
		}
		return this.removeModuleMember(exportedPackage);
	}

	@Override
	public List<CtPackageExport> getOpenedPackages() {
		if (this.moduleMembers.isEmpty()) {
			return CtElementImpl.emptyList();
		} else {
			List<CtPackageExport> openedPackages = new ArrayList<>();
			for (CtModuleMember moduleMember : this.moduleMembers) {
				if (moduleMember instanceof CtPackageExport) {
					CtPackageExport exportedPackage = (CtPackageExport) moduleMember;
					if (exportedPackage.isOpenedPackage()) {
						openedPackages.add(exportedPackage);
					}
				}
			}
			return openedPackages;
		}
	}

	@Override
	public <T extends CtModule> T setOpenedPackages(List<CtPackageExport> openedPackages) {
		if (openedPackages == null || openedPackages.isEmpty()) {
			return (T) this;
		}

		for (CtPackageExport exportedPackage : openedPackages) {
			exportedPackage.setOpenedPackage(true);
			this.addModuleMember(exportedPackage);
		}

		return (T) this;
	}

	@Override
	public <T extends CtModule> T addOpenedPackage(CtPackageExport openedPackage) {
		if (openedPackage == null) {
			return (T) this;
		}
		openedPackage.setOpenedPackage(true);
		this.addModuleMember(openedPackage);
		return (T) this;
	}

	@Override
	public <T extends CtModule> T removeOpenedPackage(CtPackageExport openedPackage) {
		if (openedPackage == null) {
			return (T) this;
		}
		return this.removeModuleMember(openedPackage);
	}

	@Override
	public List<CtModuleRequirement> getRequiredModules() {
		if (this.moduleMembers.isEmpty()) {
			return CtElementImpl.emptyList();
		} else {
			List<CtModuleRequirement> moduleRequirements = new ArrayList<>();
			for (CtModuleMember moduleMember : this.moduleMembers) {
				if (moduleMember instanceof CtModuleRequirement) {
					moduleRequirements.add((CtModuleRequirement) moduleMember);
				}
			}
			return moduleRequirements;
		}
	}

	@Override
	public <T extends CtModule> T setRequiredModules(List<CtModuleRequirement> requiredModules) {
		if (requiredModules == null || requiredModules.isEmpty()) {
			return (T) this;
		}

		for (CtModuleRequirement moduleRequirement : requiredModules) {
			this.addModuleMember(moduleRequirement);
		}
		return (T) this;
	}

	@Override
	public <T extends CtModule> T addRequiredModule(CtModuleRequirement requiredModule) {
		if (requiredModule == null) {
			return (T) this;
		}

		this.addModuleMember(requiredModule);
		return (T) this;
	}

	@Override
	public <T extends CtModule> T removeRequiredModule(CtModuleRequirement requiredModule) {
		if (requiredModule == null) {
			return (T) this;
		}
		return this.removeModuleMember(requiredModule);
	}

	@Override
	public List<CtProvidedService> getProvidedServices() {
		if (this.moduleMembers.isEmpty()) {
			return CtElementImpl.emptyList();
		} else {
			List<CtProvidedService> providedServices = new ArrayList<>();
			for (CtModuleMember moduleMember : this.moduleMembers) {
				if (moduleMember instanceof CtProvidedService) {
					providedServices.add((CtProvidedService) moduleMember);
				}
			}
			return providedServices;
		}
	}

	@Override
	public <T extends CtModule> T setProvidedServices(List<CtProvidedService> providedServices) {
		if (providedServices == null || providedServices.isEmpty()) {
			return (T) this;
		}

		for (CtProvidedService providedService : providedServices) {
			this.addModuleMember(providedService);
		}

		return (T) this;
	}

	@Override
	public <T extends CtModule> T addProvidedService(CtProvidedService providedService) {
		if (providedService == null) {
			return (T) this;
		}

		this.addModuleMember(providedService);
		return (T) this;
	}

	@Override
	public <T extends CtModule> T removeProvidedService(CtProvidedService providedService) {
		if (providedService == null) {
			return (T) this;
		}
		return this.removeModuleMember(providedService);
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
