package spoon.test.reference.testclasses;

public enum Enum {
    A,
    B,
    C;
    
    public static Enum getFirst(){
        return valueOf("A");
    }
}
