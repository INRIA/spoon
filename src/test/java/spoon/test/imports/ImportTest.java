package spoon.test.imports;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.test.imports.testclasses.SubClass;

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

		final CtType<Object> innerClass = factory.Type().get("spoon.test.imports.testclasses.DefaultClientClass$InnerClass");
		assertEquals("Type of the method must to be InnerClass accessed via DefaultClientClass.", innerClass, methodVisit.getType().getDeclaration());
	}

	@Test
	public void testNewInnerClassDefinesInItsClassAndSuperClass() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();

		SpoonCompiler compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/imports/testclasses/SuperClass.java",
						"./src/test/java/spoon/test/imports/testclasses/SubClass.java"));

		compiler.build();
		final CtClass<?> subClass = (CtClass<?>) factory.Type().get(SubClass.class);
		final CtConstructorCall<?> ctNewClass = subClass.getElements(new AbstractFilter<CtConstructorCall<?>>(CtConstructorCall.class) {
			@Override
			public boolean matches(CtConstructorCall<?> element) {
				return true;
			}
		}).get(0);

		assertEquals("new spoon.test.imports.testclasses.SubClass.Item(\"\")", ctNewClass.toString());
		final String expected = "public class SubClass extends spoon.test.imports.testclasses.SuperClass {\n"
				+ "    public void aMethod() {\n"
				+ "        new spoon.test.imports.testclasses.SubClass.Item(\"\");\n"
				+ "    }\n"
				+ "\n"
				+ "    public static class Item extends spoon.test.imports.testclasses.SuperClass.Item {\n"
				+ "        public Item(java.lang.String s) {\n"
				+ "            super(1, s);\n"
				+ "        }\n"
				+ "    }\n"
				+ "}";
		assertEquals(expected, subClass.toString());
	}
}
