package methodimport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set;

import static methodimport.ClassWithStaticMethod.staticMethod;

/**
 * This compilation unit has a method import of the method staticMethod, and the import has a
 * greater source position than the definition of staticMethod has in its respective file.
 */
public class MethodImportBelowImportedMethod {
    public static void main(String[] args) {
        staticMethod();
    }
}
