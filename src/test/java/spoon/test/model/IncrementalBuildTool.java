package spoon.test.model;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

public class IncrementalBuildTool {

	private File mOldBinaryOutputDirectory;
	private Factory mFactory;
	private List<File> mInputSources;
	private String[] mInputClasspath;
	private Map<File, File> mCorrespondanceMap;

	private File getCorrespondingClassFile(File inputFile) {
		String name = FilenameUtils.getBaseName(inputFile.getName());
		Collection<File> classFiles = FileUtils.listFiles(mOldBinaryOutputDirectory, new NameFileFilter(name + ".class"), TrueFileFilter.INSTANCE);

		if (classFiles.isEmpty()) {
			return null;
		}

		for (File classFile : classFiles) {
			String relative = mOldBinaryOutputDirectory.toPath().toAbsolutePath().relativize(classFile.toPath().toAbsolutePath()).toString();
			String suffix = FilenameUtils.removeExtension(relative) + ".java";
			if (inputFile.toPath().toAbsolutePath().toString().endsWith(suffix)) {
				return (inputFile.lastModified() <= classFile.lastModified()) ? classFile : null;
			}
		}
		return null;
	}

	private Map<File, File> getCorrespondanceMap() {
		Map<File, File> correspondanceMap = new HashMap<>();
		mInputSources.forEach(f -> correspondanceMap.put(f, getCorrespondingClassFile(f)));
		return correspondanceMap;
	}

	private String getPackageName(File classFile) {
		Path relativePath = mOldBinaryOutputDirectory.toPath().toAbsolutePath().relativize(classFile.toPath().toAbsolutePath());
		Path parent = relativePath.getParent();
		if (parent == null) {
			return "";
		}
		return parent.toString().replace('/', '.').replace('\\', '.');
	}

	private String getClassName(File classFile) {
		return FilenameUtils.removeExtension(classFile.getName());
	}

	public IncrementalBuildTool(File oldBinaryOutputDirectory, Factory oldFactory, List<File> inputSources, String[] inputClasspath) {
		mOldBinaryOutputDirectory = oldBinaryOutputDirectory;
		mFactory = oldFactory;
		mInputSources = inputSources;
		mInputClasspath = inputClasspath;
		mCorrespondanceMap = getCorrespondanceMap();
	}

	public List<File> getInputSourcesForIncrementalBuild() {
		List<File> result = new ArrayList<>();
		for (File inputFile : mInputSources) {
			File classFile = mCorrespondanceMap.get(inputFile);
			if (classFile == null) {
				result.add(inputFile);
			}
		}
		return result;
	}

	public String[] getClasspathForIncrementalBuild() {
		List<String> result = new ArrayList<>(Arrays.asList(mInputClasspath));
		for (Entry<File, File> e : mCorrespondanceMap.entrySet()) {
			if (e.getValue() != null) {
				result.add(e.getValue().getAbsolutePath());
			}
		}
		return result.toArray(new String[0]);
	}

	public Factory getFactoryForIncrementalBuild()
	{
		List<CtType<?>> types = mFactory.Type().getAll();
		for (CtType<?> type : types) {
			boolean exists = false;
			for (Entry<File, File> entry : mCorrespondanceMap.entrySet()) {
				File classFile = entry.getValue();
				if (classFile == null) {
					continue;
				}
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
