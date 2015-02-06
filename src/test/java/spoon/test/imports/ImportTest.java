package spoon.test.imports;

import org.junit.Ignore;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.NameFilter;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ImportTest {

	// TODO This test is ignored because we have proposed a wrong fix for the issue #114 on GitHub.
	@Test
	@Ignore
	public void testImportOfAnInnerClassInASuperClassPackage() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();

		SpoonCompiler compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/imports/testclasses/internal/SuperClass.java",
						"./src/test/java/spoon/test/imports/testclasses/internal/ChildClass.java",
						"./src/test/java/spoon/test/imports/testclasses/ClientClass.java"));

		compiler.build();

		final List<CtClass<?>> classes = Query.getElements(factory, new NameFilter<CtClass<?>>("ClientClass"));

		final CtClass<?> innerClass = classes.get(0).getNestedType("InnerClass");
		String expected = "spoon.test.imports.testclasses.ClientClass.InnerClass";
		assertEquals(expected, innerClass.getReference().toString());

		expected = "spoon.test.imports.testclasses.internal.ChildClass.InnerClassProtected";
		assertEquals(expected, innerClass.getSuperclass().toString());
	}

	@Test
	public void testImportOfAnInnerClassInAClassPackage() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();

		SpoonCompiler compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/imports/testclasses/internal/PublicSuperClass.java",
						"./src/test/java/spoon/test/imports/testclasses/DefaultClientClass.java"));

		compiler.build();

		final CtClass<?> client = (CtClass<?>) factory.Type().get("spoon.test.imports.testclasses.DefaultClientClass");
		final CtMethod<?> methodVisit = client.getMethodsByName("visit").get(0);

		final CtSimpleType<Object> innerClass = factory.Type().get("spoon.test.imports.testclasses.DefaultClientClass$InnerClass");
		assertEquals("Type of the method must to be InnerClass accessed via DefaultClientClass.", innerClass, methodVisit.getType().getDeclaration());
	}
}
