package spoon.test.annotation.testclasses;

/**
 * Created by thomas on 20/10/15.
 */
public class AnnotationIntrospection {

    @TestAnnotation
    public void m() throws NoSuchMethodException {
        TestAnnotation annotation = getClass().getMethod("m").getAnnotation(TestAnnotation.class);
        annotation.equals(null);
    }
}
