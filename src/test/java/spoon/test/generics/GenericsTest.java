package spoon.test.generics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;

import java.util.List;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

public class GenericsTest {

	@Test
	public void testModelBuildingTree() throws Exception {
		CtClass<?> type = build("spoon.test.generics", "Tree");
		assertEquals("Tree", type.getSimpleName());
		CtTypeParameterReference generic = (CtTypeParameterReference) type
				.getFormalTypeParameters().get(0);
		assertEquals("V", generic.getSimpleName());
		assertEquals("[java.io.Serializable, java.lang.Comparable]", generic
				.getBounds().toString());

		CtMethod<?> node5 = type.getElements(
				new NameFilter<CtMethod<?>>("node5")).get(0);
		assertEquals(
				"this.<java.lang.Class<? extends java.lang.Throwable>>foo()",
				node5.getBody().getStatement(0).toString());
	}

	@Test
	public void testModelBuildingGenericConstructor() throws Exception {
		CtClass<?> type = build("spoon.test.generics", "GenericConstructor");
		assertEquals("GenericConstructor", type.getSimpleName());
		CtTypeParameterReference generic = (CtTypeParameterReference) type
				.getElements(
						new TypeFilter<CtConstructor<?>>(CtConstructor.class))
				.get(0).getFormalTypeParameters().get(0);
		assertEquals("E", generic.getSimpleName());
	}

	@Test
	public void testDiamond2() throws Exception {
		CtClass<GenericConstructor> type = build("spoon.test.generics",
				"GenericConstructor");
		assertEquals("GenericConstructor", type.getSimpleName());
		CtConstructor<GenericConstructor> c = type.getConstructor();
		CtLocalVariable<?> var = c.getBody().getStatement(1);
		assertEquals("java.lang.Integer", var.getType()
				.getActualTypeArguments().get(0).getQualifiedName());
		CtNewClass<?> newClass = (CtNewClass<?>) var.getDefaultExpression();
		// diamond operator should have empty type arguments???
		assertTrue(newClass.getExecutable().getActualTypeArguments().isEmpty());
	}

	@Test
	public void testDiamond1() {
		Factory factory = new Factory(new DefaultCoreFactory(),
				new StandardEnvironment());
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"class Diamond {\n"
								+ "	java.util.List<String> f = new java.util.ArrayList<>();\n"
								+ "}").compile();
		CtField<?> f = clazz.getFields().get(0);
		CtNewClass<?> val = (CtNewClass<?>) f.getDefaultExpression();

		// the diamond is resolved to String
		assertEquals("java.lang.String", val.getType().getActualTypeArguments()
				.get(0).toString());
		assertEquals("new java.util.ArrayList<java.lang.String>()",
				val.toString());
	}

	@Test
	public void testModelBuildingSimilarSignatureMethods() throws Exception {
		CtClass<?> type = build("spoon.test.generics",
				"SimilarSignatureMethodes");
		List<CtNamedElement> methods = type
				.getElements(new NameFilter<CtNamedElement>("methode"));
		assertEquals(2, methods.size());
		CtTypeParameterReference generic = (CtTypeParameterReference) ((CtMethod<?>) methods
				.get(0)).getFormalTypeParameters().get(0);
		assertEquals("E", generic.getSimpleName());
		CtParameter<?> param = ((CtMethod<?>) methods.get(0)).getParameters()
				.get(0);
		assertEquals("E", param.getType().toString());
	}

	@Test
	public void testTypeParameterReference() throws Exception {
		CtClass<?> classThatBindsAGenericType = build("spoon.test.generics", "ClassThatBindsAGenericType");
		CtClass<?> classThatDefinesANewTypeArgument = (CtClass<?>) classThatBindsAGenericType.getPackage().getElements(new NameFilter("ClassThatDefinesANewTypeArgument")).get(0);

		CtTypeReference tr1 = classThatBindsAGenericType.getSuperclass();
        CtTypeReference trExtends = tr1.getActualTypeArguments().get(0);
		CtTypeReference tr2 = classThatDefinesANewTypeArgument.getFormalTypeParameters().get(0);
        CtTypeReference tr3 = classThatDefinesANewTypeArgument.getMethodsByName("foo").get(0).getParameters().get(0).getReference().getType();

        // an bound type is not an TypeParameterRefernce
        assertTrue(! (trExtends instanceof CtTypeParameterReference));

        // a declared type parameter is a CtTypeParameterReference
        assertTrue(tr2 instanceof CtTypeParameterReference);

        // a used type parameter T is a CtTypeParameterReference
        assertTrue(tr3 instanceof CtTypeParameterReference);

		assertEquals("File", trExtends.getSimpleName());
        assertEquals(java.io.File.class, trExtends.getActualClass());
		assertEquals("T", tr2.getSimpleName());
        assertEquals("T", tr3.getSimpleName());
	}
	
	
	@Test
	public void testBugCommonCollection() throws Exception {
		try {
			CtClass<?> type = build("spoon.test.generics", "BugCollection");

			CtField<?> INSTANCE = type.getElements(
					new NameFilter<CtField<?>>("INSTANCE")).get(0);
			// assertTrue(INSTANCE.getDefaultExpression().getType().getActualTypeArguments().get(0)
			// instanceof CtAnnonTypeParameterReference);
			assertEquals(
					"public static final spoon.test.generics.ACLass<?> INSTANCE = new spoon.test.generics.ACLass();",
					INSTANCE.toString());

			CtField<?> INSTANCE2 = type.getElements(
					new NameFilter<CtField<?>>("INSTANCE2")).get(0);
			INSTANCE2.getAnnotations().clear();
			assertEquals(
					"public static final spoon.test.generics.ACLass<?> INSTANCE2 = new spoon.test.generics.ACLass();",
					INSTANCE2.toString());

			CtClass<?> ComparableComparator = type
					.getPackage()
					.getElements(
							new NameFilter<CtClass<?>>("ComparableComparator"))
					.get(0);
			assertTrue(ComparableComparator
					.toString()
					.startsWith(
							"class ComparableComparator<E extends java.lang.Comparable<? super E>>"));

			CtField<?> x = type.getElements(new NameFilter<CtField<?>>("x"))
					.get(0);
			CtTypeReference<?> ref = x.getType();
			DefaultJavaPrettyPrinter pp = new DefaultJavaPrettyPrinter(
					new StandardEnvironment());
			assertFalse(pp.getContext().getIgnoreImport());

			// qualifed name
			assertEquals("java.util.Map$Entry", ref.getQualifiedName());

			// toString uses SignaturePrinter which also calls
			// getQualifiedName()
			assertEquals("java.util.Map$Entry", ref.toString());

			// now visitCtTypeReference
			assertEquals(java.util.Map.class, ref.getDeclaringType()
					.getActualClass());
			pp.visitCtTypeReference(ref);

			assertEquals("java.util.Map.Entry", pp.getResult().toString());

			CtField<?> y = type.getElements(new NameFilter<CtField<?>>("y"))
					.get(0);
			assertEquals("java.util.Map.Entry<?, ?> y;", y.toString());

			CtField<?> z = type.getElements(new NameFilter<CtField<?>>("z"))
					.get(0);
			assertEquals(
					"java.util.Map.Entry<java.lang.String, java.lang.Integer> z;",
					z.toString());

			// now as local variables
			CtLocalVariable<?> lx = type.getElements(
					new NameFilter<CtLocalVariable<?>>("lx")).get(0);
			assertEquals("java.util.Map.Entry lx", lx.toString());

			CtLocalVariable<?> ly = type.getElements(
					new NameFilter<CtLocalVariable<?>>("ly")).get(0);
			assertEquals("java.util.Map.Entry<?, ?> ly", ly.toString());

			CtLocalVariable<?> lz = type.getElements(
					new NameFilter<CtLocalVariable<?>>("lz")).get(0);
			assertEquals(
					"java.util.Map.Entry<java.lang.String, java.lang.Integer> lz",
					lz.toString());

			CtLocalVariable<?> it = type.getElements(
					new NameFilter<CtLocalVariable<?>>("it")).get(0);
			assertEquals("java.util.Iterator<java.util.Map.Entry<?, ?>> it",
					it.toString());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
