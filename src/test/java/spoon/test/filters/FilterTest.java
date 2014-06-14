package spoon.test.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.RegexFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.test.TestUtils;

public class FilterTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/filters/Foo.java"))
				.build();
	}

	@Test
	public void testFilters() throws Exception {

		CtClass<?> foo = factory.Package().get("spoon.test.filters")
				.getType("Foo");
		assertEquals("Foo", foo.getSimpleName());

		List<CtExpression<?>> expressions = foo
				.getElements(new RegexFilter<CtExpression<?>>(".* = .*"));

		assertEquals(2, expressions.size());

	}

	@Test
	public void typeFilter() throws Exception {
		Factory factory = TestUtils.build("spoon.test", "SampleClass").getFactory();
		Class<CtMethod> filterClass = CtMethod.class;
		TypeFilter<CtMethod> statementFilter = new TypeFilter<CtMethod>(filterClass);
		List<CtMethod> elements = Query.getElements(factory, statementFilter);
		for (CtMethod element : elements) {
			assertTrue(filterClass.isInstance(element));
		}
	}
	
	@Test
	public void compositeFilter1() throws Exception {
		Factory factory = TestUtils.build("spoon.test", "SampleClass").getFactory();
		TypeFilter<CtMethod> statementFilter = new TypeFilter<CtMethod>(CtMethod.class);
		TypeFilter<CtMethodImpl> statementImplFilter = new TypeFilter<CtMethodImpl>(CtMethodImpl.class);
		CompositeFilter compositeFilter = new CompositeFilter(FilteringOperator.INTERSECTION, statementFilter, statementImplFilter);
		
		List<CtMethod> elementsA = Query.getElements(factory, statementFilter);
		List<CtMethodImpl> elementsB = Query.getElements(factory, statementImplFilter);
		
		assertEquals(elementsA.size(), elementsB.size());
		assertEquals(elementsA, elementsB);
		
		List elements = Query.getElements(factory, compositeFilter);
		
		assertEquals(elementsA.size(), elements.size());
		assertEquals(elementsA, elements);
	}
	
	@Test
	public void compositeFilter2() throws Exception {
		Factory factory = TestUtils.build("spoon.test", "SampleClass").getFactory();
		TypeFilter<CtNewClass> newClassFilter = new TypeFilter<CtNewClass>(CtNewClass.class);
		TypeFilter<CtMethod> statementFilter = new TypeFilter<CtMethod>(CtMethod.class);
		CompositeFilter compositeFilter = new CompositeFilter(FilteringOperator.UNION, statementFilter, newClassFilter);
		
		List elements = Query.getElements(factory, compositeFilter);
		List<CtMethod> methods = Query.getElements(factory, statementFilter);
		List<CtNewClass> newClasses = Query.getElements(factory, newClassFilter);
		
		List<CtElement> union = new ArrayList<CtElement>();
		union.addAll(methods);
		union.addAll(newClasses);
		
		assertEquals(methods.size() + newClasses.size(), union.size());
		assertEquals(union.size(), elements.size());
		assertTrue(elements.containsAll(union));
	}
	
	@Test
	public void compositeFilter3() throws Exception {
		Factory factory = TestUtils.build("spoon.test", "SampleClass").getFactory();
		NameFilter<CtVariable<?>> nameFilterA = new NameFilter<CtVariable<?>>("j");
		NameFilter<CtVariable<?>> nameFilterB = new NameFilter<CtVariable<?>>("k");
		CompositeFilter compositeFilter = new CompositeFilter(FilteringOperator.INTERSECTION, nameFilterA, nameFilterB);
		
		List elements = Query.getElements(factory, compositeFilter);
		assertTrue(elements.isEmpty());
	}
}
