package spoon.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.adaption.TypeAdaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.Assert.assertThat;

class TypeAdaptorTest {

	@Test
	void testSubtypeSuperclass() {
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
	void testArrayParameter() {
		Factory factory = new Launcher().getFactory();
		CtType<?> parent = factory.Type().get(ArrayParameterParent.class);
		CtType<?> child = factory.Type().get(ArrayParameterChild.class);

		CtMethod<?> parentMethod = parent.getMethodsByName("crashy").get(0);
		CtMethod<?> childMethod = child.getMethodsByName("crashy").get(0);

		CtMethod<?> adapted = new TypeAdaptor(child).adaptMethod(parentMethod);
		assertThat(childMethod).isEqualTo(adapted);
	}

	private interface ArrayParameterParent<F> {

		void crashy(F... paramTypes);
	}

	private interface ArrayParameterChild extends ArrayParameterParent<String> {

		@Override
		void crashy(String... paramTypes);
	}
}
