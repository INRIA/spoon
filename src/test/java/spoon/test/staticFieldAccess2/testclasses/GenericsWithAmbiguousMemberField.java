package spoon.test.staticFieldAccess2.testclasses;

public class GenericsWithAmbiguousMemberField {
	
	String GenericsWithAmbiguousMemberField = "x";

    public <V, C extends java.util.List<V>> void m1() {
    	spoon.test.staticFieldAccess2.testclasses.GenericsWithAmbiguousMemberField.<V, C>genericMethod();
    	GenericsWithAmbiguousMemberField.length();
    	genericMethod();
    }

    public static <V, C extends java.util.List<V>> java.util.List<C> genericMethod() {
        return null;
    }
	
}
