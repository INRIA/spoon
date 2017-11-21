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
package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;

import java.util.List;

import static spoon.reflect.path.CtRole.EXPORTED_PACKAGE;
import static spoon.reflect.path.CtRole.MODIFIER;
import static spoon.reflect.path.CtRole.OPENED_PACKAGE;
import static spoon.reflect.path.CtRole.PROVIDED_SERVICE;
import static spoon.reflect.path.CtRole.REQUIRED_MODULE;
import static spoon.reflect.path.CtRole.SERVICE_TYPE;
import static spoon.reflect.path.CtRole.SUB_PACKAGE;

/**
 * Represents a Java module as defined in Java 9
 *
 * Example:
 * <pre>
 *     module com.example.foo {
 *
 *     }
 * </pre>
 */
public interface CtModule extends CtNamedElement {

	/**
	 * The name for the top level module.
	 */
	String TOP_LEVEL_MODULE_NAME = "unnamed module";

	@DerivedProperty
	boolean isUnnamedModule();

	@PropertyGetter(role = MODIFIER)
	boolean isOpenModule();

	@PropertySetter(role = MODIFIER)
	<T extends CtModule> T setIsOpenModule(boolean openModule);

	@PropertyGetter(role = SERVICE_TYPE)
	List<CtTypeReference> getConsumedServices();

	@PropertySetter(role = SERVICE_TYPE)
	<T extends CtModule> T setConsumedServices(List<CtTypeReference> consumedServices);

	@PropertySetter(role = SERVICE_TYPE)
	<T extends CtModule> T addConsumedService(CtTypeReference consumedService);

	@PropertyGetter(role = EXPORTED_PACKAGE)
	List<CtModuleExport> getExportedPackages();

	@PropertySetter(role = EXPORTED_PACKAGE)
	<T extends CtModule> T setExportedPackages(List<CtModuleExport> exportedPackages);

	@PropertySetter(role = EXPORTED_PACKAGE)
	<T extends CtModule> T addExportedPackage(CtModuleExport exportedPackage);

	@PropertyGetter(role = OPENED_PACKAGE)
	List<CtModuleExport> getOpenedPackages();

	@PropertySetter(role = OPENED_PACKAGE)
	<T extends CtModule> T setOpenedPackages(List<CtModuleExport> openedPackages);

	@PropertySetter(role = OPENED_PACKAGE)
	<T extends CtModule> T addOpenedPackage(CtModuleExport openedPackage);

	@PropertyGetter(role = REQUIRED_MODULE)
	List<CtModuleRequirement> getRequiredModules();

	@PropertySetter(role = REQUIRED_MODULE)
	<T extends CtModule> T setRequiredModules(List<CtModuleRequirement> requiredModules);

	@PropertySetter(role = REQUIRED_MODULE)
	<T extends CtModule> T addRequiredModule(CtModuleRequirement requiredModule);

	@PropertyGetter(role = PROVIDED_SERVICE)
	List<CtModuleProvidedService> getProvidedServices();

	@PropertySetter(role = PROVIDED_SERVICE)
	<T extends CtModule> T setProvidedServices(List<CtModuleProvidedService> providedServices);

	@PropertySetter(role = PROVIDED_SERVICE)
	<T extends CtModule> T addProvidedService(CtModuleProvidedService providedService);

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
