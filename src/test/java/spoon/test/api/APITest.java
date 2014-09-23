package spoon.test.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtSimpleType;
import spoon.support.JavaOutputProcessor;

public class APITest {

	@Test
	public void testOverrideOutputWriter() throws Exception {
		// this test that we can correctly set the Java output processor
		final List<Object> l = new ArrayList<>();
		Launcher spoon = new Launcher() {
			@Override
			public JavaOutputProcessor createOutputWriter(File sourceOutputDir) {				
				return new JavaOutputProcessor() { 
					@Override
					public void process(CtSimpleType<?> e) {
						l.add(e);
					}
					@Override
					public void init() {
						// we do nothing
					}

				};
			}
			
		};
		spoon.setArgs(new String[] {
						"-i", "src/test/resources/spoon/test/api/",
						"-o","target/spooned-apitest"
						});
		spoon.run();
		Assert.assertEquals(2, l.size());
	}
	
	@Test
	public void testDuplicateEntry() throws Exception {
		// it's possible to pass twice the same file as parameter
		// the virtual folder removes the duplicate before passing to JDT
		try {
			Launcher spoon = new Launcher();
			String duplicateEntry = "src/test/resources/spoon/test/api/Foo.java";
			
			// check on the JDK API
			// this is later use by FileSystemFile
			assertTrue(new File(duplicateEntry).getCanonicalFile().equals(new File("./"+duplicateEntry).getCanonicalFile()));
			
			spoon.setArgs(new String[] {
					"-i",
					// note the nasty ./
					duplicateEntry+":"+"./"+duplicateEntry,
					"-o", "target/spooned-apitest" });
			spoon.run();
		} catch (IllegalArgumentException e) // from JDT
		{
			fail();
		}
	}
	
	@Test
	public void testDuplicateFolder() throws Exception { 
		// it's possible to pass twice the same folder as parameter
		// the virtual folder removes the duplicate before passing to JDT
		try {
			Launcher spoon = new Launcher();
			String duplicateEntry = "src/test/resources/spoon/test/api/";
			spoon.setArgs(new String[] {
					"-i",
					duplicateEntry+":"+"./"+duplicateEntry,
					"-o", "target/spooned-apitest" });
			spoon.run();
		} catch (IllegalArgumentException e) // from JDT
		{
			fail();
		}
	}

	@Test
	public void testDuplicateFilePlusFolder() throws Exception {
		// more complex case: a file is given, together with the enclosing folder
		try {
			Launcher spoon = new Launcher();
			spoon.setArgs(new String[] {
					"-i",
					"src/test/resources/spoon/test/api/"+":"+"src/test/resources/spoon/test/api/Foo.java",
					"-o", "target/spooned-apitest" });
			spoon.run();
		} catch (IllegalArgumentException e) // from JDT
		{
			fail();
		}
	}
	
	@Test(expected=FileNotFoundException.class)
	public void testNotValidInput() throws Exception {
		Launcher spoon = new Launcher();
		String invalidEntry = "does/not/exists//Foo.java";
		spoon.setArgs(new String[] { "-i",
				invalidEntry, 
				"-o",
				"target/spooned-apitest" });
		spoon.run();
	}

}
