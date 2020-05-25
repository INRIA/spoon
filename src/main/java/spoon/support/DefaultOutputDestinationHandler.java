/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
public class DefaultOutputDestinationHandler implements OutputDestinationHandler {

	private File defaultOutputDirectory;
	private Environment environment;

	public DefaultOutputDestinationHandler(File defaultOutputDirectory, Environment environment) {
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

	/**
	 * @return return the filename of the current element
	 * @param pack
	 * @param type
	 */
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

	/**
	 * @return the path of the package
	 * @param pack
	 */
	protected Path getPackagePath(CtPackage pack) {
		Path packagePath = Paths.get(".");
		if (pack != null && !pack.isUnnamedPackage()) {
			packagePath = Paths.get(pack.getQualifiedName().replace('.', File.separatorChar));
		}
		return packagePath;
	}

	/**
	 * @return return the path of the module
	 * @param module
	 */
	protected Path getModulePath(CtModule module) {
		Path moduleDir = Paths.get(".");
		if (module != null && !module.isUnnamedModule() && environment.getComplianceLevel() > 8) {
			moduleDir = Paths.get(module.getSimpleName());
		}
		return moduleDir;
	}

	/**
	 * @return the root path of the destination
	 * @param module
	 * @param pack
	 * @param type
	 */
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
