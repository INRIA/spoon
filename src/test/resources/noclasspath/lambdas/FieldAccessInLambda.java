import static java.io.File.pathSeparator;

private interface InterfaceImplementedByExtendedClass {
    String iAmToLazyForAnotherFieldName;
}

public class ClassWithFieldBase implements InterfaceImplementedByExtendedClass {
    public String fieldInClassBase;
}

public class ClassWithField extends ClassWithFieldBase {
    public String fieldInClass;
}

public interface InterfaceWithFieldBase {
    String fieldInInterfaceBase;
}

public interface InterfaceWithField extends InterfaceWithFieldBase {
    String fieldInInterface;
}

public class FieldAccessInLambda extends ClassWithField implements InterfaceWithField {
    private final String localField = "spoon";

    public void lambdaLocalField(final List<String> strings) {
        strings.forEach(s -> System.out.println(s + localField));
    }

    public void lambdaStaticallyImportedField(final List<String> strings) {
        strings.forEach(s -> System.out.println(s + pathSeparator));
    }

    public void lambdaFieldInClassBase(final List<String> strings) {
        strings.forEach(s -> System.out.println(s + fieldInClassBase));
    }

    public void lambdaFieldInClass(final List<String> strings) {
        strings.forEach(s -> System.out.println(s + fieldInClass));
    }

    public void lambdaFieldInInterfaceBase(final List<String> strings) {
        strings.forEach(s -> System.out.println(s + fieldInInterfaceBase));
    }

    public void lambdaFieldInInterface(final List<String> strings) {
        strings.forEach(s -> System.out.println(s + fieldInInterface));
    }

    public void lambdaFieldInterfaceImplementedByExtendedClass(final List<String> strings) {
        strings.forEach(s -> System.out.println(s + iAmToLazyForAnotherFieldName));
    }
}