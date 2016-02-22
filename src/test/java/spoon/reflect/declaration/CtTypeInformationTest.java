package spoon.reflect.declaration;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.build;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import spoon.reflect.declaration.testclasses.ExtendsObject;
import spoon.reflect.declaration.testclasses.Subclass;
import spoon.reflect.declaration.testclasses.Subinterface;
import spoon.reflect.declaration.testclasses.TestInterface;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

public class CtTypeInformationTest {
	private Factory factory;

	@Before
	public void setUp() throws Exception {
		factory = build(ExtendsObject.class, Subclass.class, Subinterface.class, TestInterface.class);
	}

	@Test
	public void testGetSuperclass() throws Exception {
		// test superclass of class
		final CtType<?> type = this.factory.Type().get(Subclass.class);

		CtTypeReference<?> superclass = type.getSuperclass();
		Assert.assertEquals(ExtendsObject.class.getName(), superclass.getQualifiedName());

		//        superclass = superclass.getSuperclass();
		//        Assert.assertEquals(Object.class.getName(), superclass.getQualifiedName());

		Assert.assertNull(superclass.getSuperclass());

		// test superclass of interface type reference
		Set<CtTypeReference<?>> superInterfaces = type.getSuperInterfaces();
		Assert.assertEquals(1, superInterfaces.size());
		CtTypeReference<?> superinterface = superInterfaces.iterator().next();
		Assert.assertEquals(Subinterface.class.getName(), superinterface.getQualifiedName());
		Assert.assertNull(superinterface.getSuperclass());

		assertEquals(2, type.getAllMethods().size());

		// test superclass of interface
		final CtType<?> type2 = this.factory.Type().get(Subinterface.class);
		Assert.assertNull(type2.getSuperclass());

		// the interface abstract method and the implementation method have the same signature
		CtMethod<?> fooConcrete = type.getMethodsByName("foo").get(0);
		CtMethod<?> fooAbstract = type2.getMethodsByName("foo").get(0);
		assertEquals(fooConcrete.getSignature(), fooAbstract.getSignature());
		// yet they are different AST node
		Assert.assertNotEquals(fooConcrete, fooAbstract);

		assertEquals(type.getMethodsByName("foo").get(0).getSignature(),
				type2.getMethodsByName("foo").get(0).getSignature());

	}
}