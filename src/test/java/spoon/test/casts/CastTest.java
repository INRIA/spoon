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
package spoon.test.casts;

import org.junit.Test;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.casts.testclasses.Castings;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;

public class CastTest {
	Factory factory = ModelUtils.createFactory();
	@Test
	public void testCast1() {
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {"
								+ " String x=(String) new Object();" + "}"
								+ "};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		assertEquals(
				"java.lang.String x = ((java.lang.String) (new java.lang.Object()))",
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
								+ " Class<String> x=(Class<String>) new Object();"
								+ "}" + "};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];
		assertEquals(
				"java.lang.Class<java.lang.String> x = ((java.lang.Class<java.lang.String>) (new java.lang.Object()))",
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
								+ " addConsumedAnnotationType((Class<A>) x);"
								+ "}" + "};").compile();
		CtMethod<?> foo = clazz.getElements(new NamedElementFilter<>(CtMethod.class, "foo"))
				.get(0);
		CtVariableRead<?> a = (CtVariableRead<?>) clazz.getElements(
				new TypeFilter<>(CtVariableRead.class)).get(0);
		assertEquals(1, a.getTypeCasts().size());
		assertEquals("addConsumedAnnotationType(((java.lang.Class<A>) (x)))",
				foo.getBody().getStatements().get(1).toString());
	}

	@Test
	public void testCase4() throws Exception {
		// contract: If the invocation is to a method where the returned type is a generic type,
		// getType returns the actual type bound to this particular invocation.
		CtType<?> type = build("spoon.test.casts.testclasses", "Castings");

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

	@Test
	public void testTypeAnnotationOnCast() throws Exception {
		final CtType<Castings> aCastings = buildClass(Castings.class);
		final CtLocalVariable local = aCastings.getMethod("bar").getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);

		assertEquals(1, ((CtTypeReference) local.getDefaultExpression().getTypeCasts().get(0)).getAnnotations().size());
		assertEquals("((java.lang.@spoon.test.casts.testclasses.Castings.TypeAnnotation(integer = 1)" + System.lineSeparator() + "String) (\"\"))", local.getDefaultExpression().toString());
	}
}
