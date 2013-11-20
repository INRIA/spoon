package spoon.test.generics;

public class GenericMethodCallWithExtend {

	public static <E> void tmp() {
	}

	@SafeVarargs
	public static <E extends java.lang.Enum<E>> long methode(E... values) {
		GenericMethodCallWithExtend.<E> tmp();
		return 2l;
	}
}
