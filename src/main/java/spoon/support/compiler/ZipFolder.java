/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import spoon.Launcher;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;
import spoon.compiler.SpoonResourceHelper;

public class ZipFolder implements SpoonFolder {

	File file;

	List<SpoonFile> files;

	public ZipFolder(File file) throws IOException {
		if (!file.isFile()) {
			throw new IOException(file.getName() + " is not a valid zip file");
		}
		this.file = file;
	}

	@Override
	public List<SpoonFile> getAllFiles() {
		return getFiles();
	}

	@Override
	public List<SpoonFile> getAllJavaFiles() {
		List<SpoonFile> files = new ArrayList<>();

		for (SpoonFile f : getFiles()) {
			if (f.isJava()) {
				files.add(f);
			}
		}

		// no subfolder, skipping
		// for (CtFolder fol : getSubFolder())
		// files.addAll(fol.getAllJavaFile());
		return files;
	}

	@Override
	public List<SpoonFile> getFiles() {
		// Indexing content
		if (files == null) {
			files = new ArrayList<>();
			final int buffer = 2048;
			try (ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
				ByteArrayOutputStream output = new ByteArrayOutputStream(buffer)) {
				ZipEntry entry;
				while ((entry = zipInput.getNextEntry()) != null) {
					// deflate in buffer
					int count;
					byte[] data = new byte[buffer];
					while ((count = zipInput.read(data, 0, buffer)) != -1) {
						output.write(data, 0, count);
					}
					files.add(new ZipFile(this, entry.getName(), output.toByteArray()));
					output.reset();
				}
			} catch (Exception e) {
				Launcher.LOGGER.error(e.getMessage(), e);
			}
		}
		return files;
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public SpoonFolder getParent() {
		try {
			return SpoonResourceHelper.createFolder(file.getParentFile());
		} catch (FileNotFoundException e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<SpoonFolder> getSubFolders() {
		return new ArrayList<>(0);
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public String toString() {
		return getPath();
	}

	@Override
	public String getPath() {
		try {
			return file.getCanonicalPath();
		} catch (Exception e) {
			Launcher.LOGGER.error(e.getMessage(), e);
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

	@Override
	public void addFile(SpoonFile source) {
		throw new UnsupportedOperationException("not possible a real folder");
	}

	@Override
	public void addFolder(SpoonFolder source) {
		throw new UnsupportedOperationException("not possible a real folder");
	}

	/** physically extracts on disk all files of this zip file in the destinationDir `destDir` */
	public void extract(File destDir) {
		try (ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			ZipEntry entry;
			while ((entry = zipInput.getNextEntry()) != null) {
				File f = new File(destDir + File.separator + entry.getName());
				if (entry.isDirectory()) { // if it's a directory, create it
					f.mkdir();
					continue;
				}
				// deflate in buffer
				final int buffer = 2048;
				// Force parent directory creation, sometimes directory was not yet handled
				f.getParentFile().mkdirs();
				// in the zip entry iteration
				try (OutputStream output = new BufferedOutputStream(new FileOutputStream(f))) {
					int count;
					byte[] data = new byte[buffer];
					while ((count = zipInput.read(data, 0, buffer)) != -1) {
						output.write(data, 0, count);
					}
					output.flush();
				}
			}
		} catch (Exception e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
	}
}
