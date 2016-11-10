package spoon.test.support;

import static org.junit.Assert.assertEquals;
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
	public void testEqualsFileSystemFile() throws Exception {
		// two files with equivalent paths are equals
		String entry = "src/test/resources/spoon/test/api/Foo.java";
		assertTrue(new FileSystemFile(new File(entry)).equals(new FileSystemFile(new File("./"+entry))));
	}

	@Test
	public void testFileSystemFolder() throws Exception {
		String dir = "src/test/resources/spoon/test/api/";
		FileSystemFolder fileSystemFolder = new FileSystemFolder(new File(dir));

		// there is one file in api
		assertEquals(1, fileSystemFolder.getAllFiles().size());
		assertEquals(1, fileSystemFolder.getAllJavaFiles().size());

		String entry = "src/test/resources/spoon/test/api/Foo.java";
		FileSystemFile file = new FileSystemFile(new File(entry));

		// this file in Foo.java
		assertEquals(file, fileSystemFolder.getAllFiles().get(0));
	}

	@Test
	public void testVirtualFolder() throws Exception {
		String dir = "src/test/resources/spoon/test/api/";
		FileSystemFolder fileSystemFolder = new FileSystemFolder(new File(dir));

		String dir2 = "src/test/resources/spoon/test/exceptions/";
		FileSystemFolder fileSystemFolder2 = new FileSystemFolder(new File(dir2));

		SpoonFolder folder = new VirtualFolder();
		folder.addFolder(fileSystemFolder);
		folder.addFolder(fileSystemFolder2);

		assertEquals(3, folder.getAllFiles().size());

		// the README is not a Java file
		assertEquals(2, folder.getAllJavaFiles().size());
	}

	@Test
	public void testFilteringFolder() throws Exception {
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
