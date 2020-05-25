/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.testing.utils;

import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.function.Consumer;

public final class ModelUtils {
	private ModelUtils() {
		throw new AssertionError();
	}

	public static Factory createFactory() {
		return new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
	}

	/** Utility method for testing: creates the model of `packageName` from src/test/java and returns the CtType corresponding to `className` */
	public static <T extends CtType<?>> T build(String packageName, String className) throws Exception {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setCommentEnabled(false); // we don't want to parse the comments for equals
		SpoonModelBuilder comp = launcher.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/" + packageName.replace('.', '/') + "/" + className + ".java"));
		comp.build();
		return comp.getFactory().Package().get(packageName).getType(className);
	}

	/** Utility method for testing: creates the model of `packageName` and the factory from src/test/java and returns the CtType corresponding to `className` */
	public static <T extends CtType<?>> T build(String packageName, String className, final Factory f) throws Exception {
		Launcher launcher = new Launcher() {
			@Override
			public Factory createFactory() {
				return f;
			}
		};
		SpoonModelBuilder comp = launcher.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/" + packageName.replace('.', '/') + "/" + className + ".java"));
		comp.build();
		return comp.getFactory().Package().get(packageName).getType(className);
	}

	/** Utility method for testing: creates the model of the given `classesToBuild` from src/test/java and returns the factory */
	public static Factory build(Class<?>... classesToBuild) throws Exception {
		return build(launcher -> {
			launcher.getEnvironment().setNoClasspath(false);
			launcher.getEnvironment().setCommentEnabled(false);
		}, classesToBuild);
	}

	/** Utility method for testing: creates the noclasspath model of the given `classesToBuild` from src/test/java and returns the factory */
	public static Factory buildNoClasspath(Class<?>... classesToBuild) throws Exception {
		return build(launcher -> launcher.getEnvironment().setNoClasspath(true), classesToBuild);
	}

	/**
	 * Utility method for testing: creates the model of the given `classesToBuild` from src/test/java and returns the factory
	 * and allows to configure the Launcher first using `config`
	 */
	public static Factory build(Consumer<Launcher> config, Class<?>... classesToBuild) throws Exception {
		final Launcher launcher = new Launcher();
		config.accept(launcher);
		SpoonModelBuilder comp = launcher.createCompiler();
		for (Class<?> classToBuild : classesToBuild) {
			comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/" + classToBuild.getName().replace('.', '/') + ".java"));
		}
		comp.build();
		return comp.getFactory();
	}

	/** Builds the Spoon mode of the `filesToBuild` given as parameter */
	public static Factory build(File... filesToBuild) {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		SpoonModelBuilder comp = launcher.createCompiler();
		for (File fileToBuild : filesToBuild) {
			try {
				comp.addInputSource(SpoonResourceHelper.createResource(fileToBuild));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("File not found", e);
			}
		}
		comp.build();
		return comp.getFactory();
	}

	public static <T> CtType<T> buildClass(Class<T> classToBuild) throws Exception {
		return buildClass(classToBuild, true);
	}

	/** Builds and returns the Spoon model of `` classToBuild */
	public static <T> CtType<T> buildClass(Class<T> classToBuild, boolean ensureFullclasspath) throws Exception {
		if (ensureFullclasspath) {
			return build(classToBuild).Type().get(classToBuild);
		} else {
			return buildNoClasspath(classToBuild).Type().get(classToBuild);
		}
	}

	public static <T> CtType<T> buildClass(Consumer<Launcher> config, Class<T> classToBuild) throws Exception {
		return build(config, classToBuild).Type().get(classToBuild);
	}

	/** checks that the file `outputDirectoryFile` can be parsed with Spoon , given a compliance level. */
	public static void canBeBuilt(File outputDirectoryFile, int complianceLevel) {
		canBeBuilt(outputDirectoryFile, complianceLevel, false);
	}

	/** checks that the file at path `outputDirectory` can be parsed with Spoon , given a compliance level. */
	public static void canBeBuilt(String outputDirectory, int complianceLevel) {
		canBeBuilt(outputDirectory, complianceLevel, false);
	}

	/** checks that the file `outputDirectoryFile` can be parsed with Spoon , given a compliance level and the noclasspath option. */
	public static void canBeBuilt(File outputDirectoryFile, int complianceLevel, boolean noClasspath) {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setComplianceLevel(complianceLevel);
		factory.getEnvironment().setNoClasspath(noClasspath);
		final SpoonModelBuilder compiler = launcher.createCompiler(factory);
		compiler.addInputSource(outputDirectoryFile);
		try {
			compiler.build();
		} catch (Exception e) {
			final AssertionError error = new AssertionError("Can't compile " + outputDirectoryFile.getName() + " because " + e.getMessage());
			error.initCause(e);
			throw error;
		}
	}

	/** checks that the file at path `outputDirectory` can be parsed with Spoon , given a compliance level and noclasspath option. */
	public static void canBeBuilt(String outputDirectory, int complianceLevel, boolean noClasspath) {
		canBeBuilt(new File(outputDirectory), complianceLevel, noClasspath);
	}

	/**
	 * Converts `obj` to String and all EOLs and TABs are removed and sequences of white spaces are replaced by single space
	 * @param obj to be converted object
	 * @return single line string optimized for comparation
	 */
	public static String getOptimizedString(Object obj) {
		if (obj == null) {
			return "null";
		}
		return obj.toString().replaceAll("[\\r\\n\\t]+", "").replaceAll("\\s{2,}", " ");
	}

}
