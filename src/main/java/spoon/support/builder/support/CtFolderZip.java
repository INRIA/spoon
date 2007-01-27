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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import spoon.support.builder.CtFile;
import spoon.support.builder.CtFolder;
import spoon.support.builder.FileFactory;

public class CtFolderZip implements CtFolder {

	File f;

	List<CtFile> files;

	public CtFolderZip(File f) throws IOException {
		super();
		if (!f.isFile()) {
			throw new IOException(f.getName() + " is not a valid zip file");
		}
		this.f = f;
	}

	public List<CtFile> getAllFiles() {
		return getFiles();
	}

	public List<CtFile> getAllJavaFiles() {
		List<CtFile> files = new ArrayList<CtFile>();

		for (CtFile f : getFiles())
			if (f.isJava())
				files.add(f);

		// no subfolder, skipping
		// for (CtFolder fol : getSubFolder())
		// files.addAll(fol.getAllJavaFile());
		return files;
	}

	public List<CtFile> getFiles() {
		// Indexing content
		if (files == null) {
			files = new ArrayList<CtFile>();
			try {
				ZipInputStream zipInput = new ZipInputStream(
						new BufferedInputStream(new FileInputStream(f)));

				ZipEntry entry;
				while ((entry = zipInput.getNextEntry()) != null) {
					// deflate in buffer
					final int BUFFER = 2048;
					ByteArrayOutputStream output = new ByteArrayOutputStream(
							BUFFER);
					int count;
					byte data[] = new byte[BUFFER];
					while ((count = zipInput.read(data, 0, BUFFER)) != -1) {
						output.write(data, 0, count);
					}
					output.flush();
					output.close();

					files.add(new CtFileZip(this, entry.getName(), output
							.toByteArray()));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return files;
	}

	public String getName() {
		return f.getName();
	}

	public CtFolder getParent() {
		try {
			return FileFactory.createFolder(f.getParentFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<CtFolder> getSubFolder() {
		return new ArrayList<CtFolder>(0);
	}

	public boolean isFile() {
		return false;
	}

	@Override
	public String toString() {
		return getName();
	}
	
	public String getPath() {
		return toString();
	}	

}
