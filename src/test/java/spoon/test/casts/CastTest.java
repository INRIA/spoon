package spoon.test.casts;

import org.junit.Test;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.TestUtils;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

public class CastTest {
	Factory factory = TestUtils.createFactory();
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
		CtVariableRead<?> a = (CtVariableRead<?>) clazz.getElements(
				new TypeFilter<>(CtVariableRead.class)).get(0);
		assertEquals(1, a.getTypeCasts().size());
		assertEquals("addConsumedAnnotationType(((java.lang.Class<A>)(x)))",
				foo.getBody().getStatements().get(1).toString());
	}

	@Test
	public void testCase4() throws Exception {
		// contract: If the invocation is to a method where the returned type is a generic type,
		// getType returns the actual type bound to this particular invocation.
		CtType<?> type = build("spoon.test.casts", "Castings");

		final CtMethod<?> getValueMethod = type.getMethodsByName("getValue").get(0);
		final CtInvocation<?> getValueInvocation = Query.getElements(type, new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation element) {
				return "getValue".equals(element.getExecutable().getSimpleName()) && super.matches(element);
			}
		}).get(0);

		assertEquals("T", getValueMethod.getType().getSimpleName());
		assertEquals("T", getValueMethod.getParameters().get(0).getType().getActualTypeArguments().get(0).toString());

		assertEquals(type.getFactory().Class().INTEGER, getValueInvocation.getType());
	}
}
