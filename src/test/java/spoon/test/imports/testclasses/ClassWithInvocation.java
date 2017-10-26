package spoon.test.imports.testclasses;

import spoon.test.annotation.testclasses.GlobalAnnotation;

/**
 * Created by thomas on 11/09/15.
 */
public class ClassWithInvocation {
    public ClassWithInvocation() {
        test(GlobalAnnotation.class);
    }
    public String test(Class cl) {
        return cl.getCanonicalName();
    }
}
