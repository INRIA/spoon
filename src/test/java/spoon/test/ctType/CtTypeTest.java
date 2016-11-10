package spoon.test.ctType;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.TypeFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.createFactory;

public class CtTypeTest {
	@Test
	public void testHasMethodInDirectMethod() {
		CtClass<?> clazz = createFactory().Code().createCodeSnippetStatement(
			"class X { public void foo() {} }").compile();
		assertTrue(clazz.hasMethod(clazz.getMethods().iterator().next()));
	}

	@Test
	public void testHasMethodNotHasMethod() {
		Factory factory = createFactory();
		CtClass<?> clazz = factory.Code().createCodeSnippetStatement(
			"class X { public void foo() {} }").compile();
		CtClass<?> clazz2 = factory.Code().createCodeSnippetStatement(
			"class Y { public void foo2() {} }").compile();
		assertFalse(clazz.hasMethod(clazz2.getMethods().iterator().next()));
	}

	@Test
	public void testHasMethodOnNull() {
		CtClass<?> clazz = createFactory().Code().createCodeSnippetStatement(
			"class X { public void foo() {} }").compile();
		assertFalse(clazz.hasMethod(null));
	}

	@Test
	public void testHasMethodInSuperClass() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/ctType/testclasses/X.java");
		launcher.run();

		final CtClass<?> xClass = launcher.getFactory().Class().get("spoon.test.ctType.testclasses.X");
		final CtClass<?> yClass = launcher.getFactory().Class().get("spoon.test.ctType.testclasses.Y");
		final CtMethod<?> superMethod = xClass.getMethods().iterator().next();

		assertTrue(yClass.hasMethod(superMethod));
	}

	@Test
	public void testHasMethodInDefaultMethod() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/ctType/testclasses/X.java");
		launcher.getEnvironment().setComplianceLevel(8);
		launcher.run();

		final CtClass<?> x = launcher.getFactory().Class().get("spoon.test.ctType.testclasses.W");
		final CtInterface<?> z = launcher.getFactory().Interface().get("spoon.test.ctType.testclasses.Z");
		final CtMethod<?> superMethod = z.getMethods().iterator().next();

		assertTrue(x.hasMethod(superMethod));
	}


	@Test
	public void testIsAssignableFrom() throws Exception {
		final Factory factory = new Launcher().getFactory();
		TypeFactory type = factory.Type();

		assertTrue(type.DOUBLE_PRIMITIVE.isAssignableFrom(type.OBJECT));
		assertFalse(type.OBJECT.isAssignableFrom(type.DOUBLE_PRIMITIVE));
		assertFalse(factory.Class().get(Object.class).isAssignableFrom(type.DOUBLE_PRIMITIVE));

		assertTrue(type.INTEGER_PRIMITIVE.isAssignableFrom(type.INTEGER));
		assertFalse(type.INTEGER.isAssignableFrom(type.INTEGER_PRIMITIVE));

		assertTrue(type.INTEGER_PRIMITIVE.isAssignableFrom(type.createReference(Number.class)));
		assertFalse(type.createReference(Number.class).isAssignableFrom(type.INTEGER_PRIMITIVE));

		assertFalse(type.BOOLEAN_PRIMITIVE.isAssignableFrom(type.createReference(Number.class)));
	}
}
