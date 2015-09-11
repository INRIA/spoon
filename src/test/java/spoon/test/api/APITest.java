package spoon.test.api;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.support.JavaOutputProcessor;
import spoon.test.api.testclasses.Bar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class APITest {

	@Test
	public void testBasicAPIUsage() throws Exception {
		// this test shows a basic usage of the Launcher API without command line
		// and asserts there is no exception
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/test/resources/spoon/test/api");
		spoon.setSourceOutputDirectory("target/spooned");
		spoon.run();
		Factory factory = spoon.getFactory();
		for (CtPackage p : factory.Package().getAll()) {
			spoon.getEnvironment().debugMessage("package: " + p.getQualifiedName());
		}
		for (CtType<?> s : factory.Class().getAll()) {
			spoon.getEnvironment().debugMessage("class: "+s.getQualifiedName());
		}
	}

	@Test
	public void testOverrideOutputWriter() throws Exception {
		// this test that we can correctly set the Java output processor
		final List<Object> l = new ArrayList<Object>();
		Launcher spoon = new Launcher() {
			@Override
			public JavaOutputProcessor createOutputWriter(File sourceOutputDir, Environment environment) {
				return new JavaOutputProcessor() {
					@Override
					public void process(CtType<?> e) {
						l.add(e);
					}
					@Override
					public void init() {
						// we do nothing
					}

				};
			}

		};
		spoon.run(new String[] {
				"-i", "src/test/resources/spoon/test/api/",
				"-o", "target/spooned/apitest"
		});
		Assert.assertEquals(2, l.size());
	}

	@Test
	public void testDuplicateEntry() throws Exception {
		// it's possible to pass twice the same file as parameter
		// the virtual folder removes the duplicate before passing to JDT
		try {
			String duplicateEntry = "src/test/resources/spoon/test/api/Foo.java";

			// check on the JDK API
			// this is later use by FileSystemFile
			assertTrue(new File(duplicateEntry).getCanonicalFile().equals(new File("./"+duplicateEntry).getCanonicalFile()));

			Launcher.main(new String[] {
					"-i",
					// note the nasty ./
					duplicateEntry + File.pathSeparator + "./" + duplicateEntry,
					"-o", "target/spooned/apitest" });
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
			String duplicateEntry = "src/test/resources/spoon/test/api/";
			Launcher.main(new String[] {
					"-i",
					duplicateEntry + File.pathSeparator + "./" + duplicateEntry,
					"-o", "target/spooned/apitest" });
		} catch (IllegalArgumentException e) // from JDT
		{
			fail();
		}
	}

	@Test
	public void testDuplicateFilePlusFolder() throws Exception {
		// more complex case: a file is given, together with the enclosing folder
		try {
			Launcher.main(new String[] {
					"-i",
					"src/test/resources/spoon/test/api/" + File.pathSeparator
							+ "src/test/resources/spoon/test/api/Foo.java",
					"-o", "target/spooned/apitest" });
		} catch (IllegalArgumentException e) // from JDT
		{
			fail();
		}
	}

	@Test(expected=Exception.class)
	public void testNotValidInput() throws Exception {
		String invalidEntry = "does/not/exists//Foo.java";
		Launcher.main(new String[] { "-i",
				invalidEntry,
				"-o",
				"target/spooned/apitest" });
	}

	@Test
	public void testAddProcessorMethodInSpoonAPI() throws Exception {
		final SpoonAPI launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/api/testclasses");
		launcher.setSourceOutputDirectory("./target/spooned");
		final AwesomeProcessor processor = new AwesomeProcessor();
		launcher.addProcessor(processor);
		launcher.run();

		assertEquals(1, processor.getElements().size());
		final CtClass<Bar> actual = processor.getElements().get(0);
		assertEquals(2, actual.getMethods().size());
		assertNotNull(actual.getMethodsByName("prepareMojito").get(0));
		assertNotNull(actual.getMethodsByName("makeMojito").get(0));
	}

	@Test
	public void testOutputOfSpoon() throws Exception {
		final File sourceOutput = new File("./target/spoon/test/output/");
		final SpoonAPI launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/api/testclasses");
		launcher.setSourceOutputDirectory(sourceOutput);
		launcher.run();

		assertTrue(sourceOutput.exists());
	}

	@Test
	public void testDestinationOfSpoon() throws Exception {
		final File binaryOutput = new File("./target/spoon/test/binary/");
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setShouldCompile(true);
		launcher.addInputResource("./src/test/java/spoon/test/api/testclasses");
		launcher.setSourceOutputDirectory("./target/spooned");
		launcher.setBinaryOutputDirectory(binaryOutput);
		launcher.run();

		assertTrue(binaryOutput.exists());
	}

	@Test
	public void testPrintNotAllSourcesWithFilter() throws Exception {
		final File target = new File("./target/print-not-all/default");
		final SpoonAPI launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/main/java");
		launcher.setSourceOutputDirectory(target);
		launcher.setOutputFilter(new AbstractFilter<CtType<?>>(CtType.class) {
			@Override
			public boolean matches(CtType<?> element) {
				return "spoon.Launcher".equals(element.getQualifiedName())
						|| "spoon.template.AbstractTemplate".equals(element.getQualifiedName());
			}
		});
		launcher.run();

		List<File> list = new ArrayList<>(FileUtils.listFiles(target, new String[] {"java"}, true));
		final List<String> filesName = list.stream().map(File::getName).sorted().collect(Collectors.<String>toList());

		assertEquals(2, filesName.size());
		assertEquals("AbstractTemplate.java", filesName.get(0));
		assertEquals("Launcher.java", filesName.get(1));
	}

	@Test
	public void testPrintNotAllSourcesWithNames() throws Exception {
		final File target = new File("./target/print-not-all/array");
		final SpoonAPI launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/main/java");
		launcher.setSourceOutputDirectory(target);
		launcher.setOutputFilter("spoon.Launcher", "spoon.template.AbstractTemplate");
		launcher.run();

		List<File> list = new ArrayList<>(FileUtils.listFiles(target, new String[] {"java"}, true));
		final List<String> filesName = list.stream().map(File::getName).sorted().collect(Collectors.<String>toList());

		assertEquals(2, filesName.size());
		assertEquals("AbstractTemplate.java", filesName.get(0));
		assertEquals("Launcher.java", filesName.get(1));
	}

	@Test
	public void testPrintNotAllSourcesInCommandLine() throws Exception {
		final File target = new File("./target/print-not-all/command");
		final SpoonAPI launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/main/java", //
				"-o", "./target/print-not-all/command", //
				"-f", "spoon.Launcher:spoon.template.AbstractTemplate", //
				"--noclasspath"
		});

		List<File> list = new ArrayList<>(FileUtils.listFiles(target, new String[] {"java"}, true));
		final List<String> filesName = list.stream().map(File::getName).sorted().collect(Collectors.<String>toList());

		assertEquals(2, filesName.size());
		assertEquals("AbstractTemplate.java", filesName.get(0));
		assertEquals("Launcher.java", filesName.get(1));
	}
}
