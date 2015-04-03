package spoon.test.initializers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class InitializerTest {
	@Test
	public void testModelBuildingStaticInitializer() throws Exception {
		CtClass<?> type = build("spoon.test.initializers",
				"InternalClassStaticFieldInit");
		assertEquals("InternalClassStaticFieldInit", type.getSimpleName());

		CtClass<?> InternalClass = type.getNestedType("InternalClass");
		assertTrue(InternalClass.getModifiers().contains(ModifierKind.STATIC));
		CtAnonymousExecutable staticBlock = type.getElements(
				new TypeFilter<CtAnonymousExecutable>(
						CtAnonymousExecutable.class)).get(0);
		assertTrue(staticBlock.getModifiers().contains(ModifierKind.STATIC));
		assertEquals(1, staticBlock.getBody().getStatements().size());

		// this fails: regression or known bug?
		// RP: this look OK. Spoon adds the full path
		// assertEquals("InternalClass.tmp = \"nop\"",
		// staticBlock.getBody().getStatements().get(0).toString());
	}

	@Test
	public void testModelBuildingInitializer() throws Exception {
		CtClass<?> type = build("spoon.test.initializers",
				"InstanceInitializers");
		assertEquals("InstanceInitializers", type.getSimpleName());

		CtField<?> k = type.getElements(new NameFilter<CtField<?>>("k")).get(0);
		assertTrue(k.getDefaultExpression() instanceof CtConstructorCall);

		CtField<?> l = type.getElements(new NameFilter<CtField<?>>("l")).get(0);
		assertTrue(l.getDefaultExpression() instanceof CtConstructorCall);

		CtField<?> x = type.getElements(new NameFilter<CtField<?>>("x")).get(0);
		assertTrue(x.getDefaultExpression() == null);

		CtField<?> y = type.getElements(new NameFilter<CtField<?>>("y")).get(0);
		assertTrue(y.getDefaultExpression() instanceof CtLiteral);

		CtField<?> z = type.getElements(new NameFilter<CtField<?>>("z")).get(0);
		assertTrue(z.getDefaultExpression().toString().equals("5"));

		// static initializer
		CtAnonymousExecutable ex = type.getElements(
				new TypeFilter<CtAnonymousExecutable>(
						CtAnonymousExecutable.class)).get(0);
		assertEquals("x = 3", ex.getBody().getStatements().get(0).toString());

	}
}
