package spoon.reflect.declaration;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.testclasses.ExtendsArrayList;
import spoon.reflect.declaration.testclasses.Subclass;
import spoon.reflect.declaration.testclasses.Subinterface;
import spoon.reflect.declaration.testclasses.TestInterface;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.visitor.ClassTypingContext;

import java.lang.reflect.Field;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.RandomAccess;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class CtTypeInformationTest {
	private Factory factory;

	@Before
	public void setUp() throws Exception {
		factory = build(ExtendsArrayList.class, Subclass.class, Subinterface.class, TestInterface.class);
	}

	@Test
	public void testClassTypingContextContinueScanning() throws Exception {

		final CtType<?> subClass = this.factory.Type().get(Subclass.class);
		final CtTypeReference<?> subinterface = this.factory.Type().createReference(Subinterface.class);
		final CtTypeReference<?> testInterface = this.factory.Type().createReference(TestInterface.class);
		final CtTypeReference<?> extendObject = this.factory.Type().createReference(ExtendsArrayList.class);
		final CtTypeReference<?> arrayList = this.factory.Type().createReference(ArrayList.class);
		final CtTypeReference<?> abstractList = this.factory.Type().createReference(AbstractList.class);
		final CtTypeReference<?> abstractCollection = this.factory.Type().createReference(AbstractCollection.class);
		final CtTypeReference<?> object = this.factory.Type().createReference(Object.class);

		{
			final ClassTypingContext ctc = (ClassTypingContext) this.factory.createTypeAdapter(subClass);
			//contract: at the beginning, the last resolved class is a subClass
			Assert.assertEquals(subClass.getQualifiedName(), getLastResolvedSuperclass(ctc).getQualifiedName());

			//contract: this.isSubttypeOf(this) == true
			Assert.assertTrue(ctc.isSubtypeOf(subClass.getReference()));
			//contract: ClassTypingContext did not scanned whole type hierarchy. It stopped on last class, which was needed to agree on isSubtypeOf
			Assert.assertEquals(subClass.getQualifiedName(), getLastResolvedSuperclass(ctc).getQualifiedName());

			Assert.assertTrue(ctc.isSubtypeOf(subinterface));
			//contract: ClassTypingContext did not scanned whole type hierarchy. It stopped on last class, which was needed to agree on isSubtypeOf
			Assert.assertEquals(subClass.getQualifiedName(), getLastResolvedSuperclass(ctc).getQualifiedName());

			Assert.assertTrue(ctc.isSubtypeOf(testInterface));
			//contract: ClassTypingContext did not scanned whole type hierarchy. It stopped on last class, which was needed to agree on isSubtypeOf
			Assert.assertEquals(subClass.getQualifiedName(), getLastResolvedSuperclass(ctc).getQualifiedName());

			Assert.assertTrue(ctc.isSubtypeOf(factory.createCtTypeReference(Comparable.class)));
			//contract: ClassTypingContext did not scanned whole type hierarchy. It stopped on last class, which was needed to agree on isSubtypeOf
			Assert.assertEquals(subClass.getQualifiedName(), getLastResolvedSuperclass(ctc).getQualifiedName());

			Assert.assertTrue(ctc.isSubtypeOf(extendObject));
			//contract: ClassTypingContext did not scanned whole type hierarchy. It stopped on last class, which was needed to agree on isSubtypeOf
			Assert.assertEquals(extendObject.getQualifiedName(), getLastResolvedSuperclass(ctc).getQualifiedName());

			Assert.assertTrue(ctc.isSubtypeOf(arrayList));
			//contract: ClassTypingContext#isSubtypeOf returns always the same results
			Assert.assertTrue(ctc.isSubtypeOf(extendObject));
			Assert.assertTrue(ctc.isSubtypeOf(subClass.getReference()));

			//contract: ClassTypingContext did not scanned whole type hierarchy. It stopped on last class, which was needed to agree on isSubtypeOf
			Assert.assertEquals(arrayList.getQualifiedName(), getLastResolvedSuperclass(ctc).getQualifiedName());

			Assert.assertTrue(ctc.isSubtypeOf(factory.createCtTypeReference(RandomAccess.class)));
			//contract: ClassTypingContext did not scanned whole type hierarchy. It stopped on last class, which was needed to agree on isSubtypeOf
			Assert.assertEquals(arrayList.getQualifiedName(), getLastResolvedSuperclass(ctc).getQualifiedName());

			Assert.assertTrue(ctc.isSubtypeOf(abstractList));
			//contract: ClassTypingContext did not scanned whole type hierarchy. It stopped on last class, which was needed to agree on isSubtypeOf
			Assert.assertEquals(abstractList.getQualifiedName(), getLastResolvedSuperclass(ctc).getQualifiedName());

			Assert.assertTrue(ctc.isSubtypeOf(abstractCollection));
			//contract: ClassTypingContext did not scanned whole type hierarchy. It stopped on last class, which was needed to agree on isSubtypeOf
			Assert.assertEquals(abstractCollection.getQualifiedName(), getLastResolvedSuperclass(ctc).getQualifiedName());

			Assert.assertTrue(ctc.isSubtypeOf(object));
			//contract: ClassTypingContext did not scanned whole type hierarchy. It stopped on last class - even on java.lang.Object, which was needed to agree on isSubtypeOf
			Assert.assertEquals(object.getQualifiedName(), getLastResolvedSuperclass(ctc).getQualifiedName());

			//contract: ClassTypingContext returns false on a type which is not a sub type of ctc scope.
			Assert.assertFalse(ctc.isSubtypeOf(factory.Type().createReference("java.io.InputStream")));
			//contract: ClassTypingContext must scans whole type hierarchy if detecting subtypeof on type which is not a supertype
			Assert.assertNull(getLastResolvedSuperclass(ctc));
			//contract: ClassTypingContext#isSubtypeOf returns always the same results
			Assert.assertTrue(ctc.isSubtypeOf(arrayList));
			Assert.assertTrue(ctc.isSubtypeOf(extendObject));
			Assert.assertTrue(ctc.isSubtypeOf(subClass.getReference()));
		}

		{
			//now try directly a type which is not a supertype
			final ClassTypingContext ctc2 = (ClassTypingContext) this.factory.createTypeAdapter(subClass);
			//contract: at the beginning, the last resolved class is a subClass
			Assert.assertEquals(subClass.getQualifiedName(), getLastResolvedSuperclass(ctc2).getQualifiedName());
			//contract: ClassTypingContext returns false on a type which is not a sub type of ctc scope.
			Assert.assertFalse(ctc2.isSubtypeOf(factory.Type().createReference("java.io.InputStream")));
			//contract: ClassTypingContext must scans whole type hierarchy if detecting subtypeof on type which is not a supertype
			Assert.assertNull(getLastResolvedSuperclass(ctc2));

			//contract: ClassTypingContext#isSubtypeOf returns always the same results
			Assert.assertTrue(ctc2.isSubtypeOf(arrayList));
			Assert.assertTrue(ctc2.isSubtypeOf(extendObject));
			Assert.assertTrue(ctc2.isSubtypeOf(subClass.getReference()));
		}
	}

	private CtTypeInformation getLastResolvedSuperclass(ClassTypingContext ctc) throws Exception {
		Field f = ClassTypingContext.class.getDeclaredField("lastResolvedSuperclass");
		f.setAccessible(true);
		return (CtTypeInformation) f.get(ctc);
	}

	@Test
	public void testGetAllMethodsReturnsTheRightNumber() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/ExtendsObject.java");
		launcher.buildModel();
		int nbMethodsObject = launcher.getFactory().Type().get(Object.class).getAllMethods().size();

		final CtType<?> extendsObject = launcher.getFactory().Type().get("test.ExtendsObject");
		assertEquals("It should contain only 'oneMethod' and 'toString' but also contains: " + StringUtils.join(extendsObject.getMethods(), "\n"), 2, extendsObject.getMethods().size());
		assertEquals(nbMethodsObject + 1, extendsObject.getAllMethods().size());
	}

	@Test
	public void testGetSuperclass() {
		final CtType<?> extendsArrayList = this.factory.Type().get(ExtendsArrayList.class);

		// only 1 method directly in this class
		Assert.assertEquals(1, extendsArrayList.getMethods().size());

		int nbMethodExtendedArrayList = extendsArrayList.getAllMethods().size();

		final CtType<?> subClass = this.factory.Type().get(Subclass.class);
		assertEquals(2, subClass.getMethods().size());

		// the abstract method from Comparable which is overridden should not be present in the model
		assertEquals(nbMethodExtendedArrayList + 2, subClass.getAllMethods().size());

		CtTypeReference<?> superclass = subClass.getSuperclass();
		Assert.assertEquals(ExtendsArrayList.class.getName(), superclass.getQualifiedName());

		Assert.assertEquals(ExtendsArrayList.class.getName(), superclass.getQualifiedName());

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

	@Test
	public void testGetAllMethodsWontReturnOverriddenMethod() {
		final CtType<?> subClass = this.factory.Type().get(Subclass.class);
		Set<CtMethod<?>> listCtMethods = subClass.getAllMethods();

		boolean detectedCompareTo = false;
		for (CtMethod<?> ctMethod : listCtMethods) {
			if ("compareTo".equals(ctMethod.getSimpleName())) {
				assertFalse(ctMethod.hasModifier(ModifierKind.ABSTRACT));
				assertFalse(ctMethod.getParameters().get(0).getType() instanceof CtTypeParameter);
				assertEquals("Object", ctMethod.getParameters().get(0).getType().getSimpleName());
				detectedCompareTo = true;
			}
		}

		assertTrue(detectedCompareTo);
	}
}
