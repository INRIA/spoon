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

package spoon.support.builder.support;

import java.util.ArrayList;
import java.util.List;

import spoon.support.builder.CtFile;
import spoon.support.builder.CtFolder;

public class CtVirtualFolder implements CtFolder {
	List<CtFile> files = new ArrayList<CtFile>();

	List<CtFolder> folders = new ArrayList<CtFolder>();

	public boolean addFile(CtFile o) {
		return files.add(o);
	}

	public boolean addFolder(CtFolder o) {
		return folders.add(o);
	}

	public List<CtFile> getAllFiles() {
		List<CtFile> files = new ArrayList<CtFile>(getFiles());

		for (CtFolder f : folders)
			files.addAll(f.getAllJavaFiles());
		return files;
	}

	public List<CtFile> getAllJavaFiles() {
		List<CtFile> files = new ArrayList<CtFile>();

		for (CtFile f : getFiles())
			if (f.isJava())
				files.add(f);

		for (CtFolder fol : folders)
			files.addAll(fol.getAllJavaFiles());
		return files;
	}

	public List<CtFile> getFiles() {
		return files;
	}

	public String getName() {
		return "Virtual directory";
	}

	public CtFolder getParent() {
		return null;
	}

	public List<CtFolder> getSubFolder() {
		return folders;
	}

	public boolean isFile() {
		return false;
	}

	public String getPath() {
		return "Virtual folder";
	}
	

}
