import java.util.List;

import static java.io.File.pathSeparator;
import static imported.SeparateInterfaceWithField.fieldInSeparateInterface;
import static test.unknown.UnknownClass.unknownField;

interface InterfaceImplementedByExtendedClass {
    String iAmToLazyForAnotherFieldName = "spoon";
}

class ClassWithFieldBase implements InterfaceImplementedByExtendedClass {
    public String fieldInClassBase;
}

class ClassWithField extends ClassWithFieldBase {
    public String fieldInClass;
}

interface InterfaceWithFieldBase {
    String fieldInInterfaceBase = "spoon";
}

interface InterfaceWithField extends InterfaceWithFieldBase {
    String fieldInInterface = "spoon";
}

public class FieldAccessInLambda extends ClassWithField implements InterfaceWithField {
    private final String localField = "spoon";

    public void lambdaLocalField(final List<String> strings) {
        strings.forEach(s -> System.out.println(s + localField));
    }

    public void lambdaStaticallyImportedField(final List<String> strings) {
        strings.forEach(s -> System.out.println(s + pathSeparator));
    }

    public void lambdaStaticallyImportedFieldInterface(final List<String> strings) {
        strings.forEach(s -> System.out.println(s + fieldInSeparateInterface));
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

    public void lambdaFieldInUnknownClass(final List<String> strings) {
        strings.forEach(s -> System.out.println(s + unknownField));
    }
}
