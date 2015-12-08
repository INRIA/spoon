/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.support.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
		super();
		if (!file.isDirectory()) {
			throw new SpoonException("Not a directory " + file);
		}
		this.file = file;
	}

	public List<SpoonFile> getAllFiles() {
		List<SpoonFile> all = new ArrayList<SpoonFile>(getFiles());
		for (SpoonFolder f : getSubFolders()) {
			all.addAll(f.getAllFiles());
		}
		return all;
	}

	public List<SpoonFile> getFiles() {
		List<SpoonFile> files;
		files = new ArrayList<SpoonFile>();
		for (File f : file.listFiles()) {
			if (SpoonResourceHelper.isFile(f)) {
				files.add(new FileSystemFile(f));
			}
		}
		return files;
	}

	public String getName() {
		return file.getName();
	}

	public SpoonFolder getParent() {
		try {
			return SpoonResourceHelper.createFolder(file.getParentFile());
		} catch (FileNotFoundException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SpoonFolder> getSubFolders() {
		List<SpoonFolder> subFolders;
		subFolders = new ArrayList<SpoonFolder>();
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

	public boolean isFile() {
		return false;
	}

	@Override
	public String toString() {
		return getPath();
	}

	public List<SpoonFile> getAllJavaFiles() {
		List<SpoonFile> files = new ArrayList<SpoonFile>();
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

	public String getPath() {
		try {
			return file.getCanonicalPath();
		} catch (Exception e) {
			Launcher.LOGGER.error(e.getMessage(), e);
			return file.getPath();
		}
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
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			throw new SpoonException(e);
		}
	}

	@Override
	public boolean equals(Object obj) {
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
