package spoon.test.factory;

import static spoon.test.TestUtils.build;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.FieldFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;

public class FieldFactoryTest {

	@Test
	public void testCreate() throws Exception {
		
		CtClass<?> type = build("spoon.test", "SampleClass");
		FieldFactory ff = type.getFactory().Field();
		TypeFactory tf = type.getFactory().Type();
		
		Set<ModifierKind> mods = new HashSet<ModifierKind>();
		mods.add(ModifierKind.PRIVATE);
		CtTypeReference<?> tref = tf.createReference(String.class);		
		ff.create(type,mods,tref,"name");
		
		CtField<?> field = type.getField("name");
		Assert.assertEquals("name", field.getSimpleName());
		Assert.assertEquals(tref, field.getType());
		
		CtElement parent = field.getParent();
		Assert.assertFalse(parent.isRootElement());
		Assert.assertTrue(parent instanceof CtClass<?>);
		Assert.assertEquals("SampleClass", ((CtClass<?>)parent).getSimpleName());
	}
}
