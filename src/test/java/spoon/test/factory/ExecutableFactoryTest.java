package spoon.test.factory;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import spoon.compiler.Environment;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.ExecutableFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

public class ExecutableFactoryTest {

	@Test
	public void testCreateReference() {
		CoreFactory cf = new DefaultCoreFactory();
		Environment e = new StandardEnvironment();
		Factory f = new FactoryImpl(cf,e);
		ExecutableFactory ef = f.Executable();
		String signature = "boolean Object#equals(Object)";
		CtExecutableReference<Object> eref = ef.createReference(signature);
		
		String type = eref.getType().getQualifiedName();
		String decltype = eref.getDeclaringType().getQualifiedName();
		String name = eref.getSimpleName();
		List<CtTypeReference<?>> params = eref.getActualTypeArguments();
		
		Assert.assertEquals("boolean",type);
		Assert.assertEquals("Object",decltype);
		Assert.assertEquals("equals",name);
		Assert.assertEquals(1,params.size());		
	}
}
