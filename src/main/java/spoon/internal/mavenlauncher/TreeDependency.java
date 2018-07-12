package spoon.internal.mavenlauncher;

import org.apache.log4j.Level;
import spoon.Launcher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

public class TreeDependency {
	String m2RepositoryPath;
	String groupId;
	String artifactId;
	String version;
	String type;
	List<TreeDependency> dependencies = new ArrayList<>();

	TreeDependency(String groupId, String artifactId, String version, String type, String m2RepositoryPath) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.type = type;
		this.m2RepositoryPath = m2RepositoryPath;
	}

	void addDependence(TreeDependency dependence) {
		if (dependence != null) {
			dependencies.add(dependence);
		}
	}

	List<TreeDependency> getDependencyList() {
		List<TreeDependency> output = new ArrayList<>(dependencies);
		for (TreeDependency treeDependency : dependencies) {
			output.addAll(treeDependency.getDependencyList());
		}
		return output;
	}


	public List<File> toJarList() {
		List<TreeDependency> dependencyList = getDependencyList();
		List<File> output = new ArrayList<>();
		Set<TreeDependency> addedDep = new HashSet<>();
		for (TreeDependency dep : dependencyList) {
			File file = dep.getTopLevelJar();
			if (null != file && !addedDep.contains(dep)) {
				addedDep.add(dep);
				output.add(file);
			}
		}
		return output;
	}

	private File getTopLevelJar() {
		if ("pom".equals(type)) {
			return null;
		}
		if (groupId != null && version != null) {
			String fileName = artifactId + "-" + version;
			Path depPath = Paths.get(m2RepositoryPath, groupId.replaceAll("\\.", "/"), artifactId, version);
			File depFile = depPath.toFile();
			if (depFile.exists()) {
				File jarFile = Paths.get(depPath.toString(), fileName + ".jar").toFile();
				if (jarFile.exists()) {
					return jarFile;
				} else {
					Launcher.LOGGER.log(Level.ERROR, "Jar not found at " + jarFile);
				}
			} else {
				String tmp = version;
				int buildIndex = version.indexOf("-");
				if (buildIndex != -1) {
					String build = version.substring(buildIndex + 1);
					tmp = version.replace(build, "SNAPSHOT");
				} else {
					buildIndex = version.indexOf("-");
				}
				depPath = Paths.get(m2RepositoryPath, groupId.replaceAll("\\.", "/"), artifactId, tmp);
				depFile = depPath.toFile();
				if (depFile.exists()) {
					File jarFile = Paths.get(depPath.toString(), fileName + ".jar").toFile();
					if (jarFile.exists()) {
						return jarFile;
					} else {
						Launcher.LOGGER.log(Level.ERROR, "Jar not found at " + jarFile);
					}
				} else {

					Launcher.LOGGER.log(Level.ERROR, "Dependency not found at " + depPath);
				}
			}
		}
		return null;
	}

	void removeDependency(String groupId, String artifactId) {
		for (TreeDependency dep : new ArrayList<>(dependencies)) {
			if (dep.groupId != null && dep.groupId.equals(groupId) && dep.artifactId != null && dep.artifactId.equals(artifactId)) {
				this.dependencies.remove(dep);
			} else {
				dep.removeDependency(groupId, artifactId);
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TreeDependency that = (TreeDependency) o;
		return Objects.equals(groupId, that.groupId)
				&& Objects.equals(artifactId, that.artifactId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupId, artifactId);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(groupId);
		sb.append(":");
		sb.append(artifactId);
		sb.append(":");
		sb.append(version);
		if (!dependencies.isEmpty()) {
			sb.append(" {\n");
			for (TreeDependency dep : dependencies) {
				String child = dep.toString();
				for (String s : child.split("\n")) {
					sb.append("\t");
					sb.append(s);
					sb.append("\n");
				}
			}
			sb.append("}");
		}
		return sb.toString();
	}
}
