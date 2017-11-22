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
package spoon.reflect.factory;

import spoon.reflect.CtModelImpl;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleExport;
import spoon.reflect.declaration.CtModuleProvidedService;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.reflect.declaration.CtModuleImpl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ModuleFactory extends SubFactory implements Serializable {

	public static class CtUnnamedModule extends CtModuleImpl {
		{
			this.setSimpleName(CtModule.TOP_LEVEL_MODULE_NAME);
			this.setParent(new CtElementImpl() {
				@Override
				public void accept(CtVisitor visitor) {

				}

				@Override
				public CtElement getParent() throws ParentNotInitializedException {
					return null;
				}

				@Override
				public Factory getFactory() {
					return CtUnnamedModule.this.getFactory();
				}
			});

			this.setRootPackage(new CtModelImpl.CtRootPackage());
		}

		@Override
		public <T extends CtNamedElement> T setSimpleName(String name) {
			if (name == null) {
				return (T) this;
			}

			if (name.equals(CtModule.TOP_LEVEL_MODULE_NAME)) {
				return super.setSimpleName(name);
			}

			return (T) this;
		}

		@Override
		public String toString() {
			return CtModule.TOP_LEVEL_MODULE_NAME;
		}

		@Override
		public void accept(CtVisitor visitor) {
			visitor.visitCtModule(this);
			getFactory().Module().getAllModules().forEach(module -> {
				if (!module.getSimpleName().equals(CtModule.TOP_LEVEL_MODULE_NAME)) {
					module.accept(visitor);
				}
			});
		}
	}

	final Map<String, CtModule> allModules = new HashMap<>();

	public ModuleFactory(Factory factory) {
		super(factory);
		// in case of deserializing model
		if (factory.getModel() != null && factory.getModel().getUnnamedModule() != null) {
			allModules.put(CtModule.TOP_LEVEL_MODULE_NAME, factory.getModel().getUnnamedModule());
		} else {
			CtModule unnamedModule = new CtUnnamedModule();
			unnamedModule.setFactory(factory);
			allModules.put(CtModule.TOP_LEVEL_MODULE_NAME, unnamedModule);
		}

	}

	public Collection<CtModule> getAllModules() {
		return Collections.unmodifiableCollection(allModules.values());
	}

	public CtModule getModule(String moduleName) {
		return allModules.get(moduleName);
	}

	public CtModule getOrCreate(String moduleName) {
		if (moduleName == null || moduleName.isEmpty()) {
			return allModules.get(CtModule.TOP_LEVEL_MODULE_NAME);
		}

		if (!allModules.containsKey(moduleName)) {
			allModules.put(moduleName, factory.Core().createModule().setParent(this.allModules.get(CtModule.TOP_LEVEL_MODULE_NAME)).setSimpleName(moduleName));
		}
		return allModules.get(moduleName);
	}

	public CtModuleReference createReference(CtModule module) {
		return factory.Core().createModuleReference().setSimpleName(module.getSimpleName());
	}

	public CtModuleRequirement createModuleRequirement(CtModuleReference moduleReference) {
		return factory.Core().createModuleRequirement().setModuleReference(moduleReference);
	}

	public CtModuleExport createModuleExport(CtPackageReference ctPackageReference) {
		return factory.Core().createModuleExport().setPackageReference(ctPackageReference);
	}

	public CtModuleProvidedService createModuleProvidedService(CtTypeReference typeReference) {
		return factory.Core().createModuleProvidedService().setServiceType(typeReference);
	}
}
