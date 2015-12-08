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

	public FileSystemFile(File file) {
		super();
		this.file = file;
	}

	public InputStream getContent() {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		return null;
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

	@Override
	public File getFileSystemParent() {
		return file.getParentFile();
	}

	public boolean isFile() {
		return true;
	}

	public boolean isJava() {
		return getName().endsWith(".java");
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
	public String toString() {
		return file.getPath();
	}

	@Override
	public boolean isArchive() {
		return SpoonResourceHelper.isArchive(file);
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
