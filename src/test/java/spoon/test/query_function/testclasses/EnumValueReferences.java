package spoon.test.query_function.testclasses;

public class EnumValueReferences {
    public enum TestEnum {
        VALUE1,
        VALUE2
    }
    
    TestEnum field = TestEnum.VALUE1;
    
    void method() {
        TestEnum local = TestEnum.VALUE2;
    }
}
