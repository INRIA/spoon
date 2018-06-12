/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.support;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import spoon.SpoonException;
import spoon.SpoonModelBuilder;
import spoon.compiler.Environment;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.factory.Factory;

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
import java.util.Map;
import java.util.Set;

/**
 * This class store information for incremental build.
 */
public class IncrementalBuildInformation {
	private static final String FACTORY_FILENAME = "model";
	private static final String CACHE_INFO_FILENAME = "cache-info";
	private static final String CLASS_FILES_DIRNAME = "class-files";

	/**
	 * This is the part of the class that should be serialized
	 */
	public static class CacheInfo implements Serializable {
		/** Cache version */
		public static final long serialVersionUID = 1L; //TODO: Spoon version
		/** Timestamp of the last model build */
		public long lastBuildTime;
		/** Map of input source files and corresponding binary files */
		public Map<File, Set<File>> inputSourcesMap;
	}

	private Environment environment;
	private CacheInfo mCacheInfo;
	private Set<File> mRemovedSources;
	private boolean mChangesPresent = false;

	public IncrementalBuildInformation() {
		this.mRemovedSources = new HashSet<>();
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	private void checkEnvironment() {
		if (this.environment == null) {
			throw new SpoonException("The environment must be set.");
		}
	}

	private File getCacheDir() {
		this.checkEnvironment();
		return this.environment.getCacheDirectory();
	}

	private File getFactoryFile() {
		this.checkEnvironment();
		return new File(this.getCacheDir(), FACTORY_FILENAME);
	}

	private File getCacheInfoFile() {
		this.checkEnvironment();
		return new File(this.getCacheDir(), CACHE_INFO_FILENAME);
	}

	/**
	 * Return the directory containing the compiled classes for the built model
	 */
	public File getClassFilesDir() {
		this.checkEnvironment();
		return new File(this.getCacheDir(), CLASS_FILES_DIRNAME);
	}

	/**
	 * Load and return cache information from a serialized file
	 */
	public CacheInfo loadCacheInfo() throws InvalidClassException {
		try (
				FileInputStream fileStream = new FileInputStream(this.getCacheInfoFile());
				ObjectInputStream objectStream = new ObjectInputStream(new BufferedInputStream(fileStream));
		) {
			this.mCacheInfo = (CacheInfo) objectStream.readObject();
			return this.mCacheInfo;
		} catch (InvalidClassException e) {
			throw e;
		} catch (ClassNotFoundException | IOException e) {
			throw new SpoonException("unable to load cache info");
		}
	}

	private void saveCacheInfo(CacheInfo cacheInfo) {
		try (
				FileOutputStream fileStream = new FileOutputStream(this.getCacheInfoFile());
				ObjectOutputStream objectStream = new ObjectOutputStream(new BufferedOutputStream(fileStream));
		) {
			objectStream.writeObject(cacheInfo);
			objectStream.flush();
		} catch (IOException e) {
			throw new SpoonException("unable to save cache info");
		}
	}

	/**
	 * Load and return a factory from a serialized file
	 */
	public Factory loadFactory() {
		try {
			return new SerializationModelStreamer().load(new FileInputStream(this.getFactoryFile()));
		} catch (IOException e) {
			throw new SpoonException("unable to load factory from cache");
		}
	}

	private void saveFactory(Factory factory) {
		try {
			new SerializationModelStreamer().save(factory, new FileOutputStream(this.getFactoryFile()));
		} catch (IOException e) {
			throw new SpoonException("unable to save factory");
		}
	}

	/**
	 * If true, the model cannot be incrementally built and should be entirely rebuilt.
	 * This is true if the cache directories and file don't exist.
	 */
	public boolean shouldBeRebuilt() {
		return !this.getCacheDir().exists() || !this.getFactoryFile().exists() || !this.getCacheInfoFile().exists() || !this.getClassFilesDir().exists();
	}

	private void compileClasses(SpoonModelBuilder modelBuilder) {
		File binaryOutputDirectory = modelBuilder.getBinaryOutputDirectory();
		modelBuilder.setBinaryOutputDirectory(this.getClassFilesDir());
		modelBuilder.compile(SpoonModelBuilder.InputType.FILES);
		modelBuilder.setBinaryOutputDirectory(binaryOutputDirectory);
	}

	/**
	 * Caches current spoon model and binary files. Should be called only after model is built.
	 */
	public void saveCache(Factory factory, SpoonModelBuilder modelBuilder) {
		if (factory == null) {
			throw new SpoonException("factory is null");
		}

		this.compileClasses(modelBuilder);

		this.saveFactory(factory);

		// update cache info
		CacheInfo newCacheInfo = new CacheInfo();
		newCacheInfo.lastBuildTime = System.currentTimeMillis();
		Map<File, Set<File>> newSourcesMap = new HashMap<>();
		for (Map.Entry<String, CompilationUnit> e : factory.CompilationUnit().getMap().entrySet()) {
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
		Collection<File> dirs = FileUtils.listFilesAndDirs(this.getClassFilesDir(), DirectoryFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		dirs.stream()
				.filter(d -> d.exists() && FileUtils.listFiles(d, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).isEmpty())
				.forEach(FileUtils::deleteQuietly);

		newCacheInfo.inputSourcesMap = newSourcesMap;
		saveCacheInfo(newCacheInfo);
	}

	/**
	 * Set the sources that have been removed after an incremental build
	 */
	public void setRemovedSources(Set<File> removedSources) {
		this.mRemovedSources = removedSources;
	}

	/**
	 * Record the state of the changes: true if the model has changed.
	 */
	public void setChangesPresent(boolean changesPresent) {
		this.mChangesPresent = changesPresent;
	}

	/**
	 * Return true if the model has changed
	 */
	public boolean isChangesPresent() {
		return this.mChangesPresent;
	}
}
