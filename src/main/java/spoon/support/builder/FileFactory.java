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

package spoon.support.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import spoon.support.builder.support.CtFileFile;
import spoon.support.builder.support.CtFolderFile;
import spoon.support.builder.support.CtFolderZip;

public class FileFactory {
	public static boolean isArchive(File f) {
		return f.getName().endsWith(".jar") || f.getName().endsWith(".zip");
	}

	public static boolean isFile(File f) {
		return f.isFile() && !isArchive(f);
	}

	public static CtFile createFile(File f) throws FileNotFoundException {
		if (!f.exists()) {
			throw new FileNotFoundException(f.toString());
		}
		return new CtFileFile(f);
	}

	public static CtResource createResource(File f)
			throws FileNotFoundException {
		if (f.isFile()) {
			return createFile(f);
		}
		return createFolder(f);
	}

	public static CtFolder createFolder(File f) throws FileNotFoundException {
		if (!f.exists()) {
			throw new FileNotFoundException(f.toString() + " does not exist");
		}
		try {
			if (f.isDirectory()) {
				return new CtFolderFile(f);
			}
			if (isArchive(f)) {
				return new CtFolderZip(f);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
