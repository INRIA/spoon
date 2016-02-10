package spoon.test.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;
import static spoon.test.TestUtils.buildClass;
import static spoon.test.TestUtils.createFactory;

import org.junit.Assert;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.test.TestUtils;
import spoon.test.factory.testclasses.Foo;

import java.util.Arrays;
import java.util.List;

public class FactoryTest {

	@Test
	public void testClone() throws Exception {
		CtClass<?> type = build("spoon.test", "SampleClass");
		CtMethod<?> m = type.getMethodsByName("method3").get(0);
		int i = m.getBody().getStatements().size();

		m = m.getFactory().Core().clone(m);

		assertEquals(i, m.getBody().getStatements().size());
		// cloned elements must not have an initialized parent
		assertFalse(m.isParentInitialized());
	}

	@Test
	public void testFactoryOverriding()  throws Exception {

		@SuppressWarnings("serial")
		class MyCtMethod<T> extends CtMethodImpl<T>{};

		@SuppressWarnings("serial")
		final CoreFactory specialCoreFactory = new DefaultCoreFactory() {
			@Override
			public <T> CtMethod<T> createMethod() {
				return new MyCtMethod<T>();
			}
		};

		Launcher launcher = new Launcher() {
			@Override
			public Factory createFactory() {
				return new FactoryImpl(specialCoreFactory, new StandardEnvironment());
			}
		};

		CtClass<?> type = TestUtils.build("spoon.test", "SampleClass", launcher.getFactory());

		CtMethod<?> m = type.getMethodsByName("method3").get(0);

		Assert.assertTrue(m instanceof MyCtMethod);
	}

	@Test
	public void testClassAccessCreatedFromFactories() throws Exception {
		final CtType<Foo> foo = buildClass(Foo.class);

		assertEquals(1, foo.getAnnotations().size());
		assertEquals(0, foo.getAnnotations().get(0).getElementValues().size());

		foo.getFactory().Annotation().annotate(foo, Foo.Bar.class, "clazz", Foo.class);

		assertEquals(1, foo.getAnnotations().size());
		assertEquals(1, foo.getAnnotations().get(0).getElementValues().size());
		assertTrue(foo.getAnnotations().get(0).getElementValues().get("clazz") instanceof CtFieldRead);
		assertEquals("spoon.test.factory.testclasses.Foo.class", foo.getAnnotations().get(0).getElementValues().get("clazz").toString());

		foo.getFactory().Annotation().annotate(foo, Foo.Bar.class, "classes", new Class[] { Foo.class });

		assertEquals(1, foo.getAnnotations().size());
		assertEquals(2, foo.getAnnotations().get(0).getElementValues().size());
		assertTrue(foo.getAnnotations().get(0).getElementValues().get("classes") instanceof List); // this should change in a next release.
		assertEquals(1, ((List<CtExpression>) foo.getAnnotations().get(0).getElementValues().get("classes")).size());
		assertEquals("spoon.test.factory.testclasses.Foo.class", ((List<CtExpression>) foo.getAnnotations().get(0).getElementValues().get("classes")).get(0).toString());
	}
}
