/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.testing.utils;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

import java.io.File;
import java.io.FileNotFoundException;

public final class ModelUtils {
	private ModelUtils() {
		throw new AssertionError();
	}

	public static Factory createFactory() {
		return new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
	}

	public static <T extends CtType<?>> T build(String packageName, String className) throws Exception {
		SpoonCompiler comp = new Launcher().createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/" + packageName.replace('.', '/') + "/" + className + ".java"));
		comp.build();
		return comp.getFactory().Package().get(packageName).getType(className);
	}

	public static <T extends CtType<?>> T build(String packageName, String className, final Factory f) throws Exception {
		Launcher launcher = new Launcher() {
			@Override
			public Factory createFactory() {
				return f;
			}
		};
		SpoonCompiler comp = launcher.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/" + packageName.replace('.', '/') + "/" + className + ".java"));
		comp.build();
		return comp.getFactory().Package().get(packageName).getType(className);
	}

	public static Factory build(Class<?>... classesToBuild) throws Exception {
		SpoonCompiler comp = new Launcher().createCompiler();
		for (Class<?> classToBuild : classesToBuild) {
			comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/" + classToBuild.getName().replace('.', '/') + ".java"));
		}
		comp.build();
		return comp.getFactory();
	}

	public static Factory buildNoClasspath(Class<?>... classesToBuild) throws Exception {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		SpoonCompiler comp = launcher.createCompiler();
		for (Class<?> classToBuild : classesToBuild) {
			comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/" + classToBuild.getName().replace('.', '/') + ".java"));
		}
		comp.build();
		return comp.getFactory();
	}

	public static Factory build(File... filesToBuild) {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		SpoonCompiler comp = launcher.createCompiler();
		for (File fileToBuild : filesToBuild) {
			try {
				comp.addInputSource(SpoonResourceHelper.createResource(fileToBuild));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("File not found");
			}
		}
		comp.build();
		return comp.getFactory();
	}

	public static <T> CtType<T> buildClass(Class<T> classToBuild) throws Exception {
		return build(classToBuild).Type().get(classToBuild);
	}

	public static void canBeBuilt(File outputDirectoryFile, int complianceLevel) {
		canBeBuilt(outputDirectoryFile, complianceLevel, false);
	}

	public static void canBeBuilt(String outputDirectory, int complianceLevel) {
		canBeBuilt(outputDirectory, complianceLevel, false);
	}

	public static void canBeBuilt(File outputDirectoryFile, int complianceLevel, boolean noClasspath) {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setComplianceLevel(complianceLevel);
		factory.getEnvironment().setNoClasspath(noClasspath);
		final SpoonCompiler compiler = launcher.createCompiler(factory);
		compiler.addInputSource(outputDirectoryFile);
		try {
			compiler.build();
		} catch (Exception e) {
			final AssertionError error = new AssertionError("Can't compile " + outputDirectoryFile.getName());
			error.initCause(e);
			throw error;
		}
	}

	public static void canBeBuilt(String outputDirectory, int complianceLevel, boolean noClasspath) {
		canBeBuilt(new File(outputDirectory), complianceLevel, noClasspath);
	}

}
