package spoon.test.api;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

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
}
