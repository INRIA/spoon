package spoon.test.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FieldAccessFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import spoon.reflect.visitor.filter.InvocationFilter;
import spoon.reflect.visitor.filter.LineFilter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.OverriddenMethodFilter;
import spoon.reflect.visitor.filter.OverridingMethodFilter;
import spoon.reflect.visitor.filter.RegexFilter;
import spoon.reflect.visitor.filter.ReturnOrThrowFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.comparator.DeepRepresentationComparator;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.test.filters.testclasses.AbstractTostada;
import spoon.test.filters.testclasses.Antojito;
import spoon.test.filters.testclasses.FieldAccessFilterTacos;
import spoon.test.filters.testclasses.ITostada;
import spoon.test.filters.testclasses.SubTostada;
import spoon.test.filters.testclasses.Tacos;
import spoon.test.filters.testclasses.Tostada;
import spoon.testing.utils.ModelUtils;

public class FilterTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		factory = ModelUtils.build(Foo.class);
	}

	@Test
	public void testFilters() throws Exception {
		CtClass<?> foo = factory.Package().get("spoon.test.filters").getType("Foo");
		assertEquals("Foo", foo.getSimpleName());
		List<CtExpression<?>> expressions = foo.getElements(new RegexFilter<CtExpression<?>>(".* = .*"));
		assertEquals(2, expressions.size());
	}

	@Test
	public void testReturnOrThrowFilter() throws Exception {
		CtClass<?> foo = factory.Package().get("spoon.test.filters").getType("Foo");
		assertEquals("Foo", foo.getSimpleName());
		List<CtCFlowBreak> expressions = foo.getElements(new ReturnOrThrowFilter());
		assertEquals(2, expressions.size());
	}

	@Test
	public void testLineFilter() throws Exception {
		CtType<FooLine> foo = ModelUtils.buildClass(FooLine.class);
		CtMethod method = foo.getMethod("simple");
		List<CtStatement> expressions = method.getElements(new LineFilter());
		assertEquals(3, expressions.size());
		assertNull(expressions.get(0).getParent(new LineFilter()));

		method = foo.getMethod("loopBlock");
		expressions = method.getElements(new LineFilter());
		assertEquals(2, expressions.size());
		assertNull(expressions.get(0).getParent(new LineFilter()));
		assertTrue(expressions.get(1).getParent(new LineFilter()) instanceof CtLoop);

		method = foo.getMethod("loopNoBlock");
		expressions = method.getElements(new LineFilter());
		assertEquals(2, expressions.size());
		assertNull(expressions.get(0).getParent(new LineFilter()));
		assertTrue(expressions.get(1).getParent(new LineFilter()) instanceof CtLoop);

		method = foo.getMethod("ifBlock");
		expressions = method.getElements(new LineFilter());
		assertEquals(2, expressions.size());
		assertNull(expressions.get(0).getParent(new LineFilter()));
		assertTrue(expressions.get(1).getParent(new LineFilter()) instanceof CtIf);

		method = foo.getMethod("ifNoBlock");
		expressions = method.getElements(new LineFilter());
		assertEquals(2, expressions.size());
		assertNull(expressions.get(0).getParent(new LineFilter()));
		assertTrue(expressions.get(1).getParent(new LineFilter()) instanceof CtIf);

		method = foo.getMethod("switchBlock");
		expressions = method.getElements(new LineFilter());
		assertEquals(3, expressions.size());
		assertNull(expressions.get(0).getParent(new LineFilter()));
		assertTrue(expressions.get(1).getParent(new LineFilter()) instanceof CtSwitch);
		assertTrue(expressions.get(2).getParent(new LineFilter()) instanceof CtSwitch);
	}


	@Test
	public void testFieldAccessFilter() throws Exception {
		// also specifies VariableAccessFilter since FieldAccessFilter is only a VariableAccessFilter with additional static typing
		CtClass<?> foo = factory.Package().get("spoon.test.filters").getType("Foo");
		assertEquals("Foo", foo.getSimpleName());

		List<CtNamedElement> elements = foo.getElements(new NameFilter<>("i"));
		assertEquals(1, elements.size());

		CtFieldReference<?> ref = (CtFieldReference<?>)(elements.get(0)).getReference();
		List<CtFieldAccess<?>> expressions = foo.getElements(new FieldAccessFilter(ref));
		assertEquals(2, expressions.size());

		final Factory build = build(FieldAccessFilterTacos.class);
		final CtType<FieldAccessFilterTacos> fieldAccessFilterTacos = build.Type().get(FieldAccessFilterTacos.class);

		try {
			List<CtField> fields = fieldAccessFilterTacos.getElements(new TypeFilter<CtField>(CtField.class));
			for (CtField ctField : fields) {
				fieldAccessFilterTacos.getElements(new FieldAccessFilter(ctField.getReference()));
			}
		} catch (NullPointerException e) {
			fail("FieldAccessFilter must not throw a NPE.");
		}
	}


	@Test
	public void testAnnotationFilter() throws Exception {
		CtClass<?> foo = factory.Package().get("spoon.test.filters").getType("Foo");
		assertEquals("Foo", foo.getSimpleName());
		List<CtElement> expressions = foo.getElements(new AnnotationFilter<>(SuppressWarnings.class));
		assertEquals(2, expressions.size());
		List<CtMethod> methods = foo.getElements(new AnnotationFilter<>(CtMethod.class, SuppressWarnings.class));
		assertEquals(1, methods.size());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void filteredElementsAreOfTheCorrectType() throws Exception {
		Factory factory = build("spoon.test", "SampleClass").getFactory();
		Class<CtMethod> filterClass = CtMethod.class;
		TypeFilter<CtMethod> statementFilter = new TypeFilter<CtMethod>(filterClass);
		List<CtMethod> elements = Query.getElements(factory, statementFilter);
		for (CtMethod element : elements) {
			assertTrue(filterClass.isInstance(element));
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	public void intersectionOfTwoFilters() throws Exception {
		Factory factory = build("spoon.test", "SampleClass").getFactory();
		TypeFilter<CtMethod> statementFilter = new TypeFilter<CtMethod>(CtMethod.class);
		TypeFilter<CtMethodImpl> statementImplFilter = new TypeFilter<CtMethodImpl>(CtMethodImpl.class);
		CompositeFilter compositeFilter = new CompositeFilter(FilteringOperator.INTERSECTION, statementFilter, statementImplFilter);

		List<CtMethod> methodsWithInterfaceSuperclass = Query.getElements(factory, statementFilter);
		List<CtMethodImpl> methodWithConcreteClass = Query.getElements(factory, statementImplFilter);

		assertEquals(methodsWithInterfaceSuperclass.size(), methodWithConcreteClass.size());
		assertEquals(methodsWithInterfaceSuperclass, methodWithConcreteClass);

		List intersection = Query.getElements(factory, compositeFilter);

		assertEquals(methodsWithInterfaceSuperclass.size(), intersection.size());
		assertEquals(methodsWithInterfaceSuperclass, intersection);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	public void unionOfTwoFilters() throws Exception {
		Factory factory = build("spoon.test", "SampleClass").getFactory();
		TypeFilter<CtNewClass> newClassFilter = new TypeFilter<CtNewClass>(CtNewClass.class);
		TypeFilter<CtMethod> methodFilter = new TypeFilter<CtMethod>(CtMethod.class);
		CompositeFilter compositeFilter = new CompositeFilter(FilteringOperator.UNION, methodFilter, newClassFilter);

		List filteredWithCompositeFilter = Query.getElements(factory, compositeFilter);
		List<CtMethod> methods = Query.getElements(factory, methodFilter);
		List<CtNewClass> newClasses = Query.getElements(factory, newClassFilter);

		List<CtElement> union = new ArrayList<CtElement>();
		union.addAll(methods);
		union.addAll(newClasses);

		assertEquals(methods.size() + newClasses.size(), union.size());
		assertEquals(union.size(), filteredWithCompositeFilter.size());
		assertTrue(filteredWithCompositeFilter.containsAll(union));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Test
	public void classCastExceptionIsNotThrown() throws Exception {
		Factory factory = build("spoon.test", "SampleClass").getFactory();
		NameFilter<CtVariable<?>> nameFilterA = new NameFilter<CtVariable<?>>("j");
		NameFilter<CtVariable<?>> nameFilterB = new NameFilter<CtVariable<?>>("k");
		CompositeFilter compositeFilter = new CompositeFilter(FilteringOperator.INTERSECTION, nameFilterA, nameFilterB);
		List filteredWithCompositeFilter = Query.getElements(factory, compositeFilter);
		assertTrue(filteredWithCompositeFilter.isEmpty());
	}

	@Test
	public void testOverridingMethodFromAbstractClass() throws Exception {
		// contract: When we declare an abstract method on an abstract class, we must return all overriding
		// methods in sub classes and anonymous classes.
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();

		final CtClass<AbstractTostada> aClass = launcher.getFactory().Class().get(AbstractTostada.class);
		TreeSet<CtMethod<?>> ts = new TreeSet<CtMethod<?>>(new DeepRepresentationComparator());
		List<CtMethod<?>> elements = Query.getElements(launcher.getFactory(), new OverridingMethodFilter(aClass.getMethodsByName("prepare").get(0)));
		ts.addAll(elements);
		assertEquals(5, elements.size());
		final List<CtMethod<?>> overridingMethods = Arrays.asList(ts.toArray(new CtMethod[0]));
		assertEquals("spoon.test.filters.testclasses.AbstractTostada$1", overridingMethods.get(2).getParent(CtClass.class).getQualifiedName());
		assertEquals(Antojito.class, overridingMethods.get(0).getParent(CtClass.class).getActualClass());
		assertEquals(SubTostada.class, overridingMethods.get(1).getParent(CtClass.class).getActualClass());
		assertEquals("spoon.test.filters.testclasses.Tostada$1", overridingMethods.get(3).getParent(CtClass.class).getQualifiedName());
		assertEquals(Tostada.class, overridingMethods.get(4).getParent(CtClass.class).getActualClass());
	}

	@Test
	public void testOverridingMethodFromSubClassOfAbstractClass() throws Exception {
		// contract: When we ask all overriding methods from an overriding method, we must returns all methods
		// below and not above (including the declaration).
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();

		final CtClass<Tostada> aTostada = launcher.getFactory().Class().get(Tostada.class);

		TreeSet<CtMethod<?>> ts = new TreeSet<CtMethod<?>>(new DeepRepresentationComparator());
		List<CtMethod<?>> elements = Query.getElements(launcher.getFactory(), new OverridingMethodFilter(aTostada.getMethodsByName("prepare").get(0)));
		ts.addAll(elements);


		final List<CtMethod<?>> overridingMethods = Arrays.asList(ts.toArray(new CtMethod[0]));
		assertEquals(2, overridingMethods.size());
		assertEquals(SubTostada.class, overridingMethods.get(0).getParent(CtClass.class).getActualClass());
		assertEquals("spoon.test.filters.testclasses.Tostada$1", overridingMethods.get(1).getParent(CtClass.class).getQualifiedName());

		final CtClass<SubTostada> aSubTostada = launcher.getFactory().Class().get(SubTostada.class);
		assertEquals(0, Query.getElements(launcher.getFactory(), new OverridingMethodFilter(aSubTostada.getMethodsByName("prepare").get(0))).size());
	}

	@Test
	public void testOverridingMethodFromInterface() throws Exception {
		// contract: When we declare a method in an interface, we must return all overriding
		// methods in sub classes and anonymous classes.
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();

		final CtInterface<ITostada> aITostada = launcher.getFactory().Interface().get(ITostada.class);

		TreeSet<CtMethod<?>> ts = new TreeSet<CtMethod<?>>(new DeepRepresentationComparator());
		List<CtMethod<?>> elements = Query.getElements(launcher.getFactory(), new OverridingMethodFilter(aITostada.getMethodsByName("make").get(0)));
		ts.addAll(elements);
		final List<CtMethod<?>> overridingMethods = Arrays.asList(ts.toArray(new CtMethod[0]));
		assertEquals(4, overridingMethods.size());
		assertEquals(AbstractTostada.class, overridingMethods.get(0).getParent(CtType.class).getParent(CtClass.class).getActualClass());
		assertEquals("spoon.test.filters.testclasses.AbstractTostada", overridingMethods.get(1).getParent(CtClass.class).getQualifiedName());
		assertEquals(Tostada.class, overridingMethods.get(2).getParent(CtClass.class).getActualClass());
		assertEquals(Tacos.class, overridingMethods.get(3).getParent(CtClass.class).getActualClass());
	}

	@Test
	public void testOverridingMethodFromSubClassOfInterface() throws Exception {
		// contract: When we ask all overriding methods from an overriding method, we must returns all methods
		// below and not above (including the declaration).
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();

		final CtClass<AbstractTostada> anAbstractTostada = launcher.getFactory().Class().get(AbstractTostada.class);

		final List<CtMethod<?>> overridingMethods = Query.getElements(launcher.getFactory(), new OverridingMethodFilter(anAbstractTostada.getMethodsByName("make").get(0)));
		assertEquals(2, overridingMethods.size());
		assertEquals("spoon.test.filters.testclasses.AbstractTostada$1", overridingMethods.get(0).getParent(CtClass.class).getQualifiedName());
		assertEquals(Tostada.class, overridingMethods.get(1).getParent(CtClass.class).getActualClass());

		final CtClass<Tostada> aTostada = launcher.getFactory().Class().get(Tostada.class);
		assertEquals(0, Query.getElements(launcher.getFactory(), new OverridingMethodFilter(aTostada.getMethodsByName("make").get(0))).size());
	}

	@Test
	public void testOverriddenMethodFromAbstractClass() throws Exception {
		// contract: When we declare an abstract method on an abstract class, we must return an empty list
		// when we ask all overriden methods from this declaration.
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();

		final CtClass<AbstractTostada> aClass = launcher.getFactory().Class().get(AbstractTostada.class);

		assertEquals(0, Query.getElements(launcher.getFactory(), new OverriddenMethodFilter(aClass.getMethodsByName("prepare").get(0))).size());
	}

	@Test
	public void testOverriddenMethodsFromSubClassOfAbstractClass() throws Exception {
		// contract: When we ask all overridden methods from an overriding method, we must returns all methods
		// above and not below.
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();

		final CtClass<Tostada> aTostada = launcher.getFactory().Class().get(Tostada.class);

		final List<CtMethod<?>> overridenMethods = Query.getElements(launcher.getFactory(), new OverriddenMethodFilter(aTostada.getMethodsByName("prepare").get(0)));
		assertEquals(1, overridenMethods.size());
		assertEquals(AbstractTostada.class, overridenMethods.get(0).getParent(CtClass.class).getActualClass());

		final CtClass<SubTostada> aSubTostada = launcher.getFactory().Class().get(SubTostada.class);
		final List<CtMethod<?>> overridenMethodsFromSub = Query.getElements(launcher.getFactory(), new OverriddenMethodFilter(aSubTostada.getMethodsByName("prepare").get(0)));
		assertEquals(2, overridenMethodsFromSub.size());
		assertEquals(AbstractTostada.class, overridenMethodsFromSub.get(0).getParent(CtClass.class).getActualClass());
		assertEquals(Tostada.class, overridenMethodsFromSub.get(1).getParent(CtClass.class).getActualClass());
	}

	@Test
	public void testOverriddenMethodFromInterface() throws Exception {
		// contract: When we declare a method in an interface, we must return an empty list
		// when we ask all overridden methods from this declaration.
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();

		final CtInterface<ITostada> aITostada = launcher.getFactory().Interface().get(ITostada.class);

		final List<CtMethod<?>> overridingMethods = Query.getElements(launcher.getFactory(), new OverriddenMethodFilter(aITostada.getMethodsByName("make").get(0)));
		assertEquals(0, overridingMethods.size());
	}

	@Test
	public void testOverriddenMethodFromSubClassOfInterface() throws Exception {
		// contract: When we ask all overridden methods from an overriding method, we must returns all methods
		// above and not below.
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();

		final CtClass<AbstractTostada> anAbstractTostada = launcher.getFactory().Class().get(AbstractTostada.class);

		final List<CtMethod<?>> overriddenMethods = Query.getElements(launcher.getFactory(), new OverriddenMethodFilter(anAbstractTostada.getMethodsByName("make").get(0)));
		assertEquals(1, overriddenMethods.size());
		assertEquals(ITostada.class, overriddenMethods.get(0).getParent(CtInterface.class).getActualClass());

		final CtClass<Tostada> aTostada = launcher.getFactory().Class().get(Tostada.class);
		final List<CtMethod<?>> overriddenMethodsFromSub = Query.getElements(launcher.getFactory(), new OverriddenMethodFilter(aTostada.getMethodsByName("make").get(0)));
		assertEquals(2, overriddenMethodsFromSub.size());
		assertEquals(AbstractTostada.class, overriddenMethodsFromSub.get(0).getParent(CtType.class).getActualClass());
		assertEquals(ITostada.class, overriddenMethodsFromSub.get(1).getParent(CtType.class).getActualClass());
	}

	@Test
	public void testInvocationFilterWithExecutableInLibrary() throws Exception {
		// contract: When we have an invocation of an executable declared in a library,
		// we can filter it and get the executable of the invocation.
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();

		final CtClass<Tacos> aTacos = launcher.getFactory().Class().get(Tacos.class);
		final CtInvocation<?> invSize = aTacos.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				if (element.getExecutable() == null) {
					return false;
				}
				return "size".equals(element.getExecutable().getSimpleName()) && super.matches(element);
			}
		}).get(0);

		final List<CtInvocation<?>> invocations = aTacos.getElements(new InvocationFilter(invSize.getExecutable()));

		assertEquals(1, invocations.size());
		final CtInvocation<?> expectedInv = invocations.get(0);
		assertNotNull(expectedInv);
		final CtExecutableReference<?> expectedExecutable = expectedInv.getExecutable();
		assertNotNull(expectedExecutable);
		assertEquals("size", expectedExecutable.getSimpleName());
		assertNull(expectedExecutable.getDeclaration());
		final CtExecutable<?> declaration = expectedExecutable.getExecutableDeclaration();
		assertNotNull(declaration);
		assertEquals("size", declaration.getSimpleName());
	}
}
