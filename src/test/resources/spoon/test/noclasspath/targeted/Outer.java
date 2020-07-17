public class Outer {
    SomeClass cls = new SomeClass();

    private class Inner {
        public void testMethod() {
            int a = cls.val;
        }
    }
}