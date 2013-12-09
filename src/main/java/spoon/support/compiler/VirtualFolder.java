/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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
import java.util.List;

import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;

public class VirtualFolder implements SpoonFolder {
	List<SpoonFile> files = new ArrayList<SpoonFile>();

	List<SpoonFolder> folders = new ArrayList<SpoonFolder>();

	List<String> rootJavaPaths = new ArrayList<>();
	
	public List<String> getRootJavaPaths() {
		return rootJavaPaths;
	}

	private void addRootJavaPath(String path) {
		if(!rootJavaPaths.contains(path)) {
			rootJavaPaths.add(path);
		}
	}
	
	public boolean addFile(SpoonFile o) {
		if(o.isJava()) {
			addRootJavaPath(o.getPath());
		} else {
			addRootJavaPath(o.getFileSystemParent().getPath());
		}
		return files.add(o);
	}

	public boolean addFolder(SpoonFolder o) {
		if(o.isArchive()) {
			addRootJavaPath(o.getFileSystemParent().getPath());
		} else {
			addRootJavaPath(o.getPath());
		}
		return folders.add(o);
	}

	public List<SpoonFile> getAllFiles() {
		List<SpoonFile> files = new ArrayList<SpoonFile>(getFiles());

		for (SpoonFolder f : folders)
			files.addAll(f.getAllJavaFiles());
		return files;
	}

	public List<SpoonFile> getAllJavaFiles() {
		List<SpoonFile> files = new ArrayList<SpoonFile>();

		for (SpoonFile f : getFiles())
			if (f.isJava())
				files.add(f);

		for (SpoonFolder fol : folders)
			files.addAll(fol.getAllJavaFiles());
		return files;
	}

	public List<SpoonFile> getFiles() {
		return files;
	}

	public String getName() {
		return "Virtual directory";
	}

	public SpoonFolder getParent() {
		return null;
	}

	public List<SpoonFolder> getSubFolders() {
		return folders;
	}

	public boolean isFile() {
		return false;
	}

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
	
}
