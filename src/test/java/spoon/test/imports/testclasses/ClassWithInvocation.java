package spoon.test.imports.testclasses;

import sun.reflect.annotation.TypeAnnotation;

/**
 * Created by thomas on 11/09/15.
 */
public class ClassWithInvocation {
    public ClassWithInvocation() {
        test(TypeAnnotation.class);
    }
    public String test(Class cl) {
        return cl.getCanonicalName();
    }
}
