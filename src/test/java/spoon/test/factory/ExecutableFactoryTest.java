package spoon.test.factory;

import org.junit.Test;

import spoon.compiler.Environment;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.ExecutableFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
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
		ef.createReference(signature);
	}
}
