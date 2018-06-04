package spoon.test.annotation.testclasses.shadow;

public class DumbKlass {

    @StandardRetention
    public void foo() { }

    @ClassRetention
    public void fooClass() {}

    @RuntimeRetention(role = "bidule")
    public void barOneValue() {}

    @RuntimeRetention(role = { "bidule"})
    public void barMultipleValues() { }
}
