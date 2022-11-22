class A {
    A a;
    int i;
}
class C extends A {
    Object a;
}

public class ShadowFieldRead {
    public static void main(String[] args) {
        C c = new C();
        int fieldReadOfA = ((A) c).a.i;
    }
}