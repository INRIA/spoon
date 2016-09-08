import test.utils.Strings;

public class TypeAccessInLambda {

    public void testMethod(final List<String> strings) {
        strings.forEach(s -> Strings.ToUpperCase(s));
    }
}
