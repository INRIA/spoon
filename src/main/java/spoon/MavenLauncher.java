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
package spoon;

import spoon.support.compiler.SpoonPom;

import java.io.File;
import java.util.List;

/**
 * Create a Spoon launcher from a maven pom file
 */
public class MavenLauncher extends Launcher {
	private String mvnHome;
	private SOURCE_TYPE sourceType;
	private SpoonPom model;
	private boolean forceRefresh = false;

	/**
	 * @return SpoonPom corresponding to the pom file used by the launcher
	 */
	public SpoonPom getPomFile() {
		return model;
	}

	/**
	 * The type of source to consider in the model
	 */
	public enum SOURCE_TYPE {
		// only the main code of the application
		APP_SOURCE,
		// only the tests of the application
		TEST_SOURCE,
		// all the sources
		ALL_SOURCE
	}

	/**
	 * MavenLauncher constructor assuming either an environment
	 * variable M2_HOME, or that mvn command exists in PATH.
	 *
	 * @param mavenProject the path to the root of the project
	 * @param sourceType the source type (App, test, or all)
	 */
	public MavenLauncher(String mavenProject, SOURCE_TYPE sourceType) {
		this(mavenProject, sourceType, System.getenv().get("M2_HOME"), false);
	}

	/**
	 * MavenLauncher constructor assuming either an environment
	 * variable M2_HOME, or that mvn command exists in PATH.
	 *
	 * @param mavenProject the path to the root of the project
	 * @param sourceType the source type (App, test, or all)
	 * @param forceRefresh force the regeneration of classpath
	 */
	public MavenLauncher(String mavenProject, SOURCE_TYPE sourceType, boolean forceRefresh) {
		this(mavenProject, sourceType, System.getenv().get("M2_HOME"), forceRefresh);
	}

	/**
	 * @param mavenProject the path to the root of the project
	 * @param sourceType the source type (App, test, or all)
	 * @param mvnHome Path to maven install
	 */
	public MavenLauncher(String mavenProject, SOURCE_TYPE sourceType, String mvnHome) {
		this(mavenProject, sourceType, mvnHome, false);
	}

	/**
	 * @param mavenProject the path to the root of the project
	 * @param sourceType the source type (App, test, or all)
	 * @param mvnHome Path to maven install
	 * @param forceRefresh force the regeneration of classpath
	 */
	public MavenLauncher(String mavenProject, SOURCE_TYPE sourceType, String mvnHome, boolean forceRefresh) {
		this.sourceType = sourceType;
		this.mvnHome = mvnHome;
		this.forceRefresh = forceRefresh;
		init(mavenProject, null);
	}

	/**
	 * MavenLauncher constructor that skips maven invocation building
	 * classpath.
	 *
	 * @param mavenProject the path to the root of the project
	 * @param sourceType the source type (App, test, or all)
	 * @param classpath String array containing the classpath elements
	 */
	public MavenLauncher(String mavenProject, SOURCE_TYPE sourceType, String[] classpath) {
		this.sourceType = sourceType;
		init(mavenProject, classpath);
	}

	private void init(String mavenProject, String[] classpath) {
		File mavenProjectFile = new File(mavenProject);
		if (!mavenProjectFile.exists()) {
			throw new SpoonException(mavenProject + " does not exist.");
		}

		try {
			model = new SpoonPom(mavenProject, sourceType, getEnvironment());
		} catch (Exception e) {
			throw new SpoonException("Unable to read the pom", e);
		}

		// app source
		if (SOURCE_TYPE.APP_SOURCE == sourceType || SOURCE_TYPE.ALL_SOURCE == sourceType) {
			List<File> sourceDirectories = model.getSourceDirectories();
			for (File sourceDirectory : sourceDirectories) {
				this.addInputResource(sourceDirectory.getAbsolutePath());
			}
		}

		// test source
		if (SOURCE_TYPE.TEST_SOURCE == sourceType || SOURCE_TYPE.ALL_SOURCE == sourceType) {
			List<File> testSourceDirectories = model.getTestDirectories();
			for (File sourceDirectory : testSourceDirectories) {
				this.addInputResource(sourceDirectory.getAbsolutePath());
			}
		}

		if (classpath == null) {
			classpath = model.buildClassPath(mvnHome, sourceType, LOGGER, false);
		}

		// dependencies
		this.getModelBuilder().setSourceClasspath(classpath);

		// compliance level
		this.getEnvironment().setComplianceLevel(model.getSourceVersion());
	}
}
