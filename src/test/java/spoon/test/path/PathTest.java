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
import spoon.reflect.path.CtElementPathBuilder;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtPathBuilder;
import spoon.reflect.path.CtPathException;
import spoon.reflect.path.CtRole;
import spoon.reflect.path.CtPathStringBuilder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
						.resources("./src/test/java/spoon/test/path/testclasses/Foo.java"))
				.build();
	}

	private void equals(CtPath path, CtElement... elements) {
		Collection<CtElement> result = path.evaluateOn(factory.Package().getRootPackage());
		assertEquals(elements.length, result.size());
		assertArrayEquals(elements, result.toArray(new CtElement[0]));
	}

	private void equalsSet(CtPath path, Set<? extends CtElement> elements) {
		Collection<CtElement> result = path.evaluateOn(factory.Package().getRootPackage());
		assertEquals(elements.size(), result.size());
		assertTrue(result.containsAll(elements));
	}

	@Test
	public void testBuilderMethod() {
		equalsSet(
				new CtPathBuilder().name("spoon").name("test").name("path").name("testclasses").name("Foo").type(CtMethod.class).build(),

				factory.Type().get("spoon.test.path.testclasses.Foo").getMethods()
		);

		equalsSet(
				new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo/CtMethod"),

				factory.Type().get("spoon.test.path.testclasses.Foo").getMethods()
		);
	}

	@Test
	public void testBuilder() {
		equals(
				new CtPathBuilder().recursiveWildcard().name("toto").role(
						CtRole.DEFAULT_EXPRESSION).build(),

				factory.Package().get("spoon.test.path.testclasses").getType("Foo").getField("toto").getDefaultExpression()
		);
	}

	@Test
	public void testPathFromString() {
		// match the first statement of Foo.foo() method
		equals(
				new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.foo#body#statement[index=0]"),
				factory.Package().get("spoon.test.path.testclasses").getType("Foo").getMethod("foo").getBody()
						.getStatement(0));

		equals(new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.bar/CtParameter"),
				factory.Package().get("spoon.test.path.testclasses").getType("Foo").getMethod("bar",
						factory.Type().createReference(int.class),
						factory.Type().createReference(int.class))
						.getParameters().toArray(new CtElement[0])
		);

		CtLiteral<String> literal = factory.Core().createLiteral();
		literal.setValue("salut");
		literal.setType(literal.getFactory().Type().STRING);
		equals(new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.toto#defaultExpression"), literal);
	}

	@Test
	public void testMultiPathFromString() {
		// When role match a list but no index is provided, all of them must be returned
		Collection<CtElement> results = new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.foo#body#statement")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(3, results.size());
		// When role match a set but no name is provided, all of them must be returned
		results = new CtPathStringBuilder().fromString("#subPackage")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(1, results.size());
		// When role match a map but no key is provided, all of them must be returned
		results = new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.bar##annotation[index=0]#value")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(1, results.size());

	}

	@Test
	public void testIncorrectPathFromString() {
		// match the else part of the if in Foo.bar() method which does not exist (Test non existing unique element)
		Collection<CtElement> results = new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.bar#body#statement[index=2]#else")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(0, results.size());
		// match the third statement of Foo.foo() method which does not exist (Test non existing element of a list)
		results = new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.foo#body#statement[index=3]")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(0, results.size());
		// match an non existing package (Test non existing element of a set)
		results = new CtPathStringBuilder().fromString("#subPackage[name=nonExistingPackage]")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(0, results.size());
		//match a non existing field of an annotation (Test non existing element of a map)
		results = new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.bar##annotation[index=0]#value[key=misspelled]")
				.evaluateOn(factory.getModel().getRootPackage());
		assertEquals(0, results.size());
	}

	@Test
	public void testGetPathFromNonParent() {
		CtMethod fooMethod = (CtMethod) new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.foo")
				.evaluateOn(factory.getModel().getRootPackage()).iterator().next();
		CtMethod barMethod = (CtMethod) new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.bar")
				.evaluateOn(factory.getModel().getRootPackage()).iterator().next();
		try {
			new CtElementPathBuilder().fromElement(fooMethod,barMethod);
			fail("No path should be found to .spoon.test.path.testclasses.Foo.foo from .spoon.test.path.testclasses.Foo.bar");
		} catch (CtPathException e) {

		}
	}

	@Test
	public void testWildcards() {
		// get the first statements of all Foo methods
		List<CtElement> list = new LinkedList<>();
		list.add(factory.getModel().getRootPackage());
		equals(new CtPathStringBuilder().fromString(".spoon.test.path.testclasses.Foo.*#body#statement[index=0]"),
				((CtClass) factory.Package().get("spoon.test.path.testclasses").getType("Foo")).getConstructor().getBody()
						.getStatement(0),
				factory.Package().get("spoon.test.path.testclasses").getType("Foo").getMethod("foo").getBody()
						.getStatement(0),
				factory.Package().get("spoon.test.path.testclasses").getType("Foo").getMethod("bar",
						factory.Type().createReference(int.class), factory.Type().createReference(int.class)).getBody()
						.getStatement(0)
		);
	}

	@Test
	public void testRoles() {
		// get the then statement
		equals(new CtPathStringBuilder().fromString(".**/CtIf#else"),
				((CtIf) factory.Package().get("spoon.test.path.testclasses").getType("Foo").getMethod("foo").getBody()
						.getStatement(2)).getElseStatement()
		);
		equals(new CtPathStringBuilder().fromString(".**#else"),
				((CtIf) factory.Package().get("spoon.test.path.testclasses").getType("Foo").getMethod("foo").getBody()
						.getStatement(2)).getElseStatement()
		);
	}

	@Test
	public void toStringTest() {
		comparePath(".spoon.test.path.testclasses.Foo/CtMethod");
		comparePath(".spoon.test.path.testclasses.Foo.foo#body#statement[index=0]");
		comparePath(".spoon.test.path.testclasses.Foo.bar/CtParameter");
		comparePath(".spoon.test.path.testclasses.Foo.toto#defaultExpression");
		comparePath(".spoon.test.path.testclasses.Foo.*#body#statement[index=0]");
		comparePath(".**/CtIf#else");
		comparePath(".**#else");
	}

	private void comparePath(String path) throws CtPathException {
		assertEquals(path, new CtPathStringBuilder().fromString(path).toString());
	}

	@Test
	public void exceptionTest() {
		try {
			new CtPathStringBuilder().fromString("/CtClassss");
			fail();
		} catch (CtPathException e) {
			assertEquals("Unable to locate element with type CtClassss in Spoon model", e.getMessage());
		}
	}

}