public class Anonymous {
    public static abstract class AbstractClass {
    }

    public void test() {
    }

    public Anonymous.AbstractClass one = new Anonymous.AbstractClass() {
        public Anonymous.AbstractClass one = new Anonymous.AbstractClass() {
            public void test() {
            }
        };

        public void test() {
        }
    };

    public Anonymous.AbstractClass two = new Anonymous.AbstractClass() {
        public void test() {
        }
    };

    class Inner1 {
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
