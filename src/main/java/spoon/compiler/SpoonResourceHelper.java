/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.compiler;

import spoon.Launcher;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FileSystemFolder;
import spoon.support.compiler.ZipFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines a helper for manipulating resources.
 */
public abstract class SpoonResourceHelper {

	private SpoonResourceHelper() {
	}

	/**
	 * Tells if the given file is an archive file.
	 */
	public static boolean isArchive(File f) {
		return f.getName().endsWith(".jar") || f.getName().endsWith(".zip");
	}

	/**
	 * Tells if the given file is file (files are not archives).
	 */
	public static boolean isFile(File f) {
		return f.isFile() && !isArchive(f);
	}

	/**
	 * Creates the list of {@link SpoonResource} corresponding to the given
	 * paths (files, folders, archives).
	 */
	public static List<SpoonResource> resources(String... paths)
			throws FileNotFoundException {
		List<SpoonResource> files = new ArrayList<>();
		for (String path : paths) {
			files.add(createResource(new File(path)));
		}
		return files;
	}

	/**
	 * Creates the {@link SpoonFile} corresponding to the given file.
	 */
	public static SpoonFile createFile(File f) throws FileNotFoundException {
		if (!f.exists()) {
			throw new FileNotFoundException(f.toString());
		}
		return new FileSystemFile(f);
	}

	/**
	 * Creates the {@link SpoonResource} corresponding to the given file.
	 */
	public static SpoonResource createResource(File f)
			throws FileNotFoundException {
		if (isFile(f)) {
			return createFile(f);
		}
		return createFolder(f);
	}

	/**
	 * Creates the {@link SpoonFolder} corresponding to the given file.
	 */
	public static SpoonFolder createFolder(File f) throws FileNotFoundException {
		if (!f.exists()) {
			throw new FileNotFoundException(f.toString() + " does not exist");
		}
		try {
			if (f.isDirectory()) {
				return new FileSystemFolder(f);
			}
			if (isArchive(f)) {
				return new ZipFolder(f);
			}
		} catch (IOException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}

		return null;
	}

}
