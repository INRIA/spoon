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
package spoon.internal.mavenlauncher;

import org.apache.maven.model.Model;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import spoon.MavenLauncher;
import spoon.compiler.Environment;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InheritanceModel {
	List<InheritanceModel> modules = new ArrayList<>();
	Model model;
	InheritanceModel parent;
	File directory;
	MavenLauncher.SOURCE_TYPE sourceType;
	Environment environment;

	public InheritanceModel(Model model, InheritanceModel parent, File directory, MavenLauncher.SOURCE_TYPE sourceType, Environment environment) {
		this.model = model;
		this.parent = parent;
		this.directory = directory;
		this.sourceType = sourceType;
		this.environment = environment;
	}

	public void addModule(InheritanceModel module) {
		modules.add(module);
	}

	public Model getModel() {
		return model;
	}

	/**
	 * Get the parent model
	 * @return the parent model
	 */
	public InheritanceModel getParent() {
		return parent;
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
			sourcePath = Paths.get(directory.getAbsolutePath(), "src", "main", "java").toString();
		}
		File source = new File(sourcePath);
		if (source.exists()) {
			output.add(source);
		}
		File generatedSource = Paths.get(directory.getAbsolutePath(), "target", "generated-sources").toFile();
		if (generatedSource.exists()) {
			output.add(generatedSource);
		}
		for (InheritanceModel module : modules) {
			output.addAll(module.getSourceDirectories());
		}
		return output;
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
			sourcePath = Paths.get(directory.getAbsolutePath(), "src", "test", "java").toString();
		}
		File source = new File(sourcePath);
		if (source.exists()) {
			output.add(source);
		}
		File generatedSource = Paths.get(directory.getAbsolutePath(), "target", "generated-test-sources").toFile();
		if (generatedSource.exists()) {
			output.add(generatedSource);
		}
		for (InheritanceModel module : modules) {
			output.addAll(module.getTestDirectories());
		}
		return output;
	}

	public List<File> getClasspathTmpFiles(String fileName) {
		List<File> output = new ArrayList<>();
		File tmp = new File(directory, fileName);
		if (tmp.exists() && tmp.isFile()) {
			output.add(tmp);
		}
		for (InheritanceModel module : modules) {
			output.addAll(module.getClasspathTmpFiles(fileName));
		}
		return output;
	}

	static Pattern mavenProperty = Pattern.compile("\\$\\{.*\\}");

	/**
	 * Extract the variable from a string
	 */
	String extractVariable(String value) {
		String val = value;
		if (value != null && value.contains("$")) {
			Matcher matcher = mavenProperty.matcher(value);
			while (matcher.find()) {
				String var = matcher.group();
				val = val.replace(var, getProperty(var.substring(2, var.length() - 1)));
			}
		}
		return val;
	}

	/**
	 * Get the value of a property
	 * @param key the key of the property
	 * @return the property value if key exists or null
	 */
	private String getProperty(String key) {
		if ("project.version".equals(key)  || "pom.version".equals(key)) {
			if (model.getVersion() != null) {
				return model.getVersion();
			} else if (model.getParent() != null) {
				return model.getParent().getVersion();
			}
		} else if ("project.groupId".equals(key) || "pom.groupId".equals(key)) {
			if (model.getGroupId() != null) {
				return model.getGroupId();
			} else if (model.getParent() != null) {
				return model.getParent().getGroupId();
			}
		} else if ("project.artifactId".equals(key)  || "pom.artifactId".equals(key)) {
			if (model.getArtifactId() != null) {
				return model.getArtifactId();
			} else if (model.getParent() != null) {
				return model.getParent().getArtifactId();
			}
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
			return Integer.parseInt(extractVariable(javaVersion).substring(2));
		}
		for (Profile profile: model.getProfiles()) {
			if (profile.getActivation() != null && profile.getActivation().isActiveByDefault()) {
				javaVersion = getSourceVersion(profile.getBuild());
			}
		}
		if (javaVersion != null) {
			return Integer.parseInt(extractVariable(javaVersion).substring(2));
		}
		javaVersion = getProperty("java.version");
		if (javaVersion != null) {
			return Integer.parseInt(extractVariable(javaVersion).substring(2));
		}
		javaVersion = getProperty("java.src.version");
		if (javaVersion != null) {
			return Integer.parseInt(extractVariable(javaVersion).substring(2));
		}
		javaVersion = getProperty("maven.compiler.source");
		if (javaVersion != null) {
			return Integer.parseInt(extractVariable(javaVersion).substring(2));
		}
		javaVersion = getProperty("maven.compile.source");
		if (javaVersion != null) {
			return Integer.parseInt(extractVariable(javaVersion).substring(2));
		}
		// return the current compliance level of spoon
		return environment.getComplianceLevel();
	}

	private String getSourceVersion(BuildBase build) {
		for (Plugin plugin : model.getBuild().getPlugins()) {
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
		for (InheritanceModel inheritanceModel : modules) {
			String child = inheritanceModel.toString();
			for (String s : child.split("\n")) {
				sb.append("\t");
				sb.append(s);
				sb.append("\n");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Extract the information from the pom
	 * @param path the path to the pom
	 * @param parent the parent pom
	 * @return the extracted model
	 * @throws IOException when the file does not exist
	 * @throws XmlPullParserException when the file is corrupted
	 */
	public static InheritanceModel readPOM(String path, InheritanceModel parent, MavenLauncher.SOURCE_TYPE sourceType, Environment environment) throws IOException, XmlPullParserException {
		if (!path.endsWith(".xml") && !path.endsWith(".pom")) {
			path = Paths.get(path, "pom.xml").toString();
		}
		File pomFile = new File(path);
		if (!pomFile.exists()) {
			return null;
		}
		MavenXpp3Reader pomReader = new MavenXpp3Reader();
		try (FileReader reader = new FileReader(pomFile)) {
			Model model = pomReader.read(reader);
			InheritanceModel inheritanceModel = new InheritanceModel(model, parent, pomFile.getParentFile(), sourceType, environment);
			for (String module : model.getModules()) {
				inheritanceModel.addModule(readPOM(Paths.get(pomFile.getParent(), module).toString(), inheritanceModel, sourceType, environment));
			}
			return inheritanceModel;
		}
	}
}
