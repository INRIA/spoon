package spoon.test.annotation.testclasses.repeatandarrays;

public class RepeatedArrays {
    @TagArrays({"machin", "truc"})
    @TagArrays({"truc", "bidule"})
    public void method() {}

    public void withoutAnnotation() {

    }
}
