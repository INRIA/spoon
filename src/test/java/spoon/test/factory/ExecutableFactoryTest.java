package spoon.test.factory;

import org.junit.Assert;
import org.junit.Test;
import spoon.reflect.factory.ExecutableFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.List;

import static spoon.testing.utils.ModelUtils.createFactory;

public class ExecutableFactoryTest {

	@Test
	public void testCreateReference() {
		Factory f = createFactory();
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
}
