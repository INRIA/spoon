package spoon.test.factory;

import org.junit.Assert;
import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FieldFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.targeted.testclasses.Bar;
import spoon.test.targeted.testclasses.Foo;
import spoon.test.targeted.testclasses.SuperClass;

import java.util.HashSet;
import java.util.Set;

import static spoon.testing.utils.ModelUtils.build;

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
		Assert.assertTrue(parent instanceof CtClass<?>);
		Assert.assertEquals("SampleClass", ((CtClass<?>)parent).getSimpleName());
	}

	@Test
	public void testCreateFromSource() throws Exception {

		CtClass<?> target = build("spoon.test", "SampleClass");
		Factory factory = build(Foo.class, Bar.class, SuperClass.class);
		final CtClass<Object> type = factory.Class().get(Foo.class);
		CtField<?> source = type.getField("i");
		FieldFactory ff = type.getFactory().Field();
		TypeFactory tf = type.getFactory().Type();

		ff.create(target,source);

		CtField<?> field = target.getField("i");
		Assert.assertEquals("i", field.getSimpleName());
		CtTypeReference<?> tref = tf.createReference("int");
		Assert.assertEquals(tref, field.getType());

		CtElement parent = field.getParent();
		Assert.assertTrue(parent instanceof CtClass<?>);
		Assert.assertEquals("SampleClass", ((CtClass<?>)parent).getSimpleName());
	}
}
