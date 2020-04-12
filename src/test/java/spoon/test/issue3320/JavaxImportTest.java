package spoon.test.issue3320;

import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.test.issue3320.source.JavaxImportTestSource;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Apr, 2020
 **/
public class JavaxImportTest {
    @Test
    public void testThatCorrectJavaxImportsAreGenerated() {
        final Launcher l = new Launcher();
        Environment e = l.getEnvironment();

        e.setNoClasspath(true);
        e.setAutoImports(true);
        l.addProcessor(new CtClassProcessor());
        l.addInputResource("src/test/java/spoon/test/issue3320/source/JavaxImportTestSource.java");
        l.setSourceOutputDirectory("src/test/resources");
        l.run();
    }
}
