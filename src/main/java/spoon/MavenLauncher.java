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
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Create a Spoon launcher from a maven pom file
 */
public class MavenLauncher extends Launcher {
	static String mavenVersionParsing = "Maven home: ";
	static String spoonClasspathTmpFileName = "spoon.classpath.tmp";
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

	private static void generateClassPathFile(File pom, File mvnHome, SOURCE_TYPE sourceType) {
		//Run mvn dependency:build-classpath -Dmdep.outputFile="spoon.classpath.tmp"
		//This should write the classpath used by maven in spoon.classpath.tmp
		InvocationRequest request = new DefaultInvocationRequest();
		request.setBatchMode(true);
		request.setPomFile(pom);
		request.setGoals(Collections.singletonList("dependency:build-classpath"));
		Properties properties = new Properties();
		if (sourceType == SOURCE_TYPE.APP_SOURCE) {
			properties.setProperty("includeScope", "runtime");
		}
		properties.setProperty("mdep.outputFile", spoonClasspathTmpFileName);
		request.setProperties(properties);

		request.getOutputHandler(s -> LOGGER.debug(s));
		request.getErrorHandler(s -> LOGGER.debug(s));

		Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(mvnHome);
		invoker.setWorkingDirectory(pom.getParentFile());
		invoker.setErrorHandler(s -> LOGGER.debug(s));
		invoker.setOutputHandler(s -> LOGGER.debug(s));
		try {
			InvocationResult ir = invoker.execute(request);
		} catch (MavenInvocationException e) {
			throw new SpoonException("Maven invocation failed to build a classpath.");
		}
	}

	/**
	 *
	 * @param classPathFiles File[] containing the classpath elements separated with ':'
	 *                       It can be an array of file instead of an unique one for multi module projects.
	 */
	static String[] readClassPath(File... classPathFiles) throws IOException {
		List<String> classpathElements = new ArrayList<>();

		//Read the content of spoon.classpath.tmp
		for (File classPathFile: classPathFiles) {
			try (BufferedReader br = new BufferedReader(new FileReader(classPathFile))) {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();
				while (line != null) {
					sb.append(line);
					line = br.readLine();
				}
				if (!"".equals(sb.toString())) {
					String[] classpath = sb.toString().split(File.pathSeparator);
					for (String cpe : classpath) {
						if (!classpathElements.contains(cpe)) {
							classpathElements.add(cpe);
						}
					}
				}
			}
		}

		return classpathElements.toArray(new String[0]);
	}

	static String guessMavenHome() {
		String mvnHome = null;
		try {
			String[] cmd = {"mvn", "-version"};
			Process p = Runtime.getRuntime().exec(cmd);
			try (BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
				String line;

				while ((line = output.readLine()) != null) {
					if (line.contains(mavenVersionParsing)) {
						return line.replace(mavenVersionParsing, "");
					}
				}
			}

			p.waitFor();
		} catch (IOException e) {
			throw new SpoonException("Maven home detection has failed.");
		} catch (InterruptedException e) {
			throw new SpoonException("Maven home detection was interrupted.");
		}
		return mvnHome;
	}

	/**
	 * Call maven invoker to generate the classpath. Either M2_HOME must be
	 * initialized, or the command mvn must be in PATH.
	 *
	 * @param mvnHome the path to the m2repository
	 * @param mavenProject the path to the root of the project
	 * @param sourceType the source type (App, test, or all)
	 */
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
		generateClassPathFile(pom, new File(mvnHome), sourceType);

		List<File> classPathPrints;
		String[] classpath;
		try {
			classPathPrints = Files.find(Paths.get(pom.getParentFile().getAbsolutePath()),
					Integer.MAX_VALUE,
					(filePath, fileAttr) -> filePath.endsWith(spoonClasspathTmpFileName))
					.map(p -> p.toFile())
					.collect(Collectors.toList());
			File[] classPathPrintFiles = new File[classPathPrints.size()];
			classPathPrintFiles = classPathPrints.toArray(classPathPrintFiles);
			classpath = readClassPath(classPathPrintFiles);
		} catch (IOException e) {
			throw new SpoonException("Failed to generate class path for " + pom.getAbsolutePath() + ".");
		}
		return classpath;
	}
}
