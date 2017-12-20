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
package spoon.support;

import spoon.compiler.Environment;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Default behavior for the destination of the spoon.
 */
public class DefaultOutputDestination implements OutputDestination {

	private File defaultOutputDirectory;
	private Environment environment;

	public DefaultOutputDestination(File defaultOutputDirectory, Environment environment) {
		this.defaultOutputDirectory = defaultOutputDirectory;
		this.environment = environment;
	}

	@Override
	public Path getOutputPath(CtModule module, CtPackage pack, CtType type) {
		Path directory = getDirectoryPath(module, pack, type);
		Path moduleDir = getModulePath(module);
		Path packagePath = getPackagePath(pack);
		String fileName = getFileName(pack, type);

		return Paths.get(directory.toString(), moduleDir.toString(), packagePath.toString(), fileName);
	}

	protected String getFileName(CtPackage pack, CtType type) {
		String fileName;
		if (type != null) {
			fileName = type.getSimpleName() + DefaultJavaPrettyPrinter.JAVA_FILE_EXTENSION;
		} else if (pack != null) {
			fileName = DefaultJavaPrettyPrinter.JAVA_PACKAGE_DECLARATION;
		} else {
			fileName = DefaultJavaPrettyPrinter.JAVA_MODULE_DECLARATION;
		}
		return fileName;
	}

	protected Path getPackagePath(CtPackage pack) {
		Path packagePath = Paths.get("./");
		if (pack != null && !pack.isUnnamedPackage()) {
			packagePath = Paths.get(pack.getQualifiedName().replace('.', File.separatorChar));
		}
		return packagePath;
	}

	protected Path getModulePath(CtModule module) {
		Path moduleDir = Paths.get("./");
		if (module != null && !module.isUnnamedModule() && environment.getComplianceLevel() > 8) {
			moduleDir = Paths.get(getDefaultOutputDirectory().getAbsolutePath(), module.getSimpleName());
		}
		return moduleDir;
	}

	protected Path getDirectoryPath(CtModule module, CtPackage pack, CtType type) {
		return Paths.get(getDefaultOutputDirectory().getAbsolutePath());
	}

	@Override
	public File getDefaultOutputDirectory() {
		return defaultOutputDirectory;
	}

	public Environment getEnvironment() {
		return environment;
	}
}
