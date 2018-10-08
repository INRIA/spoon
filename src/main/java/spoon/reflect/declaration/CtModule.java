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
package spoon.reflect.declaration;


import java.util.List;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.reference.CtModuleReference;
import spoon.support.DerivedProperty;

import static spoon.reflect.path.CtRole.EXPORTED_PACKAGE;
import static spoon.reflect.path.CtRole.MODIFIER;
import static spoon.reflect.path.CtRole.MODULE_DIRECTIVE;
import static spoon.reflect.path.CtRole.OPENED_PACKAGE;
import static spoon.reflect.path.CtRole.PROVIDED_SERVICE;
import static spoon.reflect.path.CtRole.REQUIRED_MODULE;
import static spoon.reflect.path.CtRole.SERVICE_TYPE;
import static spoon.reflect.path.CtRole.SUB_PACKAGE;




/**
 * Represents a Java module as defined in Java 9.
 *
 * Modules are defined in `module-info.java` as follows:
 * <pre>
 *     module com.example.foo {
 *
 *     }
 * </pre>
 *
 * Modules define required modules, and exported packages for client code.
 *
 * A module can export a service, defined as a type.
 * Provided services are implementations of given service.
 * Modules can require services ("uses" in Java 9)
 *
 * An open module, with the open modifier,
 * grants access at compile time to types in only those packages which are explicitly exported,
 * but grants access at run time to types in all its packages, as if all packages had been exported.
 */
public interface CtModule extends CtNamedElement {

	/**
	 * The name for the top level module.
	 */
	String TOP_LEVEL_MODULE_NAME = "unnamed module";

	/**
	 * Returns true if the module is the unnamed module
	 */
	@DerivedProperty
	boolean isUnnamedModule();

	@PropertyGetter(role = MODIFIER)
	boolean isOpenModule();

	@PropertySetter(role = MODIFIER)
	CtModule setIsOpenModule(boolean openModule);

	@PropertySetter(role = MODULE_DIRECTIVE)
	CtModule setModuleDirectives(List<CtModuleDirective> moduleDirectives);

	@PropertySetter(role = MODULE_DIRECTIVE)
	CtModule addModuleDirective(CtModuleDirective moduleDirective);

	@PropertySetter(role = MODULE_DIRECTIVE)
	CtModule addModuleDirectiveAt(int position, CtModuleDirective moduleDirective);

	@PropertyGetter(role = MODULE_DIRECTIVE)
	List<CtModuleDirective> getModuleDirectives();

	@PropertySetter(role = MODULE_DIRECTIVE)
	CtModule removeModuleDirective(CtModuleDirective moduleDirective);

	@PropertyGetter(role = SERVICE_TYPE)
	@DerivedProperty
	List<CtUsedService> getUsedServices();

	@PropertySetter(role = SERVICE_TYPE)
	@DerivedProperty
	CtModule setUsedServices(List<CtUsedService> usedServices);

	@PropertySetter(role = SERVICE_TYPE)
	@DerivedProperty
	CtModule addUsedService(CtUsedService usedService);

	@PropertySetter(role = SERVICE_TYPE)
	@DerivedProperty
	CtModule removeUsedService(CtUsedService usedService);

	@PropertyGetter(role = EXPORTED_PACKAGE)
	@DerivedProperty
	List<CtPackageExport> getExportedPackages();

	@PropertySetter(role = EXPORTED_PACKAGE)
	@DerivedProperty
	CtModule setExportedPackages(List<CtPackageExport> exportedPackages);

	@PropertySetter(role = EXPORTED_PACKAGE)
	@DerivedProperty
	CtModule addExportedPackage(CtPackageExport exportedPackage);

	@PropertySetter(role = EXPORTED_PACKAGE)
	@DerivedProperty
	CtModule removeExportedPackage(CtPackageExport exportedPackage);

	@PropertyGetter(role = OPENED_PACKAGE)
	@DerivedProperty
	List<CtPackageExport> getOpenedPackages();

	@PropertySetter(role = OPENED_PACKAGE)
	@DerivedProperty
	CtModule setOpenedPackages(List<CtPackageExport> openedPackages);

	@PropertySetter(role = OPENED_PACKAGE)
	@DerivedProperty
	CtModule addOpenedPackage(CtPackageExport openedPackage);

	@PropertySetter(role = OPENED_PACKAGE)
	@DerivedProperty
	CtModule removeOpenedPackage(CtPackageExport openedPackage);

	@PropertyGetter(role = REQUIRED_MODULE)
	@DerivedProperty
	List<CtModuleRequirement> getRequiredModules();

	@PropertySetter(role = REQUIRED_MODULE)
	@DerivedProperty
	CtModule setRequiredModules(List<CtModuleRequirement> requiredModules);

	@PropertySetter(role = REQUIRED_MODULE)
	@DerivedProperty
	CtModule addRequiredModule(CtModuleRequirement requiredModule);

	@PropertySetter(role = REQUIRED_MODULE)
	@DerivedProperty
	CtModule removeRequiredModule(CtModuleRequirement requiredModule);

	@PropertyGetter(role = PROVIDED_SERVICE)
	@DerivedProperty
	List<CtProvidedService> getProvidedServices();

	@PropertySetter(role = PROVIDED_SERVICE)
	@DerivedProperty
	CtModule setProvidedServices(List<CtProvidedService> providedServices);

	@PropertySetter(role = PROVIDED_SERVICE)
	@DerivedProperty
	CtModule addProvidedService(CtProvidedService providedService);

	@PropertySetter(role = PROVIDED_SERVICE)
	@DerivedProperty
	CtModule removeProvidedService(CtProvidedService providedService);

	/**
	 * returns the root package of the unnamed module
	 * If there are several modules, it throws an exception
	 */
	@PropertyGetter(role = SUB_PACKAGE)
	CtPackage getRootPackage();

	@PropertySetter(role = SUB_PACKAGE)
	CtModule setRootPackage(CtPackage rootPackage);

	@DerivedProperty
	@Override
	CtModuleReference getReference();

	@Override
	CtModule clone();
}
