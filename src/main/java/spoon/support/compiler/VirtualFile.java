/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler;

import spoon.compiler.Environment;
import spoon.compiler.SpoonFile;
import spoon.compiler.SpoonFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class VirtualFile implements SpoonFile {
	public static final String VIRTUAL_FILE_NAME = "virtual_file";

	String content;

	String name = VIRTUAL_FILE_NAME;

	public VirtualFile(String content) {
		this.content = content;
	}

	public VirtualFile(String contents, String name) {
		this(contents);
		this.name = name;
	}

	@Override
	public InputStream getContent() {
		return new ByteArrayInputStream(content.getBytes());
	}

	@Override
	public char[] getContentChars(Environment env) {
		return content.toCharArray();
	}

	@Override
	public boolean isJava() {
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public SpoonFolder getParent() {
		return new VirtualFolder();
	}

	@Override
	public File getFileSystemParent() {
		return null;
	}

	@Override
	public String getPath() {
		return name;
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public boolean isArchive() {
		return false;
	}

	@Override
	public File toFile() {
		return null;
	}

	@Override
	public boolean isActualFile() {
		return false;
	}

}
