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

import org.apache.maven.cli.MavenCli;
import org.codehaus.plexus.classworlds.ClassWorld;
import spoon.internal.mavenlauncher.InheritanceModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Create a Spoon launcher from a maven pom file
 */
public class MavenLauncher extends Launcher {
	private String m2RepositoryPath;
	private SOURCE_TYPE sourceType;

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

	public MavenLauncher(String mavenProject, SOURCE_TYPE sourceType) {
		this(mavenProject, Paths.get(System.getProperty("user.home"), ".m2", "repository").toString(), sourceType);
	}

	/**
	 *
	 * @param mavenProject the path to the root of the project
	 * @param m2RepositoryPath the path to the m2repository
	 * @param sourceType the source type (App, test, or all)
	 */
	public MavenLauncher(String mavenProject, String m2RepositoryPath, SOURCE_TYPE sourceType) {
		super();
		this.m2RepositoryPath = m2RepositoryPath;
		this.sourceType = sourceType;

		File mavenProjectFile = new File(mavenProject);
		if (!mavenProjectFile.exists()) {
			throw new SpoonException(mavenProject + " does not exist.");
		}

		InheritanceModel model;
		try {
			model = InheritanceModel.readPOM(mavenProject, null, m2RepositoryPath, sourceType, getEnvironment());
		} catch (Exception e) {
			throw new SpoonException("Unable to read the pom", e);
		}
		if (model == null) {
			throw new SpoonException("Unable to create the model, pom not found?");
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

		// dependencies
		String[] classpath = buildClassPath(mavenProject);
		this.getModelBuilder().setSourceClasspath(classpath);

		// compliance level
		this.getEnvironment().setComplianceLevel(model.getSourceVersion());
	}

	private String[] buildClassPath(String path) {
		if (!path.endsWith(".xml") && !path.endsWith(".pom")) {
			path = Paths.get(path, "pom.xml").toString();
		}
		File pom = new File(path);
		File classPathPrint = new File(pom.getParentFile(), "spoon.classpath.tmp");
		System.setProperty("maven.multiModuleProjectDirectory", pom.getParentFile().getAbsolutePath());

		ClassWorld classWorld = new ClassWorld("MavenBuildCLassPathRealm", ClassLoader.getSystemClassLoader());
		MavenCli cli = new MavenCli(classWorld);
		int result = cli.doMain(
				new String[]{"dependency:build-classpath", "-Dmdep.outputFile="+classPathPrint.getAbsolutePath()},
				pom.getParentFile().getAbsolutePath(),
				System.err,
				System.out
		);

		if (result != 0) {
			throw new SpoonException("Maven invocation failed to build a classpath.");
		}

		String[] classpath = null;

		//Read the content of spoon.classpath.tmp
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(classPathPrint));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			if (sb.toString().equals("")) {
				classpath = new String[0];
			} else {
				classpath = sb.toString().split(":");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SpoonException("Failed to read classpath written in temporary file " + classPathPrint.getAbsolutePath() + ".");
		} finally {
			if (br != null) {
				try {
					br.close();
					classPathPrint.delete(); // delete
				} catch (IOException e) {
					e.printStackTrace(); //These Exceptions occur after the classpath has been read, so it should not prevent normal execution.
				}
			}
		}

		return classpath;
	}
}
