package spoon.test.staticFieldAccess2;

public class GenericsWithAmbiguousMemberField {
	
	String GenericsWithAmbiguousMemberField = "x";

    public <V, C extends java.util.List<V>> void m1() {
    	spoon.test.staticFieldAccess2.GenericsWithAmbiguousMemberField.<V, C>genericMethod();
    	GenericsWithAmbiguousMemberField.length();
    	genericMethod();
    }

    public static <V, C extends java.util.List<V>> java.util.List<C> genericMethod() {
        return null;
    }
	
}
