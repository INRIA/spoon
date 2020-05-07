// the purpose of this test class is to check that omitted type arguments are properly resolved in noclasspath mode
import java.util.ArrayList;

class GenericTypeEmptyDiamond {
    public static void main(String[] args) {
        // the context should allow the type arguments for this constructor call to be recovered
        GenericKnownExpectedType<Integer, String> someGeneric = new GenericKnownExpectedType<>();
        // meth is an unresolved method, so there is no context to allow for inference of type arguments
        meth(new GenericUnknownExpectedType<>());
        // same as the above, but with a generic type that is available on the classpath
        meth(new ArrayList<>());
    }
}