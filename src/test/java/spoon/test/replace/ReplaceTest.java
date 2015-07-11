package spoon.test.replace;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

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

		CtField<Number> i1 = (CtField<Number>) foo.getField("i");
		CtField<Number> i2 = (CtField<Number>) bar.getField("i");

		assertEquals("int", foo.getField("i").getType().getSimpleName());

		// do
		i1.replace(i2);
		assertSame(i2, foo.getField("i"));
		assertEquals("float", foo.getField("i").getType().getSimpleName());
		assertEquals(foo, i2.getParent());

		// undo
		i2.replace(i1);
		assertSame(i1, foo.getField("i"));
		assertEquals("int", foo.getField("i").getType().getSimpleName());
		assertEquals(foo, i1.getParent());
	}

	@Test
	public void testReplaceBlock() throws Exception {
		CtClass<?> foo = factory.Package().get("spoon.test.replace")
				.getType("Foo");
		CtMethod<?> m = foo.getElements(
				new NameFilter<CtMethod<?>>("foo")).get(0);
		assertEquals("foo", m.getSimpleName());

		final CtStatement parent = m.getBody().getStatements().get(2);
		CtAssignment<?, ?> assignment = (CtAssignment<?, ?>) parent;

		CtExpression<Integer> s1 = (CtExpression<Integer>) assignment.getAssignment();
		CtExpression<Integer> s2 = factory.Code().createLiteral(3);

		assertEquals("z = x + 1", assignment.toString());
		assertEquals("x + 1", s1.toString());

		// do
		s1.replace(s2);
		assertSame(s2, assignment.getAssignment());
		assertEquals("z = 3", assignment.toString());
		assertEquals(parent, s2.getParent());

		// undo
		s2.replace(s1);
		assertSame(s1, assignment.getAssignment());
		assertEquals("z = x + 1", assignment.toString());
		assertEquals(parent, s1.getParent());
	}

	@Test
	public void testReplaceReplace() throws Exception {
		// bug found by Benoit
		CtClass<?> foo = factory.Package().get("spoon.test.replace")
				.getType("Foo");

		CtMethod<?> fooMethod = foo.getElements(
				new NameFilter<CtMethod<?>>("foo")).get(0);
		assertEquals("foo", fooMethod.getSimpleName());

		CtMethod<?> barMethod = foo.getElements(
				new NameFilter<CtMethod<?>>("bar")).get(0);
		assertEquals("bar", barMethod.getSimpleName());

		CtLocalVariable<?> assignment = (CtLocalVariable<?>) fooMethod.getBody()
				.getStatements().get(0);
		CtLocalVariable<?> newAssignment = barMethod.getBody().getStatement(0);

		assignment.replace(newAssignment);

		assertEquals(fooMethod.getBody(), newAssignment.getParent());

		CtLiteral<Integer> lit = (CtLiteral<Integer>) foo.getElements(new TypeFilter<CtLiteral<?>>(CtLiteral.class))
				.get(0);
		final CtElement parent = lit.getParent();
		CtLiteral<Integer> newLit = factory.Code().createLiteral(0);
		lit.replace(newLit);
		assertEquals("int y = 0", fooMethod.getBody().getStatement(0).toString());
		assertEquals(parent, newLit.getParent());
	}

	@Test
	public void testReplaceStmtByList() {
		CtClass<?> sample = factory.Package().get("spoon.test.replace")
				.getType("Foo");

		// replace retry content by statements
		CtStatement stmt = sample.getMethod("retry").getBody().getStatement(0);
		CtBlock lst = sample.getMethod("statements").getBody();

		// replace a single statement by a statement list
		stmt.replace(lst);

		// we should have only 2 statements after (from the stmt list)
		assertEquals(2, sample.getMethod("retry").getBody().getStatements().size());
	}

	@Test
	public void testReplaceField() {
		CtClass<?> sample = factory.Package().get("spoon.test.replace")
				.getType("Foo");

		Assert.assertEquals(factory.Type().createReference(int.class), sample.getField("i").getType());

		// replace with another type
		CtField replacement = factory.Core().createField();
		replacement.setSimpleName("i");
		replacement.setType(factory.Type().createReference(double.class));
		sample.getField("i").replace(replacement);
		Assert.assertEquals(factory.Type().createReference(double.class), sample.getField("i").getType());

		// replace with another name
		replacement = factory.Core().createField();
		replacement.setSimpleName("j");
		replacement.setType(factory.Type().createReference(double.class));
		sample.getField("i").replace(replacement);
		Assert.assertNull(sample.getField("i"));
		Assert.assertNotNull(sample.getField("j"));
		Assert.assertEquals(factory.Type().createReference(double.class), sample.getField("j").getType());
	}

	@Test
	public void testReplaceMethod() {
		CtClass<?> sample = factory.Package().get("spoon.test.replace")
				.getType("Foo");

		Assert.assertNotNull(sample.getMethod("foo"));
		Assert.assertNull(sample.getMethod("notfoo"));

		CtMethod bar = factory.Core().createMethod();
		bar.setSimpleName("notfoo");
		bar.setType(factory.Type().createReference(void.class));
		sample.getMethod("foo").replace(bar);

		Assert.assertNull(sample.getMethod("foo"));
		Assert.assertNotNull(sample.getMethod("notfoo"));
	}

	@Test
	public void testReplaceExpression() {
		CtMethod<?> sample = factory.Package().get("spoon.test.replace")
				.getType("Foo").getMethod("foo");

		CtVariable<?> var = sample.getBody().getStatement(0);

		Assert.assertTrue(var.getDefaultExpression() instanceof CtLiteral);
		Assert.assertEquals(3, ((CtLiteral<?>) var.getDefaultExpression()).getValue());

		CtLiteral replacement = factory.Core().createLiteral();
		replacement.setValue(42);
		var.getDefaultExpression().replace(replacement);

		Assert.assertEquals(42, ((CtLiteral<?>) var.getDefaultExpression()).getValue());

	}

	@Test
	public void testReplaceStatement() {
		CtMethod<?> sample = factory.Package().get("spoon.test.replace")
				.getType("Foo").getMethod("foo"); 

		Assert.assertTrue(sample.getBody().getStatement(0) instanceof CtVariable);

		CtStatement replacement = factory.Core().createInvocation();
		sample.getBody().getStatement(0).replace(replacement);

		Assert.assertTrue(sample.getBody().getStatement(0) instanceof CtInvocation);
	}

}
