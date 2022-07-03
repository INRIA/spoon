/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.factory;


import java.util.Collection;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;

public class ModuleFactory extends SubFactory {
    public ModuleFactory(Factory factory) {
        super(factory);
    }

    public CtModule getUnnamedModule() {
        return factory.getModel().getUnnamedModule();
    }

    public Collection<CtModule> getAllModules() {
        return factory.getModel().getAllModules();
    }

    public CtModule getModule(String moduleName) {
		if (moduleName == null || moduleName.isEmpty()) {
			return getUnnamedModule();
		}

        return factory.getModel().getModule(moduleName);
    }

    public CtModule getOrCreate(String moduleName) {
        if (moduleName == null || moduleName.isEmpty()) {
            return getUnnamedModule();
        }

		CtModule known = getModule(moduleName);
		if (known != null) {
			return known;
		}

		CtModule fresh = factory.Core().createModule().setSimpleName(moduleName);
		factory.getModel().addModule(fresh);
		return fresh;
    }

    public CtModuleReference createReference(CtModule module) {
        return createReference(module.getSimpleName());
    }

	public CtModuleReference createReference(String module) {
		return factory.Core().createModuleReference().setSimpleName(module);
	}

	public CtModuleRequirement createModuleRequirement(CtModuleReference moduleReference) {
		return factory.Core().createModuleRequirement().setModuleReference(moduleReference);
	}

	public CtPackageExport createPackageExport(CtPackageReference ctPackageReference) {
		return factory.Core().createPackageExport().setPackageReference(ctPackageReference);
	}

	public CtProvidedService createProvidedService(CtTypeReference typeReference) {
		return factory.Core().createProvidedService().setServiceType(typeReference);
	}

	public CtUsedService createUsedService(CtTypeReference typeReference) {
		return factory.Core().createUsedService().setServiceType(typeReference);
	}
}

