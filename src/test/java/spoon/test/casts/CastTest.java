package spoon.test.casts;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

public class CastTest {
	Factory factory = new Factory(new DefaultCoreFactory(),
			new StandardEnvironment());

	@Test
	public void testCast1() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {"
								+ " String x=(String)new Object();" + "}"
								+ "};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		assertEquals(
				"java.lang.String x = ((java.lang.String)(new java.lang.Object()))",
				foo.getBody().getStatements().get(0).toString());
	}

	@Test
	public void testCast2() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						""
								+ "class X {"
								+ "public void foo() {"
								+ " Class<String> x=(Class<String>)new Object();"
								+ "}" + "};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];
		assertEquals(
				"java.lang.Class<java.lang.String> x = ((java.lang.Class<java.lang.String>)(new java.lang.Object()))",
				foo.getBody().getStatements().get(0).toString());
	}

	@Test
	public void testCast3() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						""
								+ "class X<A> {"
								+ "void addConsumedAnnotationType(Class<? extends A> annotationType) {}\n"
								+ "public void foo() {" + " Class<?> x = null;"
								+ " addConsumedAnnotationType((Class<A>)x);"
								+ "}" + "};").compile();
		CtMethod<?> foo = clazz.getElements(new NameFilter<CtMethod<?>>("foo"))
				.get(0);
		CtVariableAccess<?> a = (CtVariableAccess<?>) clazz.getElements(
				new TypeFilter<>(CtVariableAccess.class)).get(0);
		assertEquals(1, a.getTypeCasts().size());
		assertEquals("addConsumedAnnotationType(((java.lang.Class<A>)(x)))",
				foo.getBody().getStatements().get(1).toString());
	}

}
