package spoon.test.issue3321;

import org.junit.Test;
import org.junit.Ignore;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.support.sniper.SniperJavaPrettyPrinter;
import spoon.test.GitHubIssue;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Apr, 2020
 **/
public class SniperPrettyPrinterJavaxTest {
    @Test
    public void testThatCanGenerateSniperPrettyPrintedSourceForJavaxAnnotatedClasses() {
        //This test insure that annotations being cloned and associated to to elements
        //(here a CtParameter and its associated CtTypeReference) do not trigger a crash of the sniper pretty printer.
        final Launcher l = new Launcher();
        Environment e = l.getEnvironment();

        e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(e));
        e.setNoClasspath(true);
        e.setAutoImports(true);
        l.addProcessor(new CtClassProcessor());
        l.addInputResource("src/test/java/spoon/test/issue3321/source/JavaxImportTestSource.java");
        l.setSourceOutputDirectory("src/test/resources");
        l.run();


    }
}
