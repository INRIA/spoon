/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.compiler.SpoonFolder;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.support.Internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpoonPom implements SpoonResource {
	static String mavenVersionParsing = "Maven home: ";
	static String spoonClasspathTmpFileName = "spoon.classpath.tmp";
	static String spoonClasspathTmpFileNameApp = "spoon.classpath-app.tmp";
	static String spoonClasspathTmpFileNameTest = "spoon.classpath-test.tmp";
	static long classpathTmpFilesTTL = 60 * 60 * 1000L; // 1h in ms
	List<SpoonPom> modules = new ArrayList<>();
	Model model;
	SpoonPom parent;
	File pomFile;
	File directory;
	MavenLauncher.SOURCE_TYPE sourceType;
	Environment environment;

	/**
	 * Extract the information from the pom
	 * @param path the path to the pom
	 * @throws IOException when the file does not exist
	 * @throws XmlPullParserException when the file is corrupted
	 */
	public SpoonPom(String path, MavenLauncher.SOURCE_TYPE sourceType, Environment environment) throws IOException, XmlPullParserException {
		this(path, null, sourceType, environment);
	}

	/**
	 * Extract the information from the pom
	 * @param path the path to the pom
	 * @param profileFilter regex pattern to filter profiles when expanding defined modules. Only modules in matching profiles are expanded
	 * @throws IOException when the file does not exist
	 * @throws XmlPullParserException when the file is corrupted
	 */
	public SpoonPom(String path, MavenLauncher.SOURCE_TYPE sourceType, Environment environment, Pattern profileFilter) throws IOException, XmlPullParserException {
		this(path, null, sourceType, environment, profileFilter);
	}

	/**
	 * Extract the information from the pom
	 * @param path the path to the pom
	 * @param parent the parent pom
	 * @throws IOException when the file does not exist
	 * @throws XmlPullParserException when the file is corrupted
	 */
	public SpoonPom(String path, SpoonPom parent, MavenLauncher.SOURCE_TYPE sourceType, Environment environment) throws IOException, XmlPullParserException {
		this(path, parent, sourceType, environment, Pattern.compile("^$"));
	}

	/**
	 * Extract the information from the pom
	 * @param path the path to the pom
	 * @param parent the parent pom
	 * @param profileFilter regex pattern to filter profiles when expanding defined modules. Only modules in matching profiles are expanded
	 * @throws IOException when the file does not exist
	 * @throws XmlPullParserException when the file is corrupted
	 */
	public SpoonPom(String path, SpoonPom parent, MavenLauncher.SOURCE_TYPE sourceType, Environment environment, Pattern profileFilter) throws IOException, XmlPullParserException {
		this.parent = parent;
		this.sourceType = sourceType;
		this.environment = environment;

		// directory may end in .xml|.pom so don't skip if thats the case
		if ((!path.endsWith(".xml") && !path.endsWith(".pom")) || Paths.get(path).toFile().isDirectory()) {
			path = Paths.get(path, "pom.xml").toString();
		}
		this.pomFile = new File(path).getCanonicalFile();
		if (!pomFile.exists()) {
			throw new IOException("Pom does not exists.");
		}
		this.directory = pomFile.getParentFile();

		MavenXpp3Reader pomReader = new MavenXpp3Reader();
		try (FileReader reader = new FileReader(pomFile)) {
			this.model = pomReader.read(reader);

			Set<String> allModules = new HashSet<>();

			for (Profile profile : model.getProfiles()) {
				if (!profileFilter.matcher(profile.getId()).matches()) {
					continue;
				}
				for (String module : profile.getModules()) {
					allModules.add(module);
					addModule(new SpoonPom(Paths.get(pomFile.getParent(), module).toString(), this, sourceType, environment));
				}
			}

			// recursively build the POM hierarchy for modules not built from profiles
			for (String module : model.getModules()) {
				if (allModules.contains(module)) {
					continue;
				}
				addModule(new SpoonPom(Paths.get(pomFile.getParent(), module).toString(), this, sourceType, environment));
			}
		} catch (FileNotFoundException e) {
			throw new IOException("Pom does not exists.");
		}
	}

	private void addModule(SpoonPom module) {
		modules.add(module);
	}

	/**
	 * Get the list of modules defined in this POM
	 * @return the list of modules
	 */
	public List<SpoonPom> getModules() {
		return Collections.unmodifiableList(modules);
	}

	/**
	 * Get the Project Object Model
	 * @return the Project Object Model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * Get the list of source directories of the project
	 * @return the list of source directories
	 */
	public List<File> getSourceDirectories() {
		List<File> output = new ArrayList<>();
		String sourcePath = null;

		Build build = model.getBuild();
		if (build != null) {
			sourcePath = build.getSourceDirectory();
		}
		if (sourcePath == null) {
			sourcePath = getSourceDirectoryFromParent(getParentPom());
			if (sourcePath == null) {
				sourcePath = Paths.get("src/main/java").toString();
			}
		}
		sourcePath = extractVariable(sourcePath);
		Path path = Paths.get(sourcePath);

		String absoluteSourcePath;
		if (path.isAbsolute()) {
			absoluteSourcePath = path.toString();
		} else {
			absoluteSourcePath = Paths.get(directory.getAbsolutePath(), sourcePath).toString();
		}
		File source = new File(absoluteSourcePath);
		if (source.exists()) {
			output.add(source);
		}
		File generatedSource = Paths.get(directory.getAbsolutePath(), "target", "generated-sources").toFile();
		if (generatedSource.exists()) {
			output.add(generatedSource);
		}
		for (SpoonPom module : modules) {
			output.addAll(module.getSourceDirectories());
		}
		return output;
	}

	/**
	 * Climbs the pom.xml hierarchy until a model is found in which
	 * a source directory is declared.
	 * @return the uninterpolated source directory declared in the nearest ancestor
	 */
	private String getSourceDirectoryFromParent(SpoonPom parent) {
		if (parent == null) {
			return null;
		}
		String sourcePath = null;
		Build build = parent.model.getBuild();
		if (build != null) {
			sourcePath = build.getSourceDirectory();
			if (sourcePath == null && parent.getParentPom() != null) {
				return getSourceDirectoryFromParent(parent.getParentPom());
			}
		} else if (parent.getParentPom() != null) {
			return getSourceDirectoryFromParent(parent.getParentPom());
		}
		return sourcePath;
	}

	/**
	 * Get the list of test directories of the project
	 * @return the list of test directories
	 */
	public List<File> getTestDirectories() {
		List<File> output = new ArrayList<>();
		String sourcePath = null;

		Build build = model.getBuild();
		if (build != null) {
			sourcePath = build.getTestSourceDirectory();
		}
		if (sourcePath == null) {
			sourcePath = getTestSourceDirectoryFromParent(getParentPom());
			if (sourcePath == null) {
				sourcePath = Paths.get("src/test/java").toString();
			}
		}
		sourcePath = extractVariable(sourcePath);
		Path path = Paths.get(sourcePath);

		String absoluteSourcePath;
		if (path.isAbsolute()) {
			absoluteSourcePath = path.toString();
		} else {
			absoluteSourcePath = Paths.get(directory.getAbsolutePath(), sourcePath).toString();
		}
		File source = new File(absoluteSourcePath);
		if (source.exists()) {
			output.add(source);
		}
		File generatedSource = Paths.get(directory.getAbsolutePath(), "target", "generated-test-sources").toFile();
		if (generatedSource.exists()) {
			output.add(generatedSource);
		}
		for (SpoonPom module : modules) {
			output.addAll(module.getTestDirectories());
		}
		return output;
	}

	/**
	 * Climbs the pom.xml hierarchy until a model is found in which
	 * a test source directory is declared.
	 * @return the uninterpolated test source directory declared in the nearest ancestor
	 */
	private String getTestSourceDirectoryFromParent(SpoonPom parent) {
		if (parent == null) {
			return null;
		}
		String sourcePath = null;
		Build build = parent.model.getBuild();
		if (build != null) {
			sourcePath = build.getTestSourceDirectory();
			if (sourcePath == null && parent.getParentPom() != null) {
				return getTestSourceDirectoryFromParent(parent.getParentPom());
			}
		} else if (parent.getParentPom() != null) {
			return getTestSourceDirectoryFromParent(parent.getParentPom());
		}
		return sourcePath;
	}

	/**
	 * Get the list of classpath files generated by maven
	 * @return the list of classpath files
	 */
	public List<File> getClasspathTmpFiles(String fileName) {
		List<File> output = new ArrayList<>();
		File tmp = new File(directory, fileName);
		if (tmp.exists() && tmp.isFile()) {
			output.add(tmp);
		}
		for (SpoonPom module : modules) {
			output.addAll(module.getClasspathTmpFiles(fileName));
		}
		return output;
	}

	// Pattern corresponding to maven properties ${propertyName}
	private static final Pattern MAVEN_PROPERTY = Pattern.compile("\\$\\{.*?}");

	/**
	 * Extract the variable from a string
	 */
	private String extractVariable(String value) {
		String val = value;
		if (value != null && value.contains("$")) {
			Matcher matcher = MAVEN_PROPERTY.matcher(value);
			while (matcher.find()) {
				String var = matcher.group();
				val = val.replace(var, getProperty(var.substring(2, var.length() - 1)));
			}
		}
		return val;
	}

	/**
	 * Get the value of a property. Reference: https://maven.apache.org/ref/3.6.3/maven-model-builder/#Model_Interpolation
	 * @param key the key of the property
	 * @return the property value if key exists or null
	 */
	private @Nullable String getProperty(String key) {
		if ("project.version".equals(key) || "pom.version".equals(key) || "version".equals(key)) {
			if (model.getVersion() != null) {
				return model.getVersion();
			} else if (model.getParent() != null) {
				return model.getParent().getVersion();
			}
		} else if ("project.groupId".equals(key) || "pom.groupId".equals(key) || "groupId".equals(key)) {
			if (model.getGroupId() != null) {
				return model.getGroupId();
			} else if (model.getParent() != null) {
				return model.getParent().getGroupId();
			}
		} else if ("project.artifactId".equals(key) || "pom.artifactId".equals(key) || "artifactId".equals(key)) {
			if (model.getArtifactId() != null) {
				return model.getArtifactId();
			} else if (model.getParent() != null) {
				return model.getParent().getArtifactId();
			}
		} else if ("project.basedir".equals(key) || "pom.basedir".equals(key) || "basedir".equals(key)) {
			return pomFile.getParent();
		} else if ("file.separator".equals(key)) {
			return File.separator;
		}
		String value = extractVariable(model.getProperties().getProperty(key));
		if (value == null) {
			if (parent == null) {
				return null;
			}
			return parent.getProperty(key);
		}
		return value;
	}

	/**
	 * Get the source version of the project
	 * @return the source version of the project
	 */
	public int getSourceVersion() {
		String javaVersion = null;
		if (model.getBuild() != null) {
			javaVersion = getSourceVersion(model.getBuild());
		}
		if (javaVersion != null) {
			return correctJavaVersion(javaVersion);
		}
		for (Profile profile: model.getProfiles()) {
			if (profile.getActivation() != null && profile.getActivation().isActiveByDefault() && profile.getBuild() != null) {
				javaVersion = getSourceVersion(profile.getBuild());
			}
		}
		if (javaVersion != null) {
			return correctJavaVersion(javaVersion);
		}
		javaVersion = getProperty("java.version");
		if (javaVersion != null) {
			return correctJavaVersion(javaVersion);
		}
		javaVersion = getProperty("java.src.version");
		if (javaVersion != null) {
			return correctJavaVersion(javaVersion);
		}
		javaVersion = getProperty("maven.compiler.source");
		if (javaVersion != null) {
			return correctJavaVersion(javaVersion);
		}
		javaVersion = getProperty("maven.compile.source");
		if (javaVersion != null) {
			return correctJavaVersion(javaVersion);
		}
		// return the current compliance level of spoon
		return environment.getComplianceLevel();
	}

	private int correctJavaVersion(String javaVersion) {
		String version = extractVariable(javaVersion);
		return Integer.parseInt((version.contains(".") ? version.substring(2) : version));
	}

	private String getSourceVersion(BuildBase build) {
		for (Plugin plugin : build.getPlugins()) {
			if (!"maven-compiler-plugin".equals(plugin.getArtifactId())) {
				continue;
			}
			Xpp3Dom configuration = (Xpp3Dom) plugin.getConfiguration();
			if (configuration != null) {
				Xpp3Dom source = configuration.getChild("source");
				if (source != null) {
					return source.getValue();
				}
			}
			break;
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(model.getGroupId());
		sb.append(":");
		sb.append(model.getArtifactId());
		sb.append(":");
		sb.append(model.getVersion());
		if (modules.isEmpty()) {
			return sb.toString();
		}
		sb.append(" {\n");
		for (SpoonPom spoonPom : modules) {
			String child = spoonPom.toString();
			for (String s : child.split("\n")) {
				sb.append("\t");
				sb.append(s);
				sb.append("\n");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	private void generateClassPathFile(
		File mvnHome,
		MavenLauncher.SOURCE_TYPE sourceType,
		Logger LOGGER,
		boolean forceRefresh,
		Map<String, String> environmentVariables
	) {
		// Check if classpath file already exist and is recent enough (1h)
		File classpathFile = new File(directory, getSpoonClasspathTmpFileName(sourceType));
		Date date = new Date();
		long time = date.getTime();
		if (forceRefresh || !classpathFile.exists() || ((time - classpathFile.lastModified()) > classpathTmpFilesTTL)) {
			//Run mvn dependency:build-classpath -Dmdep.outputFile="spoon.classpath.tmp"
			//This should write the classpath used by maven in spoon.classpath.tmp
			InvocationRequest request = new DefaultInvocationRequest();
			request.setBatchMode(true);
			request.setPomFile(pomFile);
			request.addArg("dependency:build-classpath");
			Properties properties = new Properties();
			if (sourceType == MavenLauncher.SOURCE_TYPE.APP_SOURCE) {
				properties.setProperty("includeScope", "runtime");
			}
			properties.setProperty("mdep.outputFile", getSpoonClasspathTmpFileName(sourceType));
			request.setProperties(properties);
			request.setReactorFailureBehavior(InvocationRequest.ReactorFailureBehavior.FailNever);

			for (Map.Entry<String, String> entry : environmentVariables.entrySet()) {
				request.addShellEnvironment(entry.getKey(), entry.getValue());
			}

			request.setBaseDirectory(directory);
			if (LOGGER != null) {
				request.setOutputHandler(LOGGER::debug);
				request.setErrorHandler(LOGGER::debug);
			}

			Invoker invoker = new DefaultInvoker();
			invoker.setMavenHome(mvnHome);
			try {
				invoker.execute(request);
			} catch (MavenInvocationException e) {
				throw new SpoonException("Maven invocation failed to build a classpath.");
			}
			classpathFile.setLastModified(time);
		}
	}

	/**
	 *
	 * @param classPathFiles File[] containing the classpath elements separated with ':'
	 *                       It can be an array of file instead of an unique one for multi module projects.
	 */
	private static String[] readClassPath(File... classPathFiles) throws IOException {
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
				if (!sb.toString().isEmpty()) {
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

	/**
	 * Try to guess Maven home when none is provided.
	 * @return the path toward maven install on the local machine.
	 * @throws SpoonException if path to maven executable is wrong, process is interrupted, or maven home could not be
	 * found.
	 */
	public static String guessMavenHome() {
		try {
			String[] cmd = new String[]{getPathToMavenExecutable(), "-version"};
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
			Thread.currentThread().interrupt();
			throw new SpoonException("Maven home detection was interrupted.");
		}
		throw new SpoonException("Couldn't find path to maven home.");
	}

	private static String getPathToMavenExecutable() {
		String executableName;
		if (System.getProperty("os.name").contains("Windows")) {
			executableName = "mvn.cmd";
		} else {
			executableName = "mvn";
		}
		for (String dirname : System.getenv("PATH").split(File.pathSeparator)) {
			File file = new File(dirname, executableName);
			if (file.isFile() && file.canExecute()) {
				return file.getAbsolutePath();
			}
		}
		throw new SpoonException("Maven executable does not exist on PATH.");
	}

	/**
	 * Call maven invoker to generate the classpath. Either M2_HOME must be
	 * initialized, or the command mvn must be in PATH.
	 *
	 * @param mvnHome the path to the m2repository
	 * @param sourceType the source type (App, test, or all)
	 * @param LOGGER Logger used for maven output
	 * @param forceRefresh if true forces the invocation of maven to regenerate classpath
	 * @return the complete classpath of the requested source types
	 */
	public String[] buildClassPath(String mvnHome, MavenLauncher.SOURCE_TYPE sourceType, Logger LOGGER, boolean forceRefresh) {
		return this.buildClassPath(mvnHome, sourceType, LOGGER, forceRefresh, MavenOptions.empty());
	}

	/**
	 * Call maven invoker to generate the classpath. Either M2_HOME must be
	 * initialized, or the command mvn must be in PATH.
	 *
	 * @param mvnHome the path to the m2repository
	 * @param sourceType the source type (App, test, or all)
	 * @param LOGGER Logger used for maven output
	 * @param forceRefresh if true forces the invocation of maven to regenerate classpath
	 * @param mavenOptions additional options to pass to maven
	 */
	public String[] buildClassPath(
		String mvnHome,
		MavenLauncher.SOURCE_TYPE sourceType,
		Logger LOGGER,
		boolean forceRefresh,
		MavenOptions mavenOptions
	) {
		if (mvnHome == null) {
			mvnHome = guessMavenHome();
		}
		generateClassPathFile(new File(mvnHome), sourceType, LOGGER, forceRefresh, mavenOptions.getEnvironmentVariables());

		List<File> classPathPrints;
		String[] classpath;
		try {
			classPathPrints = getClasspathTmpFiles(getSpoonClasspathTmpFileName(sourceType));
			File[] classPathPrintFiles = new File[classPathPrints.size()];
			classPathPrintFiles = classPathPrints.toArray(classPathPrintFiles);
			classpath = readClassPath(classPathPrintFiles);
		} catch (IOException e) {
			throw new SpoonException("Failed to generate class path for " + pomFile.getAbsolutePath() + ".");
		}
		return classpath;
	}

	private static String getSpoonClasspathTmpFileName(MavenLauncher.SOURCE_TYPE sourceType) {
		// As the temporary file containing the classpath is re-generated only
		// once per hour, we need a different file for different dependency
		// resolution scopes.
		if (MavenLauncher.SOURCE_TYPE.TEST_SOURCE == sourceType) {
			return spoonClasspathTmpFileNameTest;
		} else if (MavenLauncher.SOURCE_TYPE.APP_SOURCE == sourceType) {
			return spoonClasspathTmpFileNameApp;
		} else {
			return spoonClasspathTmpFileName;
		}
	}

	/**
	 * Get the parent model
	 * @return the parent model
	 */
	public SpoonPom getParentPom() {
		return parent;
	}

	/**
	 * Get the parent directory
	 * @return the parent directory
	 */
	@Override
	public SpoonFolder getParent() {
		try {
			return SpoonResourceHelper.createFolder(directory);
		} catch (FileNotFoundException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public File getFileSystemParent() {
		return directory;
	}

	@Override
	public String getName() {
		return model.getName();
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public boolean isArchive() {
		return false;
	}

	@Override
	public String getPath() {
		return pomFile.getPath();
	}

	@Override
	public File toFile() {
		return pomFile;
	}

	/**
	 * Additional options for maven invocations.
	 */
	@Internal
	public static class MavenOptions {
		private final Map<String, String> environmentVariables;

		private MavenOptions() {
			this.environmentVariables = new HashMap<>();
		}

		/**
		 * Adds a maven environment variable.
		 * @param key the name of the variable
		 * @param value its value
		 */
		public void setEnvironmentVariable(String key, String value) {
			this.environmentVariables.put(key, value);
		}

		/**
		 * @return all set environment variables
		 */
		public Map<String, String> getEnvironmentVariables() {
			return environmentVariables;
		}

		/**
		 * @return a new empty options instance
		 */
		public static MavenOptions empty() {
			return new MavenOptions();
		}
	}
}
