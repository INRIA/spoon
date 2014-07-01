package spoon.test.fieldaccesses;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import java.util.List;

import org.junit.Test;

import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTargetedAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class FieldAccessTest {

	@Test
	public void testModelBuildingFieldAccesses() throws Exception {
		CtSimpleType<?> type = build("spoon.test.fieldaccesses", "Mouse");
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
	public void testModelBuildingOuterThisAccesses() throws Exception {
		CtSimpleType<?> type = build("spoon.test.fieldaccesses",
				"InnerClassThisAccess");
		assertEquals("InnerClassThisAccess", type.getSimpleName());

		CtMethod<?> meth1 = type.getElements(
				new NameFilter<CtMethod<?>>("method2")).get(0);
		assertEquals(
				"spoon.test.fieldaccesses.InnerClassThisAccess.this.method()",
				meth1.getBody().getStatements().get(0).toString());

		CtClass<?> c = type.getElements(
				new NameFilter<CtClass<?>>("InnerClass")).get(0);
		assertEquals("InnerClass", c.getSimpleName());
		CtConstructor<?> ctr = c.getConstructor(type.getFactory().Type()
				.createReference(boolean.class));
		assertEquals("this.b = b", ctr.getBody().getLastStatement().toString());
	}

	@Test
	public void testModelBuildingOuterSuperAccesses() throws Exception {
		CtSimpleType<?> type = build("spoon.test.fieldaccesses",
				"InternalSuperCall");
		assertEquals("InternalSuperCall", type.getSimpleName());

		CtMethod<?> meth0 = type.getElements(
				new NameFilter<CtMethod<?>>("methode")).get(0);
		assertEquals(
				"spoon.test.fieldaccesses.InternalSuperCall.super.toString()",
				meth0.getBody().getStatements().get(0).toString());
	}

	@Test
	public void testBCUBug20140402() throws Exception {
		CtSimpleType<?> type = build("spoon.test.fieldaccesses",
				"BCUBug20140402");
		assertEquals("BCUBug20140402", type.getSimpleName());

		CtLocalVariable var = type.getElements(
				new TypeFilter<CtLocalVariable>(CtLocalVariable.class)).get(0);
		CtFieldAccess expr = (CtFieldAccess) var.getDefaultExpression();
		assertEquals(
				"length",
				expr.getVariable().toString());
		assertEquals(
				"int",
				expr.getType().getSimpleName());
		
		// in the model the top-most field access is .length get(0)
		// and the second one is ".data" get(1)
		CtFieldAccess fa = expr.getElements(new TypeFilter<CtFieldAccess>(CtFieldAccess.class)).get(1);
		// we check that we have the data
		assertEquals(
				"data",
				fa.getVariable().toString());
		assertEquals(
				"java.lang.Object[]",
				fa.getType().toString());
	}
	
	@Test
	public void testTargetedAccessPosition() throws Exception{
		CtSimpleType<?> type = build("spoon.test.fieldaccesses", "TargetedAccessPosition");
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
		assertEquals(0, ((CtTargetedAccess<?>)vars.get(0).getTarget()).getTarget().getPosition().getSourceEnd() - 
				((CtTargetedAccess<?>)vars.get(0).getTarget()).getTarget().getPosition().getSourceStart());
	}
}
