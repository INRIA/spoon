package spoon.test.ctElement;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.test.TestUtils;
import spoon.test.ctElement.testclasses.Returner;

public class MetadataTest {


	@Test
	public void testMetadata() throws Exception {

		final Factory factory = TestUtils.build(Returner.class);
		final CtClass<Returner> returnerClass = factory.Class().get(Returner.class);
		final CtMethod<?> staticMethod = returnerClass.getMethodsByName("get").get(0);
		final CtReturn<Integer> ret = staticMethod.getBody().getLastStatement();
		
		final CtMethod<?> staticMethod2 = returnerClass.getMethodsByName("get2").get(0);
		final CtReturn<Integer> ret2 = staticMethod2.getBody().getLastStatement();
		
		ret.putMetadata("foo", "bar");
		ret.putMetadata("fiz", 1);
		
		assertNotNull(ret.getMetadata("fiz"));
		assertNull(ret2.getMetadata("fiz"));
		assertEquals(ret.getMetadata("fiz"), 1);
		assertEquals(ret.getMetadata("foo"), "bar");
	}
}
