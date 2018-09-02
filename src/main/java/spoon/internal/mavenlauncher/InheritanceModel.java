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

import org.apache.log4j.Level;
import org.apache.maven.model.Model;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Build;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.compiler.Environment;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class InheritanceModel {
	List<InheritanceModel> modules = new ArrayList<>();
	Model model;
	InheritanceModel parent;
	File directory;
	Map<String, String> dependencyManagements = new HashMap<>();
	String m2RepositoryPath;
	MavenLauncher.SOURCE_TYPE sourceType;
	Environment environment;

	public InheritanceModel(Model model, InheritanceModel parent, File directory, String m2RepositoryPath, MavenLauncher.SOURCE_TYPE sourceType, Environment environment) {
		this.model = model;
		this.parent = parent;
		this.directory = directory;
		this.m2RepositoryPath = m2RepositoryPath;
		this.sourceType = sourceType;
		this.environment = environment;
		init(model, parent, directory);
	}

	private void init(Model model, InheritanceModel parent, File directory) {
		// if possible, build the parent model from the relative path
		if (parent == null && model.getParent() != null) {
			try {
				File parentPath = new File(directory, model.getParent().getRelativePath());
				this.parent = readPOM(parentPath.getPath(), null, m2RepositoryPath, sourceType, environment);
				if (this.parent == null) {
					this.parent = this.readPom(model.getParent().getGroupId(), model.getParent().getArtifactId(), model.getParent().getVersion());
					if (this.model.getGroupId() == null && this.parent != null) {
						this.model.setGroupId(this.parent.model.getGroupId());
					}
					if (this.model.getVersion() == null && this.parent != null) {
						this.model.setVersion(this.parent.model.getVersion());
					}
				}
			} catch (Exception e) {
				Launcher.LOGGER.error("Parent model cannot be resolved: " + e.getMessage(), e);
			}
		}
		DependencyManagement dependencyManagement = model.getDependencyManagement();
		if (dependencyManagement != null) {
			List<Dependency> dependencies = dependencyManagement.getDependencies();
			for (Dependency dependency : dependencies) {
				if ("import".equals(dependency.getScope())) {
					InheritanceModel pom = readPom(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
					if (pom != null) {
						for (String depKey : pom.dependencyManagements.keySet()) {
							if (!dependencyManagements.containsKey(depKey)) {
								dependencyManagements.put(depKey, pom.dependencyManagements.get(depKey));
							}
						}
					}
				} else {
					String depKey = dependency.getGroupId() + ":" + dependency.getArtifactId();
					if (!dependencyManagements.containsKey(depKey)) {
						dependencyManagements.put(depKey, extractVersion(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion()));
					}
				}
			}
		}
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

	/**
	 * Extract the variable from a string
	 */
	String extractVariable(String value) {
		if (value != null && value.startsWith("$")) {
			value = getProperty(value.substring(2, value.length() - 1));
		}
		return value;
	}

	private List<Version> getVersionsFromM2(String groupId, String artifactId) {
		String groupIdPath = groupId.replace(".", "/");
		File[] fileVersion = Paths.get(m2RepositoryPath, groupIdPath, artifactId).toFile().listFiles(file -> {
			if (!file.isDirectory()) {
				return false;
			}
			String version = file.getName();
			return Paths.get(m2RepositoryPath, groupIdPath, artifactId, version, artifactId + "-" + version + ".jar").toFile().exists();
		});
		if (fileVersion == null) {
			return Collections.emptyList();
		}
		List<Version> versions = Arrays.stream(fileVersion).map(f -> new Version(f.getName())).collect(Collectors.toList());
		versions.sort(Comparator.reverseOrder());
		return versions;
	}

	private String extractVersion(String groupId, String artifactId, String version) {
		if (version == null) {
			String depKey = groupId + ":" + artifactId;
			if (dependencyManagements.containsKey(depKey)) {
				return dependencyManagements.get(depKey);
			} else if (this.parent != null) {
				return this.parent.extractVersion(groupId, artifactId, version);
			}
		}
		version = extractVariable(version);
		if (version != null && version.startsWith("[")) {
			List<Version> versionsFromM2 = getVersionsFromM2(groupId, artifactId);
			RangeVersion rangeVersion = new RangeVersion(version);
			for (Version v : versionsFromM2) {
				if (rangeVersion.isIncluded(v)) {
					version = v.version;
					break;
				}
			}
		}
		return version;
	}

	private InheritanceModel readPom(String groupId, String artifactId, String version) {
		String folderVersion = extractVersion(groupId, artifactId, version);
		groupId = groupId.replace(".", "/");
		String fileName = artifactId + "-" + version;
		Path depPath = Paths.get(m2RepositoryPath, groupId, artifactId, folderVersion, fileName + ".pom");
		try {
			InheritanceModel model = readPOM(depPath.toString(), null, m2RepositoryPath, sourceType, environment);
			if (model == null) {
				int buildIndex = version.indexOf('-');
				if (buildIndex != -1) {
					String build = version.substring(buildIndex + 1);
					folderVersion = version.replace(build, "SNAPSHOT");
				}
				depPath = Paths.get(m2RepositoryPath, groupId, artifactId, folderVersion, fileName + ".pom");
				model = readPOM(depPath.toString(), null, m2RepositoryPath, sourceType, environment);
			}
			return model;
		} catch (Exception e) {
			Launcher.LOGGER.log(Level.ERROR, "Unable to read the POM:" + depPath.toString(), e);
			return null;
		}
	}

	private TreeDependency getTreeDependency(Dependency dependency, boolean isLib, Set<TreeDependency> hierarchy) {
		String groupId = extractVariable(dependency.getGroupId());
		String artifactId = extractVariable(dependency.getArtifactId());
		String version = extractVersion(groupId, artifactId, dependency.getVersion());
		if (version == null) {
			Launcher.LOGGER.warn("A dependency version cannot be resolved: " + groupId + ":" + artifactId + ":" + version);
			return null;
		}
		// pass only the optional dependency if it's in a library dependency
		if (isLib && dependency.isOptional()) {
			return null;
		}

		// ignore test dependencies for app source code
		if ("test".equals(dependency.getScope()) && (MavenLauncher.SOURCE_TYPE.APP_SOURCE == sourceType || isLib)) {
			return null;
		}
		// ignore not transitive dependencies
		if (isLib && ("test".equals(dependency.getScope()) || "provided".equals(dependency.getScope()))) {
			Launcher.LOGGER.log(Level.WARN, "Dependency ignored (scope: provided or test):" + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + version);
			return null;
		}
		TreeDependency dependence = new TreeDependency(groupId, artifactId, version, dependency.getType(), m2RepositoryPath);
		try {
			InheritanceModel dependencyModel = readPom(groupId, artifactId, version);
			if (dependencyModel != null) {
				dependence = dependencyModel.getTreeDependency(true, hierarchy);
				dependence.groupId = groupId;
				dependence.artifactId = artifactId;
				dependence.version = version;

				if (dependency.getExclusions() != null) {
					for (int i = 0; i < dependency.getExclusions().size(); i++) {
						Exclusion exclusion = dependency.getExclusions().get(i);
						dependence.removeDependency(exclusion.getGroupId(), exclusion.getArtifactId());
					}
				}
			}

		} catch (Exception e) {
			Launcher.LOGGER.log(Level.ERROR, "Unable to read the pom of the dependency:" + dependence.toString(), e);
		}
		return dependence;
	}

	public TreeDependency getTreeDependency() {
		return getTreeDependency(false, new HashSet<>());
	}

	/**
	 * Get the list of dependencies available in the local maven repository
	 *
	 * @param isLib: If false take dependency of the main project; if true, take dependencies of a library of the project
	 * @return the list of  dependencies
	 */
	private TreeDependency getTreeDependency(boolean isLib, Set<TreeDependency> hierarchy) {
		String groupId = extractVariable(model.getGroupId());
		String artifactId = extractVariable(model.getArtifactId());
		String version = extractVersion(groupId, artifactId, model.getVersion());
		TreeDependency dependence = new TreeDependency(groupId, artifactId, version, model.getPackaging(), m2RepositoryPath);
		if (hierarchy.contains(dependence)) {
			return dependence;
		}
		hierarchy.add(dependence);

		// add the parent has a dependency
		if (this.parent != null) {
			dependence.addDependence(this.parent.getTreeDependency(isLib, hierarchy));
		}

		List<Dependency> dependencies = model.getDependencies();
		for (Dependency dependency : dependencies) {
			dependence.addDependence(getTreeDependency(dependency, isLib, hierarchy));
		}

		if (!isLib) {
			for (InheritanceModel module : modules) {
				if (module.model.getGroupId() == null) {
					module.model.setGroupId(groupId);
				}
				if (module.model.getVersion() == null) {
					module.model.setVersion(version);
				}
				dependence.addDependence(module.getTreeDependency(isLib, hierarchy));
			}
		}
		return dependence;
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
		if (model.getBuild() != null) {
			for (Plugin plugin : model.getBuild().getPlugins()) {
				if (!"maven-compiler-plugin".equals(plugin.getArtifactId())) {
					continue;
				}
				Xpp3Dom configuration = (Xpp3Dom) plugin.getConfiguration();
				Xpp3Dom source = configuration.getChild("source");
				if (source != null) {
					return Integer.parseInt(extractVariable(source.getValue()).substring(2));
				}
				break;
			}
		}
		String javaVersion = getProperty("java.version");
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
	public static InheritanceModel readPOM(String path, InheritanceModel parent, String m2RepositoryPath, MavenLauncher.SOURCE_TYPE sourceType, Environment environment) throws IOException, XmlPullParserException {
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
			InheritanceModel inheritanceModel = new InheritanceModel(model, parent, pomFile.getParentFile(), m2RepositoryPath, sourceType, environment);
			for (String module : model.getModules()) {
				if (path.contains(m2RepositoryPath)) {
					InheritanceModel modulePom = readPOM(path.replaceAll(model.getArtifactId(), module), inheritanceModel, m2RepositoryPath, sourceType, environment);
					if (modulePom != null) {
						inheritanceModel.addModule(modulePom);
					}
				} else {
					inheritanceModel.addModule(readPOM(Paths.get(pomFile.getParent(), module).toString(), inheritanceModel, m2RepositoryPath, sourceType, environment));
				}
			}
			return inheritanceModel;
		}
	}
}
