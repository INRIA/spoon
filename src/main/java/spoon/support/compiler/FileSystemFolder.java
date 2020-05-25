/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;
import spoon.compiler.SpoonResourceHelper;

public class FileSystemFolder implements SpoonFolder {

	File file;

	public FileSystemFolder(File file) {
		if (!file.isDirectory()) {
			throw new SpoonException("Not a directory " + file);
		}
		try {
			this.file = file.getCanonicalFile();
		} catch (Exception e) {
			throw new SpoonException(e);
		}
	}

	public FileSystemFolder(String path) {
		this(new File(path));
	}

	@Override
	public List<SpoonFile> getAllFiles() {
		List<SpoonFile> all = new ArrayList<>(getFiles());
		for (SpoonFolder f : getSubFolders()) {
			all.addAll(f.getAllFiles());
		}
		return all;
	}

	@Override
	public List<SpoonFile> getFiles() {
		List<SpoonFile> files;
		files = new ArrayList<>();
		for (File f : file.listFiles()) {
			if (SpoonResourceHelper.isFile(f)) {
				files.add(new FileSystemFile(f));
			}
		}
		return files;
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public SpoonFolder getParent() {
		try {
			return SpoonResourceHelper.createFolder(file.getParentFile());
		} catch (FileNotFoundException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<SpoonFolder> getSubFolders() {
		List<SpoonFolder> subFolders;
		subFolders = new ArrayList<>();
		for (File f : file.listFiles()) {
			if (!(SpoonResourceHelper.isArchive(f) || f.isFile())) {
				try {
					subFolders.add(SpoonResourceHelper.createFolder(f));
				} catch (FileNotFoundException e) {
					Launcher.LOGGER.error(e.getMessage(), e);
				}
			}
		}
		return subFolders;
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public String toString() {
		return getPath();
	}

	@Override
	public List<SpoonFile> getAllJavaFiles() {
		List<SpoonFile> files = new ArrayList<>();
		for (SpoonFile f : getFiles()) {
			if (f.isJava()) {
				files.add(f);
			}
		}
		for (SpoonFolder fol : getSubFolders()) {
			files.addAll(fol.getAllJavaFiles());
		}
		return files;
	}

	@Override
	public String getPath() {
			return file.getPath();
		}

	@Override
	public boolean isArchive() {
		return false;
	}

	@Override
	public File getFileSystemParent() {
		return file.getParentFile();
	}

	@Override
	public File toFile() {
		return file;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return toString().equals(obj.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public void addFile(SpoonFile source) {
		throw new UnsupportedOperationException("not possible a real folder");
	}

	@Override
	public void addFolder(SpoonFolder source) {
		throw new UnsupportedOperationException("not possible a real folder");
	}

}
