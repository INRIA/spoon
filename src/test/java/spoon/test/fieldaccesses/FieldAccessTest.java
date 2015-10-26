package spoon.test.fieldaccesses;

import org.junit.Test;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.TestUtils;
import spoon.test.fieldaccesses.testclasses.Panini;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static spoon.test.TestUtils.build;

public class FieldAccessTest {

	@Test
	public void testModelBuildingFieldAccesses() throws Exception {
		CtType<?> type = build("spoon.test.fieldaccesses", "Mouse");
		assertEquals("Mouse", type.getSimpleName());

		CtMethod<?> meth1 = type.getElements(
				new NameFilter<CtMethod<?>>("meth1")).get(0);
		CtMethod<?> meth1b = type.getElements(
				new NameFilter<CtMethod<?>>("meth1b")).get(0);

		assertEquals(
				3,
				meth1.getElements(
						new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class))
						.size());

		assertEquals(
				2,
				meth1b.getElements(
						new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class))
						.size());

		CtMethod<?> meth2 = type.getElements(
				new NameFilter<CtMethod<?>>("meth2")).get(0);
		assertEquals(
				2,
				meth2.getElements(
						new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class))
						.size());

		CtMethod<?> meth3 = type.getElements(
				new NameFilter<CtMethod<?>>("meth3")).get(0);
		assertEquals(
				3,
				meth3.getElements(
						new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class))
						.size());

		CtMethod<?> meth4 = type.getElements(
				new NameFilter<CtMethod<?>>("meth4")).get(0);
		assertEquals(
				1,
				meth4.getElements(
						new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class))
						.size());

	}

	@Test
	public void testBCUBug20140402() throws Exception {
		CtType<?> type = build("spoon.test.fieldaccesses",
				"BCUBug20140402");
		assertEquals("BCUBug20140402", type.getSimpleName());

		CtLocalVariable<?> var = type.getElements(
				new TypeFilter<CtLocalVariable<?>>(CtLocalVariable.class)).get(0);
		CtFieldAccess<?> expr = (CtFieldAccess<?>) var.getDefaultExpression();
		assertEquals(
				"length",
				expr.getVariable().toString());
		assertEquals(
				"int",
				expr.getType().getSimpleName());

		// in the model the top-most field access is .length get(0)
		// and the second one is ".data" get(1)
		CtFieldAccess<?> fa = expr.getElements(new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class)).get(1);
		// we check that we have the data
		assertEquals(
				"data",
				fa.getVariable().toString());
		assertEquals(
				"java.lang.Object[]",
				fa.getType().toString());

		// testing the proxy method setAssignment/getAssignment on local variables
		var.setAssignment(null);
		assertEquals(null, var.getAssignment());
		assertEquals("int a", var.toString());

		// testing the proxy method setAssignment/getAssignment on fields
		CtField<?> field = type.getElements(
				new TypeFilter<CtField<?>>(CtField.class)).get(0);
		assertNotNull(field.getAssignment());
		field.setAssignment(null);
		assertEquals(null, field.getAssignment());
		assertEquals("java.lang.Object[] data;", field.toString());

	}

	@Test
	public void testTargetedAccessPosition() throws Exception{
		CtType<?> type = build("spoon.test.fieldaccesses", "TargetedAccessPosition");
		List<CtFieldAccess<?>> vars = type.getElements(
				new TypeFilter<CtFieldAccess<?>>(CtFieldAccess.class));
		//vars is [t.ta.ta, t.ta]
		assertEquals(2, vars.size());

		assertEquals(vars.get(1), vars.get(0).getTarget());

		// 6 is length(t.ta.ta) - 1
		assertEquals(6, vars.get(0).getPosition().getSourceEnd() - vars.get(0).getPosition().getSourceStart());

		// 3 is length(t.ta) - 1
		assertEquals(3, vars.get(0).getTarget().getPosition().getSourceEnd() - vars.get(0).getTarget().getPosition().getSourceStart());

		// 0 is length(t)-1
		assertEquals(0, ((CtFieldAccess<?>)vars.get(0).getTarget()).getTarget().getPosition().getSourceEnd() -
				((CtFieldAccess<?>)vars.get(0).getTarget()).getTarget().getPosition().getSourceStart());
	}

	@Test
	public void testFieldAccessInLambda() throws Exception {
		Factory build = null;
		try {
			build = TestUtils.build(MyClass.class);
		} catch (NullPointerException ignore) {
			fail();
		}

		final CtFieldAccess logFieldAccess = Query.getElements(build, new TypeFilter<>(CtFieldAccess.class)).get(0);

		assertEquals(Logger.class, logFieldAccess.getType().getActualClass());
		assertEquals("LOG", logFieldAccess.getVariable().getSimpleName());
		assertEquals(MyClass.class, logFieldAccess.getVariable().getDeclaringType().getActualClass());

		String expectedLambda = "() -> {" + System.lineSeparator() + "    spoon.test.fieldaccesses.MyClass.LOG.info(\"bla\");" + System.lineSeparator() + "}";
		assertEquals(expectedLambda, logFieldAccess.getParent(CtLambda.class).toString());
	}

	@Test
	public void testFieldAccessInAnonymousClass() throws Exception {
		final Factory factory = TestUtils.build(Panini.class);
		final CtType<Panini> panini = factory.Type().get(Panini.class);

		final CtFieldRead fieldInAnonymous = panini.getElements(new TypeFilter<>(CtFieldRead.class)).get(0);
		assertEquals("ingredient", fieldInAnonymous.getTarget().toString());
		assertEquals("next", fieldInAnonymous.getVariable().getSimpleName());
		assertEquals("ingredient.next", fieldInAnonymous.toString());
	}
}
