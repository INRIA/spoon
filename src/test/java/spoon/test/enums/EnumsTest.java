package spoon.test.enums;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.test.annotation.AnnotationTest;
import spoon.test.enums.testclasses.Burritos;
import spoon.test.enums.testclasses.Foo;
import spoon.test.enums.testclasses.NestedEnums;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EnumsTest {

	@Test
	public void testModelBuildingEnum() throws Exception {
		CtEnum<Regular> enumeration = build("spoon.test.enums", "Regular");
		assertEquals("Regular", enumeration.getSimpleName());
		assertEquals(3, Regular.values().length);
		assertEquals(3, enumeration.getEnumValues().size());
		assertEquals("A", enumeration.getEnumValues().get(0).getSimpleName());
		assertEquals(5, enumeration.getFields().size());
	}

	@Test
	public void testAnnotationsOnEnum() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/enums/testclasses",
				"-o", "./target/spooned"
		});

		final CtEnum<?> foo = (CtEnum) launcher.getFactory().Type().get(Foo.class);
		assertEquals(1, foo.getFields().size());
		assertEquals(1, foo.getFields().get(0).getAnnotations().size());
		assertEquals(Deprecated.class, AnnotationTest.getActualClassFromAnnotation(
				foo.getFields().get(0).getAnnotations().get(0)));
		assertEquals(
				"public enum Foo {" + DefaultJavaPrettyPrinter.LINE_SEPARATOR
						+ "    @java.lang.Deprecated"
						+ DefaultJavaPrettyPrinter.LINE_SEPARATOR + "    Bar;}",
				foo.toString());
	}

	@Test
	public void testEnumWithoutField() throws Exception {
		final Factory factory = build(Burritos.class);
		final CtType<Burritos> burritos = factory.Type().get(Burritos.class);
		assertEquals("public enum Burritos {" + DefaultJavaPrettyPrinter.LINE_SEPARATOR //
				+ "    ;" + DefaultJavaPrettyPrinter.LINE_SEPARATOR + DefaultJavaPrettyPrinter.LINE_SEPARATOR //
				+ "    public static void m() {" + DefaultJavaPrettyPrinter.LINE_SEPARATOR //
				+ "    }" + DefaultJavaPrettyPrinter.LINE_SEPARATOR //
				+ "}", burritos.toString());
	}

	@Test
	public void testGetAllMethods() throws Exception {
		// contract: getAllMethods also returns the methods of Enum
		final Factory factory = build(Burritos.class);
		final CtType<Burritos> burritos = factory.Type().get(Burritos.class);
		CtMethod name = factory.Core().createMethod();
		name.setSimpleName("name"); // from Enum
		name.setType(factory.Type().createReference(String.class));
		assertTrue(burritos.hasMethod(name));
		assertTrue(burritos.getAllMethods().contains(name));
	}

	@Test
	public void testNestedPrivateEnumValues() throws Exception {
		// contract: ...
		CtType<?> ctClass = ModelUtils.buildClass(NestedEnums.class);
		{
			CtEnum<?> ctEnum = ctClass.getNestedType("PrivateENUM");
			assertEquals(asSet(ModifierKind.PRIVATE), ctEnum.getModifiers());
			assertEquals(asSet(ModifierKind.PRIVATE, ModifierKind.STATIC, ModifierKind.FINAL), ctEnum.getField("VALUE").getModifiers());
		}
		{
			CtEnum<?> ctEnum = ctClass.getNestedType("PublicENUM");
			assertEquals(asSet(ModifierKind.PUBLIC), ctEnum.getModifiers());
			assertEquals(asSet(ModifierKind.PUBLIC, ModifierKind.STATIC, ModifierKind.FINAL), ctEnum.getField("VALUE").getModifiers());
		}
		{
			CtEnum<?> ctEnum = ctClass.getNestedType("ProtectedENUM");
			assertEquals(asSet(ModifierKind.PROTECTED), ctEnum.getModifiers());
			assertEquals(asSet(ModifierKind.PROTECTED, ModifierKind.STATIC, ModifierKind.FINAL), ctEnum.getField("VALUE").getModifiers());
		}
		{
			CtEnum<?> ctEnum = ctClass.getNestedType("PackageProtectedENUM");
			assertEquals(asSet(), ctEnum.getModifiers());
			assertEquals(asSet(ModifierKind.STATIC, ModifierKind.FINAL), ctEnum.getField("VALUE").getModifiers());
		}
	}
	
	private <T> Set<T> asSet(T... values) {
		return new HashSet<>(Arrays.asList(values));
	}
}
