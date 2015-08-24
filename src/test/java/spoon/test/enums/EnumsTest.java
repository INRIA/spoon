package spoon.test.enums;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.test.annotation.AnnotationTest;
import spoon.test.enums.testclasses.Foo;

public class EnumsTest {

	@Test
	public void testModelBuildingEnum() throws Exception {
		CtEnum<Regular> enumeration = build("spoon.test.enums", "Regular");
		assertEquals("Regular", enumeration.getSimpleName());
		assertEquals(3, Regular.values().length);
		assertEquals(3, enumeration.getValues().size());
		assertEquals("A", enumeration.getValues().get(0).getSimpleName());
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
}
