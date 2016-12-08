package spoon.test.reference;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractReferenceFilter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by gerard on 21/11/2014.
 */
public class ExecutableReferenceGenericTest {

	private Factory factory;
	public static final String NAME_MY_CLASS_1 = "MyClass";

	@Before
	public void setUp() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();

		SpoonCompiler compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/reference/MyClass.java",
						"./src/test/java/spoon/test/reference/MyClass2.java",
						"./src/test/java/spoon/test/reference/MyClass3.java"));

		compiler.build();
	}

	@Test
	public void testReferencesBetweenConstructors() throws Exception {
		final List<CtConstructor<?>> constructors = getConstructorsByClass("MyClass");

		CtConstructor<?> emptyConstructor = constructors.get(0);
		CtConstructor<?> oneParamConstructor = constructors.get(1);
		CtConstructor<?> twoParamsConstructor = constructors.get(2);

		// Empty constructor which has a reference to the constructor with one parameter.
		List<CtExecutableReference<?>> refConstructors = getCtConstructorsByCtConstructor(emptyConstructor);
		assertEquals(1, refConstructors.size());
		assertEquals(1, refConstructors.get(0).getDeclaration().getParameters().size());
		assertEquals(oneParamConstructor, refConstructors.get(0).getDeclaration());

		// Constructor with one parameter which has a reference to the constructor with two parameter.
		refConstructors = getCtConstructorsByCtConstructor(oneParamConstructor);
		assertEquals(1, refConstructors.size());
		assertEquals(2, refConstructors.get(0).getDeclaration().getParameters().size());
		assertEquals(twoParamsConstructor, refConstructors.get(0).getDeclaration());
	}

	@Test
	public void testReferencesBetweenConstructorsInOtherClass() throws Exception {
		final List<CtConstructor<?>> constructors = getConstructorsByClass("MyClass2");
		final CtConstructor<?> ctConstructor = constructors.get(0);
		final List<CtExecutableReference<?>> refConstructors = getCtConstructorsReferencedInCtConstructor(ctConstructor);

		final CtClass<?> clazz1 = getCtClassByName("MyClass");
		final CtConstructor<?> emptyConstructorClass1 = getConstructorsByClass(clazz1.getSimpleName()).get(0);
		final CtClass<?> clazz3 = getCtClassByName("MyClass3");
		final CtConstructor<?> emptyConstructorClass3 = getConstructorsByClass(clazz3.getSimpleName()).get(0);

		assertEquals(3, refConstructors.size());
		assertEquals(0, emptyConstructorClass1.getParameters().size());
		assertEquals(0, emptyConstructorClass3.getParameters().size());
		assertNull(refConstructors.get(0).getDeclaration()); // reference to Object constructor.
		assertNotNull(refConstructors.get(0).getExecutableDeclaration());
		assertEquals(emptyConstructorClass1, refConstructors.get(1).getDeclaration());
		assertEquals(emptyConstructorClass3, refConstructors.get(2).getDeclaration());
	}

	@Test
	public void testOneReferenceBetweenMethodsInSameClass() throws Exception {
		final CtClass<?> clazz = getCtClassByName("MyClass");

		CtMethod<?> method1 = getCtMethodByNameFromCtClass(clazz, "method1");
		List<CtExecutableReference<?>> refsMethod1 = getReferencesOfAMethod(method1);
		CtMethod<?> expected = getCtMethodByNameFromCtClass(clazz, "method2");

		assertEquals(1, refsMethod1.size());
		assertEquals(expected, refsMethod1.get(0).getDeclaration());
	}

	@Test
	public void testMultiReferenceBetweenMethodsWithGenericInSameClass() throws Exception {
		final CtClass<?> clazz = getCtClassByName("MyClass");

		CtMethod<?> method2 = getCtMethodByNameFromCtClass(clazz, "method2");
		List<CtExecutableReference<?>> refsMethod2 = getReferencesOfAMethod(method2);
		CtMethod<?> expectedMethod1 = getCtMethodByNameFromCtClass(clazz, "method1");
		CtMethod<?> expectedMethod5 = getCtMethodByNameFromCtClass(clazz, "method5");

		assertEquals(3, refsMethod2.size());
		assertEquals(expectedMethod1, refsMethod2.get(0).getDeclaration());
		assertEquals(expectedMethod1, refsMethod2.get(1).getDeclaration());
		assertEquals(expectedMethod5, refsMethod2.get(2).getDeclaration());
	}

	@Test
	public void testMultiReferencesBetweenMethodsWithoutGenericInSameClass() throws Exception {
		final CtClass<?> clazz = getCtClassByName("MyClass");

		CtMethod<?> method3 = getCtMethodByNameFromCtClass(clazz, "method3");
		List<CtExecutableReference<?>> refsMethod3 = getReferencesOfAMethod(method3);
		CtMethod<?> expectedMethod2 = getCtMethodByNameFromCtClass(clazz, "method2");
		CtMethod<?> expectedMethod4 = getCtMethodByNameFromCtClass(clazz, "method4");

		assertEquals(2, refsMethod3.size());
		assertEquals(expectedMethod2, refsMethod3.get(0).getDeclaration());
		assertEquals(expectedMethod4, refsMethod3.get(1).getDeclaration());
	}

	@Test
	public void testMethodWithoutReferences() throws Exception {
		final CtClass<?> clazz = getCtClassByName("MyClass");

		CtMethod<?> method4 = getCtMethodByNameFromCtClass(clazz, "method4");
		List<CtExecutableReference<?>> refsMethod4 = getReferencesOfAMethod(method4);

		assertEquals(0, refsMethod4.size());
	}

	@Test
	public void testMethodGenericWithoutReferences() throws Exception {
		final CtClass<?> clazz = getCtClassByName("MyClass");

		CtMethod<?> method5 = getCtMethodByNameFromCtClass(clazz, "method5");
		List<CtExecutableReference<?>> refsMethod5 = getReferencesOfAMethod(method5);

		assertEquals(0, refsMethod5.size());
	}

	@Test
	public void testOneReferenceWithGenericMethodOutOfTheClass() throws Exception {
		final CtClass<?> clazz = getCtClassByName("MyClass");
		final CtClass<?> clazz2 = getCtClassByName("MyClass2");

		CtMethod<?> methodA = getCtMethodByNameFromCtClass(clazz2, "methodA");
		List<CtExecutableReference<?>> refsMethodA = getReferencesOfAMethod(methodA);
		CtMethod<?> expectedMethod1 = getCtMethodByNameFromCtClass(clazz, "method1");

		assertEquals(1, refsMethodA.size());
		assertEquals(expectedMethod1, refsMethodA.get(0).getDeclaration());
	}

	@Test
	public void testOneReferenceWithMethodNotGenericOutOfTheClass() throws Exception {
		final CtClass<?> clazz = getCtClassByName("MyClass");
		final CtClass<?> clazz2 = getCtClassByName("MyClass2");

		CtMethod<?> methodB = getCtMethodByNameFromCtClass(clazz2, "methodB");
		List<CtExecutableReference<?>> refsMethodB = getReferencesOfAMethod(methodB);
		CtMethod<?> expectedMethod2 = getCtMethodByNameFromCtClass(clazz, "method2");

		assertEquals(1, refsMethodB.size());
		assertEquals(expectedMethod2, refsMethodB.get(0).getDeclaration());
	}

	@Test
	public void testMultiReferenceWithGenericMethodOutOfTheClass() throws Exception {
		final CtClass<?> clazz2 = getCtClassByName("MyClass2");
		final CtClass<?> clazz3 = getCtClassByName("MyClass3");

		CtMethod<?> methodC = getCtMethodByNameFromCtClass(clazz2, "methodC");
		List<CtExecutableReference<?>> refsMethodC = getReferencesOfAMethod(methodC);
		CtMethod<?> expectedMethodI = getCtMethodByNameFromCtClass(clazz3, "methodI");
		CtMethod<?> expectedMethodII = getCtMethodByNameFromCtClass(clazz3, "methodII");

		assertEquals(2, refsMethodC.size());
		assertEquals(expectedMethodI, refsMethodC.get(0).getDeclaration());
		assertEquals(expectedMethodII, refsMethodC.get(1).getDeclaration());
	}

	@Test
	public void testReferencesBetweenMethods() throws Exception {
		final CtClass<?> clazz2 = getCtClassByName("MyClass2");

		CtMethod<?> methodD = getCtMethodByNameFromCtClass(clazz2, "methodD");

		// Method D references the method E.
		List<CtExecutableReference<?>> refsMethodD = getReferencesOfAMethod(methodD);
		CtMethod<?> expectedMethodE = getCtMethodByNameFromCtClass(clazz2, "methodE");
		assertEquals(1, refsMethodD.size());
		assertEquals(expectedMethodE, refsMethodD.get(0).getDeclaration());

		// Method E references the method F.
		List<CtExecutableReference<?>> refsMethodE = getReferencesOfAMethod(expectedMethodE);
		CtMethod<?> expectedMethodF = getCtMethodByNameFromCtClass(clazz2, "methodF");
		assertEquals(1, refsMethodE.size());
		assertEquals(expectedMethodF, refsMethodE.get(0).getDeclaration());
	}

	@Test
	public void testExecutableReferences() throws Exception {
		// factory has loaded MyClass, MyClass2 and MyClass3

		CtClass<?> classMyClass = Query.getElements(factory, new NameFilter<CtClass>("MyClass")).get(0);
		assertEquals("MyClass", classMyClass.getSimpleName());
		List<CtExecutableReference<?>> refsExecutableClass1 = Query.getElements(classMyClass,
				new AbstractReferenceFilter<CtExecutableReference<?>>(CtExecutableReference.class) {
					public boolean matches(CtExecutableReference<?> reference) {
						return true;
					}
				});

		CtClass<?> classMyClass2 =  Query.getElements(factory, new NameFilter<CtClass>("MyClass2")).get(0);
		assertEquals("MyClass2", classMyClass2.getSimpleName());
		List<CtExecutableReference<?>> refsExecutableClass2 = Query.getElements(classMyClass2,
				new AbstractReferenceFilter<CtExecutableReference<?>>(CtExecutableReference.class) {
					public boolean matches(CtExecutableReference<?> reference) {
						return true;
					}
				});

		assertEquals(11, refsExecutableClass1.size());
		for (CtExecutableReference<?> ref : refsExecutableClass1) {
			assertNotNull(ref);
			if (!ref.toString().equals("java.lang.Object#Object()")) {
				assertNotNull(ref.getDeclaration());
			}
		}

		assertEquals(9, refsExecutableClass2.size());
		for (CtExecutableReference<?> ref : refsExecutableClass2) {
			assertNotNull(ref);
			if (!ref.toString().equals("java.lang.Object#Object()")) {
				assertNotNull(ref.getDeclaration());
			}
		}
	}

	private List<CtConstructor<?>> getConstructorsByClass(final String myClass) {
		return Query.getElements(factory, new Filter<CtConstructor<?>>() {
			@Override
			public boolean matches(CtConstructor<?> element) {
				return myClass.equals(((CtClass<?>) element.getParent()).getSimpleName());
			}
		});
	}

	private List<CtExecutableReference<?>> getCtConstructorsByCtConstructor(CtConstructor<?> aConstructor) {
		if (aConstructor.getBody().getStatements().size() == 0) {
			return new ArrayList<>();
		}
		if (!(aConstructor.getBody().getStatement(0) instanceof CtInvocation)) {
			return new ArrayList<>();
		}
		final CtInvocation inv = aConstructor.getBody().getStatement(0);
		if (!inv.getExecutable().getSimpleName().equals(CtExecutableReference.CONSTRUCTOR_NAME)) {
			return new ArrayList<>();
		}
		return inv.getExecutable().getReferences(new AbstractReferenceFilter<CtExecutableReference<?>>(CtExecutableReference.class) {
			@Override
			public boolean matches(CtExecutableReference<?> reference) {
				return reference.isConstructor();
			}
		});
	}

	private List<CtExecutableReference<?>> getCtConstructorsReferencedInCtConstructor(CtConstructor<?> aConstructor) {
		return aConstructor.getElements(new AbstractReferenceFilter<CtExecutableReference<?>>(CtExecutableReference.class) {
			@Override
			public boolean matches(CtExecutableReference<?> reference) {
				return reference.isConstructor();
			}
		});
	}

	private CtClass<?> getCtClassByName(final String name) {
		return Query.getElements(factory, new Filter<CtClass<?>>() {
			@Override
			public boolean matches(CtClass<?> element) {
				return name.equals(element.getSimpleName());
			}
		}).get(0);
	}

	private List<CtExecutableReference<?>> getReferencesOfAMethod(CtMethod<?> method1) {
		return method1.getElements(new ReferenceTypeFilter<CtExecutableReference<?>>(CtExecutableReference.class));
	}

	private CtMethod<?> getCtMethodByNameFromCtClass(CtClass<?> clazz, String nameMethod5) {
		return clazz.getMethodsByName(nameMethod5).get(0);
	}
}
