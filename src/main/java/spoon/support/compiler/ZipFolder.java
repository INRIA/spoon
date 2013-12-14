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

import spoon.Spoon;
import spoon.compiler.SpoonResourceHelper;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;

public class ZipFolder implements SpoonFolder {

	File file;

	List<SpoonFile> files;

	public ZipFolder(File file) throws IOException {
		super();
		if (!file.isFile()) {
			throw new IOException(file.getName() + " is not a valid zip file");
		}
		this.file = file;
	}

	public List<SpoonFile> getAllFiles() {
		return getFiles();
	}

	public List<SpoonFile> getAllJavaFiles() {
		List<SpoonFile> files = new ArrayList<SpoonFile>();

		for (SpoonFile f : getFiles())
			if (f.isJava())
				files.add(f);

		// no subfolder, skipping
		// for (CtFolder fol : getSubFolder())
		// files.addAll(fol.getAllJavaFile());
		return files;
	}

	public List<SpoonFile> getFiles() {
		// Indexing content
		if (files == null) {
			files = new ArrayList<SpoonFile>();
			ZipInputStream zipInput = null;
			try {
				zipInput = new ZipInputStream(new BufferedInputStream(
						new FileInputStream(file)));

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

					files.add(new ZipFile(this, entry.getName(), output
							.toByteArray()));
				}
				zipInput.close();

			} catch (Exception e) {
				Spoon.logger.error(e.getMessage(), e);
			}
		}
		return files;
	}

	public String getName() {
		return file.getName();
	}

	public SpoonFolder getParent() {
		try {
			return SpoonResourceHelper.createFolder(file.getParentFile());
		} catch (FileNotFoundException e) {
			Spoon.logger.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SpoonFolder> getSubFolders() {
		return new ArrayList<SpoonFolder>(0);
	}

	public boolean isFile() {
		return false;
	}

	@Override
	public String toString() {
		return getPath();
	}

	public String getPath() {
		try {
			return file.getCanonicalPath();
		} catch (Exception e) {
			Spoon.logger.error(e.getMessage(), e);
			return file.getPath();
		}
	}

	@Override
	public boolean isArchive() {
		return true;
	}

	@Override
	public File getFileSystemParent() {
		return file.getParentFile();
	}

	@Override
	public File toFile() {
		return file;
	}

	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
