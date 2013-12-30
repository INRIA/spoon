package spoon.test.exceptions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

public class ExceptionTest {
	Factory factory = new Factory(new DefaultCoreFactory(),
			new StandardEnvironment());

	@Test
	public void testCatchOrder() {
		// test the order of the model
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {"
								+ " try{}catch(RuntimeException e){}catch(Exception e){}" + "}"
								+ "};").compile();
		CtTry tryStmt = (CtTry) clazz.getElements(new TypeFilter<>(CtTry.class)).get(0);

		// the first caught exception is RuntimeException
		assertEquals(
				RuntimeException.class,
				tryStmt.getCatchers().get(0).getParameter().getType().getActualClass());
		
		assertEquals(
				Exception.class,
				tryStmt.getCatchers().get(1).getParameter().getType().getActualClass());
	}

}
