package spoon.test.secondaryclasses;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;

import java.awt.event.ActionListener;
import java.util.List;

import org.junit.Test;

import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class ClassesTest {

	@Test
	public void testClassWithInternalPublicClassOrInterf() throws Exception {
		CtClass type = (CtClass)build ("spoon.test.secondaryclasses",  "ClassWithInternalPublicClassOrInterf");
		assertEquals("ClassWithInternalPublicClassOrInterf", type.getSimpleName());
		assertEquals(3, type.getElements(new TypeFilter<CtType>(CtType.class)).size());
		assertEquals(2, type.getNestedTypes().size());
		assertTrue(type.getNestedTypes().contains((type.getFactory().Class().get(ClassWithInternalPublicClassOrInterf.InternalClass.class))));
		assertEquals(1, type.getElements(new NameFilter("InternalInterf")).size());		
	}

	@Test
	public void testAnonymousClass() throws Exception {
		CtClass type = (CtClass)build ("spoon.test.secondaryclasses",  "AnonymousClass");
		assertEquals("AnonymousClass", type.getSimpleName());
		
		CtNewClass x = type.getElements(new TypeFilter<CtNewClass>(CtNewClass.class)).get(0);
		
		// ActionListner is not in the Spoon path
		assertNull(x.getType().getDeclaration());
		
		// but the actual class is known
		assertEquals(ActionListener.class, x.getType().getActualClass());
	}

	@Test
	public void testTopLevel() throws Exception {
		CtClass type = (CtClass)build ("spoon.test.secondaryclasses",  "TopLevel");
		assertEquals("TopLevel", type.getSimpleName());
		
		CtClass x = (CtClass) type.getElements(new NameFilter("InnerClass")).get(0);
		List<CtField> fields = x.getFields();
		assertEquals(1, fields.size());		
		assertEquals(1, fields.get(0).getType().getActualTypeArguments().size());
		assertEquals("?", fields.get(0).getType().getActualTypeArguments().get(0).getSimpleName());
	}

}
