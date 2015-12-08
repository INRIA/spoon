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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;

public class VirtualFolder implements SpoonFolder {
	private final Set<SpoonFile> files = new HashSet<SpoonFile>();
	private final Set<SpoonFolder> folders = new HashSet<SpoonFolder>();

	@Override
	public void addFile(SpoonFile o) {
		files.add(o);
	}

	@Override
	public void addFolder(SpoonFolder o) {
		folders.add(o);
	}

	@Override
	public List<SpoonFile> getAllFiles() {
		List<SpoonFile> result = new ArrayList<SpoonFile>();
		for (SpoonFolder f : folders) {
			result.addAll(f.getAllFiles());
		}

		for (SpoonFile f : getFiles()) {
			// we take care not to add a file that was already found in a folder
			if (!result.contains(f)) {
				result.add(f);
			}
		}
		return result;
	}

	@Override
	public List<SpoonFile> getAllJavaFiles() {
		List<SpoonFile> result = new ArrayList<SpoonFile>();

		for (SpoonFile f : getAllFiles()) {
			if (f.isJava()) {
				result.add(f);
			}
		}

		return result;
	}

	@Override
	public List<SpoonFile> getFiles() {
		return Collections.unmodifiableList(new ArrayList<SpoonFile>(files));
	}

	@Override
	public String getName() {
		return "Virtual directory";
	}

	@Override
	public SpoonFolder getParent() {
		return null;
	}

	@Override
	public List<SpoonFolder> getSubFolders() {
		return Collections.unmodifiableList(new ArrayList<SpoonFolder>(folders));
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public String getPath() {
		// it has to be real path for snippet building
		return ".";
	}

	@Override
	public File getFileSystemParent() {
		return null;
	}

	@Override
	public boolean isArchive() {
		return false;
	}

	@Override
	public File toFile() {
		return null;
	}

	@Override
	public String toString() {
		return "<virtual folder>: " + super.toString();
	}

}
