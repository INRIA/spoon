/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
