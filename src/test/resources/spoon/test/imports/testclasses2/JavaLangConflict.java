package spoon.test.imports.testclasses2;

import java.io.File;

/**
 * Created by urli on 22/05/2017.
 */
public class JavaLangConflict {
    String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
}
