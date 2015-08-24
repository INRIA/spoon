package spoon.test.trycatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.test.TestUtils.build;
import static spoon.test.TestUtils.createFactory;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTryWithResource;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.trycatch.testclasses.Foo;

public class TryCatchTest {

	@Test
	public void testModelBuildingInitializer() throws Exception {
		CtClass<Main> type = build("spoon.test.trycatch", "Main");
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
		CtTry tryStmt = (CtTry) clazz.getElements(new TypeFilter<>(CtTry.class)).get(
				0);
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
		CtTry tryStmt = (CtTry) clazz.getElements(new TypeFilter<>(CtTry.class)).get(
				0);

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
		CtTry tryStmt = (CtTry) clazz.getElements(new TypeFilter<>(CtTry.class)).get(0);
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
		CtClass<?> clazz = build("spoon.test.trycatch", "RethrowingClass");

		CtMethod<?> method = (CtMethod<?>) clazz.getMethods().toArray()[0];
		Set<CtTypeReference<? extends Throwable>> thrownTypes = method
				.getThrownTypes();

		// Checks we throw 2 exceptions and not one.
		assertEquals(2, thrownTypes.size());

		CtTry ctTry = clazz.getElements(new TypeFilter<CtTry>(CtTry.class))
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
		CtClass<?> clazz = build("spoon.test.trycatch", "TryCatchResourceClass");

		CtMethod<?> method = clazz.getMethodsByName("readFirstLineFromFile").get(0);
		CtTryWithResource ctTryWithResource = method.getElements(
				new TypeFilter<CtTryWithResource>(CtTryWithResource.class)).get(0);

		// Checks try has only one resource.
		assertTrue(ctTryWithResource.getResources().size() == 1);
	}

	@Test
	public void testTryWithResources() throws Exception {
		CtClass<?> clazz = build("spoon.test.trycatch", "TryCatchResourceClass");

		CtMethod<?> method = clazz.getMethodsByName("writeToFileZipFileContents").get(0);
		CtTryWithResource ctTryWithResource = method.getElements(
				new TypeFilter<CtTryWithResource>(CtTryWithResource.class)).get(0);

		// Checks try has more than one resource.
		assertTrue(ctTryWithResource.getResources().size() > 1);
	}

	@Test
	public void testMultiTryCatchWithCustomExceptions() throws Exception {
		final Launcher launcher = new Launcher();
		final SpoonCompiler compiler = launcher.createCompiler();
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

		final String expected = " catch (spoon.test.trycatch.testclasses.internal.MyException | spoon.test.trycatch.testclasses.internal.MyException2 ignore) {" + System.lineSeparator() + "}";
		assertEquals(expected, ctCatch.toString());
	}

	@Test
	public void testCompileMultiTryCatchWithCustomExceptions() throws Exception {
		spoon.Launcher.main(new String[] {
				"-i", "src/test/java/spoon/test/trycatch/testclasses",
				"-o", "target/spooned",
				"--destination","target/spooned-build",
				"--compile"
		});

		final Launcher launcher = new Launcher();
		final SpoonCompiler newCompiler = launcher.createCompiler();
		newCompiler.addInputSource(new File("./target/spooned/spoon/test/trycatch/testclasses/"));
		try {
			assertTrue(newCompiler.build());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
