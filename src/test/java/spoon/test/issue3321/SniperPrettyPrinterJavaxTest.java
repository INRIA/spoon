package spoon.test.issue3321;

import spoon.support.sniper.SniperJavaPrettyPrinter;
import spoon.Launcher;
import spoon.compiler.Environment;
import org.junit.jupiter.api.Test;

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
