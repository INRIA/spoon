/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.support.compiler.SpoonPom;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Create a Spoon launcher from a maven pom file
 */
public class MavenLauncher extends Launcher {
	private static final Logger LOGGER  = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private String mvnHome;
	private SOURCE_TYPE sourceType;
	private SpoonPom model;
	private boolean forceRefresh = false;
	private final SpoonPom.MavenOptions mavenOptions = SpoonPom.MavenOptions.empty();

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
	 * @param profileFilter regular expression of profiles to <b>include</b> in the built model
	 */
	public MavenLauncher(String mavenProject, SOURCE_TYPE sourceType, Pattern profileFilter) {
		this(mavenProject, sourceType, System.getenv().get("M2_HOME"), false, profileFilter);
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
	 * MavenLauncher constructor assuming either an environment
	 * variable M2_HOME, or that mvn command exists in PATH.
	 *
	 * @param mavenProject the path to the root of the project
	 * @param sourceType the source type (App, test, or all)
	 * @param forceRefresh force the regeneration of classpath
	 * @param profileFilter regular expression of profiles to <b>include</b> in the built model
	 */
	public MavenLauncher(String mavenProject, SOURCE_TYPE sourceType, boolean forceRefresh, Pattern profileFilter) {
		this(mavenProject, sourceType, System.getenv().get("M2_HOME"), forceRefresh, profileFilter);
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
	 * @param profileFilter regular expression of profiles to <b>include</b> in the built model
	 */
	public MavenLauncher(String mavenProject, SOURCE_TYPE sourceType, String mvnHome, Pattern profileFilter) {
		this(mavenProject, sourceType, mvnHome, false, profileFilter);
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
		init(mavenProject, null, Pattern.compile("^$"));
	}

	/**
	 * @param mavenProject the path to the root of the project
	 * @param sourceType the source type (App, test, or all)
	 * @param mvnHome Path to maven install
	 * @param forceRefresh force the regeneration of classpath
	 * @param profileFilter regular expression of profiles to <b>include</b> in the built model
	 */
	public MavenLauncher(String mavenProject, SOURCE_TYPE sourceType, String mvnHome, boolean forceRefresh, Pattern profileFilter) {
		this.sourceType = sourceType;
		this.mvnHome = mvnHome;
		this.forceRefresh = forceRefresh;
		init(mavenProject, null, profileFilter);
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
		init(mavenProject, classpath, Pattern.compile("^$"));
	}

	/**
	 * MavenLauncher constructor that skips maven invocation building
	 * classpath.
	 *
	 * @param mavenProject the path to the root of the project
	 * @param sourceType the source type (App, test, or all)
	 * @param classpath String array containing the classpath elements
	 * @param profileFilter regular expression of profiles to <b>include</b> in the built model
	 */
	public MavenLauncher(String mavenProject, SOURCE_TYPE sourceType, String[] classpath, Pattern profileFilter) {
		this.sourceType = sourceType;
		init(mavenProject, classpath, profileFilter);
	}

	/**
	 * Adds an environment variable to the maven invocation.
	 * <p>
	 * <br><strong>Note that you need to call {@link #rebuildClasspath()} after calling this method for changes to take effect.</strong>
	 *
	 * @param key the name of the environment variable
	 * @param value its value
	 * @see #rebuildClasspath() #rebuildClasspath() for changes to take effect
	 */
	public void setEnvironmentVariable(String key, String value) {
		mavenOptions.setEnvironmentVariable(key, value);
	}

	private void init(String mavenProject, String[] classpath, Pattern profileFilter) {
		File mavenProjectFile = new File(mavenProject);
		if (!mavenProjectFile.exists()) {
			throw new SpoonException(mavenProject + " does not exist.");
		}

		try {
			model = new SpoonPom(mavenProject, sourceType, getEnvironment(), profileFilter);
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
			classpath = model.buildClassPath(mvnHome, sourceType, LOGGER, forceRefresh, mavenOptions);
			LOGGER.info("Running in FULLCLASSPATH mode. Source folders and dependencies are inferred from the pom.xml file (doc: http://spoon.gforge.inria.fr/launcher.html).");
		} else {
			LOGGER.info("Running in FULLCLASSPATH mode. Classpath is manually set (doc: http://spoon.gforge.inria.fr/launcher.html).");
		}

		// dependencies
		factory.getEnvironment().setNoClasspath(false);
		this.getModelBuilder().setSourceClasspath(classpath);

		// compliance level
		this.getEnvironment().setComplianceLevel(model.getSourceVersion());
	}

	@Override
	protected void reportClassPathMode() {
		// skip classpath mode logs from Launcher
	}

	/**
	 * Triggers regeneration of the classpath that is used for building the model, based on pom.xml
	 */
	public void rebuildClasspath() {
		String[] classpath = model.buildClassPath(mvnHome, sourceType, LOGGER, true, mavenOptions);
		this.getModelBuilder().setSourceClasspath(classpath);
	}
}
