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
package spoon.test.support;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonFolder;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FileSystemFolder;
import spoon.support.compiler.FilteringFolder;
import spoon.support.compiler.VirtualFolder;

public class ResourceTest {

	@Test
	public void testEqualsFileSystemFile() {
		// two files with equivalent paths are equals
		String entry = "src/test/resources/spoon/test/api/Foo.java";
		assertTrue(new FileSystemFile(new File(entry)).equals(new FileSystemFile(new File("./"+entry))));
	}

	@Test
	public void testFileSystemFolder() {
		String dir = "src/test/resources/spoon/test/api/";
		FileSystemFolder fileSystemFolder = new FileSystemFolder(new File(dir));

		// there is one file in api
		assertEquals(2, fileSystemFolder.getAllFiles().size());
		assertEquals(2, fileSystemFolder.getAllJavaFiles().size());

		String entry = "src/test/resources/spoon/test/api/Foo.java";
		FileSystemFile file = new FileSystemFile(new File(entry));

		String entry1 = "src/test/resources/spoon/test/api/CommentedClass.java";
		FileSystemFile file1 = new FileSystemFile(new File(entry1));

		assertThat(fileSystemFolder.getAllFiles().contains(file), is(true));
		assertThat(fileSystemFolder.getAllFiles().contains(file1), is(true));
	}

	@Test
	public void testVirtualFolder() {
		String dir = "src/test/resources/spoon/test/api/";
		FileSystemFolder fileSystemFolder = new FileSystemFolder(new File(dir));

		String dir2 = "src/test/resources/spoon/test/exceptions/";
		FileSystemFolder fileSystemFolder2 = new FileSystemFolder(new File(dir2));

		SpoonFolder folder = new VirtualFolder();
		folder.addFolder(fileSystemFolder);
		folder.addFolder(fileSystemFolder2);

		assertEquals(4, folder.getAllFiles().size());

		// the README is not a Java file
		assertEquals(3, folder.getAllJavaFiles().size());
	}

	@Test
	public void testFilteringFolder() {
		// contract: one can filter input files
		// the core of this test is the removeIfMatches at the end

		// all files
		SpoonModelBuilder mb = new Launcher().getModelBuilder();
		FilteringFolder resources = new FilteringFolder();
		resources.addFolder(new FileSystemFolder("src/test/java/spoon/test/visibility/"));
		mb.addInputSource(resources);
		mb.build();
		int nbAll = mb.getFactory().getModel().getAllTypes().size();
		assertEquals(12, nbAll);

		SpoonModelBuilder mb2 = new Launcher().getModelBuilder();
		FilteringFolder resources2 = new FilteringFolder();
		resources2.addFolder(new FileSystemFolder("src/test/java/spoon/test/visibility/packageprotected/"));
		mb2.addInputSource(resources2);
		mb2.build();
		int nbPackageProtected = mb2.getFactory().getModel().getAllTypes().size();
		assertEquals(2, nbPackageProtected);

		// now the core of this test
		SpoonModelBuilder mb3 = new Launcher().getModelBuilder();
		FilteringFolder resources3 = new FilteringFolder();
		resources3.addFolder(new FileSystemFolder("src/test/java/spoon/test/visibility/"));
		// we remove a number of input resources
		resources3.removeAllThatMatch(".*packageprotected.*");
		mb3.addInputSource(resources3);
		mb3.build();
		assertEquals(nbAll - nbPackageProtected, mb3.getFactory().getModel().getAllTypes().size());
	}

}
