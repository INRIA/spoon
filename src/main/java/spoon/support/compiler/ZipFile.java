/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;

public class ZipFile implements SpoonFile {

	byte[] buffer;

	String name;

	ZipFolder parent;

	public ZipFile(ZipFolder parent, String name, byte[] buffer) {
		this.buffer = buffer;
		this.name = name;
		this.parent = parent;
	}

	@Override
	public InputStream getContent() {
		return new ByteArrayInputStream(buffer);
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
		return false;
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
