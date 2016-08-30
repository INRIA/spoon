import test.UnknownClass;

public class UnknownCalls {

    public static void main(final String[] args) {
        UnknownClass[] ucs = new UnknownClass[10];
        for (int i = 0; i < ucs.length; i++) {
            // unknown constructor call
            ucs[i] = new UnknownClass();
            // unknown method call
            ucs[i].anUnknownMethod();
        }
    }
}
