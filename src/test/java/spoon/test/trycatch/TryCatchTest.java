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
package spoon.test.trycatch;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.CtExtendedModifier;
import spoon.test.trycatch.testclasses.Foo;
import spoon.test.trycatch.testclasses.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;

public class TryCatchTest {

	@Test
	public void testModelBuildingInitializer() throws Exception {
		CtClass<Main> type = build("spoon.test.trycatch.testclasses", "Main");
		assertEquals("Main", type.getSimpleName());

		CtMethod<Void> m = type.getMethod("test");
		assertNotNull(m);
		assertEquals(2, m.getBody().getStatements().size());
		assertTrue(m.getBody().getStatements().get(0) instanceof CtTry);
		assertTrue(m.getBody().getStatements().get(1) instanceof CtTryWithResource);
		CtTryWithResource t2 = m.getBody().getStatement(1);
		assertNotNull(t2.getResources());

	}

	@Test
	public void testFullyQualifiedException() {
		Factory factory = createFactory();
		// test the order of the model
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {"
								+ " try{}catch(java.lang.RuntimeException e){}"
								+ "}};").compile();
		CtTry tryStmt = clazz.getElements(new TypeFilter<>(CtTry.class)).get(0);
		assertEquals(1, tryStmt.getCatchers().size());
	}

	@Test
	public void testCatchOrder() {
		Factory factory = createFactory();
		// test the order of the model
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {"
								+ " try{}catch(RuntimeException e){java.lang.System.exit(0);}"
								+ "      catch(Exception e){}" + "}"
								+ "};").compile();
		CtTry tryStmt = clazz.getElements(new TypeFilter<>(CtTry.class)).get(0);

		// the first caught exception is RuntimeException
		assertEquals(
				RuntimeException.class,
				tryStmt.getCatchers().get(0).getParameter().getType().getActualClass());
		assertEquals("java.lang.System.exit(0)", tryStmt.getCatchers().get(0).getBody().getStatement(0).toString());

		assertEquals(
				Exception.class,
				tryStmt.getCatchers().get(1).getParameter().getType().getActualClass());
	}

	@Test
	public void testExceptionJava7() {
		Factory factory = createFactory();
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {"
								+ " try{}catch(RuntimeException | Error e){System.exit(0);}" + "}"
								+ "};").compile();
		CtTry tryStmt = clazz.getElements(new TypeFilter<>(CtTry.class)).get(0);
		List<CtCatch> catchers = tryStmt.getCatchers();
		assertEquals(1, catchers.size());

		assertEquals(
				Throwable.class,
				catchers.get(0).getParameter().getType().getActualClass());

		assertEquals(2, catchers.get(0).getParameter().getMultiTypes().size());

		assertEquals(
				RuntimeException.class,
				catchers.get(0).getParameter().getMultiTypes().get(0).getActualClass());
		assertEquals(
				Error.class,
				catchers.get(0).getParameter().getMultiTypes().get(1).getActualClass());

		// the code of the catch block is duplicated
		assertEquals("java.lang.System.exit(0)", catchers.get(0).getBody().getStatement(0).toString());
	}

	@Test
	public void testRethrowingExceptionsJava7() throws Exception {
		CtClass<?> clazz = build("spoon.test.trycatch.testclasses", "RethrowingClass");

		CtMethod<?> method = (CtMethod<?>) clazz.getMethods().toArray()[0];
		Set<CtTypeReference<? extends Throwable>> thrownTypes = method
				.getThrownTypes();

		// Checks we throw 2 exceptions and not one.
		assertEquals(2, thrownTypes.size());

		CtTry ctTry = clazz.getElements(new TypeFilter<>(CtTry.class))
				.get(0);

		Class<? extends CtCatchVariableReference> exceptionClass = ctTry
				.getCatchers().get(0).getParameter().getReference().getClass();

		// Checks the exception in the catch isn't on the signature of the method.
		for (CtTypeReference<? extends Throwable> thrownType : thrownTypes) {
			assertNotEquals(thrownType.getClass(), exceptionClass);
		}
	}

	@Test
	public void testTryWithOneResource() throws Exception {
		CtClass<?> clazz = build("spoon.test.trycatch.testclasses", "TryCatchResourceClass");

		CtMethod<?> method = clazz.getMethodsByName("readFirstLineFromFile").get(0);
		CtTryWithResource ctTryWithResource = method.getElements(
				new TypeFilter<>(CtTryWithResource.class)).get(0);

		// Checks try has only one resource.
		assertTrue(ctTryWithResource.getResources().size() == 1);
	}

	@Test
	public void testTryWithResources() throws Exception {
		CtClass<?> clazz = build("spoon.test.trycatch.testclasses", "TryCatchResourceClass");

		CtMethod<?> method = clazz.getMethodsByName("writeToFileZipFileContents").get(0);
		CtTryWithResource ctTryWithResource = method.getElements(
				new TypeFilter<>(CtTryWithResource.class)).get(0);

		// Checks try has more than one resource.
		assertTrue(ctTryWithResource.getResources().size() > 1);
	}

	@Test
	public void testMultiTryCatchWithCustomExceptions() {
		final Launcher launcher = new Launcher();
		final SpoonModelBuilder compiler = launcher.createCompiler();
		compiler.addInputSource(new File("./src/test/java/spoon/test/trycatch/testclasses/"));
		compiler.build();
		Factory factory = compiler.getFactory();

		final CtClass<?> foo = (CtClass<?>) factory.Type().get(Foo.class);
		final CtCatch ctCatch = foo.getElements(new AbstractFilter<CtCatch>(CtCatch.class) {
			@Override
			public boolean matches(CtCatch element) {
				return true;
			}
		}).get(0);

		final String expected = "catch (spoon.test.trycatch.testclasses.internal.MyException | spoon.test.trycatch.testclasses.internal.MyException2 ignore) {" + System.lineSeparator() + "}";
		assertEquals(expected, ctCatch.toString());
	}

	@Test
	public void testCompileMultiTryCatchWithCustomExceptions() {
		spoon.Launcher.main(new String[] {
				"-i", "src/test/java/spoon/test/trycatch/testclasses",
				"-o", "target/spooned"
		});

		final Launcher launcher = new Launcher();
		final SpoonModelBuilder newCompiler = launcher.createCompiler();
		newCompiler.addInputSource(new File("./target/spooned/spoon/test/trycatch/testclasses/"));
		try {
			assertTrue(newCompiler.build());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void testTryCatchVariableGetType() {
		Factory factory = createFactory();
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {"
								+ " try{}catch(RuntimeException e){System.exit(0);}" + "}"
								+ "};").compile();
		CtTry tryStmt = clazz.getElements(new TypeFilter<>(CtTry.class)).get(0);
		List<CtCatch> catchers = tryStmt.getCatchers();
		assertEquals(1, catchers.size());

		CtCatchVariable<?> catchVariable = catchers.get(0).getParameter();

		assertEquals(
				RuntimeException.class,
				catchVariable.getType().getActualClass());

		assertEquals(1, catchVariable.getMultiTypes().size());

		assertEquals(
				RuntimeException.class,
				catchVariable.getMultiTypes().get(0).getActualClass());

		//contract: the manipulation with catch variable type is possible
		catchVariable.setType((CtTypeReference)factory.Type().createReference(IllegalArgumentException.class));
		assertEquals(IllegalArgumentException.class,catchVariable.getType().getActualClass());
		//contract setType influences multitypes
		assertEquals(1, catchVariable.getMultiTypes().size());
		assertEquals(IllegalArgumentException.class, catchVariable.getMultiTypes().get(0).getActualClass());

		catchVariable.setMultiTypes(Collections.singletonList(factory.Type().createReference(UnsupportedOperationException.class)));
		assertEquals(UnsupportedOperationException.class,catchVariable.getType().getActualClass());
		//contract setType influences multitypes
		assertEquals(1, catchVariable.getMultiTypes().size());
		assertEquals(UnsupportedOperationException.class, catchVariable.getMultiTypes().get(0).getActualClass());

		catchVariable.setMultiTypes(Arrays.asList(
				factory.Type().createReference(UnsupportedOperationException.class),
				factory.Type().createReference(IllegalArgumentException.class)
				));
		assertEquals(2, catchVariable.getMultiTypes().size());
		assertEquals(UnsupportedOperationException.class, catchVariable.getMultiTypes().get(0).getActualClass());
		assertEquals(IllegalArgumentException.class, catchVariable.getMultiTypes().get(1).getActualClass());

		//contract setMultiTypes influences types, which contains common super class of all multi types
		assertEquals(RuntimeException.class,catchVariable.getType().getActualClass());
	}

	@Test
	public void testCatchWithExplicitFinalVariable() throws IOException {
		// contract: explicit final modifier are possible in catch variables and should be kept when pretty-printing
		String inputResource = "./src/test/java/spoon/test/trycatch/testclasses/Bar.java";
		Launcher launcher = new Launcher();
		launcher.addInputResource(inputResource);
		launcher.getEnvironment().setComplianceLevel(5);
		launcher.buildModel();

		CtTry tryStmt = launcher.getModel().getElements(new TypeFilter<>(CtTry.class)).get(0);
		List<CtCatch> catchers = tryStmt.getCatchers();
		assertEquals(1, catchers.size());

		CtCatchVariable<?> catchVariable = catchers.get(0).getParameter();
		assertTrue(catchVariable.hasModifier(ModifierKind.FINAL));

		Set<CtExtendedModifier> extendedModifierSet = catchVariable.getExtendedModifiers();
		assertEquals(1, extendedModifierSet.size());

		assertEquals(new CtExtendedModifier(ModifierKind.FINAL, false), extendedModifierSet.iterator().next());

		launcher = new Launcher();
		launcher.addInputResource(inputResource);
		launcher.setSourceOutputDirectory("./target/spoon-trycatch");
		launcher.getEnvironment().setShouldCompile(true);
		launcher.getEnvironment().setComplianceLevel(5);
		launcher.run();

		File f = new File("target/spoon-trycatch/spoon/test/trycatch/testclasses/Bar.java");
		String content = StringUtils.join(Files.readAllLines(f.toPath()),"\n");

		assertTrue(content.contains("catch (final java.lang.Exception e)"));
	}

	@Test
	public void testCatchWithUnknownExceptions() {
		// contract: unknown exception type in multicatch should not cause IndexOutOfBoundsException
		String inputResource = "./src/test/resources/spoon/test/noclasspath/exceptions/Foo.java";
		Launcher launcher = new Launcher();
		launcher.addInputResource(inputResource);
		launcher.getEnvironment().setNoClasspath(true);
		CtModel model = launcher.buildModel();

		List<CtCatch> catches = model.getElements(new TypeFilter<>(CtCatch.class));
		assertNotNull(catches.get(0).getParameter().getType()); // catch with single UnknownException
		assertNull(catches.get(1).getParameter().getType()); // multicatch with UnknownException
		assertNull(catches.get(2).getParameter().getType()); // multicatch with UnknownException
	}
}
