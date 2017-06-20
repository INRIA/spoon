package spoon.test.generics;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.StandardEnvironment;
import spoon.support.comparator.CtLineElementComparator;
import spoon.support.util.SortedList;
import spoon.support.visitor.ClassTypingContext;
import spoon.support.visitor.GenericTypeAdapter;
import spoon.support.visitor.MethodTypingContext;
import spoon.test.ctType.testclasses.ErasureModelA;
import spoon.test.generics.testclasses.Banana;
import spoon.test.generics.testclasses.CelebrationLunch;
import spoon.test.generics.testclasses.CelebrationLunch.WeddingLunch;
import spoon.test.generics.testclasses.FakeTpl;
import spoon.test.generics.testclasses.Lunch;
import spoon.test.generics.testclasses.Mole;
import spoon.test.generics.testclasses.Orange;
import spoon.test.generics.testclasses.Paella;
import spoon.test.generics.testclasses.Panini;
import spoon.test.generics.testclasses.Spaghetti;
import spoon.test.generics.testclasses.Tacos;
import spoon.testing.utils.ModelUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.buildNoClasspath;
import static spoon.testing.utils.ModelUtils.canBeBuilt;
import static spoon.testing.utils.ModelUtils.createFactory;

public class GenericsTest {

	@Test
	public void testBugComparableComparator() throws Exception {
		CtClass<?> type = build("spoon.test.generics",
				"ComparableComparatorBug");

		assertEquals("ComparableComparatorBug", type.getSimpleName());

		CtField<?> field = type
				.getElements(
						new TypeFilter<CtField<?>>(CtField.class))
				.get(1);

		assertEquals(0, field.getType().getActualTypeArguments().size());
		assertEquals(0, ((CtConstructorCall<?>) field.getDefaultExpression()).getType().getActualTypeArguments().size());
	}

	@Test
	public void testModelBuildingTree() throws Exception {
		CtClass<?> type = build("spoon.test.generics", "Tree");
		assertEquals("Tree", type.getSimpleName());

		// New type parameter declaration.
		CtTypeParameter typeParameter = type.getFormalCtTypeParameters().get(0);
		assertEquals("V", typeParameter.getSimpleName());
		assertEquals("[java.io.Serializable, java.lang.Comparable<V>]", typeParameter.getSuperclass().asCtIntersectionTypeReference().getBounds().toString());

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
		CtTypeParameter typeParameter = type.getElements(new TypeFilter<CtConstructor<?>>(CtConstructor.class)).get(0).getFormalCtTypeParameters().get(0);
		assertEquals("E", typeParameter.getSimpleName());
	}

	@Test
	public void testDiamond2() throws Exception {
		CtClass<GenericConstructor> type = build("spoon.test.generics", "GenericConstructor");
		assertEquals("GenericConstructor", type.getSimpleName());
		CtConstructor<GenericConstructor> c = type.getConstructor();
		CtLocalVariable<?> var = c.getBody().getStatement(1);
		assertEquals("java.lang.Integer", var.getType().getActualTypeArguments().get(0).getQualifiedName());
		CtConstructorCall<?> constructorCall = (CtConstructorCall<?>) var.getDefaultExpression();
		// diamond operator should have empty type arguments???
		assertTrue(constructorCall.getExecutable().getActualTypeArguments().isEmpty());
	}

	@Test
	public void testDiamond1() {
		Factory factory = createFactory();
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"class Diamond {\n"
								+ "	java.util.List<String> f = new java.util.ArrayList<>();\n"
								+ "}").compile();
		CtField<?> f = clazz.getFields().get(0);
		CtConstructorCall<?> val = (CtConstructorCall<?>) f.getDefaultExpression();

		// the diamond is resolved to String but we don't print it, so we use the fully qualified name.
		assertTrue(val.getType().getActualTypeArguments().get(0).isImplicit());
		assertEquals("", val.getType().getActualTypeArguments().get(0).toString());
		assertEquals("java.lang.String", val.getType().getActualTypeArguments().get(0).getQualifiedName());
		assertEquals("new java.util.ArrayList<>()",val.toString());
	}

	@Test
	public void testModelBuildingSimilarSignatureMethods() throws Exception {
		CtClass<?> type = build("spoon.test.generics", "SimilarSignatureMethodes");
		List<CtNamedElement> methods = type.getElements(new NameFilter<CtNamedElement>("methode"));
		assertEquals(2, methods.size());
		CtTypeParameter typeParameter = ((CtMethod<?>) methods.get(0)).getFormalCtTypeParameters().get(0);
		assertEquals("E", typeParameter.getSimpleName());
		CtParameter<?> param = ((CtMethod<?>) methods.get(0)).getParameters().get(0);
		assertEquals("E", param.getType().toString());
	}

	@Test
	public void testTypeParameterReference() throws Exception {
		Factory factory = build(ClassThatBindsAGenericType.class, ClassThatDefinesANewTypeArgument.class);
		CtClass<?> classThatBindsAGenericType = factory.Class().get(ClassThatBindsAGenericType.class);
		CtClass<?> classThatDefinesANewTypeArgument = factory.Class().get(ClassThatDefinesANewTypeArgument.class);

		CtTypeReference<?> tr1 = classThatBindsAGenericType.getSuperclass();
		CtTypeReference<?> trExtends = tr1.getActualTypeArguments().get(0);
		CtTypeParameter tr2 = classThatDefinesANewTypeArgument.getFormalCtTypeParameters().get(0);
		CtTypeReference<?> tr3 = classThatDefinesANewTypeArgument.getMethodsByName("foo").get(0).getParameters().get(0).getReference().getType();

		// an bound type is not an TypeParameterRefernce
		assertTrue(!(trExtends instanceof CtTypeParameterReference));

		// a used type parameter T is a CtTypeParameterReference
		assertTrue(tr3 instanceof CtTypeParameterReference);

		assertEquals("File", trExtends.getSimpleName());
		assertEquals(java.io.File.class, trExtends.getActualClass());
		assertEquals("T", tr2.getSimpleName());
		assertEquals("T", tr3.getSimpleName());
	}

	@Test
	public void testTypeParameterDeclarer() throws Exception {
		// contract: one can lookup the declarer of a type parameter if it is in appropriate context (the declararer is in the parent hierarchy)
		CtClass<?> classThatDefinesANewTypeArgument = build("spoon.test.generics", "ClassThatDefinesANewTypeArgument");
		CtTypeParameter typeParam = classThatDefinesANewTypeArgument.getFormalCtTypeParameters().get(0);
		assertEquals("T", classThatDefinesANewTypeArgument.getFormalCtTypeParameters().get(0).getSimpleName());
		assertSame(classThatDefinesANewTypeArgument, typeParam.getTypeParameterDeclarer());
		CtTypeParameterReference typeParamReference = typeParam.getReference();
		assertSame(typeParam, typeParamReference.getDeclaration());

		// creating an appropriate context
		CtMethod m = classThatDefinesANewTypeArgument.getFactory().createMethod();
		m.setParent(classThatDefinesANewTypeArgument);
		// setting the return type of the method
		m.setType(typeParamReference);
		classThatDefinesANewTypeArgument.addMethod(m);

		// the final assertions
		assertSame(typeParam, typeParamReference.getDeclaration());

		assertSame(classThatDefinesANewTypeArgument, typeParamReference.getDeclaration().getParent());

		// now testing that the getDeclaration of a type parameter is actually a dynamic lookup
		CtClass<?> c2 = classThatDefinesANewTypeArgument.clone();
		c2.addMethod(m);
		assertSame(c2, typeParamReference.getDeclaration().getParent());
		// even if we rename it
		typeParamReference.setSimpleName("R"); // renaming the reference
		c2.getFormalCtTypeParameters().get(0).setSimpleName("R"); // renaming the declaration
		assertSame(c2, typeParamReference.getDeclaration().getParent());
	}

	@Test
	public void testGenericMethodCallWithExtend() throws Exception {
		CtClass<?> type = build("spoon.test.generics", "GenericMethodCallWithExtend");
		CtMethod<?> meth = type.getMethodsByName("methode").get(0);

		// an bound type is not an TypeParameterRefernce
		assertEquals("E extends java.lang.Enum<E>", meth.getFormalCtTypeParameters().get(0).toString());
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
			INSTANCE2.setAnnotations(new ArrayList<CtAnnotation<?>>());
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

			// qualifed name
			assertEquals("java.util.Map$Entry", ref.getQualifiedName());

			// toString uses DefaultJavaPrettyPrinter
			assertEquals("java.util.Map.Entry", ref.toString());

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

	@Test
	public void testInstanceOfMapEntryGeneric() throws Exception {
		CtClass<?> type = build("spoon.test.generics", "InstanceOfMapEntryGeneric");
		CtMethod<?> meth = type.getMethodsByName("methode").get(0);

		CtBinaryOperator<?> instOf = (CtBinaryOperator<?>) ((CtLocalVariable<?>) meth.getBody().getStatement(0)).getDefaultExpression();
		assertEquals(BinaryOperatorKind.INSTANCEOF, instOf.getKind());
		assertEquals("o instanceof java.util.Map.Entry<?, ?>", instOf.toString());
	}

	@Test
	public void testAccessToGenerics() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();

		SpoonModelBuilder compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources(
						"./src/test/java/spoon/test/generics/Foo.java",
						"./src/test/java/spoon/test/generics/Bar.java"));

		compiler.build();

		CtClass<?> foo = (CtClass<?>) factory.Type().get(Foo.class);
		CtInterface<?> bar = (CtInterface<?>) factory.Type().get(Bar.class);
		final CtNewClass<?> newAnonymousBar = foo.getElements(new AbstractFilter<CtNewClass<?>>(CtNewClass.class) {
			@Override
			public boolean matches(CtNewClass<?> element) {
				return element.getAnonymousClass() != null && element.getAnonymousClass().isAnonymous();
			}
		}).get(0);

		final List<CtTypeParameter> barTypeParamGenerics = bar.getFormalCtTypeParameters();
		final CtTypeReference<?> anonymousBar = newAnonymousBar.getType();

		assertEquals("Name of the first generic parameter in Bar interface must to be I.", "I", barTypeParamGenerics.get(0).getSimpleName());
		assertEquals("Name of the first generic parameter in Bar usage must to be K.", "K", anonymousBar.getActualTypeArguments().get(0).getSimpleName());

		assertEquals("Name of the second generic parameter in Bar interface must to be O.", "O", barTypeParamGenerics.get(1).getSimpleName());
		assertEquals("Name of the second generic parameter in Bar usage must to be V.", "V", anonymousBar.getActualTypeArguments().get(1).getSimpleName());
	}

	@Test
	public void testConstructorCallGenerics() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/generics/testclasses/",
				"-o", "./target/spooned/"
		});

		final CtClass<?> aTacos = launcher.getFactory().Class().get(Tacos.class);
		assertEquals(2, aTacos.getFormalCtTypeParameters().size());
		final CtTypeReference interfaces = aTacos.getSuperInterfaces().toArray(new CtTypeReference[0])[0];
		assertEquals(1, interfaces.getActualTypeArguments().size());

		final CtMethod<?> m = aTacos.getMethodsByName("m").get(0);
		final CtElement local1 = m.getBody().getStatement(0).getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);
		final CtTypeReference<Object> leftSideLocal1 = (CtTypeReference<Object>) local1.getElements(new ReferenceTypeFilter<>(CtTypeReference.class)).get(0);
		final CtConstructorCall<Object> rightSideLocal1 = (CtConstructorCall<Object>) local1.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals(1, leftSideLocal1.getActualTypeArguments().size());
		assertEquals(1, rightSideLocal1.getType().getActualTypeArguments().size());
		assertEquals("java.util.List<java.lang.String> l = new java.util.ArrayList<>()", local1.toString());

		final CtElement local2 = m.getBody().getStatement(1).getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);
		final CtTypeReference<Object> leftSideLocal2 = (CtTypeReference<Object>) local2.getElements(new ReferenceTypeFilter<>(CtTypeReference.class)).get(0);
		assertEquals(0, leftSideLocal2.getActualTypeArguments().size());
		assertEquals("java.util.List l2", local2.toString());

		final CtElement local3 = m.getBody().getStatement(2).getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);
		final CtTypeReference<Object> leftSideLocal3 = (CtTypeReference<Object>) local3.getElements(new ReferenceTypeFilter<>(CtTypeReference.class)).get(0);
		final CtConstructorCall<Object> rightSideLocal3 = (CtConstructorCall<Object>) local3.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals(2, leftSideLocal3.getActualTypeArguments().size());
		assertEquals(2, rightSideLocal3.getType().getActualTypeArguments().size());
		assertEquals("spoon.test.generics.testclasses.IBurritos<?, ?> burritos = new Burritos<>()", local3.toString());

		final CtElement local4 = m.getBody().getStatement(3).getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);
		final CtTypeReference<Object> leftSideLocal4 = (CtTypeReference<Object>) local4.getElements(new ReferenceTypeFilter<>(CtTypeReference.class)).get(0);
		final CtConstructorCall<Object> rightSideLocal4 = (CtConstructorCall<Object>) local4.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals(1, leftSideLocal4.getActualTypeArguments().size());
		assertEquals(1, rightSideLocal4.getType().getActualTypeArguments().size());
		assertEquals("java.util.List<?> l3 = new java.util.ArrayList<java.lang.Object>()", local4.toString());

		final CtConstructorCall constructorCall1 = (CtConstructorCall) m.getBody().getStatement(4).getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals(1, constructorCall1.getActualTypeArguments().size());
		assertEquals(2, constructorCall1.getType().getActualTypeArguments().size());
		assertEquals("new <java.lang.Integer>spoon.test.generics.testclasses.Tacos<java.lang.Object, java.lang.String>()", constructorCall1.toString());

		final CtConstructorCall constructorCall2 = (CtConstructorCall) m.getBody().getStatement(5).getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals(0, constructorCall2.getActualTypeArguments().size());
		assertEquals(2, constructorCall2.getType().getActualTypeArguments().size());
		assertEquals("new spoon.test.generics.testclasses.Tacos<>()", constructorCall2.toString());

		canBeBuilt("./target/spooned/spoon/test/generics/testclasses/", 8);
	}

	@Test
	public void testInvocationGenerics() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/generics/testclasses/",
				"-o", "./target/spooned/"
		});

		final CtClass<?> aTacos = launcher.getFactory().Class().get(Tacos.class);

		final CtConstructor<?> defaultConstructor = aTacos.getConstructor();
		final CtInvocation<?> explicitConstructorCall = (CtInvocation<?>) defaultConstructor.getBody().getStatement(0).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		assertEquals(1, explicitConstructorCall.getExecutable().getActualTypeArguments().size());
		assertEquals("<java.lang.String>this(1)", explicitConstructorCall.toString());

		final CtMethod<?> m = aTacos.getMethodsByName("m2").get(0);
		final CtInvocation invocation1 = m.getBody().getStatement(0).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		assertEquals(1, invocation1.getExecutable().getActualTypeArguments().size());
		assertEquals("this.<java.lang.String>makeTacos(null)", invocation1.toString());

		final CtInvocation invocation2 = m.getBody().getStatement(1).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		assertEquals(0, invocation2.getExecutable().getActualTypeArguments().size());
		assertEquals("this.makeTacos(null)", invocation2.toString());

		canBeBuilt("./target/spooned/spoon/test/generics/testclasses/", 8);
	}

	@Test
	public void testNewClassGenerics() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/generics/testclasses/",
				"-o", "./target/spooned/"
		});

		final CtClass<?> aTacos = launcher.getFactory().Class().get(Tacos.class);

		final CtMethod<?> m = aTacos.getMethodsByName("m3").get(0);
		final CtNewClass newClass1 = m.getBody().getStatement(0).getElements(new TypeFilter<>(CtNewClass.class)).get(0);
		assertEquals(0, newClass1.getActualTypeArguments().size());
		assertEquals(2, newClass1.getType().getActualTypeArguments().size());
		assertEquals("new javax.lang.model.util.SimpleTypeVisitor7<spoon.test.generics.testclasses.Tacos, java.lang.Void>() {}", newClass1.toString());

		final CtNewClass newClass2 = m.getBody().getStatement(1).getElements(new TypeFilter<>(CtNewClass.class)).get(0);
		assertEquals(0, newClass2.getActualTypeArguments().size());
		assertEquals(2, newClass2.getType().getActualTypeArguments().size());
		assertEquals("new javax.lang.model.util.SimpleTypeVisitor7<spoon.test.generics.testclasses.Tacos, java.lang.Void>() {}", newClass2.toString());

		canBeBuilt("./target/spooned/spoon/test/generics/testclasses/", 8);
	}

	@Test
	public void testMethodsWithGenericsWhoExtendsObject() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/generics/testclasses/",
				"-o", "./target/spooned/"
		});

		final CtClass<?> aTacos = launcher.getFactory().Class().get(Tacos.class);

		final CtMethod<?> m = aTacos.getMethodsByName("m4").get(0);
		final CtInvocation<?> invocation1 = m.getBody().getStatement(0).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		assertEquals(2, invocation1.getExecutable().getActualTypeArguments().size());
		assertEquals("spoon.test.generics.testclasses.Tacos.<V, C>makeTacos()", invocation1.toString());

		final CtInvocation<?> invocation2 = m.getBody().getStatement(1).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		assertEquals(0, invocation2.getExecutable().getActualTypeArguments().size());
		assertEquals("spoon.test.generics.testclasses.Tacos.makeTacos()", invocation2.toString());
	}

	@Test
	public void testName() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/generics/testclasses/",
				"-o", "./target/spooned/"
		});

		final CtClass<?> aFactory = launcher.getFactory().Class().get(Tacos.BeerFactory.class);

		final CtMethod<?> m = aFactory.getMethodsByName("newBeer").get(0);
		final CtConstructorCall constructorCall1 = m.getBody().getStatement(0).getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals("new Beer()", constructorCall1.toString());
	}

	@Test
	public void testGenericWithExtendsInDeclaration() throws Exception {
		final Factory build = build(Panini.class);
		final CtType<Panini> panini = build.Type().get(Panini.class);

		final CtMethod<?> apply = panini.getMethodsByName("apply").get(0);
		assertEquals(1, apply.getType().getActualTypeArguments().size());
		assertEquals("? super java.lang.Object", apply.getType().getActualTypeArguments().get(0).toString());

		assertEquals(1, apply.getParameters().get(0).getType().getActualTypeArguments().size());
		assertEquals("? extends java.lang.Long", apply.getParameters().get(0).getType().getActualTypeArguments().get(0).toString());
	}

	@Test
	public void testGenericInField() throws Exception {
		final Factory build = build(Spaghetti.class);
		final CtType<Panini> aSpaghetti = build.Type().get(Spaghetti.class);

		assertTrue(aSpaghetti.toString().contains("private spoon.test.generics.testclasses.Spaghetti<B>.Tester tester;"));
		assertTrue(aSpaghetti.toString().contains("private spoon.test.generics.testclasses.Spaghetti<B>.Tester tester1;"));

		assertTrue(aSpaghetti.toString().contains("private spoon.test.generics.testclasses.Spaghetti<B>.That<java.lang.String, java.lang.String> field;"));
		assertTrue(aSpaghetti.toString().contains("private spoon.test.generics.testclasses.Spaghetti<java.lang.String>.That<java.lang.String, java.lang.String> field1;"));
		assertTrue(aSpaghetti.toString().contains("private spoon.test.generics.testclasses.Spaghetti<java.lang.Number>.That<java.lang.String, java.lang.String> field2;"));
	}

	@Test
	public void testGenericsInQualifiedNameInConstructorCall() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/generics/testclasses/",
				"-o", "./target/spooned/"
		});

		final CtClass<Tacos> aTacos = launcher.getFactory().Class().get(Tacos.class);
		final CtType<?> burritos = aTacos.getNestedType("Burritos");

		SortedList<CtConstructorCall> elements = new SortedList<CtConstructorCall>(new CtLineElementComparator());
		elements.addAll(burritos.getElements(new TypeFilter<>(CtConstructorCall.class)));

		assertEquals(3, elements.size());

		// Constructor call.
		assertEquals(0, elements.get(1).getExecutable().getType().getActualTypeArguments().size());
		assertNotNull(elements.get(1).getType().getDeclaringType());
		assertEquals("new Pozole()", elements.get(1).toString());
		assertEquals(2, elements.get(0).getExecutable().getType().getActualTypeArguments().size());
		assertNotNull(elements.get(0).getType().getDeclaringType());
		assertEquals("new Burritos<K, V>()", elements.get(0).toString());

		// New class.
		assertEquals(2, elements.get(2).getExecutable().getType().getActualTypeArguments().size());
		assertNotNull(elements.get(2).getType().getDeclaringType());
		assertEquals("new Burritos<K, V>() {}", elements.get(2).toString());
	}

	@Test
	public void testGenericsOnLocalType() throws Exception {
		// contract: A local type can have actual generic types.
		final CtType<Mole> aMole = buildClass(Mole.class);
		final CtMethod<Object> cook = aMole.getMethod("cook");
		final CtConstructorCall<?> newCook = cook.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);

		assertEquals(1, newCook.getType().getActualTypeArguments().size());
		assertEquals("new Cook<java.lang.String>()", newCook.toString());
	}

	@Test
	public void testGenericsInConstructorCall() throws Exception {
		// contract: A constructor call have generics declared before the type and the
		// TypeReference have generics declared after itself. e.g, new <String>Test<String>().
		final CtType<Mole> aMole = buildClass(Mole.class);
		final CtMethod<Object> prepare = aMole.getMethod("prepare");
		final CtConstructorCall<?> newPrepare = prepare.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);

		assertEquals(1, newPrepare.getActualTypeArguments().size());
		assertEquals("java.lang.Integer", newPrepare.getActualTypeArguments().get(0).toString());
		assertEquals(1, newPrepare.getType().getActualTypeArguments().size());
		assertEquals("java.lang.String", newPrepare.getType().getActualTypeArguments().get(0).toString());
	}

	@Test
	public void testWildcard() throws Exception {
		List<CtWildcardReference> wildcardReferences = buildClass(Paella.class).getElements(new TypeFilter<CtWildcardReference>(CtWildcardReference.class));
		// 4 = the class declaration + the constructor declaration + the method declaration + the type parameter of the method declaration
		assertEquals(4, wildcardReferences.size());
	}

	@Test
	public void testDeclarationOfTypeParameterReference() throws Exception {
		CtType<Tacos> aTacos = buildNoClasspath(Tacos.class).Type().get(Tacos.class);
		for (CtTypeParameterReference parameterReference : aTacos.getElements(new TypeFilter<CtTypeParameterReference>(CtTypeParameterReference.class) {
			@Override
			public boolean matches(CtTypeParameterReference element) {
				return !(element instanceof CtWildcardReference) && !(element.getParent() instanceof CtReference);
			}
		})) {
			assertNotNull(parameterReference.getDeclaration());
		}
	}

	@Test
	public void testIsGenericsMethod() throws Exception {
		CtType<Tacos> aTacos = buildNoClasspath(Tacos.class).Type().get(Tacos.class);
		CtTypeParameter typeParameter = aTacos.getFormalCtTypeParameters().get(0);
		assertTrue(typeParameter.isGenerics());
		assertTrue(typeParameter.getReference().isGenerics());

		CtTypeReference ctTypeReference = aTacos.getSuperInterfaces().toArray(new CtTypeReference[aTacos.getSuperInterfaces().size()])[0];
		assertFalse(aTacos.isGenerics());
		assertFalse(ctTypeReference.isGenerics());
	}
	@Test
	public void testTypeParameterReferenceAsActualTypeArgument() throws Exception {
		CtType<Tacos> aTacos = buildNoClasspath(ClassThatDefinesANewTypeArgument.class).Type().get(ClassThatDefinesANewTypeArgument.class);
		
		CtTypeReference<?> typeRef = aTacos.getReference();

		assertSame(aTacos, typeRef.getDeclaration());

		CtTypeParameter typeParam = aTacos.getFormalCtTypeParameters().get(0);
		CtTypeParameterReference typeParamRef = typeParam.getReference();
		assertSame(typeParam, typeParamRef.getDeclaration());

		assertEquals("spoon.test.generics.ClassThatDefinesANewTypeArgument", typeRef.toString());

		// creating a reference to "ClassThatDefinesANewTypeArgument<T>"
		//this assignment changes parent of typeParamRef to TYPEREF
		typeRef.addActualTypeArgument(typeParamRef);

		assertEquals("spoon.test.generics.ClassThatDefinesANewTypeArgument<T>", typeRef.toString());

		// this does not change the declaration
		assertSame(aTacos, typeRef.getDeclaration());
		//stored typeParamRef is same like the added one, no clone - OK
		assertSame(typeParamRef, typeRef.getActualTypeArguments().get(0));
		//typeParamRef has got new parent 
		assertSame(typeRef, typeParamRef.getParent());

		// null because without context
		assertEquals(null, typeParamRef.getDeclaration());
		assertEquals(typeParam, typeParamRef.getTypeParameterDeclaration());
		typeParamRef.setSimpleName("Y");
		assertEquals(typeParam, typeParamRef.getTypeParameterDeclaration());
	}
	@Test
	public void testGenericTypeReference() throws Exception {

		// contract: the parameter includingFormalTypeParameter of createReference enables one to also create actual type arguments

		CtType<Tacos> aTacos = buildNoClasspath(Tacos.class).Type().get(Tacos.class);
		//this returns a type reference with uninitialized actual type arguments.
//		CtTypeReference<?> genericTypeRef = aTacos.getReference();
		CtTypeReference<?> genericTypeRef = aTacos.getFactory().Type().createReference(aTacos, true);
		
		assertTrue(genericTypeRef.getActualTypeArguments().size()>0);
		assertEquals(aTacos.getFormalCtTypeParameters().size(), genericTypeRef.getActualTypeArguments().size());
		for(int i=0; i<aTacos.getFormalCtTypeParameters().size(); i++) {
			assertSame("TypeParameter reference idx="+i+" is different", aTacos.getFormalCtTypeParameters().get(i), genericTypeRef.getActualTypeArguments().get(i).getTypeParameterDeclaration());

			// contract: getTypeParameterDeclaration goes back to the declaration, eevn without context
			assertSame(aTacos.getFormalCtTypeParameters().get(i), genericTypeRef.getActualTypeArguments().get(i).getTypeParameterDeclaration());

		}
	}
	@Test
	public void testCtTypeReference_getSuperclass() throws Exception {
		Factory factory = build(new File("src/test/java/spoon/test/generics/testclasses"));
		CtClass<?> ctClassCelebrationLunch = factory.Class().get(CelebrationLunch.class);
		CtTypeReference<?> trWeddingLunch_Mole = ctClassCelebrationLunch.filterChildren(new NameFilter<>("disgust")).map((CtTypedElement te)->{
			return te.getType();
		}).first();
		
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch<java.lang.Integer, java.lang.Long, java.lang.Double>.WeddingLunch<spoon.test.generics.testclasses.Mole>",trWeddingLunch_Mole.toString());
		CtType<?> tWeddingLunch_X = trWeddingLunch_Mole.getDeclaration();
		CtTypeReference<?> trCelebrationLunch_Tacos_Paella_X = tWeddingLunch_X.getSuperclass();
		//current correct behavior of CtType#getSuperclass()
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch<"
				+ "spoon.test.generics.testclasses.Tacos, "
				+ "spoon.test.generics.testclasses.Paella, "
				+ "X"
				+ ">",trCelebrationLunch_Tacos_Paella_X.toString());
		//current - wrong behavior of CtTypeReference#getSuperclass()
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch<"
				+ "spoon.test.generics.testclasses.Tacos, "
				+ "spoon.test.generics.testclasses.Paella, "
				+ "X"
				+ ">",trWeddingLunch_Mole.getSuperclass().toString());
		//future suggested behavior of CtTypeReference#getSuperclass() - the 3rd argument is Mole.
//		assertEquals("spoon.test.generics.testclasses.CelebrationLunch<"
//				+ "spoon.test.generics.testclasses.Tacos, "
//				+ "spoon.test.generics.testclasses.Paella, "
//				+ "spoon.test.generics.testclasses.Mole"
//				+ ">",trWeddingLunch_Mole.getSuperclass().toString());
		//future suggested behavior of CtTypeReference#getSuperclass() 
//		assertEquals("spoon.test.generics.testclasses.Lunch<"
//				+ "spoon.test.generics.testclasses.Mole, "
//				+ "spoon.test.generics.testclasses.Tacos"
//				+ ">",trWeddingLunch_Mole.getSuperclass().getSuperclass().toString());
	}

	@Test
	public void testTypeAdapted() throws Exception {
		// contract: one can get the actual value of a generic type in a given context
		CtClass<?> ctModel = (CtClass<?>) ModelUtils.buildClass(ErasureModelA.class);
		CtTypeParameter tpA = ctModel.getFormalCtTypeParameters().get(0);
		CtTypeParameter tpB = ctModel.getFormalCtTypeParameters().get(1);
		CtTypeParameter tpC = ctModel.getFormalCtTypeParameters().get(2);
		CtTypeParameter tpD = ctModel.getFormalCtTypeParameters().get(3);

		CtClass<?> ctModelB = ctModel.filterChildren(new NameFilter<>("ModelB")).first();
		ClassTypingContext sth = new ClassTypingContext(ctModelB);
		// in ModelB, "A" is "A2"
		assertEquals("A2", sth.adaptType(tpA).getQualifiedName());
		// in ModelB, "B" is "B2"
		assertEquals("B2", sth.adaptType(tpB).getQualifiedName());
		// and so on and so forth
		assertEquals("C2", sth.adaptType(tpC).getQualifiedName());
		assertEquals("D2", sth.adaptType(tpD).getQualifiedName());

		CtClass<?> ctModelC = ctModel.filterChildren(new NameFilter<>("ModelC")).first();
		ClassTypingContext sthC = new ClassTypingContext(ctModelC);
		assertEquals("java.lang.Integer", sthC.adaptType(tpA).getQualifiedName());
		assertEquals("java.lang.RuntimeException", sthC.adaptType(tpB).getQualifiedName());
		assertEquals("java.lang.IllegalArgumentException", sthC.adaptType(tpC).getQualifiedName());
		assertEquals("java.util.List", sthC.adaptType(tpD).getQualifiedName());
	}


	@Test
	public void testClassTypingContext() throws Exception {
		// contract: a ClassTypingContext enables one to perform type resolution of generic types
		Factory factory = build(new File("src/test/java/spoon/test/generics/testclasses"));
		CtClass<?> ctClassCelebrationLunch = factory.Class().get(CelebrationLunch.class);
		CtTypeReference<?> typeReferenceOfDisgust = ctClassCelebrationLunch.filterChildren(new NameFilter<>("disgust")).map((CtTypedElement te)->{
			return te.getType();
		}).first();
		
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch<java.lang.Integer, java.lang.Long, java.lang.Double>.WeddingLunch<spoon.test.generics.testclasses.Mole>",typeReferenceOfDisgust.toString());
		//method WeddingLunch#eatMe
		CtMethod<?> tWeddingLunch_eatMe = typeReferenceOfDisgust.getDeclaration().filterChildren((CtNamedElement e)->"eatMe".equals(e.getSimpleName())).first();
		
		CtClass<?> ctClassLunch = factory.Class().get(Lunch.class);
		//method Lunch#eatMe
		CtMethod<?> ctClassLunch_eatMe = ctClassLunch.filterChildren((CtNamedElement e)->"eatMe".equals(e.getSimpleName())).first();

		
		//type of first parameter of  method WeddingLunch#eatMe
		CtTypeReference<?> ctWeddingLunch_X = tWeddingLunch_eatMe.getParameters().get(0).getType();
		// X is the type parameter of WeddingLunch
		assertEquals("X", ctWeddingLunch_X.getSimpleName());
		//type of first parameter of method Lunch#eatMe
		CtTypeReference<?> ctClassLunch_A = ctClassLunch_eatMe.getParameters().get(0).getType();
		assertEquals("A", ctClassLunch_A.getSimpleName());
		
		//are these two types same?
		ClassTypingContext typingContextOfDisgust = new ClassTypingContext(typeReferenceOfDisgust);
		
		//contract: the class typing context provides its scope 
		assertSame(typeReferenceOfDisgust.getTypeDeclaration(), typingContextOfDisgust.getAdaptationScope());
		
		// in disgust, X of WeddingLunch is bound to "Model"
		assertEquals("spoon.test.generics.testclasses.Mole", typingContextOfDisgust.adaptType(ctWeddingLunch_X).getQualifiedName());
		//adapt A to scope of CelebrationLunch<Integer,Long,Double>.WeddingLunch<Mole>

		// in disgust, the A of Lunch is bound to "Mole"
		assertEquals("spoon.test.generics.testclasses.Mole", typingContextOfDisgust.adaptType(ctClassLunch_A).getQualifiedName());

		// I don't understand the goal and utility of this one
		assertEquals("java.lang.Double", typingContextOfDisgust.getEnclosingGenericTypeAdapter().adaptType(ctClassLunch_A).getQualifiedName());


		// now we resolve those types, but in the context of the declaration, where no concrete types exist
		//are these two types same in scope of CelebrationLunch<K,L,M>.WddingLunch<X> class itself
		ClassTypingContext sthOftWeddingLunch_X = new ClassTypingContext(typeReferenceOfDisgust.getDeclaration());
		
		//contract: the class typing context provides its scope 
		assertSame(typeReferenceOfDisgust.getDeclaration(), sthOftWeddingLunch_X.getAdaptationScope());
		
		// in WeddingLunch "X" is still "X"
		assertEquals("X", sthOftWeddingLunch_X.adaptType(ctWeddingLunch_X).getQualifiedName());

		// in WeddingLunch the "A" from Lunch of is called "X"
		assertEquals("X", sthOftWeddingLunch_X.adaptType(ctClassLunch_A).getQualifiedName());

		// ?????
		//adapt A to scope of enclosing class of CelebrationLunch<K,L,M>.WddingLunch<X>, which is CelebrationLunch<K,L,M>
		assertEquals("M", sthOftWeddingLunch_X.getEnclosingGenericTypeAdapter().adaptType(ctClassLunch_A).getQualifiedName());
	}
	
	@Test
	public void testRecursiveTypeAdapting() throws Exception {
		CtType<?> classOrange = buildClass(Orange.class);
		CtClass<?> classA = classOrange.getNestedType("A");
		CtTypeParameter typeParamO = classA.getFormalCtTypeParameters().get(0);
		CtTypeParameter typeParamM = classA.getFormalCtTypeParameters().get(1);
		assertEquals("O", typeParamO.getQualifiedName());
		assertEquals("M", typeParamM.getQualifiedName());
		assertEquals("K", typeParamO.getSuperclass().getQualifiedName());
		assertEquals("O", typeParamM.getSuperclass().getQualifiedName());
		assertEquals("K", typeParamM.getSuperclass().getSuperclass().getQualifiedName());
		
		CtClass<?> classB = classOrange.getNestedType("B");
		CtTypeParameter typeParamN = classB.getFormalCtTypeParameters().get(0);
		CtTypeParameter typeParamP = classB.getFormalCtTypeParameters().get(1);
		assertEquals("N", typeParamN.getQualifiedName());
		assertEquals("P", typeParamP.getQualifiedName());
		
		ClassTypingContext ctcB = new ClassTypingContext(classB);
		assertEquals("N", ctcB.adaptType(typeParamO).getQualifiedName());
		assertEquals("P", ctcB.adaptType(typeParamM).getQualifiedName());
		//contract: superClass of CtTypeParam is adapted too
		assertEquals("K", ctcB.adaptType(typeParamO).getSuperclass().getQualifiedName());
		assertEquals("N", ctcB.adaptType(typeParamM).getSuperclass().getQualifiedName());
		assertEquals("K", ctcB.adaptType(typeParamM).getSuperclass().getSuperclass().getQualifiedName());
		
		CtTypeReference<?> typeRef_list2m = classA.getField("list2m").getType();
		assertEquals("java.util.List<java.util.List<M>>", typeRef_list2m.toString());
		//contract: the CtTypeReference is adapted recursive including actual type arguments
		assertEquals("java.util.List<java.util.List<P>>", ctcB.adaptType(typeRef_list2m).toString());
		
		CtTypeReference<?> typeRef_ListQextendsM = classA.getMethodsByName("method").get(0).getParameters().get(0).getType();
		assertEquals("java.util.List<? extends M>", typeRef_ListQextendsM.toString());
		//contract: the CtTypeReference is adapted recursive including actual type arguments and their bounds
		assertEquals("java.util.List<? extends P>", ctcB.adaptType(typeRef_ListQextendsM).toString());
	}
	
	@Test
	public void testMethodTypingContext() throws Exception {
		Factory factory = build(new File("src/test/java/spoon/test/generics/testclasses"));
		CtClass<?> ctClassWeddingLunch = factory.Class().get(WeddingLunch.class);
		CtMethod<?> trWeddingLunch_eatMe = ctClassWeddingLunch.filterChildren(new NameFilter<>("eatMe")).first();

		MethodTypingContext methodSTH = new MethodTypingContext().setMethod(trWeddingLunch_eatMe);

		//contract: the method typing context provides its scope 
		assertSame(trWeddingLunch_eatMe, methodSTH.getAdaptationScope());

		CtClass<?> ctClassLunch = factory.Class().get(Lunch.class);
		CtMethod<?> trLunch_eatMe = ctClassLunch.filterChildren(new NameFilter<>("eatMe")).first();
		
		CtInvocation<?> invokeReserve = factory.Class().get(CelebrationLunch.class)
				.filterChildren(new TypeFilter<>(CtInvocation.class))
				.select((CtInvocation i)->"reserve".equals(i.getExecutable().getSimpleName()))
				.first();
		
		
		MethodTypingContext methodReserveTC = new MethodTypingContext().setInvocation(invokeReserve);
		//contract: the method typing context provides its scope 
		assertSame(invokeReserve.getExecutable().getDeclaration(), methodReserveTC.getAdaptationScope());
		
		//check that MethodTypingContext made from invocation knows actual type arguments of method and all declaring types
		//1) check method actual type argument
		CtMethod<?> methodReserve = (CtMethod<?>) invokeReserve.getExecutable().getDeclaration();
		CtTypeParameter methodReserve_S = methodReserve.getFormalCtTypeParameters().get(0);
		assertEquals("S", methodReserve_S.getSimpleName());
		assertEquals("spoon.test.generics.testclasses.Tacos", methodReserveTC.adaptType(methodReserve_S).getQualifiedName());

		//2) check actual type arguments of declaring type `Section` 
		CtClass classSection = (CtClass)methodReserve.getDeclaringType();
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch$WeddingLunch$Section", classSection.getQualifiedName());
		CtTypeParameter classSection_Y = classSection.getFormalCtTypeParameters().get(0);
		assertEquals("Y", classSection_Y.getSimpleName());
		assertEquals("spoon.test.generics.testclasses.Paella", methodReserveTC.adaptType(classSection_Y).getQualifiedName());
		
		//3) check actual type arguments of declaring type `WeddingLunch` 
		CtClass classWeddingLunch = (CtClass)classSection.getDeclaringType();
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch$WeddingLunch", classWeddingLunch.getQualifiedName());
		CtTypeParameter classWeddingLunch_X = classWeddingLunch.getFormalCtTypeParameters().get(0);
		assertEquals("X", classWeddingLunch_X.getSimpleName());
		assertEquals("spoon.test.generics.testclasses.Mole", methodReserveTC.adaptType(classWeddingLunch_X).getQualifiedName());
		
		//4) check actual type arguments of declaring type `CelebrationLunch` 
		CtClass classCelebrationLunch = (CtClass)classWeddingLunch.getDeclaringType();
		assertEquals("spoon.test.generics.testclasses.CelebrationLunch", classCelebrationLunch.getQualifiedName());
		CtTypeParameter classCelebrationLunch_K = classCelebrationLunch.getFormalCtTypeParameters().get(0);
		CtTypeParameter classCelebrationLunch_L = classCelebrationLunch.getFormalCtTypeParameters().get(1);
		CtTypeParameter classCelebrationLunch_M = classCelebrationLunch.getFormalCtTypeParameters().get(2);
		assertEquals("K", classCelebrationLunch_K.getSimpleName());
		assertEquals("L", classCelebrationLunch_L.getSimpleName());
		assertEquals("M", classCelebrationLunch_M.getSimpleName());
		assertEquals("spoon.test.generics.testclasses.Tacos", methodReserveTC.adaptType(classCelebrationLunch_K).getQualifiedName());
		assertEquals("spoon.test.generics.testclasses.Paella", methodReserveTC.adaptType(classCelebrationLunch_L).getQualifiedName());
		assertEquals("spoon.test.generics.testclasses.Mole", methodReserveTC.adaptType(classCelebrationLunch_M).getQualifiedName());

		//method->Section->WeddingLunch->CelebrationLunch
		GenericTypeAdapter celebrationLunchTC = methodReserveTC.getEnclosingGenericTypeAdapter().getEnclosingGenericTypeAdapter().getEnclosingGenericTypeAdapter();
		assertEquals("java.lang.Integer", celebrationLunchTC.adaptType(classCelebrationLunch_K).getQualifiedName());
		assertEquals("java.lang.Long", celebrationLunchTC.adaptType(classCelebrationLunch_L).getQualifiedName());
		assertEquals("java.lang.Double", celebrationLunchTC.adaptType(classCelebrationLunch_M).getQualifiedName());
	}
	
	@Test
	public void testMethodTypingContextAdaptMethod() throws Exception {
		// core contracts of MethodTypingContext#adaptMethod
		Factory factory = build(new File("src/test/java/spoon/test/generics/testclasses"));
		CtClass<?> ctClassLunch = factory.Class().get(Lunch.class);

		// represents <C> void eatMe(A paramA, B paramB, C paramC){}
		CtMethod<?> trLunch_eatMe = ctClassLunch.filterChildren(new NameFilter<>("eatMe")).first();
		CtClass<?> ctClassWeddingLunch = factory.Class().get(WeddingLunch.class);

		ClassTypingContext ctcWeddingLunch = new ClassTypingContext(ctClassWeddingLunch);
		// we all analyze new methods
		final MethodTypingContext methodSTH = new MethodTypingContext().setClassTypingContext(ctcWeddingLunch);
		CtMethod<?> adaptedLunchEatMe = ctcWeddingLunch.adaptMethod(trLunch_eatMe);

		//contract: adapting of method declared in different scope, returns new method
		assertTrue(adaptedLunchEatMe != trLunch_eatMe);

		//check that new method is adapted correctly
		//is declared in correct class
		assertSame(ctClassWeddingLunch, adaptedLunchEatMe.getDeclaringType());
		//  is not member of the same class (WeddingLunch)
		for (CtTypeMember typeMember : ctClassWeddingLunch.getTypeMembers()) {
			assertFalse(adaptedLunchEatMe==typeMember);
		}
		// the name is the same
		assertEquals("eatMe", adaptedLunchEatMe.getSimpleName());
		// it has the same number of of formal type parameters
		assertEquals(1, adaptedLunchEatMe.getFormalCtTypeParameters().size());
		assertEquals("C", adaptedLunchEatMe.getFormalCtTypeParameters().get(0).getQualifiedName());

		//parameters are correct
		assertEquals(3, adaptedLunchEatMe.getParameters().size());

		// "A paramA" becomes "X paramA" becomes Lunch%A corresponds to X in WeddingLunch
		assertEquals("X", adaptedLunchEatMe.getParameters().get(0).getType().getQualifiedName());
		// B paramB becomes Tacos becomes Lunch%B corresponds to Tacos in WeddingLunch (class WeddingLunch<X> extends CelebrationLunch<Tacos, Paella, X>)
		assertEquals(Tacos.class.getName(), adaptedLunchEatMe.getParameters().get(1).getType().getQualifiedName());
		// "C paramC" stays "C paramC"
		assertEquals("C", adaptedLunchEatMe.getParameters().get(2).getType().getQualifiedName());

		//contract: adapting of adapted method returns input method
		assertSame(adaptedLunchEatMe, ctcWeddingLunch.adaptMethod(adaptedLunchEatMe));

		//contract: method typing context creates adapted method automatically
		methodSTH.setMethod(trLunch_eatMe);
		//contract: method typing context creates adapted method automatically, which is equal to manually adapted one
		assertEquals(adaptedLunchEatMe, methodSTH.getAdaptationScope());
		
	}
	
	@Test
	public void testClassTypingContextMethodSignature() throws Exception {
		// core contracts of MethodTypingContext#adaptMethod
		Factory factory = build(new File("src/test/java/spoon/test/generics/testclasses"));
		CtClass<?> ctClassLunch = factory.Class().get(Lunch.class);
		CtClass<?> ctClassWeddingLunch = factory.Class().get(WeddingLunch.class);

		// represents <C> void eatMe(A paramA, B paramB, C paramC){}
		CtMethod<?> trLunch_eatMe = ctClassLunch.filterChildren(new NameFilter<>("eatMe")).first();
		
		// represents <C> void eatMe(M paramA, K paramB, C paramC)
		CtMethod<?> trWeddingLunch_eatMe = ctClassWeddingLunch.filterChildren(new NameFilter<>("eatMe")).first();
		
		ClassTypingContext ctcWeddingLunch = new ClassTypingContext(ctClassWeddingLunch);
		
		assertTrue(ctcWeddingLunch.isOverriding(trLunch_eatMe, trLunch_eatMe));
		assertTrue(ctcWeddingLunch.isOverriding(trLunch_eatMe, trWeddingLunch_eatMe));
		assertTrue(ctcWeddingLunch.isSubSignature(trLunch_eatMe, trWeddingLunch_eatMe));

		//contract: check that adapting of methods still produces same results, even when scopeMethod is already assigned
		assertTrue(ctcWeddingLunch.isOverriding(trWeddingLunch_eatMe, trLunch_eatMe));
		assertTrue(ctcWeddingLunch.isOverriding(trWeddingLunch_eatMe, trWeddingLunch_eatMe));
		assertTrue(ctcWeddingLunch.isSubSignature(trWeddingLunch_eatMe, trWeddingLunch_eatMe));
	}
	
	
	
	@Test
	public void testClassContextOnInnerClass() throws Exception {
		CtClass<?> classBanana = (CtClass<?>)buildClass(Banana.class);
		CtClass<?> classVitamins = classBanana.getNestedType("Vitamins");
		CtTypeReference<?> refList_T = classVitamins.getSuperclass();
		//contract: generic types defined in enclocing classe (Banana<T>) are resolved from inner class hierarchy (Vitamins->List<T>) too.
		assertSame(classBanana.getFormalCtTypeParameters().get(0), new ClassTypingContext(classVitamins).adaptType(refList_T.getActualTypeArguments().get(0)).getDeclaration());
	}

	private void checkFakeTpl(CtInterface<?> fakeTplItf) {
		assertNotNull(fakeTplItf);

		CtMethod<?> applyMethod = fakeTplItf.getMethodsByName("apply").get(0);

		CtTypeReference<?> returnType = applyMethod.getType();
		assertEquals("T", returnType.getSimpleName());
		assertTrue(returnType instanceof CtTypeParameterReference);
		assertEquals("CtElement", returnType.getSuperclass().getSimpleName());

		CtParameter<?> targetType = applyMethod.getParameters().get(0);

		List<CtTypeReference<?>> targetTypeArgument = targetType.getType().getActualTypeArguments();
		assertEquals(1, targetTypeArgument.size());

		assertTrue(targetTypeArgument.get(0) instanceof CtWildcardReference);

		CtMethod<?> testMethod = fakeTplItf.getMethodsByName("test").get(0);
		List<CtParameter<?>> parameters = testMethod.getParameters();
		assertEquals(3, parameters.size());

		CtParameter thirdParam = parameters.get(2);
		assertTrue(thirdParam.getType() instanceof CtTypeParameterReference);
	}

	@Test
	public void testWildCardonShadowClass() throws Exception {
		// contract: generics should be treated the same way in shadow classes

		// test that apply argument type contains a wildcard
		Launcher launcher = new Launcher();
		Factory factory = launcher.getFactory();

		launcher.addInputResource("src/test/java/spoon/test/generics/testclasses/FakeTpl.java");
		launcher.buildModel();

		CtInterface<?> fakeTplItf = factory.Interface().get("spoon.test.generics.testclasses.FakeTpl");
		checkFakeTpl(fakeTplItf);

		// same test with a shadow class
		launcher = new Launcher();
		factory = launcher.getFactory();
		CtInterface<?> fakeTplItf2 = factory.Interface().get(FakeTpl.class);
		checkFakeTpl(fakeTplItf2);
	}

	@Test
	public void testDiamondComplexGenericsRxJava() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses/rxjava/");
		launcher.setSourceOutputDirectory("./target/spooned-rxjava");
		launcher.run();

		Factory factory = launcher.getFactory();

		List<CtConstructorCall> invocations = factory.getModel().getElements(new TypeFilter<>(CtConstructorCall.class));

		boolean invocationDetected = false;
		for (CtConstructorCall call : invocations) {
			if (call.getType().getSimpleName().equals("ToNotificationSubscriber")) {
				assertEquals(1, call.getType().getActualTypeArguments().size());

				CtTypeReference actualTA = call.getType().getActualTypeArguments().get(0);
				assertTrue(actualTA instanceof CtWildcardReference);
				assertEquals("?", actualTA.getSimpleName());
				assertTrue( ((CtWildcardReference)actualTA).getBoundingType() == null );
				invocationDetected = true;
			}
		}

		canBeBuilt("./target/spooned-rxjava",8);

		assertTrue(invocationDetected);
	}

	@Test
	public void testGetDeclarationOfTypeParameterReference() {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses/ExtendedPaella.java");
		launcher.addInputResource("./src/test/java/spoon/test/generics/testclasses/Paella.java");
		launcher.buildModel();

		Factory factory = launcher.getFactory();

		CtClass extendedPaella = factory.getModel().getElements(new NameFilter<CtClass>("ExtendedPaella")).get(0);
		List<CtTypeParameter> typeParameterList = extendedPaella.getFormalCtTypeParameters();

		assertEquals(1, typeParameterList.size());

		CtMethod totoMethod = factory.getModel().getElements(new NameFilter<CtMethod>("toto")).get(0);
		CtTypeReference returnTypeToto = totoMethod.getType();
		CtTypeReference paramToto = ((CtParameter)totoMethod.getParameters().get(0)).getType();

		CtType declaration = returnTypeToto.getDeclaration();

		assertSame(typeParameterList.get(0), declaration);
		assertSame(typeParameterList.get(0), paramToto.getDeclaration());

		CtMethod machinMethod = factory.getModel().getElements(new NameFilter<CtMethod>("machin")).get(0);
		CtTypeReference returnTypeMachin = machinMethod.getType();
		List<CtTypeParameter> formalCtTypeParameters = machinMethod.getFormalCtTypeParameters();

		assertEquals(1, formalCtTypeParameters.size());

		CtType declarationMachin = returnTypeMachin.getDeclaration();

		assertNotSame(typeParameterList.get(0), declarationMachin);
		assertSame(formalCtTypeParameters.get(0), declarationMachin);

		CtClass innerPaella = factory.getModel().getElements(new NameFilter<CtClass>("InnerPaella")).get(0);
		List<CtTypeParameter> innerTypeParametersList = innerPaella.getFormalCtTypeParameters();

		assertEquals(typeParameterList.get(0), innerTypeParametersList.get(0).getSuperclass().getDeclaration());

		CtMethod innerMachinMethod = factory.getModel().getElements(new NameFilter<CtMethod>("innerMachin")).get(0);
		CtTypeReference returnTypeInnerMachin = innerMachinMethod.getType();
		CtTypeReference paramInnerMachinType = ((CtParameter)innerMachinMethod.getParameters().get(0)).getType();
		List<CtTypeParameter> innerMachinFormalCtType = innerMachinMethod.getFormalCtTypeParameters();

		assertSame(typeParameterList.get(0), returnTypeInnerMachin.getDeclaration());
		assertSame(innerMachinFormalCtType.get(0), paramInnerMachinType.getDeclaration());

		CtMethod innerTotoMethod = factory.getModel().getElements(new NameFilter<CtMethod>("innerToto")).get(0);
		CtTypeReference returnInnerToto = innerTotoMethod.getType();
		CtTypeReference paramInnerToto = ((CtParameter)innerTotoMethod.getParameters().get(0)).getType();
		List<CtTypeParameter> innerTotoFormatCtType = innerTotoMethod.getFormalCtTypeParameters();

		assertSame(innerTotoFormatCtType.get(0), paramInnerToto.getDeclaration());
		assertSame(innerTypeParametersList.get(0), returnInnerToto.getDeclaration());
	}
}
