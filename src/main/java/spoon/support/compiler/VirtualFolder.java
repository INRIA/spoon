/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
	protected final Set<SpoonFile> files = new HashSet<>();

	@Override
	public void addFile(SpoonFile o) {
		files.add(o);
	}

	@Override
	public void addFolder(SpoonFolder o) {
		for (SpoonFile f : o.getAllFiles()) {
			if (f.isFile()) {
				files.add(f);
			}
		}
	}

	@Override
	public List<SpoonFile> getAllFiles() {
		// there are never folders added to files in this class so just return a List here.
		// The files are already deduplicated based on Set logic.
		return new ArrayList<>(files);
	}

	@Override
	public List<SpoonFile> getAllJavaFiles() {
		List<SpoonFile> result = new ArrayList<>();

		for (SpoonFile f : getAllFiles()) {
			if (f.isJava()) {
				result.add(f);
			}
		}

		return result;
	}

	@Override
	public List<SpoonFile> getFiles() {
		return Collections.unmodifiableList(new ArrayList<>(files));
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
		List<SpoonFolder> result = new ArrayList<>();
		for (SpoonFile f : getAllFiles()) {
			SpoonFolder folder = f.getParent();
			if (folder != null && !result.contains(folder)) {
				result.add(folder);
			}
		}
		return Collections.unmodifiableList(result);
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
