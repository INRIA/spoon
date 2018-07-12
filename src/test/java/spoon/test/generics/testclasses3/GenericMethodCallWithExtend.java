package spoon.test.generics.testclasses3;

public class GenericMethodCallWithExtend {

	public static <E> void tmp() {
	}

	@SafeVarargs
	public static <E extends java.lang.Enum<E>> long methode(E... values) {
		GenericMethodCallWithExtend.<E> tmp();
		return 2l;
	}

	public  <A extends Number & Comparable<? super A>> Class<A> m2() {
		return null;
	}
}
