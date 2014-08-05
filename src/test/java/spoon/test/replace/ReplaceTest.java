package spoon.test.replace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class ReplaceTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/replace/Foo.java"))
				.build();
	}

	@Test
	public void testReplaceSet() throws Exception {

		CtClass<?> foo = factory.Package().get("spoon.test.replace")
				.getType("Foo");
		assertEquals("Foo", foo.getSimpleName());
		CtClass<?> bar = factory.Package().get("spoon.test.replace")
				.getType("Bar");
		assertEquals("Bar", bar.getSimpleName());

		CtField<?> i1 = foo.getField("i");
		CtField<?> i2 = bar.getField("i");

		assertEquals("int", foo.getField("i").getType().getSimpleName());

		// do
		i1.replace(i2);
		assertSame(i2, foo.getField("i"));
		assertEquals("float", foo.getField("i").getType().getSimpleName());

		// undo
		i2.replace(i1);
		assertSame(i1, foo.getField("i"));
		assertEquals("int", foo.getField("i").getType().getSimpleName());
	}

	@Test
	public void testReplaceBlock() throws Exception {
		CtClass<?> foo = factory.Package().get("spoon.test.replace")
				.getType("Foo");
		CtMethod<?> m = (CtMethod<?>) foo.getElements(
				new NameFilter("foo")).get(0);
		assertEquals("foo", m.getSimpleName());

		CtAssignment<?, ?> assignment = (CtAssignment<?, ?>) m.getBody()
				.getStatements().get(2);

		CtExpression<?> s1 = assignment.getAssignment();
		CtExpression<?> s2 = factory.Code().createLiteral(3);

		assertEquals("z = x + 1", assignment.toString());
		assertEquals("x + 1", s1.toString());

		// do
		s1.replace(s2);
		assertSame(s2, assignment.getAssignment());
		assertEquals("z = 3", assignment.toString());

		// undo
		s2.replace(s1);
		assertSame(s1, assignment.getAssignment());
		assertEquals("z = x + 1", assignment.toString());
	}

	@Test
	public void testReplaceReplace() throws Exception {
		// bug found by Benoit
		CtClass<?> foo = factory.Package().get("spoon.test.replace")
				.getType("Foo");
		
		CtMethod<?> fooMethod = (CtMethod<?>) foo.getElements(
				new NameFilter("foo")).get(0);
		assertEquals("foo", fooMethod.getSimpleName());

		CtMethod<?> barMethod = (CtMethod<?>) foo.getElements(
				new NameFilter("bar")).get(0);
		assertEquals("bar", barMethod.getSimpleName());

		CtLocalVariable<?> assignment = (CtLocalVariable<?>) fooMethod.getBody()
				.getStatements().get(0);
		CtLocalVariable<?> newAssignment = barMethod.getBody().getStatement(0);

		assignment.replace(newAssignment);
		
		assertEquals(fooMethod.getBody(), newAssignment.getParent());
		
		CtLiteral<?> lit = foo.getElements(
				new TypeFilter<CtLiteral<?>>(CtLiteral.class)).get(0);
		CtLiteral<?> newLit = factory.Code().createLiteral(0);
		lit.replace(newLit);
		assertEquals("int y = 0", fooMethod.getBody().getStatement(0).toString());				
	}

}
