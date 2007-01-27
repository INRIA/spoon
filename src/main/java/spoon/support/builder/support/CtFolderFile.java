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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import spoon.support.builder.CtFile;
import spoon.support.builder.CtFolder;
import spoon.support.builder.FileFactory;

public class CtFolderFile implements CtFolder {

	File file;

	List<CtFile> files;

	List<CtFolder> subFolders;

	public CtFolderFile(File file) throws IOException {
		super();
		if (!file.isDirectory())
			throw new IOException("Not a directory");
		this.file = file;
	}

	public List<CtFile> getAllFiles() {
		List<CtFile> all = new ArrayList<CtFile>(getFiles());
		for (CtFolder f : getSubFolder()) {
			all.addAll(f.getAllFiles());
		}
		return all;
	}

	public List<CtFile> getFiles() {
		if (files == null) {
			files = new ArrayList<CtFile>();
			for (File f : file.listFiles()) {
				if (FileFactory.isFile(f))
					files.add(new CtFileFile(f));
			}
		}
		return files;
	}

	public String getName() {
		return file.getName();
	}

	public CtFolder getParent() {
		try {
			return FileFactory.createFolder(file.getParentFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<CtFolder> getSubFolder() {
		if (subFolders == null) {
			subFolders = new ArrayList<CtFolder>();
			for (File f : file.listFiles()) {
				if (!FileFactory.isFile(f))
					try {
						subFolders.add(FileFactory.createFolder(f));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
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
		return file.toString();
	}

	public List<CtFile> getAllJavaFiles() {
		List<CtFile> files = new ArrayList<CtFile>();
		for (CtFile f : getFiles())
			if (f.isJava())
				files.add(f);
		for (CtFolder fol : getSubFolder())
			files.addAll(fol.getAllJavaFiles());
		return files;
	}

	public String getPath() {
		return toString();
	}
	

	
}
