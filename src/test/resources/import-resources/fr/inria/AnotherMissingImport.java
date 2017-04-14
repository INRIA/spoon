
package fr.inria;
import externallib.SomeType;

public class AnotherMissingImport {

    public void doSomething(SomeType<String> someType) {
    }

    public static void main(String[] args) {
        AnotherMissingImport instance = new AnotherMissingImport();
        instance.doSomething(null);
    }
}
