package spoon.test.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static spoon.test.TestUtils.build;

import org.junit.Assert;
import org.junit.Test;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.test.TestUtils;

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

		class MyCtMethod<T> extends CtMethodImpl<T>{};
		
		@SuppressWarnings("serial")
		CoreFactory cf = new DefaultCoreFactory() {
			@Override
			public <T> CtMethod<T> createMethod() {
				return new MyCtMethod<T>();				
			}
		};
		
		Factory myFactory = new FactoryImpl(cf, new StandardEnvironment());
		
		CtClass<?> type = TestUtils.build("spoon.test", "SampleClass", myFactory);
		
		CtMethod<?> m = type.getMethodsByName("method3").get(0);
		
		Assert.assertTrue(m instanceof MyCtMethod);
	}
}
