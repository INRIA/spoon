/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
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
