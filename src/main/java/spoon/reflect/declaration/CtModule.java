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

import spoon.reflect.reference.CtModuleExport;
import spoon.reflect.reference.CtModuleProvidedService;
import spoon.reflect.reference.CtModuleRequirement;
import spoon.reflect.reference.CtTypeReference;

import java.util.Set;

public interface CtModule extends CtNamedElement {

	/**
	 * The name for the top level module.
	 */
	String TOP_LEVEL_MODULE_NAME = "unnamed module";

	boolean isUnnamedModule();

	boolean isOpenModule();

	<T extends CtModule> T setIsOpenModule(boolean openModule);

	Set<CtTypeReference> getConsumedServices();

	<T extends CtModule> T setConsumedServices(Set<CtTypeReference> consumedServices);

	Set<CtModuleExport> getExportedPackages();

	<T extends CtModule> T setExportedPackages(Set<CtModuleExport> exportedPackages);

	Set<CtModuleExport> getOpenedPackages();

	<T extends CtModule> T setOpenedPackages(Set<CtModuleExport> openedPackages);

	Set<CtModuleRequirement> getRequiredModules();

	<T extends CtModule> T setRequiredModules(Set<CtModuleRequirement> requiredModules);

	Set<CtModuleProvidedService> getProvidedServices();

	<T extends CtModule> T setProvidedServices(Set<CtModuleProvidedService> providedServices);

	CtPackage getRootPackage();

	<T extends CtModule> T setRootPackage(CtPackage rootPackage);
}
