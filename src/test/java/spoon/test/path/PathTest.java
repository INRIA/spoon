package spoon.test.path;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtPathBuilder;
import spoon.reflect.path.CtPathException;
import spoon.reflect.path.CtPathRole;
import spoon.reflect.path.CtPathStringBuilder;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by nicolas on 10/06/2015.
 */
public class PathTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/path/Foo.java"))
				.build();
	}

	private void equals(CtPath path, CtElement... elements) {
		Collection<CtElement> result = path.evaluateOn(Arrays.asList(factory.Package().getRootPackage()));
		assertEquals(elements.length, result.size());
		assertArrayEquals(elements, result.toArray(new CtElement[0]));
	}

	@Test
	public void testBuilderMethod() throws Exception {
		equals(
				new CtPathBuilder().name("spoon").name("test").name("path").name("Foo").type(CtMethod.class).build(),

				factory.Type().get("spoon.test.path.Foo").getMethods().toArray(new CtMethod[0])
		);

		equals(
				new CtPathStringBuilder().fromString(".spoon.test.path.Foo/CtMethod"),

				factory.Type().get("spoon.test.path.Foo").getMethods().toArray(new CtMethod[0])
		);
	}

	@Test
	public void testBuilder() {
		equals(
				new CtPathBuilder().recursiveWildcard().name("toto").role(CtPathRole.DEFAULT_VALUE).build(),

				factory.Package().get("spoon.test.path").getType("Foo").getField("toto").getDefaultExpression()
		);
	}

	@Test
	public void testPathFromString() throws Exception {
		// match the first statement of Foo.foo() method
		equals(
				new CtPathStringBuilder().fromString(".spoon.test.path.Foo.foo#body[index=0]"),
				factory.Package().get("spoon.test.path").getType("Foo").getMethod("foo").getBody()
						.getStatement(0));

		equals(new CtPathStringBuilder().fromString(".spoon.test.path.Foo.bar/CtParameter"),
				factory.Package().get("spoon.test.path").getType("Foo").getMethod("bar",
						factory.Type().createReference(int.class),
						factory.Type().createReference(int.class))
						.getParameters().toArray(new CtElement[0])
		);

		CtLiteral<String> literal = factory.Core().createLiteral();
		literal.setValue("salut");
		equals(new CtPathStringBuilder().fromString(".spoon.test.path.Foo.toto#defaultValue"), literal);
	}

	@Test
	public void testWildcards() throws Exception {
		// get the first statements of all Foo methods
		equals(new CtPathStringBuilder().fromString(".spoon.test.path.Foo.*#body[index=0]"),
				((CtClass) factory.Package().get("spoon.test.path").getType("Foo")).getConstructor().getBody()
						.getStatement(0),
				factory.Package().get("spoon.test.path").getType("Foo").getMethod("foo").getBody()
						.getStatement(0),
				factory.Package().get("spoon.test.path").getType("Foo").getMethod("bar",
						factory.Type().createReference(int.class), factory.Type().createReference(int.class)).getBody()
						.getStatement(0)
		);
	}

	@Test
	public void testRoles() throws Exception {
		// get the then statement
		equals(new CtPathStringBuilder().fromString(".**/CtIf#else"),
				((CtIf) factory.Package().get("spoon.test.path").getType("Foo").getMethod("foo").getBody()
						.getStatement(2)).getThenStatement()
		);
		equals(new CtPathStringBuilder().fromString(".**#else"),
				((CtIf) factory.Package().get("spoon.test.path").getType("Foo").getMethod("foo").getBody()
						.getStatement(2)).getThenStatement()
		);
	}

	@Test
	public void toStringTest() throws Exception {
		comparePath(".spoon.test.path.Foo/CtMethod");
		comparePath(".spoon.test.path.Foo.foo#body[index=0]");
		comparePath(".spoon.test.path.Foo.bar/CtParameter");
		comparePath(".spoon.test.path.Foo.toto#defaultValue");
		comparePath(".spoon.test.path.Foo.*#body[index=0]");
		comparePath(".**/CtIf#else");
		comparePath(".**#else");
	}

	private void comparePath(String path) throws CtPathException {
		assertEquals(path, new CtPathStringBuilder().fromString(path).toString());
	}

}