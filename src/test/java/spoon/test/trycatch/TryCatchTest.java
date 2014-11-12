package spoon.test.trycatch;

import static org.junit.Assert.*;
import static spoon.test.TestUtils.*;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.TestUtils;

import java.util.Set;

public class TryCatchTest {

	@Test
	public void testModelBuildingInitializer() throws Exception {
		CtClass<Main> type = build ("spoon.test.trycatch", "Main");
		assertEquals("Main", type.getSimpleName());

		CtMethod<Void> m = type.getMethod("test");
		assertNotNull(m);
		assertEquals(2, m.getBody().getStatements().size());
		assertTrue(m.getBody().getStatements().get(0) instanceof CtTry);
		assertTrue(m.getBody().getStatements().get(1) instanceof CtTry);
		CtTry t1 = m.getBody().getStatement(0);
		assertTrue(t1.getResources().isEmpty());
		CtTry t2 = m.getBody().getStatement(1);
		assertNotNull(t2.getResources());

	}

	Factory factory = createFactory();

	@Test
	public void testFullyQualifiedException() {
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
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {"
								+ " try{}catch(RuntimeException | Error e){System.exit(0);}" + "}"
								+ "};").compile();
		CtTry tryStmt = (CtTry) clazz.getElements(new TypeFilter<>(CtTry.class)).get(
				0);

		assertEquals(2, tryStmt.getCatchers().size());

		assertEquals(
				RuntimeException.class,
				tryStmt.getCatchers().get(0).getParameter().getType().getActualClass());

		assertEquals(
				Error.class,
				tryStmt.getCatchers().get(1).getParameter().getType().getActualClass());

		// the code of the catch block is duplicated
		assertEquals("java.lang.System.exit(0)", tryStmt.getCatchers().get(0).getBody().getStatement(0).toString());
		assertEquals("java.lang.System.exit(0)", tryStmt.getCatchers().get(1).getBody().getStatement(0).toString());
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

		Class<? extends CtLocalVariableReference> exceptionClass = ctTry
				.getCatchers().get(0).getParameter().getReference().getClass();

		// Checks the exception in the catch isn't on the signature of the method.
		for (CtTypeReference<? extends Throwable> thrownType : thrownTypes) {
			assertNotEquals(thrownType.getClass(), exceptionClass);
		}
	}


}
