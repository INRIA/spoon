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
package spoon.test.api;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.compiler.SpoonFolder;
import spoon.support.compiler.FileSystemFolder;

public class FileSystemFolderTest {

	@Test
	public void jarFileIsNotSubfolder() {
		String folderPath = "./src/test/resources/folderWithJar";
		FileSystemFolder folder = new FileSystemFolder(new File(folderPath));
		List<SpoonFolder> subFolders = folder.getSubFolders();
		assertTrue(subFolders.isEmpty());
	}

	@Test
	public void testLauncherWithWrongPathAsInput() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/wrong/direction/File.java");
		try {
			spoon.buildModel();
		} catch (SpoonException spe) {
			Throwable containedException = spe.getCause();
			assertTrue(containedException instanceof FileNotFoundException);
		}
	}
}
