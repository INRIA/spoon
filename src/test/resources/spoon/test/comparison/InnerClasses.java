package spoon;

public class InnerClasses {
    public static abstract class AbstractClass {
    }

    public void test() {
    }

    public AbstractClass one = new AbstractClass() {
        public AbstractClass one = new AbstractClass() {
            public void test() {
            }
        };

        public void test() {
        }
    };

    static public AbstractClass two = new AbstractClass() {
        public void test() {
        }
    };

    static class Inner1 {
        public void test() {
        }
    }

    class Inner2 {
        class Inner3 {
            public void test() {
            }
        }
    }
}
