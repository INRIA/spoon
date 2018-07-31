package spoon.test.staticFieldAccess2.testclasses;

public class ChildOfGenericsWithAmbiguousStaticField extends spoon.test.staticFieldAccess2.testclasses.GenericsWithAmbiguousStaticField
{

    public <V, C extends java.util.List<V>> void m1() {
    	spoon.test.staticFieldAccess2.testclasses.GenericsWithAmbiguousStaticField.<V, C>genericMethod();
    	genericMethod();
    }

}
