package spoon.test.trycatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.TestUtils;

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
	
	Factory factory = TestUtils.createFactory();

	@Test
	public void testFullyQualifiedException() {
		// test the order of the model
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {"
								+ " try{}catch(java.lang.RuntimeException e){}"
								+ "}};").compile();
		CtTry tryStmt = (CtTry) clazz.getElements(new TypeFilter<>(CtTry.class)).get(0);
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
		CtTry tryStmt = (CtTry) clazz.getElements(new TypeFilter<>(CtTry.class)).get(0);

		System.out.println(tryStmt.toString());

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
		CtTry tryStmt = (CtTry) clazz.getElements(new TypeFilter<>(CtTry.class)).get(0);

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



}
