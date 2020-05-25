/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.codehaus.plexus.util.CollectionUtils;

import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.SerializationModelStreamer;

/**
 * Create a Spoon launcher for incremental build
 */
public class IncrementalLauncher extends Launcher {

	private static class CacheInfo implements Serializable {
		/** Cache version */
		public static final long serialVersionUID = 1L; //TODO: Spoon version
		/** Timestamp of the last model build */
		public long lastBuildTime;
		/** Map of input source files and corresponding binary files */
		public Map<File, Set<File>> inputSourcesMap;
	}

	private final Set<File> mInputSources;
	private final File mIncrementalCacheDirectory;
	private final File mModelFile;
	private final File mCacheInfoFile;
	private final File mClassFilesDir;
	private final boolean mChangesPresent;
	private Set<String> mSourceClasspath;
	private Set<File> mRemovedSources = new HashSet<>();
	private Set<File> mAddedSources = new HashSet<>();
	private Set<File> mCommonSources = new HashSet<>();
	private CacheInfo mCacheInfo = null;

	private static CacheInfo loadCacheInfo(File file) throws InvalidClassException {
		try (FileInputStream fileStream = new FileInputStream(file);
			ObjectInputStream objectStream = new ObjectInputStream(new BufferedInputStream(fileStream))) {
			return (CacheInfo) objectStream.readObject();
		} catch (InvalidClassException e) {
			throw e;
		} catch (ClassNotFoundException | IOException e) {
			throw new SpoonException("unable to load cache info");
		}
	}

	private static void saveCacheInfo(CacheInfo cacheInfo, File file) {
		try (FileOutputStream fileStream = new FileOutputStream(file);
			ObjectOutputStream objectStream = new ObjectOutputStream(new BufferedOutputStream(fileStream))) {
			objectStream.writeObject(cacheInfo);
			objectStream.flush();
		} catch (IOException e) {
			throw new SpoonException("unable to save cache info");
		}
	}

	private static Factory loadFactory(File file) {
		try {
			return new SerializationModelStreamer().load(new FileInputStream(file));
		} catch (IOException e) {
			throw new SpoonException("unable to load factory from cache");
		}
	}

	private static void saveFactory(Factory factory, File file) {
		try {
			new SerializationModelStreamer().save(factory, new FileOutputStream(file));
		} catch (IOException e) {
			throw new SpoonException("unable to save factory");
		}
	}

	private static Set<File> getAllJavaFiles(Set<File> resources) {
		Set<File> javaFiles = new HashSet<>();
		for (File e : resources) {
			if (e.isDirectory()) {
				Collection<File> files = FileUtils.listFiles(e, new SuffixFileFilter(".java"), TrueFileFilter.INSTANCE);
				files.forEach(f -> {
					try {
						javaFiles.add(f.getCanonicalFile());
					} catch (IOException e1) {
						throw new SpoonException("unable to locate input source file: " + f);
					}
				});
			} else if (e.isFile() && e.getName().endsWith(".java")) {
				try {
					javaFiles.add(e.getCanonicalFile());
				} catch (IOException e1) {
					throw new SpoonException("unable to locate input source file: " + e);
				}
			}
		}
		return javaFiles;
	}

	/**
	 * Creates a {@link Launcher} for incremental build.
	 * @param inputResources Resources to be parsed to build the spoon model.
	 * @param sourceClasspath Source classpath of the spoon model.
	 * @param cacheDirectory The directory to store all incremental information. If it's empty, full rebuild will be performed.
	 * @param forceRebuild Force to perform full rebuild, ignoring incremental cache.
	 * @throws IllegalArgumentException
	 * @throws SpoonException
	 */
	public IncrementalLauncher(Set<File> inputResources, Set<String> sourceClasspath, File cacheDirectory, boolean forceRebuild) {
		if (cacheDirectory == null) {
			throw new IllegalArgumentException("unable to create incremental launcher with null cache directory");
		}

		mInputSources = getAllJavaFiles(inputResources);
		mSourceClasspath = new HashSet<>(sourceClasspath);
		mIncrementalCacheDirectory = cacheDirectory;
		mModelFile = new File(cacheDirectory, "model");
		mCacheInfoFile = new File(cacheDirectory, "cache-info");
		mClassFilesDir = new File(cacheDirectory, "class-files");

		if (!mIncrementalCacheDirectory.exists() || !mModelFile.exists() || !mCacheInfoFile.exists() || !mClassFilesDir.exists()) {
			forceRebuild = true;
		} else {
			try {
				mCacheInfo = loadCacheInfo(mCacheInfoFile);
			} catch (InvalidClassException | SpoonException e) {
				// Incompatible cache version or unable to load cache. So force rebuild.
				forceRebuild = true;
			}
		}

		if (!mIncrementalCacheDirectory.exists() && !mIncrementalCacheDirectory.mkdirs()) {
				throw new SpoonException("unable to create cache directory");
		}

		if (!mClassFilesDir.exists() && !mClassFilesDir.mkdirs()) {
				throw new SpoonException("unable to create class files directory");
		}

		if (forceRebuild) {
			// Build model from scratch.
			factory = createFactory();
			processArguments();
			mInputSources.forEach(f -> addInputResource(f.getPath()));
			mChangesPresent = true;
			setBinaryOutputDirectory(mClassFilesDir);
		} else {
			// Load model from cache.
			Factory oldFactory = loadFactory(mModelFile);
			oldFactory.getModel().setBuildModelIsFinished(false);

			// Build model incrementally.
			mRemovedSources = new HashSet<>(CollectionUtils.subtract(mCacheInfo.inputSourcesMap.keySet(), mInputSources));
			mAddedSources = new HashSet<>(CollectionUtils.subtract(mInputSources, mCacheInfo.inputSourcesMap.keySet()));
			mCommonSources = new HashSet<>(CollectionUtils.intersection(mCacheInfo.inputSourcesMap.keySet(), mInputSources));

			Set<File> incrementalSources = new HashSet<>(mAddedSources);
			for (File e : mCommonSources) {
				if (e.lastModified() >= mCacheInfo.lastBuildTime) {
					incrementalSources.add(e);
				}
			}

			List<CtType<?>> oldTypes = oldFactory.Type().getAll();

			Set<CtType<?>> changedTypes = new HashSet<>();
			for (CtType<?> type : oldTypes) {
				File typeFile = type.getPosition().getFile();
				if (incrementalSources.contains(typeFile)) {
					changedTypes.add(type);
				}
			}

			for (CtType<?> type : oldTypes) {
				File typeFile = type.getPosition().getFile();
				if (mRemovedSources.contains(typeFile)) {
					type.delete();
					continue;
				}
				// If a type refers to some changed type, we should rebuild it.
				Set<CtTypeReference<?>> referencedTypes = type.getReferencedTypes();
				for (CtType<?> changedType : changedTypes) {
					if (referencedTypes.contains(changedType.getReference())) {
						incrementalSources.add(typeFile);
						type.delete();
						break;
					}
				}
			}

			try {
				mSourceClasspath.add(mClassFilesDir.getCanonicalPath());
			} catch (IOException e2) {
				throw new SpoonException("unable to locate class files dir: " + mClassFilesDir);
			}

			Collection<CtPackage> oldPackages = oldFactory.Package().getAll();
			for (CtPackage pkg : oldPackages) {
				if (pkg.getTypes().isEmpty() && pkg.getPackages().isEmpty() && !pkg.isUnnamedPackage()) {
					pkg.delete();
				}
			}

			factory = oldFactory;
			processArguments();
			incrementalSources.forEach(f ->  addInputResource(f.getPath()));
			mChangesPresent = !mRemovedSources.isEmpty() || !mAddedSources.isEmpty() || !incrementalSources.isEmpty();
			setBinaryOutputDirectory(mClassFilesDir);
		}

		getEnvironment().setSourceClasspath(mSourceClasspath.toArray(new String[0]));
	}

	/**
	 * Creates a {@link Launcher} for incremental build.
	 * @param inputResources Resources to be parsed to build the spoon model.
	 * @param sourceClasspath Source classpath of the spoon model.
	 * @param cacheDirectory The directory to store all incremental information. If it's empty, full rebuild will be performed.
	 * @throws IllegalArgumentException
	 * @throws SpoonException
	 */
	public IncrementalLauncher(Set<File> inputResources, Set<String> sourceClasspath, File cacheDirectory) {
		this(inputResources, sourceClasspath, cacheDirectory, false);
	}

	/** Returns true, if any source code changes after previous build are present, and false otherwise. */
	public boolean changesPresent() {
		return mChangesPresent;
	}

	/** Caches current spoon model and binary files. Should be called only after model is built. */
	public void saveCache() {
		if (mIncrementalCacheDirectory == null) {
			throw new SpoonException("incremental cache directory is null");
		}

		Factory factory = getFactory();
		if (factory == null) {
			throw new SpoonException("factory is null");
		}

		getModelBuilder().compile(SpoonModelBuilder.InputType.FILES);

		saveFactory(factory, mModelFile);

		CacheInfo newCacheInfo = new CacheInfo();
		newCacheInfo.lastBuildTime = System.currentTimeMillis();
		Map<File, Set<File>> newSourcesMap = new HashMap<>();
		for (Entry<String, CompilationUnit> e : factory.CompilationUnit().getMap().entrySet()) {
			newSourcesMap.put(new File(e.getKey()), new HashSet<>(e.getValue().getBinaryFiles()));
		}

		if (mCacheInfo != null) {
			newSourcesMap.putAll(mCacheInfo.inputSourcesMap);
			for (File r : mRemovedSources) {
				newSourcesMap.get(r).forEach(File::delete); // Removes corresponding .class files
				newSourcesMap.remove(r);
			}
		}

		// Removes all empty directories
		Collection<File> dirs = FileUtils.listFilesAndDirs(mClassFilesDir, DirectoryFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		dirs.stream()
			.filter(d -> d.exists() && FileUtils.listFiles(d, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).isEmpty())
			.forEach(FileUtils::deleteQuietly);

		newCacheInfo.inputSourcesMap = newSourcesMap;
		saveCacheInfo(newCacheInfo, mCacheInfoFile);
	}
}
