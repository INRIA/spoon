/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import spoon.SpoonException;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;
import spoon.support.Internal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class ZipFile implements SpoonFile {

	private final String name;
	private final ZipFolder parent;
	private final Path tempFile;
	private final byte[] content;

	/**
	 * Creates a new zip file. Should never be called manually.
	 *
	 * @param parent the parent folder
	 * @param name the name of the file
	 * @param content the content of the file
	 * @deprecated use {@link ZipFile#ZipFile(ZipFolder, String, Path)}
	 */
	@Deprecated
	public ZipFile(ZipFolder parent, String name, byte[] content) {
		this.content = content;
		this.name = name;
		this.parent = parent;
		this.tempFile = null;
	}

	/**
	 * Creates a new zip file. Should never be called manually.
	 *
	 * @param parent the parent folder
	 * @param name the name of the file
	 * @param tempFile the temporary file it was cached to
	 */
	@Internal
	public ZipFile(ZipFolder parent, String name, Path tempFile) {
		this.parent = parent;
		this.name = name;
		this.tempFile = tempFile;
		this.content = null;
	}

	@Override
	public InputStream getContent() {
		if (content != null) {
			return new ByteArrayInputStream(content);
		}
		try {
			return Files.newInputStream(Objects.requireNonNull(tempFile));
		} catch (IOException e) {
			throw new SpoonException(e);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public SpoonFolder getParent() {
		return parent;
	}

	@Override
	public File getFileSystemParent() {
		return getParent().getFileSystemParent();
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public boolean isJava() {
		return getName().endsWith(".java");
	}

	@Override
	public String getPath() {
		if (tempFile != null) {
			return tempFile.toAbsolutePath().toString();
		}
		return toString();
	}

	@Override
	public String toString() {
		return parent + "!" + getName();
	}

	@Override
	public boolean isArchive() {
		return true;
	}

	@Override
	public File toFile() {
		return null;
	}

	@Override
	public boolean isActualFile() {
		return tempFile != null;
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(name, parent, tempFile);
		result = 31 * result + Arrays.hashCode(content);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ZipFile)) {
			return false;
		}
		ZipFile zipFile = (ZipFile) obj;
		return Objects.equals(name, zipFile.name)
			&& Objects.equals(parent, zipFile.parent)
			&& Objects.equals(tempFile, zipFile.tempFile)
			&& Arrays.equals(content, zipFile.content);
	}

}
