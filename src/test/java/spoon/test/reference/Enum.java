package spoon.test.reference;

public enum Enum {
    A,
    B,
    C;
    
    public static Enum getFirst(){
        return valueOf("A");
    }
}
