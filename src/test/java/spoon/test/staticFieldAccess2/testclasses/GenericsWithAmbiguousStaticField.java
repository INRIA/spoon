package spoon.test.staticFieldAccess2.testclasses;

public class GenericsWithAmbiguousStaticField {
	
	static String GenericsWithAmbiguousStaticField = "x";

    public <V, C extends java.util.List<V>> void m1() {
    	spoon.test.staticFieldAccess2.testclasses.GenericsWithAmbiguousStaticField.<V, C>genericMethod();
    	genericMethod();
    }

    public static <V, C extends java.util.List<V>> java.util.List<C> genericMethod() {
        return null;
    }
	
}
