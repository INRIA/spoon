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

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenInvocationException;
import spoon.internal.mavenlauncher.InheritanceModel;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
		this(mavenProject, m2RepositoryPath, sourceType, System.getenv().get("M2_HOME"));
	}

	/**
	 *
	 * @param mavenProject the path to the root of the project
	 * @param m2RepositoryPath the path to the m2repository
	 * @param sourceType the source type (App, test, or all)
	 * @param mvnHome Path to maven install
	 */
	public MavenLauncher(String mavenProject, String m2RepositoryPath, SOURCE_TYPE sourceType, String mvnHome) {
		this(mavenProject, m2RepositoryPath, sourceType, buildClassPath(mvnHome, mavenProject, sourceType));
	}

	/**
	 *
	 * @param mavenProject the path to the root of the project
	 * @param m2RepositoryPath the path to the m2repository
	 * @param sourceType the source type (App, test, or all)
	 * @param classpath String array containing the classpath elements
	 */
	public MavenLauncher(String mavenProject, String m2RepositoryPath, SOURCE_TYPE sourceType, String[] classpath) {
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

		this.getModelBuilder().setSourceClasspath(classpath);

		// compliance level
		this.getEnvironment().setComplianceLevel(model.getSourceVersion());
	}

	private static void generateClassPathFile(File pom, File mvnHome, SOURCE_TYPE sourceType, File outputFile) {
		//File classPathPrint = new File(pom.getParentFile(), "spoon.classpath.tmp");

		//Run mvn dependency:build-classpath -Dmdep.outputFile="spoon.classpath.tmp"
		//This should write the classpath used by maven in spoon.classpath.tmp
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(pom);
		request.setGoals(Arrays.asList("dependency:build-classpath"));
		Properties properties = new Properties();
		if (sourceType == SOURCE_TYPE.APP_SOURCE) {
			properties.setProperty("mdep.includeScope", "runtime");
		}
		properties.setProperty("mdep.outputFile", outputFile.getAbsolutePath());
		request.setProperties(properties);

		//FIXME Should the standard output made silent and error verbose?
		//request.getOutputHandler(s -> System.err.println(s));
		//request.getErrorHandler(s -> System.err.println(s));

		Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(mvnHome);
		invoker.setWorkingDirectory(pom.getParentFile());
		try {
			InvocationResult ir = invoker.execute(request);
		} catch (MavenInvocationException e) {
			e.printStackTrace();
			throw new SpoonException("Maven invocation failed to build a classpath.");
		}
	}

	public static String[] readClassPath(File classPathFile) {
		String[] classpath = null;

		//Read the content of spoon.classpath.tmp
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(classPathFile));
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
			throw new SpoonException("Failed to read classpath written in temporary file " + classPathFile.getAbsolutePath() + ".");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace(); //These Exceptions occur after the classpath has been read, so it should not prevent normal execution.
				}
			}
		}

		return classpath;
	}

	static String guessMavenHome() {
		String mvnHome = null;
		try {
			String[] cmd = {"mvn", "-version"};
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;

			while ((line = output.readLine()) != null) {
				if (line.contains("Maven home: ")) {
					mvnHome = line.replace("Maven home: ", "");
					return mvnHome;
				}
			}

			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return mvnHome;
	}

	public static String[] buildClassPath(String mvnHome, String mavenProject, SOURCE_TYPE sourceType) {
		if (mvnHome == null) {
			mvnHome = guessMavenHome();
			if (mvnHome == null) {
				throw new SpoonException("M2_HOME must be initialized to use this MavenLauncher constructor.");
			}
		}
		String projectPath = mavenProject;
		if (!projectPath.endsWith(".xml") && !projectPath.endsWith(".pom")) {
			projectPath = Paths.get(projectPath, "pom.xml").toString();
		}
		File pom = new File(projectPath);
		File classPathPrint = new File(pom.getParentFile(), "spoon.classpath.tmp");
		generateClassPathFile(pom, new File(mvnHome), sourceType, classPathPrint);
		String[] classpath = readClassPath(classPathPrint);
		//classPathPrint.delete();
		return classpath;
	}
}
