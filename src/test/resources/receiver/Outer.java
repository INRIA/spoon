package receiver;
public class Outer {
    class Middle {
        class Inner {
            public Inner(Middle Middle.this) {

            }
        }
    }
}
