package spoon.test.ctElement;

import org.junit.Test;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.test.ctElement.testclasses.Returner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static spoon.testing.utils.ModelUtils.build;

public class MetadataTest {


	@Test
	public void testMetadata() throws Exception {

		final Factory factory = build(Returner.class);
		final CtClass<Returner> returnerClass = factory.Class().get(Returner.class);
		final CtMethod<?> staticMethod = returnerClass.getMethodsByName("get").get(0);
		final CtReturn<Integer> ret = staticMethod.getBody().getLastStatement();

		final CtMethod<?> staticMethod2 = returnerClass.getMethodsByName("get2").get(0);
		final CtReturn<Integer> ret2 = staticMethod2.getBody().getLastStatement();

		ret.putMetadata("foo", "bar");
		ret.putMetadata("fiz", 1);

		assertNotNull(ret.getMetadata("fiz"));
		assertNull(ret2.getMetadata("fiz"));
		assertEquals(1, ret.getMetadata("fiz"));
		assertEquals("bar", ret.getMetadata("foo"));
	}
}
