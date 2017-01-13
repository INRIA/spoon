package spoon.test.ctType;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.test.ctType.testclasses.X;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.buildClass;
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
	public void testIsSubTypeOf() throws Exception {
		CtType<X> xCtType = buildClass(X.class);

		assertFalse(xCtType.isSubtypeOf(xCtType.getFactory().Type().createReference("spoon.test.ctType.testclasses.Y")));
		assertTrue(xCtType.getFactory().Type().get("spoon.test.ctType.testclasses.Y").isSubtypeOf(xCtType.getReference()));
	}

	@Test
	public void testOverridesMethod() {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/ctType/testclasses/X.java");
		launcher.run();

		final CtClass<?> yClass= launcher.getFactory().Class().get(
				"spoon.test.ctType.testclasses.Y");
		assertFalse(yClass.overridesMethod("clone()"));
		assertFalse(yClass.overridesMethod("equals(java.lang.Object)"));
		assertFalse(yClass.overridesMethod("finalize()"));
		assertFalse(yClass.overridesMethod("hashCode()"));
		assertFalse(yClass.overridesMethod("toString()"));
		assertFalse(yClass.overridesMethod("foo()"));

		// check multiple overrides
		final CtClass<?> multiOverrideClass = launcher.getFactory().Class().get(
				"spoon.test.ctType.testclasses.MultiOverrideClass");
		assertFalse(multiOverrideClass.overridesMethod("clone()"));
		assertTrue(multiOverrideClass.overridesMethod("equals(java.lang.Object)"));
		assertFalse(multiOverrideClass.overridesMethod("finalize()"));
		assertTrue(multiOverrideClass.overridesMethod("hashCode()"));
		assertTrue(multiOverrideClass.overridesMethod("toString()"));

		// with @Override
		final CtClass<?> aClass = launcher.getFactory().Class().get(
				"spoon.test.ctType.testclasses.A");
		assertTrue(aClass.overridesMethod("foo()"));
		// prepend an arbitrary declaring type
		assertTrue(aClass.overridesMethod("FooBar#foo()"));
		assertTrue(aClass.overridesMethod("java.lang.Object#foo()"));
		// prepend an arbitrary return type
		assertTrue(aClass.overridesMethod("void foo()"));
		assertTrue(aClass.overridesMethod("foobar foo()"));

		// without @Override
		final CtClass<?> bClass = launcher.getFactory().Class().get(
				"spoon.test.ctType.testclasses.B");
		assertTrue(bClass.overridesMethod("foo()"));
		// prepend an arbitrary declaring type
		assertTrue(bClass.overridesMethod("FooBar#foo()"));
		assertTrue(bClass.overridesMethod("java.lang.Object#foo()"));
		// prepend an arbitrary return type
		assertTrue(bClass.overridesMethod("void foo()"));
		assertTrue(bClass.overridesMethod("foobar foo()"));

		// transitive
		final CtClass<?> cClass = launcher.getFactory().Class().get(
				"spoon.test.ctType.testclasses.C");
		assertFalse(cClass.overridesMethod("foo()"));
		final CtClass<?> dClass = launcher.getFactory().Class().get(
				"spoon.test.ctType.testclasses.D");
		// prepend an arbitrary declaring type
		assertTrue(dClass.overridesMethod("FooBar#foo()"));
		assertTrue(dClass.overridesMethod("java.lang.Object#foo()"));
		// prepend an arbitrary return type
		assertTrue(dClass.overridesMethod("void foo()"));
		assertTrue(dClass.overridesMethod("foobar foo()"));

		// from interface
		final CtClass<?> wClass = launcher.getFactory().Class().get(
				"spoon.test.ctType.testclasses.W");
		assertFalse(cClass.overridesMethod("foo()"));
		final CtClass<?> vClass = launcher.getFactory().Class().get(
				"spoon.test.ctType.testclasses.V");
		assertTrue(vClass.overridesMethod("foo()"));
	}
}
