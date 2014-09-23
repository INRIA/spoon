package spoon.test.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import spoon.compiler.SpoonFolder;
import spoon.support.compiler.FileSystemFile;
import spoon.support.compiler.FileSystemFolder;
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

}
