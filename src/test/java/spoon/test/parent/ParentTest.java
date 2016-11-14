package spoon.test.parent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.replace.testclasses.Tacos;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

public class ParentTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		spoon.setArgs(new String[] {"--output-type", "nooutput" });
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/parent/Foo.java"))
				.build();
	}

	@Test
	public void testParent() throws Exception {
		// toString should not throw a parent exception even if parents are not
		// set
		try {
			CtLiteral<Object> literal = factory.Core().createLiteral();
			literal.setValue(1);
			CtBinaryOperator<?> minus = factory.Core().createBinaryOperator();
			minus.setKind(BinaryOperatorKind.MINUS);
			minus.setRightHandOperand(literal);
			minus.setLeftHandOperand(literal);
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@Test
	public void testParentSet() throws Exception {
		CtClass<?> foo = factory.Package().get("spoon.test.parent")
				.getType("Foo");

		CtMethod<?> fooMethod = foo.getMethodsByName("foo").get(0);
		assertEquals("foo", fooMethod.getSimpleName());

		CtLocalVariable<?> localVar = (CtLocalVariable<?>) fooMethod.getBody()
				.getStatements().get(0);

		CtAssignment<?,?> assignment = (CtAssignment<?,?>) fooMethod.getBody()
				.getStatements().get(1);


		CtLiteral<?> newLit = factory.Code().createLiteral(0);
		localVar.setDefaultExpression((CtExpression) newLit);
		assertEquals(localVar, newLit.getParent());

		CtLiteral<?> newLit2 = factory.Code().createLiteral(1);
		assignment.setAssignment((CtExpression) newLit2);
		assertEquals(assignment, newLit2.getParent());

	}

	@Test
	public void testParentPackage() throws Exception {
		// addType should set Parent
		CtClass<?> clazz = factory.Core().createClass();
		clazz.setSimpleName("Foo");
		CtPackage pack = factory.Core().createPackage();
		pack.setSimpleName("bar");
		pack.addType(clazz);
		assertTrue(pack.getTypes().contains(clazz));
		assertEquals(pack, clazz.getParent());
	}

	@Test
	public void testParentOfCtPackageReference() throws Exception {
		// contract: a parent at a top level must be the root package and in the code, the element which call getParent().
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/reference-package");
		launcher.run();

		final CtType<Object> panini = launcher.getFactory().Type().get("Panini");

		CtElement topLevelParent = panini.getPackage().getParent();
		assertNotNull(topLevelParent);
		assertEquals(CtPackage.TOP_LEVEL_PACKAGE_NAME, panini.getPackage().getSimpleName());
		CtPackage pack1 = factory.Package().getRootPackage();

		// the factory are not the same
		assertNotEquals(factory, launcher.getFactory());
		// so the root packages are not deeply equals
		assertNotEquals(pack1, topLevelParent);

		final CtTypeReference<?> burritos = panini.getReferences(new ReferenceTypeFilter<CtTypeReference<?>>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference<?> reference) {
				return "Burritos".equals(reference.getSimpleName()) && super.matches(reference);
			}
		}).get(0);

		assertNotNull(burritos.getPackage().getParent());
		assertEquals("com.awesome", burritos.getPackage().getSimpleName());
		assertEquals(burritos, burritos.getPackage().getParent());
	}

	@Test
	public void testParentOfCtVariableReference() throws Exception {
		// contract: parent of a variable reference is the element which call getVariable().
		final Factory factory = build(Tacos.class);
		final CtType<Tacos> aTacos = factory.Type().get(Tacos.class);

		final CtInvocation inv = aTacos.getMethodsByName("m3").get(0).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		final CtVariableRead<?> variableRead = (CtVariableRead<?>) inv.getArguments().get(0);
		final CtParameterReference<?> aParameterReference = (CtParameterReference<?>) variableRead.getVariable();

		assertNotNull(aParameterReference.getParent());
		assertEquals(variableRead, aParameterReference.getParent());
	}

	@Test
	public void testParentOfCtExecutableReference() throws Exception {
		// contract: parent of a executable reference is the element which call getExecutable().
		final Factory factory = build(Tacos.class);
		final CtType<Tacos> aTacos = factory.Type().get(Tacos.class);

		final CtInvocation inv = aTacos.getMethodsByName("m3").get(0).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		final CtExecutableReference oldExecutable = inv.getExecutable();

		assertNotNull(oldExecutable.getParent());
		assertEquals(inv, oldExecutable.getParent());
	}

	@Test
	public void testParentOfGenericInTypeReference() throws Exception {
		// contract: parent of a generic in a type reference is the type reference.
		final Factory factory = build(Tacos.class);
		final CtTypeReference referenceWithGeneric = Query.getReferences(factory, new ReferenceTypeFilter<CtTypeReference>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference reference) {
				return reference.getActualTypeArguments().size() > 0 && super.matches(reference);
			}
		}).get(0);
		final CtTypeReference<?> generic = referenceWithGeneric.getActualTypeArguments().get(0);

		assertNotNull(generic.getParent());
		assertEquals(referenceWithGeneric, generic.getParent());
	}

	@Test
	public void testParentOfPrimitiveReference() throws Exception {
		// contract: parent of a primitive different isn't different of other type. Its parent is the element which used this type.
		final Factory factory = build(Tacos.class);
		final CtType<Tacos> aTacos = factory.Type().get(Tacos.class);
		final CtMethod<?> aMethod = aTacos.getMethodsByName("m").get(0);

		assertNotNull(aMethod.getType().getParent());
		assertEquals(factory.Type().INTEGER_PRIMITIVE, aMethod.getType());
		assertEquals(aMethod, aMethod.getType().getParent());
	}

	public static void checkParentContract(CtPackage pack) {
		for(CtElement elem: pack.getElements(new TypeFilter<>(CtElement.class))) {
			// there is always one parent
			Assert.assertNotNull("no parent for "+elem.getClass()+"-"+elem.getPosition(), elem.getParent());
		}

		// the scanner and the parent are in correspondence
		new CtScanner() {
			Deque<CtElement> elementStack = new ArrayDeque<CtElement>();
			@Override
			public void scan(CtElement e) {
				if (e==null) { return; }
				if (e instanceof CtReference) { return; }
				if (!elementStack.isEmpty()) {
					assertEquals(elementStack.peek(), e.getParent());
				}
				elementStack.push(e);
				e.accept(this);
				elementStack.pop();
			};
		}.scan(pack);

	}

	@Test
	public void testGetParentWithFilter() throws Exception {
		// addType should set Parent
		CtClass<Foo> clazz = (CtClass<Foo>) factory.Class().getAll().get(0);

		CtMethod<Object> m = clazz.getMethod("m");
		// get three = "" in one = two = three = "";
		CtExpression statement = ((CtAssignment)((CtAssignment)m.getBody().getStatement(3)).getAssignment()).getAssignment();
		CtPackage ctPackage = statement.getParent(new TypeFilter<CtPackage>(CtPackage.class));
		assertEquals(Foo.class.getPackage().getName(), ctPackage.getQualifiedName());

		CtStatement ctStatement = statement
				.getParent(new AbstractFilter<CtStatement>(CtStatement.class) {
					@Override
					public boolean matches(CtStatement element) {
						return element.getParent() instanceof CtStatementList && super.matches(element);
					}
				});
		// the filter has to return one = two = three = ""
		assertEquals(m.getBody().getStatement(3), ctStatement);

		m = clazz.getMethod("internalClass");
		CtStatement ctStatement1 = m.getElements(
				new AbstractFilter<CtStatement>(CtStatement.class) {
					@Override
					public boolean matches(CtStatement element) {
						return element instanceof CtLocalVariable && super.matches(element);
					}
				}).get(0);

		// get the top class
		ctStatement1.getParent(CtType.class);
		CtType parent = ctStatement1
				.getParent(new AbstractFilter<CtType>(CtType.class) {
					@Override
					public boolean matches(CtType element) {
						return !element.isAnonymous() && element.isTopLevel() && super.matches(element);
					}
				});
		assertEquals(clazz, parent);
		assertNotEquals(ctStatement1.getParent(CtType.class), parent);

		// not present element
		CtWhile ctWhile = ctStatement1.getParent(new TypeFilter<CtWhile>(CtWhile.class));
		assertEquals(null, ctWhile);

		CtStatement statementParent = statement
				.getParent(new AbstractFilter<CtStatement>(CtStatement.class) {
					@Override
					public boolean matches(CtStatement element) {
						return true;
					}
				});
		// getParent must not return the current element
		assertNotEquals(statement, statementParent);
	}

	@Test
	public void testHasParent() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/resources/reference-package/Panini.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		try {
			final CtType<Object> aPanini = launcher.getFactory().Type().get("Panini");
			assertNotNull(aPanini);
			assertFalse(aPanini.hasParent(aPanini.getFactory().Core().createAnnotation()));
			assertTrue(aPanini.getMethod("m").hasParent(aPanini));
		} catch (NullPointerException e) {
			fail();
		}
	}
}
