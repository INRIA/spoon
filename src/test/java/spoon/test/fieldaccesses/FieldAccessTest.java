package spoon.test.fieldaccesses;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.code.CtFieldAccess;
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

}
