// the purpose of this test class is to check that omitted type arguments are properly resolved
class GenericTypeEmptyDiamond {
    public static void main(String[] args) {
        GenericKnownExpectedType<Integer, String> someGeneric = new GenericKnownExpectedType<>();
        meth(new GenericUnknownExpectedType<>());
    }
}