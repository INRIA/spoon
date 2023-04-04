/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import org.apache.commons.io.FileUtils;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;
import spoon.compiler.SpoonResourceHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
			try (FileSystem zip = FileSystems.newFileSystem(URI.create("jar:" + file.toURI()), Map.of())) {
				Path tempFolder = Files.createTempDirectory("spoon-zip-file-proxy");
				// Try to clean up - not guaranteed to work!
				Runtime.getRuntime().addShutdownHook(new Thread(() -> {
					try {
						FileUtils.deleteDirectory(tempFolder.toFile());
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}));


				for (Path directory : zip.getRootDirectories()) {
					copyFolder(directory, tempFolder);
				}
			} catch (Exception e) {
				Launcher.LOGGER.error("Error copying zip file contents", e);
			}
		}
		return files;
	}

	private void copyFolder(Path source, Path target) throws IOException {
		try (Stream<Path> stream = Files.walk(source)) {
			for (Path path : (Iterable<Path>) stream::iterator) {
				// This little dance is needed, as resolve with the Path fails: The two paths are in different and
				// incompatible file systems!
				String relativePath = source.relativize(path).toString();
				Path targetFile = target.resolve(relativePath);

				if (Files.isDirectory(path)) {
					Files.createDirectories(targetFile);
				} else {
					// walked in depth-first order, so we can just copy it and expect the parent to exist
					Files.copy(path, targetFile);
					files.add(new ZipFile(this, relativePath, targetFile));
				}
			}
		}
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
