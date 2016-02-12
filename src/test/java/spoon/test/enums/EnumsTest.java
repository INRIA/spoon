package spoon.test.enums;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.test.annotation.AnnotationTest;
import spoon.test.enums.testclasses.Burritos;
import spoon.test.enums.testclasses.Foo;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.build;

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
						+ "@java.lang.Deprecated"
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
}
