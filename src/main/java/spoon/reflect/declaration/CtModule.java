/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.reference.CtModuleReference;
import spoon.support.DerivedProperty;

import java.util.List;

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
	<T extends CtModule> T setIsOpenModule(boolean openModule);

	@PropertySetter(role = MODULE_DIRECTIVE)
	<T extends CtModule> T setModuleDirectives(List<CtModuleDirective> moduleDirectives);

	@PropertySetter(role = MODULE_DIRECTIVE)
	<T extends CtModule> T addModuleDirective(CtModuleDirective moduleDirective);

	@PropertySetter(role = MODULE_DIRECTIVE)
	<T extends CtModule> T addModuleDirectiveAt(int position, CtModuleDirective moduleDirective);

	@PropertyGetter(role = MODULE_DIRECTIVE)
	List<CtModuleDirective> getModuleDirectives();

	@PropertySetter(role = MODULE_DIRECTIVE)
	<T extends CtModule> T removeModuleDirective(CtModuleDirective moduleDirective);

	@PropertyGetter(role = SERVICE_TYPE)
	@DerivedProperty
	List<CtUsedService> getUsedServices();

	@PropertySetter(role = SERVICE_TYPE)
	@DerivedProperty
	<T extends CtModule> T setUsedServices(List<CtUsedService> usedServices);

	@PropertySetter(role = SERVICE_TYPE)
	@DerivedProperty
	<T extends CtModule> T addUsedService(CtUsedService usedService);

	@PropertySetter(role = SERVICE_TYPE)
	@DerivedProperty
	<T extends CtModule> T removeUsedService(CtUsedService usedService);

	@PropertyGetter(role = EXPORTED_PACKAGE)
	@DerivedProperty
	List<CtPackageExport> getExportedPackages();

	@PropertySetter(role = EXPORTED_PACKAGE)
	@DerivedProperty
	<T extends CtModule> T setExportedPackages(List<CtPackageExport> exportedPackages);

	@PropertySetter(role = EXPORTED_PACKAGE)
	@DerivedProperty
	<T extends CtModule> T addExportedPackage(CtPackageExport exportedPackage);

	@PropertySetter(role = EXPORTED_PACKAGE)
	@DerivedProperty
	<T extends CtModule> T removeExportedPackage(CtPackageExport exportedPackage);

	@PropertyGetter(role = OPENED_PACKAGE)
	@DerivedProperty
	List<CtPackageExport> getOpenedPackages();

	@PropertySetter(role = OPENED_PACKAGE)
	@DerivedProperty
	<T extends CtModule> T setOpenedPackages(List<CtPackageExport> openedPackages);

	@PropertySetter(role = OPENED_PACKAGE)
	@DerivedProperty
	<T extends CtModule> T addOpenedPackage(CtPackageExport openedPackage);

	@PropertySetter(role = OPENED_PACKAGE)
	@DerivedProperty
	<T extends CtModule> T removeOpenedPackage(CtPackageExport openedPackage);

	@PropertyGetter(role = REQUIRED_MODULE)
	@DerivedProperty
	List<CtModuleRequirement> getRequiredModules();

	@PropertySetter(role = REQUIRED_MODULE)
	@DerivedProperty
	<T extends CtModule> T setRequiredModules(List<CtModuleRequirement> requiredModules);

	@PropertySetter(role = REQUIRED_MODULE)
	@DerivedProperty
	<T extends CtModule> T addRequiredModule(CtModuleRequirement requiredModule);

	@PropertySetter(role = REQUIRED_MODULE)
	@DerivedProperty
	<T extends CtModule> T removeRequiredModule(CtModuleRequirement requiredModule);

	@PropertyGetter(role = PROVIDED_SERVICE)
	@DerivedProperty
	List<CtProvidedService> getProvidedServices();

	@PropertySetter(role = PROVIDED_SERVICE)
	@DerivedProperty
	<T extends CtModule> T setProvidedServices(List<CtProvidedService> providedServices);

	@PropertySetter(role = PROVIDED_SERVICE)
	@DerivedProperty
	<T extends CtModule> T addProvidedService(CtProvidedService providedService);

	@PropertySetter(role = PROVIDED_SERVICE)
	@DerivedProperty
	<T extends CtModule> T removeProvidedService(CtProvidedService providedService);

	/**
	 * returns the root package of the unnamed module
	 * If there are several modules, it throws an exception
	 */
	@PropertyGetter(role = SUB_PACKAGE)
	CtPackage getRootPackage();

	@PropertySetter(role = SUB_PACKAGE)
	<T extends CtModule> T setRootPackage(CtPackage rootPackage);

	@DerivedProperty
	@Override
	CtModuleReference getReference();

	@Override
	CtModule clone();
}
