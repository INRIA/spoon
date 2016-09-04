public class FieldAccessInLambda {
    private final String fieldVariable = "spoon";

    public void testMethod(final List<String> strings) {
        strings.forEach(s -> System.out.println(s + fieldVariable));
    }
}