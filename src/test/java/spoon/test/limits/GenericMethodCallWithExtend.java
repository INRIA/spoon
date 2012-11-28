package spoon.test.limits;

public class GenericMethodCallWithExtend {
	
	public static <E> void tmp(){}
	
	public static <E extends java.lang.Enum<E>> long methode(E... values) {
		GenericMethodCallWithExtend.<E>tmp();
		return 2l;
	}
}
