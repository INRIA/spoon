package spoon.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.adaption.TypeAdaptor;
import spoon.support.compiler.VirtualFile;
import spoon.testing.utils.GitHubIssue;
import spoon.testing.utils.ModelTest;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.Assert.assertThat;

class TypeAdaptorTest {

	@Test
	void testSubtypeSuperclass() {
		// contract: Subtypes for classes should be computed transitively
		Factory factory = new Launcher().getFactory();
		CtClass<?> top = factory.Class().get(Top.class);
		CtClass<?> middle = factory.Class().get(Middle.class);
		CtClass<?> bottom = factory.Class().get(Bottom.class);

		assertTrue(TypeAdaptor.isSubtype(middle, top.getReference()));
		assertTrue(TypeAdaptor.isSubtype(bottom, top.getReference()));
		assertTrue(TypeAdaptor.isSubtype(bottom, middle.getReference()));
	}

	@Test
	void testSubtypeSuperinterface() {
		// contract: Subtype relationship includes interfaces
		Factory factory = new Launcher().getFactory();
		CtType<?> topInterface = factory.Type().get(TopInterface.class);
		CtClass<?> middle = factory.Class().get(Middle.class);
		CtClass<?> bottom = factory.Class().get(Bottom.class);

		assertTrue(TypeAdaptor.isSubtype(middle, topInterface.getReference()));
		assertTrue(TypeAdaptor.isSubtype(bottom, topInterface.getReference()));
	}

	private interface TopInterface {

	}

	private interface MiddleInterface extends TopInterface {

	}

	private static class Top {

	}

	private static class Middle extends Top implements MiddleInterface {

	}

	private static class Bottom extends Middle {

	}

	@Test
	void testAdaptToTypeInStartReference() {
		// contract: Adapting to a type reference respects its actual type arguments
		// Adapt the `T` from List<T> to `List<String>` and expect String
		Factory factory = new Launcher().getFactory();
		CtType<?> list = factory.Type().get(List.class);
		CtClass<?> string = factory.Class().get(String.class);

		CtTypeReference<?> listReference = list.getReference();
		listReference.setActualTypeArguments(List.of(string.getReference()));

		assertThat(new TypeAdaptor(listReference).adaptType(list.getFormalCtTypeParameters().get(0)))
			.isEqualTo(string.getReference());
	}

	@Test
	void testAdaptTypeGenericRename() {
		// contract: Type adaption considers renames of generic parameters
		Factory factory = new Launcher().getFactory();
		CtType<?> top = factory.Type().get(GenericRenameTop.class);
		CtType<?> middle = factory.Type().get(GenericRenameMiddle.class);

		CtType<?> diamondTop = factory.Type().get(DiamondTop.class);
		CtType<?> diamondBottom = factory.Type().get(DiamondBottom.class);

		CtTypeReference<?> topToMiddle = new TypeAdaptor(middle).adaptType(
			top.getFormalCtTypeParameters().get(0).getReference()
		);
		CtTypeReference<?> diamondTopToBottom = new TypeAdaptor(diamondBottom).adaptType(
			diamondTop.getFormalCtTypeParameters().get(0).getReference()
		);

		assertThat(topToMiddle.getTypeDeclaration())
			.isEqualTo(middle.getFormalCtTypeParameters().get(0));
		assertThat(diamondTopToBottom.getTypeDeclaration())
			.isEqualTo(diamondBottom.getFormalCtTypeParameters().get(0));
	}

	private interface GenericRenameTop<T> {

	}

	private interface GenericRenameMiddle<R> extends GenericRenameTop<R> {

	}

	private interface DiamondTop<T> {

	}

	private interface DiamondLeft<U> extends DiamondTop<U> {

	}

	private interface DiamondRight<V> extends DiamondTop<V> {

	}

	private interface DiamondBottom<W> extends DiamondLeft<W>, DiamondRight<W> {

	}

	@Test
	void testTypeAdaptConcreteType() {
		// contract: Adapting a generic parameter to a concrete type works
		// Adapt the `T` from ConcreteTypeTop<T> to ConcreteTypeMiddle and expect List<String>
		Factory factory = new Launcher().getFactory();
		CtType<?> top = factory.Type().get(ConcreteTypeTop.class);
		CtType<?> bottom = factory.Type().get(ConcreteTypeBottom.class);

		CtTypeReference<?> topToBottom = new TypeAdaptor(bottom).adaptType(
			top.getFormalCtTypeParameters().get(0).getReference()
		);
		assertEquals("java.util.List", topToBottom.getTypeDeclaration().getQualifiedName());
		assertEquals(
			"java.lang.String",
			topToBottom.getActualTypeArguments().get(0).getQualifiedName()
		);
	}

	interface ConcreteTypeTop<T> {

	}

	interface ConcreteTypeMiddle extends ConcreteTypeTop<List<String>> {

	}

	interface ConcreteTypeBottom<T extends String & CharSequence> extends ConcreteTypeMiddle {

	}

	@Test
	void testTypeAdaptMethod() {
		// contract: Adapting generic parameters declared at a method to an overridden method works
		// Adapt `R` from `<R> R foo(T t)` to `<S> S foo(R t)` and expect `R`
		Factory factory = new Launcher().getFactory();
		CtType<?> parent = factory.Type().get(MethodParent.class);
		CtType<?> child = factory.Type().get(MethodChild.class);

		CtMethod<?> parentMethod = parent.getMethodsByName("foo").get(0);
		CtMethod<?> childMethod = child.getMethodsByName("foo").get(0);

		CtTypeReference<?> adaptedReturnType = new TypeAdaptor(childMethod)
			.adaptType(parentMethod.getType());

		assertEquals("S", adaptedReturnType.getSimpleName());
		assertEquals(childMethod.getType().getDeclaration(), adaptedReturnType.getDeclaration());

		CtTypeReference<?> adaptedParameterType = new TypeAdaptor(
			childMethod.getParameters().get(0).getType()
		)
			.adaptType(parentMethod.getParameters().get(0).getType());
		assertEquals("R", adaptedParameterType.getSimpleName());
	}

	interface MethodParent<T> {

		<R> R foo(T t);
	}

	interface MethodChild<R> extends MethodParent<R> {

		<S> S foo(R t);
	}

	@Test
	void testRawtypeToObject() {
		// contract: Generic parameters inherited via a rawtype are resolved to Object
		Factory factory = new Launcher().getFactory();
		CtType<?> top = factory.Type().get(RawTop.class);
		CtType<?> bottom = factory.Type().get(RawBottom.class);
		CtClass<?> object = factory.Class().get(Object.class);
		CtTypeParameter topParameter = top.getFormalCtTypeParameters().get(0);

		assertThat(new TypeAdaptor(bottom).adaptType(topParameter.getReference()))
			.isEqualTo(object.getReference());
	}

	private static class RawTop<T> {
	}

	@SuppressWarnings("rawtypes")
	private static class RawMiddle extends RawTop {
	}

	private static class RawBottom extends RawMiddle {
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"justNormal",
		"withOwnParam",
		"withOwnParamAndParameters",
		"withMultipleExtendingOwn",
		"withAdaptableTypeInOwn",
		"withIntersection"
	})
	void adaptSimpleMethod(String name) {
		// contract: Various method signatures are equal to the existing subclass-method after adaption
		Factory factory = new Launcher().getFactory();
		CtType<?> parent = factory.Type().get(AdaptSimpleMethodParent.class);
		CtType<?> child = factory.Type().get(AdaptSimpleMethodChild.class);

		TypeAdaptor typeAdaptor = new TypeAdaptor(child);

		CtMethod<?> parentMethod = parent.getMethodsByName(name).get(0);
		CtMethod<?> childMethod = child.getMethodsByName(name).get(0);
		CtMethod<?> adapted = typeAdaptor.adaptMethod(parentMethod);

		for (int i = 0; i < childMethod.getParameters().size(); i++) {
			CtParameter<?> childParam = childMethod.getParameters().get(i);
			CtParameter<?> adaptedParam = adapted.getParameters().get(i);

			assertEquals(
				childParam.getType(),
				adaptedParam.getType(),
				"Parameter type differs for " + childParam.getSimpleName() + " " + adapted
			);
		}
		assertEquals(
			childMethod.getSignature(),
			adapted.getSignature(),
			"Signature differs for " + name + " " + adapted
		);
		assertEquals(
			childMethod.getType(),
			adapted.getType(),
			"Return type differs for " + name + " " + adapted
		);
	}

	private interface AdaptSimpleMethodParent<S, T> {

		T justNormal(List<T> input, S test);

		<Q> Q withOwnParam();

		<Q> Q withOwnParamAndParameters(List<Q> qs, T t, S s);

		<A, B extends List<A>> A withMultipleExtendingOwn(B b);

		<A, B extends List<T>> A withAdaptableTypeInOwn(B b);

		<A, B extends List<T> & MethodParent<S>> A withIntersection(B b);
	}

	private interface AdaptSimpleMethodChild<R> extends AdaptSimpleMethodParent<String, R> {

		@Override
		R justNormal(List<R> input, String test);

		@Override
		<Q> Q withOwnParam();

		@Override
		<Q> Q withOwnParamAndParameters(List<Q> qs, R r, String s);

		@Override
		<A, B extends List<A>> A withMultipleExtendingOwn(B as);

		@Override
		<A, B extends List<R>> A withAdaptableTypeInOwn(B rs);

		@Override
		<A, B extends List<R> & MethodParent<String>> A withIntersection(B rs);
	}

	@Test
	void testConstructorParameter() {
		// contract: Adapting a generic parameter works in a constructor
		Factory factory = new Launcher().getFactory();
		CtClass<?> top = factory.Class().get(ConstructorWithGenericParameterTop.class);
		CtClass<?> bottom = factory.Class().get(ConstructorWithGenericParameterBottom.class);

		CtConstructor<?> topConstructor = top.getConstructors().iterator().next();
		CtTypeReference<?> parameterType = topConstructor.getParameters().get(0).getType();

		assertThat(new TypeAdaptor(bottom).adaptType(parameterType))
			.isEqualTo(bottom.getFormalCtTypeParameters().get(0).getReference());
	}

	private static class ConstructorWithGenericParameterTop<T> {
		public ConstructorWithGenericParameterTop(T t) {
		}
	}

	private static class ConstructorWithGenericParameterBottom<R> extends ConstructorWithGenericParameterTop<R> {
		public ConstructorWithGenericParameterBottom(R r) {
			super(r);
		}
	}

	@Test
	void testGenericConstructor() {
		// contract: Adapting a generic parameter declared on a constructor works
		Factory factory = new Launcher().getFactory();
		CtClass<?> top = factory.Class().get(GenericConstructorTop.class);
		CtClass<?> bottom = factory.Class().get(GenericConstructorBottom.class);

		CtConstructor<?> topConstructor = top.getConstructors().iterator().next();
		CtConstructor<?> bottomConstructor = bottom.getConstructors().iterator().next();
		CtTypeReference<?> parameterType = topConstructor.getParameters().get(0).getType();

		assertTrue(
			topConstructor.getFormalCtTypeParameters().get(0).isSubtypeOf(parameterType),
			"top constructor generic is not a String subtype"
		);
		assertTrue(
			bottomConstructor.getFormalCtTypeParameters().get(0).isSubtypeOf(parameterType),
			"bottom constructor generic is not a String subtype"
		);
		assertEquals(
			"R",
			new TypeAdaptor(bottomConstructor).adaptType(parameterType).getSimpleName()
		);
	}

	private static class GenericConstructorTop {
		public <T extends String> GenericConstructorTop(T t) {
		}
	}

	private static class GenericConstructorBottom extends GenericConstructorTop {
		public <R extends String> GenericConstructorBottom(R r) {
			super(r);
		}
	}


	@Test
	void testArrayParameter() {
		// contract: Adaption of a varargs and array parameter works
		Factory factory = new Launcher().getFactory();
		CtType<?> parent = factory.Type().get(ArrayParameterParent.class);
		CtType<?> child = factory.Type().get(ArrayParameterChild.class);

		CtMethod<?> parentCrashyMethod = parent.getMethodsByName("crashy").get(0);
		CtMethod<?> childCrashyMethod = child.getMethodsByName("crashy").get(0);
		CtMethod<?> parentCrashyArrayMethod = parent.getMethodsByName("crashyArray").get(0);
		CtMethod<?> childCrashyArrayMethod = child.getMethodsByName("crashyArray").get(0);

		assertThat(childCrashyMethod).isEqualTo(new TypeAdaptor(child).adaptMethod(parentCrashyMethod));
		assertThat(childCrashyArrayMethod).isEqualTo(new TypeAdaptor(child).adaptMethod(parentCrashyArrayMethod));
	}

	private interface ArrayParameterParent<F> {

		void crashy(F... paramTypes);

		void crashyArray(F[] paramTypes);
	}

	private interface ArrayParameterChild extends ArrayParameterParent<String> {

		@Override
		void crashy(String... paramTypes);

		@Override
		void crashyArray(String[] paramTypes);
	}

	@ModelTest("src/test/java/spoon/support/TypeAdaptorTest.java")
	void testIsOverridingWithMethodDeclaredParameter(Factory factory) {
		// contract: Overriding checks of a method with a generic throws clause work
		CtType<?> parent = factory.Type().get(GenericThrowsParent.class.getName());
		CtType<?> child = factory.Type().get(GenericThrowsChild.class.getName());

		CtMethod<?> parentCrashyMethod = parent.getMethodsByName("orElseThrow").get(0);
		CtMethod<?> childCrashyMethod = child.getMethodsByName("orElseThrow").get(0);

		assertTrue(new TypeAdaptor(child).isOverriding(childCrashyMethod, parentCrashyMethod));
		assertFalse(new TypeAdaptor(child).isOverriding(parentCrashyMethod, childCrashyMethod));
	}

	@ModelTest("src/test/java/spoon/support/TypeAdaptorTest.java")
	void testOverridenOverloadingWithMethodDeclaredParameter(Factory factory) {
		// contract: Overloading/Overriding distinction works with type parameters with different erasure
		CtType<?> parent = factory.Type().get(GenericThrowsParent.class.getName());
		CtType<?> child = factory.Type().get(GenericThrowsChild.class.getName());

		CtMethod<?> parentOverloadedMethod = parent.getMethodsByName("overloaded").get(0);
		CtMethod<?> childOverloadedMethod = child.getMethodsByName("overloaded").get(0);
		CtMethod<?> parentOverriddenMethod = parent.getMethodsByName("overriden").get(0);
		CtMethod<?> childOverriddenMethod = child.getMethodsByName("overriden").get(0);

		assertFalse(new TypeAdaptor(child).isOverriding(childOverloadedMethod, parentOverloadedMethod));
		assertFalse(new TypeAdaptor(child).isOverriding(parentOverloadedMethod, childOverloadedMethod));

		assertTrue(new TypeAdaptor(child).isOverriding(childOverriddenMethod, parentOverriddenMethod));
		assertFalse(new TypeAdaptor(child).isOverriding(parentOverriddenMethod, childOverriddenMethod));
	}

	private static abstract class GenericThrowsParent {
		public abstract <E extends Throwable> void orElseThrow(E throwable) throws E;

		public <T extends String> void overloaded(T t) {
		}

		public abstract <T extends String> void overriden(T t);
	}

	private static class GenericThrowsChild extends GenericThrowsParent {
		@Override
		public <E extends Throwable> void orElseThrow(E throwable) throws E {
			throw throwable;
		}

		public <T extends CharSequence> void overloaded(T t) {
		}

		public <T extends String> void overriden(T t) {
		}
	}

	@Test
	@GitHubIssue(issueNumber = 5226, fixed = true)
	void testAdaptingTypeFromEnclosingClass() {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setComplianceLevel(11);
		launcher.addInputResource("src/test/java/spoon/support/TypeAdaptorTest.java");
		CtType<?> type = launcher.getFactory()
			.Type()
			.get(UseGenericFromEnclosingType.class);
		@SuppressWarnings("rawtypes")
		List<CtMethod> methods = type.getElements(new TypeFilter<>(CtMethod.class))
			.stream()
			.filter(it -> it.getSimpleName().equals("someMethod"))
			.collect(Collectors.toList());
		CtMethod<?> test1Method = methods.stream()
			.filter(it -> !it.getDeclaringType().getSimpleName().startsWith("Extends"))
			.findAny()
			.orElseThrow();
		CtMethod<?> test2Method = methods.stream()
			.filter(it -> it.getDeclaringType().getSimpleName().startsWith("Extends"))
			.findAny()
			.orElseThrow();

		assertTrue(test2Method.isOverriding(test1Method));
		assertFalse(test1Method.isOverriding(test2Method));
	}

	public static class UseGenericFromEnclosingType {

		public static class Enclosing<T> {

			public class Enclosed<S> {

				void someMethod(S s, T t) {
				}
			}
		}

		public static class ExtendsEnclosing extends Enclosing<String> {

			public class ExtendsEnclosed extends Enclosed<Integer> {

				@Override
				void someMethod(Integer s, String t) {
					throw new UnsupportedOperationException();
				}
			}
		}
	}

	@Test
	@GitHubIssue(issueNumber = 5462, fixed = true)
	void testArraySubtypingShadow() {
		// contract: Array subtyping should hold for shadow types
		Factory factory = new Launcher().getFactory();
		CtTypeReference<Integer> intType = factory.Type().integerType();
		CtArrayTypeReference<?> intArr = factory.createArrayReference(intType, 1);
		CtArrayTypeReference<?> intArr2 = factory.createArrayReference(intType, 2);

		CtTypeReference<Number> numType = factory.createCtTypeReference(Number.class);
		CtArrayTypeReference<?> numArr = factory.createArrayReference(numType, 1);
		CtArrayTypeReference<?> numArr2 = factory.createArrayReference(numType, 2);

		CtArrayTypeReference<?> objArr = factory.createArrayReference(factory.Type().objectType(), 1);

		verifySubtype(intArr, numArr, true);
		verifySubtype(intArr2, numArr2, true);

		verifySubtype(intArr, numArr2, false);
		verifySubtype(intArr, numType, false);
		verifySubtype(intArr, intType, false);
		verifySubtype(numArr, numType, false);
		verifySubtype(numArr, intType, false);
		verifySubtype(intArr2, numType, false);
		verifySubtype(intArr2, intType, false);
		verifySubtype(numArr2, numType, false);
		verifySubtype(numArr2, intType, false);

		// int[][] < Object[], as int[] < Object
		verifySubtype(intArr2, objArr, true);
		verifySubtype(numArr2, objArr, true);
	}

	@Test
	@GitHubIssue(issueNumber = 5462, fixed = true)
	void testArraySubtypingNoShadow() {
		// contract: Array subtyping should hold for non-shadow types
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(false);
		launcher.getEnvironment().setShouldCompile(true);
		launcher.addInputResource(new VirtualFile(
			"public class Foo {}",
			"Foo.java"
		));
		launcher.addInputResource(new VirtualFile(
			"public class Bar extends Foo {}",
			"Bar.java"
		));
		launcher.buildModel();
		Factory factory = launcher.getFactory();

		CtTypeReference<?> barType = factory.Type().get("Bar").getReference();
		CtArrayTypeReference<?> barArr = factory.createArrayReference(barType, 1);
		CtArrayTypeReference<?> barArr2 = factory.createArrayReference(barType, 2);

		CtTypeReference<?> fooType = factory.Type().get("Foo").getReference();
		CtArrayTypeReference<?> fooArr = factory.createArrayReference(fooType, 1);
		CtArrayTypeReference<?> fooArr2 = factory.createArrayReference(fooType, 2);

		verifySubtype(barArr, fooArr, true);
		verifySubtype(barArr2, fooArr2, true);

		verifySubtype(barArr, fooArr2, false);
		verifySubtype(barArr, fooType, false);
		verifySubtype(barArr, barType, false);
	}

	@Test
	@GitHubIssue(issueNumber = 5462, fixed = true)
	void testArraySubtypingInbuiltTypes() {
		// contract: Array subtyping should hold for inbuilt types (Cloneable, Serializable, Object)
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(false);
		launcher.getEnvironment().setShouldCompile(true);
		launcher.addInputResource(new VirtualFile(
			"public class Foo {}",
			"Foo.java"
		));
		CtTypeReference<?> fooType = launcher.buildModel().getAllTypes().iterator().next().getReference();
		Factory factory = launcher.getFactory();

		CtArrayTypeReference<?> fooArray = factory.createArrayReference(fooType, 1);
		CtArrayTypeReference<?> intArray = factory.createArrayReference(factory.Type().integerPrimitiveType(), 1);
		CtArrayTypeReference<?> objArray = factory.createArrayReference(factory.Type().objectType(), 1);

		verifySubtype(fooArray, factory.createCtTypeReference(Object.class), true);
		verifySubtype(fooArray, factory.createCtTypeReference(Cloneable.class), true);
		verifySubtype(fooArray, factory.createCtTypeReference(Serializable.class), true);

		verifySubtype(intArray, factory.createCtTypeReference(Object.class), true);
		verifySubtype(intArray, factory.createCtTypeReference(Cloneable.class), true);
		verifySubtype(intArray, factory.createCtTypeReference(Serializable.class), true);

		verifySubtype(objArray, factory.createCtTypeReference(Object.class), true);
		verifySubtype(objArray, factory.createCtTypeReference(Cloneable.class), true);
		verifySubtype(objArray, factory.createCtTypeReference(Serializable.class), true);
	}

	private static void verifySubtype(CtTypeReference<?> bottom, CtTypeReference<?> top, boolean shouldSubtype) {
		String message = bottom + (shouldSubtype ? " < " : " !< ") + top;
		boolean resultReference = bottom.isSubtypeOf(top);
		boolean resultAdaptor = new TypeAdaptor(bottom).isSubtypeOf(top);
		assertEquals(shouldSubtype, resultReference, message);
		assertEquals(shouldSubtype, resultAdaptor, message);

		// There are no type declarations for source level arrays
		if (bottom.getTypeDeclaration() != null) {
			boolean resultAdaptorTwo = TypeAdaptor.isSubtype(bottom.getTypeDeclaration(), top);
			assertEquals(shouldSubtype, resultAdaptorTwo, message);
		}

	}
}
