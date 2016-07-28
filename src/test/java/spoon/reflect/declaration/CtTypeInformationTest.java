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

		final CtType<?> extendObject = this.factory.Type().get(ExtendsObject.class);

		// only 1 method directly in this class
		Assert.assertEquals(1, extendObject.getMethods().size());

		// + 48 of ArrayList (in library)
		// + 12 of java.lang.Object
		Assert.assertEquals(1+12+48, extendObject.getAllMethods().size());

		final CtType<?> subClass = this.factory.Type().get(Subclass.class);
		assertEquals(2, subClass.getMethods().size());
		assertEquals(61+2, subClass.getAllMethods().size());

		CtTypeReference<?> superclass = subClass.getSuperclass();
		Assert.assertEquals(ExtendsObject.class.getName(), superclass.getQualifiedName());

		Assert.assertEquals(ExtendsObject.class.getName(), superclass.getQualifiedName());

		Assert.assertNotNull(superclass.getSuperclass());

		// test superclass of interface type reference
		Set<CtTypeReference<?>> superInterfaces = subClass.getSuperInterfaces();
		Assert.assertEquals(1, superInterfaces.size());
		CtTypeReference<?> superinterface = superInterfaces.iterator().next();
		Assert.assertEquals(Subinterface.class.getName(), superinterface.getQualifiedName());
		Assert.assertNull(superinterface.getSuperclass());

		// test superclass of interface
		final CtType<?> type2 = this.factory.Type().get(Subinterface.class);
		Assert.assertNull(type2.getSuperclass());

		// the interface abstract method and the implementation method have the same signature
		CtMethod<?> fooConcrete = subClass.getMethodsByName("foo").get(0);
		CtMethod<?> fooAbstract = type2.getMethodsByName("foo").get(0);
		assertEquals(fooConcrete.getSignature(), fooAbstract.getSignature());
		// yet they are different AST node
		Assert.assertNotEquals(fooConcrete, fooAbstract);

		assertEquals(subClass.getMethodsByName("foo").get(0).getSignature(),
				type2.getMethodsByName("foo").get(0).getSignature());

	}
}