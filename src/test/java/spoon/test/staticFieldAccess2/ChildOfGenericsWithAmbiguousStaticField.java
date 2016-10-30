package spoon.test.staticFieldAccess2;

public class ChildOfGenericsWithAmbiguousStaticField extends GenericsWithAmbiguousStaticField
{

    public <V, C extends java.util.List<V>> void m1() {
    	spoon.test.staticFieldAccess2.GenericsWithAmbiguousStaticField.<V, C>genericMethod();
    	genericMethod();
    }

}
