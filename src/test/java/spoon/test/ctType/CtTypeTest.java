package spoon.test.ctType;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.ctType.testclasses.X;

import java.util.List;

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
		CtType<?> yCtType = xCtType.getFactory().Type().get("spoon.test.ctType.testclasses.Y");

		assertFalse(xCtType.isSubtypeOf(yCtType.getReference()));
		assertTrue(yCtType.isSubtypeOf(xCtType.getReference()));
		//contract: x isSubtypeOf x
		//using CtTypeReference implementation
		assertTrue(xCtType.getReference().isSubtypeOf(xCtType.getReference()));
		//using CtType implementation
		assertTrue(xCtType.isSubtypeOf(xCtType.getReference()));
	}

	@Test
	public void testIsSubTypeOfonTypeParameters() throws Exception {
		CtType<X> xCtType = buildClass(X.class);

		CtType<?> oCtType = xCtType.getFactory().Type().get("spoon.test.ctType.testclasses.O");

		List<CtTypeParameter> typeParameters = oCtType.getFormalCtTypeParameters();
		assertTrue(typeParameters.size() == 1);

		CtType<?> aCtType = typeParameters.get(0);

		List<CtMethod<?>> methods = oCtType.getMethodsByName("foo");

		assertTrue(methods.size() == 1);

		CtMethod<?> fooMethod = methods.get(0);
		CtType<?> bCtType = fooMethod.getType().getDeclaration();

		//The TypeParameters are not supported yet
//		assertTrue(bCtType.isSubtypeOf(xCtType.getReference()));
//		assertTrue(bCtType.isSubtypeOf(aCtType.getReference()));
	}
}
