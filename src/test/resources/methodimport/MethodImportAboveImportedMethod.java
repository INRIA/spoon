package methodimport;
import static methodimport.ClassWithStaticMethod.staticMethod;

/**
 * This compilation unit has a method import of the method staticMethod, and the import has a
 * lesser source position than the definition of staticMethod has in its respective file.
 */
public class MethodImportAboveImportedMethod {
    public static void main(String[] args) {
        staticMethod();
    }
}
