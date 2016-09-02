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

        UnknownCalls[][][] ucsmd = new UnknownCalls[10][10][10];
        for (int i = 0; i < ucsmd.length; i++) {
            for (int j = 0; j < ucsmd[i].length; j++) {
                for (int k = 0; k < ucsmd[i][j].length; k++) {
                    // unknown constructor call
                    ucs[i][j][k] = new UnknownClass();
                    // unknown method call
                    ucs[i][j][k].anUnknownMethod();
                }
            }
        }
    }
}
