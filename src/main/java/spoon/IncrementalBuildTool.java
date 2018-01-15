package spoon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

public class IncrementalBuildTool {

	private Set<File> mInputSources;
	private Set<String> mInputClasspath;
	private Factory mFactory;
	private Map<String, List<File>> mOldBinariesMap;
	private File mOldBinariesDirectory;
	private Map<File, List<File>> mIncrementalMap;

	private List<File> getCorrespondingClassFiles(File inputSource) {
		if (mOldBinariesMap == null || mOldBinariesMap.isEmpty()) {
			return null;
		}

		String path;
		try {
			path = inputSource.getCanonicalPath();
		} catch (IOException e) {
			throw new SpoonException("unable to get canonical path of the input source");
		}

		List<File> classFiles = mOldBinariesMap.get(path);
		if (classFiles == null || classFiles.isEmpty()) {
			return null;
		}

		classFiles.sort((f1, f2) -> Integer.compare(f1.getName().length(), f2.getName().length()));
		return inputSource.lastModified() <= classFiles.get(0).lastModified() ? classFiles : null;
	}

	private Map<File, List<File>> getIncrementalMap() {
		Map<File, List<File>> incrementalMap = new HashMap<>();
		mInputSources.forEach(f -> incrementalMap.put(f, getCorrespondingClassFiles(f)));
		return incrementalMap;
	}

	private String getClassName(File classFile) {
		return FilenameUtils.removeExtension(classFile.getName());
	}

	private String getPackageName(File classFile) {
		Path relativePath = mOldBinariesDirectory.toPath().toAbsolutePath().relativize(classFile.toPath().toAbsolutePath());
		Path parent = relativePath.getParent();
		if (parent == null) {
			return "";
		}
		return parent.toString().replace('/', '.').replace('\\', '.');
	}

	public static Set<File> getAllFilesByExtention(Set<File> resources, String extention) {
		Set<File> javaFiles = new HashSet<>();
		for (File e : resources) {
			if (e.isDirectory()) {
				Collection<File> files = FileUtils.listFiles(e, new SuffixFileFilter(extention), TrueFileFilter.INSTANCE);
				javaFiles.addAll(files);
			} else if (e.isFile() && e.getName().endsWith(extention)) {
				javaFiles.add(e);
			}
		}
		return javaFiles;
	}

	public IncrementalBuildTool(Set<File> inputResources, Set<String> inputClasspath, Factory oldFactory, Map<String, List<File>> oldBinariesMap, File oldBinariesDirectory) {
		if (oldFactory == null) {
			throw new IllegalArgumentException("unable to create incremental build tool with null old factory");
		}

		if (oldBinariesMap == null) {
			throw new IllegalArgumentException("unable to create incremental build tool with null old binaries map");
		}

		if (oldBinariesDirectory == null) {
			throw new IllegalArgumentException("unable to create incremental build tool with null old binaries directory");
		}

		mInputSources = getAllFilesByExtention(inputResources, ".java");
		mInputClasspath = inputClasspath;
		mFactory = oldFactory;
		mOldBinariesMap = oldBinariesMap;
		mOldBinariesDirectory = oldBinariesDirectory;
		mIncrementalMap = getIncrementalMap();
	}

	public Set<File> getInputSourcesForIncrementalBuild() {
		Set<File> result = new HashSet<>();
		for (File inputFile : mInputSources) {
			List<File> classFiles = mIncrementalMap.get(inputFile);
			if (classFiles == null || classFiles.isEmpty()) {
				result.add(inputFile);
			}
		}
		return result;
	}

	public Set<String> getClasspathForIncrementalBuild() {
		Set<String> result = new HashSet<>(mInputClasspath);
		for (Entry<File, List<File>> e : mIncrementalMap.entrySet()) {
			if (e.getValue() != null) {
				e.getValue().forEach(f -> {
					try {
						result.add(f.getCanonicalPath());
					} catch (IOException ex) { }
				});
			}
		}
		return result;
	}

	public Factory getFactoryForIncrementalBuild() {
		List<CtType<?>> types = mFactory.Type().getAll();
		for (CtType<?> type : types) {
			boolean exists = false;
			for (Entry<File, List<File>> entry : mIncrementalMap.entrySet()) {
				List<File> classFiles = entry.getValue();
				if (classFiles == null || classFiles.isEmpty()) {
					continue;
				}
				File classFile = classFiles.get(0);
				String packageName = getPackageName(classFile);
				String className = getClassName(classFile);
				if (packageName.equals(type.getPackage().getQualifiedName()) && className.equals(type.getSimpleName())) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				type.delete();
			}
		}

		Collection<CtPackage> packages = mFactory.Package().getAll();
		for (CtPackage pkg : packages) {
			if (pkg.getTypes().isEmpty()) {
				pkg.delete();
			}
		}
		return mFactory;
	}
}
