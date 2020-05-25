/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;

public class FileSystemFile implements SpoonFile {

	File file;

	public FileSystemFile(String path) {
		this(new File(path));
	}

	public FileSystemFile(File file) {
		try {
			this.file = file.getCanonicalFile();
		} catch (IOException e) {
			throw new SpoonException(e);
		}
	}

	@Override
	public InputStream getContent() {
		try {
			if (!this.file.exists()) {
				throw new FileNotFoundException("The following file does not exist: " + this.file.getCanonicalPath());
			}
			return new FileInputStream(file);
		} catch (IOException e) {
			throw new SpoonException(e);
		}
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
	public File getFileSystemParent() {
		return file.getParentFile();
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public boolean isJava() {
		return getName().endsWith(".java");
	}

	@Override
	public String getPath() {
		try {
			return file.getCanonicalPath();
		} catch (Exception e) {
			Launcher.LOGGER.error(e.getMessage(), e);
			return file.getPath();
		}
	}

	@Override
	public String toString() {
		return file.getPath();
	}

	@Override
	public boolean isArchive() {
		return SpoonResourceHelper.isArchive(file);
	}

	@Override
	public File toFile() {
		return file;
	}

	@Override
	public boolean isActualFile() {
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		return toFile().equals(((SpoonResource) obj).toFile());
	}

	@Override
	public int hashCode() {
		return toFile().hashCode();
	}
}
