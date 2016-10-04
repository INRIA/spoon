public class MultipleDeclarationsOfLocalVariable {

    public static void main(final String[] args) {
        {
            int j = -1;
        }
        int j = 42;
        int i=j;
    }
}
