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
package spoon.test.processing;

import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.processing.AbstractManualProcessor;
import spoon.processing.AbstractProcessor;
import spoon.processing.Processor;
import spoon.processing.ProcessorProperties;
import spoon.processing.ProcessorPropertiesImpl;
import spoon.processing.Property;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import spoon.test.processing.processors.RenameProcessor;
import spoon.test.processing.processors.CtClassProcessor;
import spoon.test.processing.processors.CtInterfaceProcessor;
import spoon.test.processing.processors.CtTypeProcessor;
import spoon.testing.utils.ProcessorUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

public class ProcessingTest {

	@Test
	public void testInsertBegin() throws Exception {
		CtClass<?> type = build("spoon.test.processing.testclasses", "SampleForInsertBefore");
		for (CtMethod<?> meth : type.getMethods()) {
			int i = meth.getBody().getStatements().size();
			meth.getBody().insertBegin(type.getFactory().Code()
					.createCodeSnippetStatement("int i = 0;"));
			assertEquals("insert failed for method " + meth.getSimpleName(),
					i + 1, meth.getBody().getStatements().size());
			assertEquals("insert failed for method " + meth.getSimpleName(),
					"int i = 0;", meth.getBody().getStatement(0).toString());
		}
		for (CtConstructor<?> constructor : type.getConstructors()) {
			int i = constructor.getBody().getStatements().size();
			constructor.getBody().insertBegin(type.getFactory().Code()
					.createCodeSnippetStatement("int i = 0;"));
			assertEquals("insert failed for constructor " + constructor.getSimpleName(),
					i + 1,
					constructor.getBody().getStatements().size());
			assertEquals("insert failed for constructor " + constructor.getSimpleName(),
					"int i = 0;",
					constructor.getBody().getStatement(1).toString());
		}

		CtConstructor<?> constructor = type.getConstructor(type.getFactory().Type().INTEGER_PRIMITIVE);
		String myBeforeStatementAsString = "int before";
		for (CtSwitch<?> ctSwitch : constructor.getElements(new TypeFilter<CtSwitch<?>>(CtSwitch.class))) {
			ctSwitch.insertBefore(type.getFactory().Code()
					.createCodeSnippetStatement(myBeforeStatementAsString));
		}
		assertEquals("insert has not been done at the right position", myBeforeStatementAsString, constructor.getBody().getStatement(3).toString());
		assertEquals("insert has not been done at the right position", myBeforeStatementAsString, constructor.getBody().getStatement(5).toString());
		assertEquals("insert has not been done at the right position", myBeforeStatementAsString, constructor.getBody().getStatement(7).toString());

		assertNotEquals("switch should not be the same", constructor.getBody().getStatement(6), constructor.getBody().getStatement(8));
		assertNotEquals("switch should not be the same", constructor.getBody().getStatement(6).toString(), constructor.getBody().getStatement(8).toString());

	}

	@Test
	public void testInsertEnd() throws Exception {
		CtClass<?> type = build("spoon.test.processing.testclasses", "SampleForInsertBefore");
		for (CtMethod<?> meth : type.getMethods()) {
			int i = meth.getBody().getStatements().size();
			meth.getBody().insertEnd(type.getFactory().Code()
					.createCodeSnippetStatement("int i = 0"));
			assertEquals("insert failed for method " + meth.getSimpleName(),
					i + 1, meth.getBody().getStatements().size());
			assertEquals("insert failed for method " + meth.getSimpleName(),
					"int i = 0", meth.getBody().getStatement(meth.getBody().getStatements().size() - 1).toString());
		}
		for (CtConstructor<?> constructor : type.getConstructors()) {
			int i = constructor.getBody().getStatements().size();
			constructor.getBody().insertEnd(type.getFactory().Code()
					.createCodeSnippetStatement("int i = 0"));
			assertEquals("insert failed for constructor " + constructor.getSimpleName(),
					i + 1,
					constructor.getBody().getStatements().size());
			assertEquals("insert failed for constructor",
					"int i = 0",
					constructor.getBody().getStatement(constructor.getBody().getStatements().size() - 1).toString());
		}

		CtConstructor<?> constructor = type.getConstructor(type.getFactory().Type().INTEGER_PRIMITIVE);
		String myBeforeStatementAsString = "int after";
		for (CtSwitch<?> ctSwitch : constructor.getElements(new TypeFilter<CtSwitch<?>>(CtSwitch.class))) {
			ctSwitch.insertAfter(type.getFactory().Code()
					.createCodeSnippetStatement(myBeforeStatementAsString));
		}
		assertEquals("insert has not been done at the right position", myBeforeStatementAsString, constructor.getBody().getStatement(3).toString());
		assertEquals("insert has not been done at the right position", myBeforeStatementAsString, constructor.getBody().getStatement(5).toString());
		assertEquals("insert has not been done at the right position", myBeforeStatementAsString, constructor.getBody().getStatement(7).toString());

		assertNotEquals("switch should not be the same", constructor.getBody().getStatement(6), constructor.getBody().getStatement(8));
		assertNotEquals("switch should not be the same", constructor.getBody().getStatement(6).toString(), constructor.getBody().getStatement(8).toString());

	}

	@Test
	public void testProcessorNotFoundThrowAnException() {
		try {
			new Launcher().run(new String[]{
					"-p", "fr.inria.gforge.spoon.MakeAnAwesomeTacosProcessor"
			});
			fail("The processor doesn't exist. We must throw an exception.");
		} catch (SpoonException ignore) {
		}
	}

	//toy class for the next test
	class WrongProcessor extends AbstractProcessor<CtElement> {
		public WrongProcessor(int myParameter) {

		}

		@Override
		public void process(CtElement element) {
			System.out.println(element);
		}
	}

	@Test
	public void testProcessorWithNoArgumentsInConstructor() {

		/* throw correctly an exception when trying to use a processor with constructor with args */

		Launcher l = new Launcher();
		l.getEnvironment().setLevel(Level.ERROR.toString());
		l.buildModel();
		try {
			new JDTBasedSpoonCompiler(l.getFactory()).instantiateAndProcess(Collections.singletonList("spoon.test.processing.ProcessingTest$WrongProcessor"));
			fail();
		} catch (SpoonException e) {
			assertTrue(e.getMessage().startsWith("Unable to instantiate processor"));
			assertTrue(e.getMessage().endsWith("Your processor should have a constructor with no arguments"));
			assertTrue(e.getCause() instanceof java.lang.InstantiationException);// we are able to retrieve the exception parent
		}
	}

	@Test
	public void testInitProperties() {
		class AProcessor extends AbstractManualProcessor {
			@Property
			String aString;

			@Property
			int anInt;

			@Property
			Object anObject;

			@Property
			int[] arrayInt;

			@Property
			List<String> listString;

			@Property
			boolean[] arrayBoolean;

			@Property
			Map<String,Double> mapStringDouble;

			@Override
			public void process() {

			}
		}

		AProcessor p = new AProcessor();
		Launcher launcher = new Launcher();
		p.setFactory(launcher.getFactory());

		ProcessorProperties props = new ProcessorPropertiesImpl();
		props.set("aString", "foo");
		props.set("anInt", 5);
		Object o = new Object();
		props.set("anObject", o);

		int[] arrayInt = { 1, 2, 3};
		props.set("arrayInt", arrayInt);
		props.set("listString", Arrays.asList(new String[]{"42"}));

		boolean[] arrayBoolean = { true };
		props.set("arrayBoolean", arrayBoolean);
		HashMap<String,Double> mapTest = new HashMap<>();
		mapTest.put("foobar",42.42);
		props.set("mapStringDouble", mapTest);

		ProcessorUtils.initProperties(p, props);

		assertEquals("foo", p.aString);
		assertEquals(5, p.anInt);
		assertSame(o, p.anObject);
		assertSame(arrayInt, p.arrayInt);
		assertEquals(Arrays.asList(new String[]{"42"}), p.listString);
		assertSame(arrayBoolean, p.arrayBoolean);
		assertSame(mapTest, p.mapStringDouble);
	}

	@Test
	public void testInitPropertiesWithWrongType() {
		class AProcessor extends AbstractManualProcessor {
			@Property
			String aString;

			@Property
			int anInt;

			@Property
			Object anObject;

			@Override
			public void process() {

			}
		}

		AProcessor p = new AProcessor();
		Launcher launcher = new Launcher();
		p.setFactory(launcher.getFactory());

		ProcessorProperties props = new ProcessorPropertiesImpl();
		props.set("aString", "foo");
		props.set("anObject", "foo");
		props.set("anInt", "foo");

		try {
			ProcessorUtils.initProperties(p, props);
			fail();
		} catch (SpoonException e) {
			assertTrue(e.getMessage().contains("anInt"));
		}


		assertEquals("foo", p.aString);
		assertEquals(0, p.anInt);
		assertNull(p.anObject);
	}

	@Test
	public void testInitPropertiesWithStringType() {
		class AProcessor extends AbstractManualProcessor {
			@Property
			String aString;

			@Property
			int anInt;

			@Property
			Object anObject;

			@Property
			int[] arrayInt;

			@Property
			List<String> listString;

			@Property
			boolean[] arrayBoolean;

			@Property
			Map<String,Double> mapStringDouble;

			@Override
			public void process() {

			}
		}

		AProcessor p = new AProcessor();
		Launcher launcher = new Launcher();
		p.setFactory(launcher.getFactory());

		ProcessorProperties props = new ProcessorPropertiesImpl();
		props.set("aString", "foo");
		props.set("anInt", "42");
		props.set("anObject", "{}");
		props.set("arrayInt", "[42,43]");
		props.set("listString", "[\"foo\", \"bar\"]");
		props.set("arrayBoolean", "[true]");
		props.set("mapStringDouble","{\"foo\": 10.21, \"bar\": 14.42}");

		ProcessorUtils.initProperties(p, props);

		assertEquals("foo", p.aString);
		assertEquals(42, p.anInt);
		assertNotNull(p.anObject);

		assertArrayEquals(new int[] {42, 43}, p.arrayInt);
		assertEquals(Arrays.asList(new String[]{"foo", "bar"}), p.listString);
		assertArrayEquals(new boolean[]{true}, p.arrayBoolean);
		Map<String, Double> mamap = new HashMap<>();
		mamap.put("foo", 10.21);
		mamap.put("bar", 14.42);
		assertEquals(mamap, p.mapStringDouble);
	}

	@Test
	public void testProcessorWithGenericType() {
		// contract: we can use generic type for another abstract processor

		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses");

		CtClassProcessor classProcessor = new CtClassProcessor();
		spoon.addProcessor(classProcessor);

		spoon.run();

		assertFalse(classProcessor.elements.isEmpty());

		for (CtType type : classProcessor.elements) {
			assertTrue("Type "+type.getSimpleName()+" is not a class", type instanceof CtClass);
		}
	}

	@Test
	public void testCallProcessorWithMultipleTypes() {
		// contract: when calling a processor capable of treating CtClass and another capable of treating CtType, they are called on the right types

		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses");

		CtClassProcessor classProcessor = new CtClassProcessor();
		spoon.addProcessor(classProcessor);

		CtTypeProcessor typeProcessor = new CtTypeProcessor();
		spoon.addProcessor(typeProcessor);

		CtInterfaceProcessor interfaceProcessor = new CtInterfaceProcessor();
		spoon.addProcessor(interfaceProcessor);

		spoon.run();

		assertFalse(classProcessor.elements.isEmpty());

		for (CtType type : classProcessor.elements) {
			assertTrue("Type "+type.getSimpleName()+" is not a class", type instanceof CtClass);
		}

		assertFalse(classProcessor.elements.isEmpty());

		for (CtType type : interfaceProcessor.elements) {
			assertTrue("Type "+type.getSimpleName()+" is not an interface", type instanceof CtInterface);
		}

		assertFalse(typeProcessor.elements.isEmpty());

		for (CtType type : typeProcessor.elements) {
			if (type instanceof CtClass) {
				assertTrue(classProcessor.elements.contains(type));
				assertFalse(interfaceProcessor.elements.contains(type));
			} else if (type instanceof CtInterface){
				assertFalse(classProcessor.elements.contains(type));
				assertTrue(interfaceProcessor.elements.contains(type));
			} else {
				assertFalse(classProcessor.elements.contains(type));
				assertFalse(interfaceProcessor.elements.contains(type));
			}
		}
	}

	@Test
	public void testProcessDontMessWithImports() throws IOException {
		// contract: after processing the imports are properly computed
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(false);
		launcher.getEnvironment().setAutoImports(true);
		launcher.addInputResource("src/test/resources/spoon/test/processor/test");
		File tempDir = new File("target/testprocess"); //Files.createTempDir();
		launcher.setSourceOutputDirectory(tempDir);
		launcher.addProcessor(new RenameProcessor("A", "D"));
		launcher.run();

		File Dfile = new File(tempDir, "spoon/test/processing/testclasses/test/sub/D.java");
		assertTrue(Dfile.exists());

		File Bfile = new File(tempDir, "spoon/test/processing/testclasses/test/B.java");
		assertTrue(Bfile.exists());

		String fileContent = StringUtils.join(Files.readLines(Bfile, Charset.defaultCharset()), "\n");

		assertFalse(fileContent.contains("import spoon.test.processing.testclasses.test.sub.A;"));
		assertTrue(fileContent.contains("import spoon.test.processing.testclasses.test.sub.D;"));
		assertTrue(fileContent.contains("private D a = new D();"));
	}

	@Test
	public void testNullableSettingForProcessor() throws IOException {
		//contract: nullable( = notNullable == false) properties with a null value must not throw a exception.
		class AProcessor extends AbstractProcessor<CtElement> {
			@Property(notNullable = false)
			String aString = null;

			@Property
			int anInt;

			@Property
			Object anObject;

			@Property
			int[] arrayInt;

			@Override
			public void process(CtElement element) {
			}
		}
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/spoon/test/processor/test");
		Processor<CtElement> processor = new AProcessor();
		processor.setFactory(launcher.getFactory());
		ProcessorProperties props = new ProcessorPropertiesImpl();

		props.set("aString", null);
		props.set("anInt", "42");
		props.set("anObject", null);
		props.set("arrayInt", "[42,44]");

		ProcessorUtils.initProperties(processor, props);
		launcher.addProcessor(processor);
		launcher.run();
		int warnings = launcher.getEnvironment().getWarningCount();
		assertTrue(warnings == 0);
	}

	@Test
	public void testNullableSettingForProcessor2() throws IOException {
		//contract: notNullable( = notNullable == true) properties with a null value must throw a spoonexception.

		class AProcessor extends AbstractProcessor<CtElement> {
			@Property(notNullable = true)
			String aString = null;

			@Property(notNullable = true)
			int anInt;

			@Property(notNullable = true)
			Object anObject;

			@Property(notNullable = true)
			int[] arrayInt;

			@Override
			public void process(CtElement element) {
			}
		}
		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/spoon/test/processor/test");
		Processor<CtElement> processor = new AProcessor();
		processor.setFactory(launcher.getFactory());
		ProcessorProperties props = new ProcessorPropertiesImpl();

		props.set("aString", null);
		props.set("anInt", "42");
		props.set("anObject", null);
		props.set("arrayInt", "[42,43]");

		assertThrows(SpoonException.class, () -> ProcessorUtils.initProperties(processor, props));
	}
}
