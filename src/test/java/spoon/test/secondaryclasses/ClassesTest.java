package spoon.test.secondaryclasses;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;

import java.awt.event.ActionListener;
import java.util.List;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.secondaryclasses.AnonymousClass.I;

public class ClassesTest {

	@Test
	public void testClassWithInternalPublicClassOrInterf() throws Exception {
		CtClass<?> type = build("spoon.test.secondaryclasses",
				"ClassWithInternalPublicClassOrInterf");
		assertEquals("ClassWithInternalPublicClassOrInterf",
				type.getSimpleName());
		assertEquals(3,
				type.getElements(new TypeFilter<CtType<?>>(CtType.class))
						.size());
		assertEquals(2, type.getNestedTypes().size());
		assertTrue(type
				.getNestedTypes()
				.contains(
						(type.getFactory().Class()
								.get(ClassWithInternalPublicClassOrInterf.InternalClass.class))));
		assertEquals(
				1,
				type.getElements(
						new NameFilter<CtNamedElement>("InternalInterf"))
						.size());
	}

	@Test
	public void testAnonymousClass() throws Exception {
		CtClass<?> type = build("spoon.test.secondaryclasses", "AnonymousClass");
		assertEquals("AnonymousClass", type.getSimpleName());

		CtNewClass<?> x = type.getElements(
				new TypeFilter<CtNewClass<?>>(CtNewClass.class)).get(0);
		CtNewClass<?> y = type.getElements(
				new TypeFilter<CtNewClass<?>>(CtNewClass.class)).get(1);

		if (x.getParent() instanceof CtBlock) {
			CtNewClass<?> z = x;
			x = y;
			y = z;
		}

		// ActionListner is not in the Spoon path
		assertNull(x.getType().getDeclaration());

		// but the actual class is known
		assertEquals(ActionListener.class, x.getType().getActualClass());

		assertNotNull(y.getType().getDeclaration());

		assertEquals(".", y.getExecutable().toString());

		// the superclass of an anonymous class can be an interface (the one
		// from which it is built)
		assertEquals(type.getFactory().Type().createReference(I.class), y
				.getAnonymousClass().getSuperclass());

	}

	@Test
	public void testTopLevel() throws Exception {
		CtClass<?> type = build("spoon.test.secondaryclasses", "TopLevel");
		assertEquals("TopLevel", type.getSimpleName());

		CtClass<?> x = type.getElements(
				new NameFilter<CtClass<?>>("InnerClass")).get(0);
		List<CtField<?>> fields = x.getFields();
		assertEquals(1, fields.size());
		assertEquals(1, fields.get(0).getType().getActualTypeArguments().size());
		assertEquals("?",
				fields.get(0).getType().getActualTypeArguments().get(0)
						.getSimpleName());
	}

	@Test
	public void testInnerClassContruction() throws Exception {
		Factory f = build(PrivateInnerClasses.class);
		CtClass<?> c = f.Class().get(PrivateInnerClasses.class);
		assertNotNull(c);
		assertEquals(0, f.getEnvironment().getErrorCount());
	}

}
