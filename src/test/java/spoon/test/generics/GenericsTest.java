package spoon.test.generics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.buildNoClasspath;
import static spoon.testing.utils.ModelUtils.canBeBuilt;
import static spoon.testing.utils.ModelUtils.createFactory;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
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
import spoon.reflect.declaration.CtTypeParameter;
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
import spoon.test.generics.testclasses.Mole;
import spoon.test.generics.testclasses.Paella;
import spoon.test.generics.testclasses.Panini;
import spoon.test.generics.testclasses.Spaghetti;
import spoon.test.generics.testclasses.Tacos;

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
				"spoon.test.generics.Tree.this.<java.lang.Class<? extends java.lang.Throwable>>foo()",
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
		CtClass<?> classThatBindsAGenericType = build("spoon.test.generics", "ClassThatBindsAGenericType");
		CtClass<?> classThatDefinesANewTypeArgument = classThatBindsAGenericType.getPackage().getElements(new NameFilter<CtClass<?>>("ClassThatDefinesANewTypeArgument")).get(0);

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

		SpoonCompiler compiler = spoon.createCompiler(
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
		final CtTypeReference<Object> leftSideLocal1 = (CtTypeReference<Object>) local1.getReferences(new ReferenceTypeFilter<>(CtTypeReference.class)).get(0);
		final CtConstructorCall<Object> rightSideLocal1 = (CtConstructorCall<Object>) local1.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals(1, leftSideLocal1.getActualTypeArguments().size());
		assertEquals(1, rightSideLocal1.getType().getActualTypeArguments().size());
		assertEquals("java.util.List<java.lang.String> l = new java.util.ArrayList<>()", local1.toString());

		final CtElement local2 = m.getBody().getStatement(1).getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);
		final CtTypeReference<Object> leftSideLocal2 = (CtTypeReference<Object>) local2.getReferences(new ReferenceTypeFilter<>(CtTypeReference.class)).get(0);
		assertEquals(0, leftSideLocal2.getActualTypeArguments().size());
		assertEquals("java.util.List l2", local2.toString());

		final CtElement local3 = m.getBody().getStatement(2).getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);
		final CtTypeReference<Object> leftSideLocal3 = (CtTypeReference<Object>) local3.getReferences(new ReferenceTypeFilter<>(CtTypeReference.class)).get(0);
		final CtConstructorCall<Object> rightSideLocal3 = (CtConstructorCall<Object>) local3.getElements(new TypeFilter<>(CtConstructorCall.class)).get(0);
		assertEquals(2, leftSideLocal3.getActualTypeArguments().size());
		assertEquals(2, rightSideLocal3.getType().getActualTypeArguments().size());
		assertEquals("spoon.test.generics.testclasses.IBurritos<?, ?> burritos = new Burritos<>()", local3.toString());

		final CtElement local4 = m.getBody().getStatement(3).getElements(new TypeFilter<>(CtLocalVariable.class)).get(0);
		final CtTypeReference<Object> leftSideLocal4 = (CtTypeReference<Object>) local4.getReferences(new ReferenceTypeFilter<>(CtTypeReference.class)).get(0);
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
		assertEquals("spoon.test.generics.testclasses.Tacos.this.<java.lang.String>makeTacos(null)", invocation1.toString());

		final CtInvocation invocation2 = m.getBody().getStatement(1).getElements(new TypeFilter<>(CtInvocation.class)).get(0);
		assertEquals(0, invocation2.getExecutable().getActualTypeArguments().size());
		assertEquals("spoon.test.generics.testclasses.Tacos.this.makeTacos(null)", invocation2.toString());

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
		assertEquals(3, buildClass(Paella.class).getElements(new TypeFilter<CtWildcardReference>(CtWildcardReference.class)).size());
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
}
