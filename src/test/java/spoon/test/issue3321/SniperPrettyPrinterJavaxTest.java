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
    @GitHubIssue(issueNumber = 3321)
    @Ignore("UnresolvedBug")
    public void testThatCanGenerateSniperPrettyPrintedSourceForJavaxAnnotatedClasses() {
        final Launcher l = new Launcher();
        Environment e = l.getEnvironment();

        e.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(e));
        e.setNoClasspath(true);
        e.setAutoImports(true);
        l.addProcessor(new CtClassProcessor());
        l.addInputResource("src/test/java/spoon/test/issue3321/source/JavaxImportTestSource.java");
        l.setSourceOutputDirectory("src/test/resources");

        //CtModel model = l.buildModel();

        l.run();
    }
}
