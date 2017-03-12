package spoon.test.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
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
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtQueryImpl;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.QueryFailurePolicy;
import spoon.reflect.visitor.chain.ScanningMode;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.CtScannerFunction;
import spoon.reflect.visitor.filter.FieldAccessFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import spoon.reflect.visitor.filter.InvocationFilter;
import spoon.reflect.visitor.filter.LineFilter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.OverriddenMethodFilter;
import spoon.reflect.visitor.filter.OverriddenMethodQuery;
import spoon.reflect.visitor.filter.OverridingMethodFilter;
import spoon.reflect.visitor.filter.ParentFunction;
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
		assertEquals("spoon.test.filters.testclasses.AbstractTostada$1", overridingMethods.get(3).getParent(CtClass.class).getQualifiedName());
		assertEquals(Antojito.class, overridingMethods.get(1).getParent(CtClass.class).getActualClass());
		assertEquals(SubTostada.class, overridingMethods.get(2).getParent(CtClass.class).getActualClass());
		assertEquals("spoon.test.filters.testclasses.Tostada$1", overridingMethods.get(0).getParent(CtClass.class).getQualifiedName());
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
		assertEquals(3, overridingMethods.size());
		assertEquals("spoon.test.filters.testclasses.AbstractTostada$1", overridingMethods.get(2).getParent(CtClass.class).getQualifiedName());
		assertEquals(SubTostada.class, overridingMethods.get(1).getParent(CtClass.class).getActualClass());
		assertEquals("spoon.test.filters.testclasses.Tostada$1", overridingMethods.get(0).getParent(CtClass.class).getQualifiedName());

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
		assertEquals(AbstractTostada.class, overridingMethods.get(3).getParent(CtType.class).getParent(CtClass.class).getActualClass());
		assertEquals("spoon.test.filters.testclasses.AbstractTostada", overridingMethods.get(1).getParent(CtClass.class).getQualifiedName());
		assertEquals(Tostada.class, overridingMethods.get(0).getParent(CtClass.class).getActualClass());
		assertEquals(Tacos.class, overridingMethods.get(2).getParent(CtClass.class).getActualClass());
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

		List<CtMethod<?>> overridingMethods = Query.getElements(launcher.getFactory(), new OverridingMethodFilter(anAbstractTostada.getMethodsByName("make").get(0)));
		assertEquals(2, overridingMethods.size());
		assertEquals("spoon.test.filters.testclasses.AbstractTostada$1", overridingMethods.get(0).getParent(CtClass.class).getQualifiedName());
		assertEquals(Tostada.class, overridingMethods.get(1).getParent(CtClass.class).getActualClass());

		final CtClass<Tostada> aTostada = launcher.getFactory().Class().get(Tostada.class);
		overridingMethods = Query.getElements(launcher.getFactory(), new OverridingMethodFilter(aTostada.getMethodsByName("make").get(0)));
		assertEquals(1, overridingMethods.size());
		assertEquals("spoon.test.filters.testclasses.AbstractTostada$1", overridingMethods.get(0).getParent(CtClass.class).getQualifiedName());
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
		assertEquals(0, aClass.getMethodsByName("prepare").get(0).map(new OverriddenMethodQuery()).list().size());
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

		OverriddenMethodFilter filter = new OverriddenMethodFilter(aITostada.getMethodsByName("make").get(0));
		List<CtMethod<?>> overridingMethods = Query.getElements(launcher.getFactory(), filter);
		assertEquals(0, overridingMethods.size());
		List<CtMethod> overridingMethods2 = aITostada.getMethodsByName("make").get(0).map(new OverriddenMethodQuery()).list(CtMethod.class);
		assertEquals(0, overridingMethods2.size());
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
		OverriddenMethodFilter filter = new OverriddenMethodFilter(aTostada.getMethodsByName("make").get(0));
		final List<CtMethod<?>> overriddenMethodsFromSub = Query.getElements(launcher.getFactory(), filter);
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
	
	@Test
	public void testReflectionBasedTypeFilter() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();

		//First collect all classes using tested TypeFilter
		List<CtClass<?>> allClasses = launcher.getFactory().Package().getRootPackage().getElements(new TypeFilter<CtClass<?>>(CtClass.class));
		assertTrue(allClasses.size()>0);
		allClasses.forEach(result->{
			assertTrue(result instanceof CtClass);
		});
		//then do it using Filter whose type is computed by reflection
		List<CtClass<?>> allClasses2 = launcher.getFactory().Package().getRootPackage().getElements(new Filter<CtClass<?>>() {
			@Override
			public boolean matches(CtClass<?> element) {
				return true;
			}
		});
		assertArrayEquals(allClasses.toArray(), allClasses2.toArray());

		//then do it using Filter implemented by lambda expression
		List<CtClass<?>> allClasses3 = launcher.getFactory().Package().getRootPackage().getElements((CtClass<?> element)->true);
		assertArrayEquals(allClasses.toArray(), allClasses3.toArray());
		
		//last try AbstractFilter constructor without class parameter
		final CtClass<Tacos> aTacos = launcher.getFactory().Class().get(Tacos.class);
		final CtInvocation<?> invSize = aTacos.getElements(new AbstractFilter<CtInvocation<?>>(/*no class is needed here*/) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				if (element.getExecutable() == null) {
					return false;
				}
				return "size".equals(element.getExecutable().getSimpleName()) && super.matches(element);
			}
		}).get(0);
		assertNotNull(invSize);
	}
	@Test
	public void testQueryStepScannWithConsumer() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		class Context {
			int counter = 0;
		}
		Context context = new Context();
		
		CtQuery l_qv = launcher.getFactory().getModel().getRootPackage().filterChildren(new TypeFilter<>(CtClass.class));
		
		assertEquals(0, context.counter);
		l_qv.forEach(cls->{
			assertTrue(cls instanceof CtClass);
			context.counter++;
		});
		assertTrue(context.counter>0);
	}
	
	@Test
	public void testQueryBuilderWithFilterChain() throws Exception {
		// contract: query methods can be lazy evaluated in a foreach
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		class Context {
			CtMethod<?> method;
			int count = 0;
		}
		
		Context context = new Context();

		// chaining queries
		CtQuery q = launcher.getFactory().Package().getRootPackage()
				.filterChildren(new TypeFilter<CtMethod<?>>(CtMethod.class))
				// using a lazily-evaluated lambda
				.map((CtMethod<?> method) -> {context.method = method;return method;})
				.map(new OverriddenMethodQuery());

		// actual evaluation
		q.forEach((CtMethod<?> method) -> {
			assertTrue(context.method.getReference().isOverriding(method.getReference()));
			context.count++;
		});
		// sanity check
		assertTrue(context.count>0);
	}
	
	@Test
	public void testFilterQueryStep() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		//Contract: the filter(Filter) can be used to detect if input of query step should pass to next query step.
		List<CtElement> realList = launcher.getFactory().Package().getRootPackage().filterChildren(e->{return true;}).select(new TypeFilter<>(CtClass.class)).list();
		List<CtElement> expectedList = launcher.getFactory().Package().getRootPackage().filterChildren(new TypeFilter<>(CtClass.class)).list();
		assertArrayEquals(expectedList.toArray(), realList.toArray());
		assertTrue(expectedList.size()>0);
	}

	@Test
	public void testFilterChildrenWithoutFilterQueryStep() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		//Contract: the filterChildren(null) without Filter returns same results like filter which returns true for each input.
		List<CtElement> list = launcher.getFactory().Package().getRootPackage().filterChildren(null).list();
		Iterator<CtElement> iter = list.iterator();
		launcher.getFactory().Package().getRootPackage().filterChildren(e->{return true;}).forEach(real->{
			//the elements produced by each query are same
			CtElement expected = iter.next();
			if(real!=expected) {
				assertEquals(expected, real);
			}
		});
		assertTrue(list.size()>0);
		assertTrue(iter.hasNext()==false);
	}

	@Test
	public void testFunctionQueryStep() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		class Context {
			int count = 0;
		}
		
		Context context = new Context();

		CtQuery query = launcher.getFactory().Package().getRootPackage().filterChildren((CtClass<?> c)->{return true;}).name("filter CtClass only")
			.map((CtClass<?> c)->c.getSuperInterfaces()).name("super interfaces")
			.map((CtTypeReference<?> iface)->iface.getTypeDeclaration())
			.map((CtType<?> iface)->iface.getAllMethods()).name("allMethods if interface")
			.map((CtMethod<?> method)->method.getSimpleName().equals("make"))
			.map((CtMethod<?> m)->m.getType())
			.map((CtTypeReference<?> t)->t.getTypeDeclaration());
		((CtQueryImpl)query).logging(true);
		query.forEach((CtInterface<?> c)->{
				assertEquals("ITostada", c.getSimpleName());
				context.count++;
			});
		assertTrue(context.count>0);
	}
	@Test
	public void testInvalidQueryStep() throws Exception {
		// contract: with default policy an exception is thrown is the input type of a query step
		// does not correspond to the output type of the previous step
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		try {
			launcher.getFactory().Package().getRootPackage().filterChildren((CtClass<?> c)->{return true;}).name("step1")
				.map((CtMethod<?> m)->m).name("invalidStep2")
				.map((o)->o).name("step3")
				.forEach((CtInterface<?> c)->{
					fail();
				});
			fail();
		} catch (SpoonException e) {
			assertTrue(e.getMessage().indexOf("Step invalidStep2) spoon.support.reflect.declaration.CtClassImpl cannot be cast to spoon.reflect.declaration.CtMethod")>=0);
		}
	}
	@Test
	public void testInvalidQueryStepFailurePolicyIgnore() throws Exception {
		// contract: with QueryFailurePolicy.IGNORE, no exception is thrown
		// and only valid elements are kept for the next step

		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		class Context {
			int count = 0;
		}
		Context context = new Context();
		
		launcher.getFactory().Package().getRootPackage().filterChildren((CtElement c)->{return true;}).name("step1")
			.map((CtMethod<?> m)->m).name("invalidStep2")
			.map((o)->o).name("step3")
			.failurePolicy(QueryFailurePolicy.IGNORE)
			.forEach((CtElement c)->{
				assertTrue(c instanceof CtMethod);
				context.count++;
			});
		assertTrue(context.count>0);
	}
	@Test
	public void testElementMapFunction() throws Exception {
		// contract: a map(Function) can be followed by a forEach(...) or by a list()
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		CtClass<?> cls = launcher.getFactory().Class().get(Tacos.class);
		cls.map((CtClass<?> c)->c.getParent())
			.forEach((CtElement e)->{
				assertEquals(cls.getParent(), e);
			});
		assertEquals(cls.getParent(), cls.map((CtClass<?> c)->c.getParent()).list().get(0));
	}
	@Test
	public void testElementMapFunctionOtherContracts() throws Exception {
		// contract: when a function returns an array, all non-null values are sent to the next step
		final Launcher launcher = new Launcher();
		CtQuery q = launcher.getFactory().Query().createQuery().map((String s)->new String[]{"a", null, s});
		List<String> list = q.setInput(null).list();
		assertEquals(0, list.size());
		
		list = q.setInput("c").list();
		assertEquals(2, list.size());
		assertEquals("a", list.get(0));
		assertEquals("c", list.get(1));

		// contract: the input is stored, and we can evaluate the same query several times
		list = q.list(); // using "c" as input
		assertEquals(2, list.size());
		assertEquals("a", list.get(0));
		assertEquals("c", list.get(1));

		// contract: when input is null then the query function is not called at all.
		CtQuery q2 = launcher.getFactory().Query().createQuery().map((String s)->{ throw new AssertionError();});
		assertEquals(0, q2.setInput(null).list().size());
	}
	@Test
	public void testElementMapFunctionNull() throws Exception {
		// contract: when a function returns null, it is discarded at the next step
		final Launcher launcher = new Launcher();
		CtQuery q = launcher.getFactory().Query().createQuery().map((String s)->null);
		List<String> list = q.setInput("c").list();
		assertEquals(0, list.size());
	}
	@Test
	public void testReuseOfQuery() throws Exception {
		// contract: a query created from an existing element can be reused on other inputs
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		CtClass<?> cls = launcher.getFactory().Class().get(Tacos.class);
		CtClass<?> cls2 = launcher.getFactory().Class().get(Tostada.class);

		// by default the query starts with "cls" as input
		CtQuery q = cls.map((CtClass c) -> c.getSimpleName());
		// high-level assert
		assertEquals(cls.getSimpleName(), q.list().get(0));
		// low-level assert on implementation
		assertEquals(1, ((CtQueryImpl)q).getInputs().size());
		assertSame(cls, ((CtQueryImpl)q).getInputs().get(0));

		// now changing the input of query to cls2
		q.setInput(cls2);
		//the input is still cls2
		assertEquals(cls2.getSimpleName(), q.list().get(0));
		assertEquals(1, ((CtQueryImpl)q).getInputs().size());
		assertSame(cls2, ((CtQueryImpl)q).getInputs().get(0));

	}
	@Test
	public void testReuseOfBaseQuery() throws Exception {
		// contract: an empty  query can be used on several inputs
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		CtClass<?> cls = launcher.getFactory().Class().get(Tacos.class);
		CtClass<?> cls2 = launcher.getFactory().Class().get(Tostada.class);

		// here is the query
		CtQuery q = launcher.getFactory().Query().createQuery().map((CtClass c) -> c.getSimpleName());
		// using it on a first input
		assertEquals("Tacos", q.setInput(cls).list().get(0));
		// using it on a second input
		assertEquals("Tostada", q.setInput(cls2).list().get(0));
	}



	// now testing map(CtConsumableFunction)

	@Test
	public void testElementMapConsumableFunction() throws Exception {
		// contract: a method map(CtConsumableFunction) is provided
		// a simple consumer.accept() is equivalent to a single return in a CtFunction

		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		CtClass<?> cls = launcher.getFactory().Class().get(Tacos.class);

		// long version
		class aFunction implements CtConsumableFunction<CtClass> {
			@Override
			public void apply(CtClass c, CtConsumer out) {
				// equivalent to a single return
				out.accept(c.getParent());
			}
		}
		assertEquals(cls.getParent(), cls.map(new aFunction()).list().get(0));

		// now the same with Java8 one-liner
		assertEquals(cls.getParent(), cls.map((CtClass<?> c, CtConsumer<Object> out)->out.accept(c.getParent())).list().get(0));
	}

	@Test
	public void testQueryInQuery() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		class Context {
			int count = 0;
		}
		
		Context context = new Context();
		
		CtClass<?> cls = launcher.getFactory().Class().get(Tacos.class);

		// first query
		CtQuery allChildPublicClasses = launcher.getFactory().Query().createQuery().filterChildren((CtClass clazz)->clazz.hasModifier(ModifierKind.PUBLIC));

		// second query,involving the first query
		CtQuery q = launcher.getFactory().Package().getRootPackage().map((CtElement in)->allChildPublicClasses.setInput(in).list());

		// now the assertions
		q.forEach((CtElement clazz)->{
			context.count++;
			assertTrue(clazz instanceof CtClass);
			assertTrue(((CtClass<?>)clazz).hasModifier(ModifierKind.PUBLIC));
		});
		assertEquals(6, context.count);
		context.count=0; //reset

		// again second query, but now with CtConsumableFunction
		CtQuery q2 = launcher.getFactory().Package().getRootPackage().map((CtElement in, CtConsumer<Object> out)->allChildPublicClasses.setInput(in).forEach(out));

		// now the assertions
		q2.forEach((CtElement clazz)->{
			context.count++;
			assertTrue(clazz instanceof CtClass);
			assertTrue(((CtClass<?>)clazz).hasModifier(ModifierKind.PUBLIC));
		});
		assertEquals(6, context.count);
		context.count=0; //reset

		// again second query, but with low-level circuitry thanks to cast
		CtQuery q3 = launcher.getFactory().Package().getRootPackage().map((in, out)->((CtQueryImpl)allChildPublicClasses).evaluate(in, out));

		// now the assertions
		q3.forEach((CtElement clazz)->{
			context.count++;
			assertTrue(clazz instanceof CtClass);
			assertTrue(((CtClass<?>)clazz).hasModifier(ModifierKind.PUBLIC));
		});
		assertEquals(6, context.count);
	}
	
	@Test
	public void testEmptyQuery() throws Exception {
		// contract: unbound or empty query

		final Launcher launcher = new Launcher();
		
		//contract: empty query returns no element
		assertEquals(0, launcher.getFactory().createQuery().list().size());
		assertEquals(0, launcher.getFactory().createQuery(null).list().size());
		//contract: empty query returns no element
		launcher.getFactory().createQuery().forEach(x->fail());
		launcher.getFactory().createQuery(null).forEach(x->fail());
		//contract: empty query calls no mapping
		assertEquals(0, launcher.getFactory().createQuery().map(x->{fail();return true;}).list().size());
		assertEquals(0, launcher.getFactory().createQuery(null).map(x->{fail();return true;}).list().size());
		//contract: empty query calls no filterChildren
		assertEquals(0, launcher.getFactory().createQuery().filterChildren(x->{fail();return true;}).list().size());
		assertEquals(0, launcher.getFactory().createQuery(null).filterChildren(x->{fail();return true;}).list().size());
	}
	
	@Test
	public void testBoundQuery() throws Exception {
		// contract: bound query, without any mapping

		final Launcher launcher = new Launcher();
		
		//contract: bound query returns bound element
		List<String> list = launcher.getFactory().createQuery("x").list();
		assertEquals(1, list.size());
		assertEquals("x", list.get(0));
	}

	@Test
	public void testClassCastExceptionOnForEach() throws Exception {
		// contract: bound query, without any mapping

		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		class Context {
			int count = 0;
		}
		
		Context context = new Context();
		//contract: if the query produces elements which cannot be cast to forEach consumer, then they are ignored
		launcher.getFactory().Package().getRootPackage().filterChildren(f->{return true;}).forEach((CtType t)->{
			context.count++;
		});
		assertTrue(context.count>0);
	}
	
	@Test
	public void testEarlyTerminatingQuery() throws Exception {
		// contract: a method first evaluates query until first element is found and then terminates the query

		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		class Context {
			boolean wasTerminated = false;
			void failIfTerminated(String place) {
				assertTrue("The "+place+" is called after query was terminated.", wasTerminated==false);
			}
		}
		
		Context context = new Context();
		
		CtMethod firstMethod = launcher.getFactory().Package().getRootPackage().filterChildren(e->{
			context.failIfTerminated("Filter#match of filterChildren");
			return true;
		}).map((CtElement e)->{
			context.failIfTerminated("Array returning CtFunction#apply of map");
			//send result twice to check that second item is skipped
			return new CtElement[]{e,e};
		}).map((CtElement e)->{
			context.failIfTerminated("List returning CtFunction#apply of map");
			//send result twice to check that second item is skipped
			return Arrays.asList(new CtElement[]{e,e});
		}).map((CtElement e, CtConsumer<Object> out)->{
			context.failIfTerminated("CtConsumableFunction#apply of map");
			if(e instanceof CtMethod) {
				//this call should pass and cause termination of the query
				out.accept(e);
				context.wasTerminated = true;
				//let it call out.accept(e); once more to check that next step is not called after query is terminated
			}
			out.accept(e);
		}).map(e->{
			context.failIfTerminated("CtFunction#apply of map after CtConsumableFunction");
			return e;
		}).first(CtMethod.class);
		
		assertTrue(firstMethod!=null);
		assertTrue(context.wasTerminated);
	}
	@Test
	public void testParentFunction() throws Exception {
		// contract: a mapping function which returns all parents of CtElement

		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		CtClass<?> cls = launcher.getFactory().Class().get(Tacos.class);
		CtLocalVariable<?> varStrings = cls.filterChildren(new NameFilter<>("strings")).first();
		
		class Context {
			CtElement expectedParent;
		}
		
		Context context = new Context();
		
		context.expectedParent = varStrings;
		
		varStrings.map(new ParentFunction()).forEach((parent)->{
			context.expectedParent = context.expectedParent.getParent();
			assertSame(context.expectedParent, parent);
		});
		
		//context.expectedParent is last visited element
		
		//Check that last visited element was root package
		assertSame(launcher.getFactory().getModel().getRootPackage(), context.expectedParent);
		
		//contract: if includingSelf(false), then parent of input element is first element
		assertSame(varStrings.getParent(), varStrings.map(new ParentFunction().includingSelf(false)).first());
		//contract: if includingSelf(true), then input element is first element
		assertSame(varStrings, varStrings.map(new ParentFunction().includingSelf(true)).first());
	}
	@Test
	public void testCtScannerListener() throws Exception {
		// contract: CtScannerFunction can be subclassed and configured by a CtScannerListener

		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput","--level","info" });
		launcher.addInputResource("./src/test/java/spoon/test/filters/testclasses");
		launcher.run();
		
		class Context {
			long nrOfEnter = 0;
			long nrOfEnterRetTrue = 0;
			long nrOfExit = 0;
			long nrOfResults = 0;
		}
		
		Context context1 = new Context();

		// scan only packages until top level classes. Do not scan class internals
		List<CtElement> result1 = launcher.getFactory().getModel().getRootPackage().map(new CtScannerFunction().setListener(new CtScannerListener() {
			@Override
			public ScanningMode enter(CtElement element) {
				context1.nrOfEnter++;
				if (element instanceof CtType) {
					return ScanningMode.SKIP_CHILDREN;
				}
				return ScanningMode.NORMAL;
			}
			@Override
			public void exit(CtElement element) {
				context1.nrOfExit++;
			}
			
		})).list();

		//check that test is visiting some nodes
		assertTrue(context1.nrOfEnter>0);
		assertTrue(result1.size()>0);
		//contract: if enter is called and returns SKIP_CHILDREN or NORMAL, then exit must be called too. Exceptions are ignored for now
		assertEquals(context1.nrOfEnter, context1.nrOfExit);

		Context context2 = new Context();
		
		Iterator iter = result1.iterator();
		
		//scan only from packages till top level classes. Do not scan class internals
		launcher.getFactory().getModel().getRootPackage().map(new CtScannerFunction().setListener(new CtScannerListener() {
			int inClass = 0;
			@Override
			public ScanningMode enter(CtElement element) {
				context2.nrOfEnter++;
				if(inClass>0) {
					//we are in class. skip this node and all children
					return ScanningMode.SKIP_ALL;
				}
				if (element instanceof CtType) {
					inClass++;
				}
				context2.nrOfEnterRetTrue++;
				return ScanningMode.NORMAL;
			}
			@Override
			public void exit(CtElement element) {
				context2.nrOfExit++;
				if (element instanceof CtType) {
					inClass--;
				}
				assertTrue(inClass==0 || inClass==1);
			}
			
		})).forEach(ele->{
			context2.nrOfResults++;
			assertTrue(ele instanceof CtPackage || ele instanceof CtType);
			//check that first and second query returned same results
			assertSame(ele, iter.next());
		});
		//check that test is visiting some nodes
		assertTrue(context2.nrOfEnter>0);
		assertTrue(context2.nrOfEnter>context2.nrOfEnterRetTrue);
		assertEquals(result1.size(), context2.nrOfResults);
		//contract: if enter is called and does not returns SKIP_ALL, then exit must be called too. Exceptions are ignored for now
		assertEquals(context2.nrOfEnterRetTrue, context2.nrOfExit);
	}
}
