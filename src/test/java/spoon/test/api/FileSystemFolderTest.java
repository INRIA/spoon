package spoon.test.api;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExpectedException;
import spoon.Launcher;
import spoon.LauncherTest;
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
			Throwable containedException = spe.getCause().getCause();
			assertTrue(containedException instanceof FileNotFoundException);
		}
	}
}
