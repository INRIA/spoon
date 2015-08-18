package spoon.test.factory;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.ExecutableFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.test.TestUtils;

import static spoon.test.TestUtils.build;

public class ExecutableFactoryTest {

	@Test
	public void testCreateReference() {
		Factory f = TestUtils.createFactory();
		ExecutableFactory ef = f.Executable();
		String signature = "boolean Object#equals(Object)";
		CtExecutableReference<Object> eref = ef.createReference(signature);
		
		String type = eref.getType().getQualifiedName();
		String decltype = eref.getDeclaringType().getQualifiedName();
		String name = eref.getSimpleName();
		List<CtTypeReference<?>> params = eref.getParameters();
		List<CtTypeReference<?>> atas = eref.getActualTypeArguments();
		
		Assert.assertEquals("boolean",type);
		Assert.assertEquals("Object",decltype);
		Assert.assertEquals("equals",name);
		Assert.assertEquals(1,params.size());
		Assert.assertEquals(0,atas.size());
	}

	@Test
	public void testCreateReference2() throws Exception {
		CtClass<?> type = build("spoon.test.factory", "ExecutableTestClass");
		CtMethod<?> get = type.getMethodsByName("get").get(0);
		Assert.assertEquals(get.getFormalTypeParameters().size(), 1);
		Assert.assertEquals(get.getFormalTypeParameters().get(0).getSimpleName(), "E");
		CtExecutableReference<?> reference = get.getReference();
		Assert.assertEquals(reference.getActualTypeArguments().size(), 1);
		Assert.assertEquals(reference.getActualTypeArguments().get(0).getSimpleName(), "E");
	}
}
