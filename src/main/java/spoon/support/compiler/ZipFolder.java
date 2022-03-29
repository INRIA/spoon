/*
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
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import spoon.Launcher;
import spoon.SpoonException;
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
		return getFiles().stream().filter(SpoonFile::isJava).collect(Collectors.toList());
	}

	@Override
	public List<SpoonFile> getFiles() {
		// Indexing content
		if (files == null) {
			files = new ArrayList<>();
			try (ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
				ByteArrayOutputStream output = new ByteArrayOutputStream(2048)) {

				ZipEntry entry;
				while ((entry = zipInput.getNextEntry()) != null) {
					zipInput.transferTo(output);
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
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ZipFolder)) {
			return false;
		}
		ZipFolder other = (ZipFolder) obj;
		return Objects.equals(file, other.file);
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
				if (!f.toPath().normalize().startsWith(destDir.toPath())) {
					// test against zip slips
						throw new SpoonException("Entry is outside of the target dir: " + entry.getName());
				}
				if (entry.isDirectory()) { // if it's a directory, create it
					f.mkdir();
					continue;
				}
				// deflate in buffer
				// Force parent directory creation, sometimes directory was not yet handled
				f.getParentFile().mkdirs();
				// in the zip entry iteration
				try (OutputStream output = new BufferedOutputStream(new FileOutputStream(f))) {
					zipInput.transferTo(output);
				}
			}
		} catch (Exception e) {
			Launcher.LOGGER.error(e.getMessage(), e);
		}
	}
}
