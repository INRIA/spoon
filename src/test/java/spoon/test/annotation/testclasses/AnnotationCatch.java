package spoon.test.annotation.testclasses;

public class AnnotationCatch {
    void m1(String s) {
        try {
            s.length();
        } catch (@CustomAnnotation(something =  "annotation string") NullPointerException e) {
            e.printStackTrace();
        }
    }
}
