package spoon.test.parent;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

public class TopLevelTypeTest
{
	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		spoon.setArgs(new String[] {"--output-type", "nooutput" });
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/parent/Foo.java"))
				.build();
	}


	@Test
	public void testTopLevelType() {
		CtClass<?> foo = factory.Class().get(Foo.class);
		assertEquals(foo, foo.getTopLevelType());
		CtMethod<?> internalClassMethod = foo.getMethod("internalClass");
		assertEquals(foo, internalClassMethod.getDeclaringType());
		assertEquals(foo, internalClassMethod.getTopLevelType());
		CtClass<?> internalClass = (CtClass<?>)internalClassMethod.getBody().getStatement(0);
		assertEquals(foo, internalClassMethod.getDeclaringType());
		assertEquals(foo, internalClassMethod.getTopLevelType());
		CtMethod<?> mm = internalClass.getMethod("m");
		assertEquals(internalClass, mm.getDeclaringType());
		assertEquals(foo, mm.getTopLevelType());
	}

}
