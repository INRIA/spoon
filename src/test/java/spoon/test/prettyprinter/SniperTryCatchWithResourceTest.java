package spoon.test.prettyprinter;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.compiler.Environment;
import spoon.processing.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.support.sniper.SniperJavaPrettyPrinter;

import java.io.File;
import java.util.Set;

/**
 * @author Benjamin DANGLOT
 * benjamin.danglot@davidson.fr
 * on 22/04/2022
 */
public class SniperTryCatchWithResourceTest {

    @Test
    void test() {
        final Launcher launcher = new Launcher();
        final Environment environment = launcher.getEnvironment();
        environment.setLevel("INFO");
        environment.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(environment));

        launcher.addInputResource("src/test/java/spoon/test/prettyprinter/testclasses/TryCatchWithResource.java");
        launcher.addProcessor(new AbstractProcessor<CtMethod<?>>() {
            @Override
            public void process(CtMethod<?> element) {
                element.getBody().insertBegin(
                        element.getFactory().createCodeSnippetStatement("System.out.println(\"PRINT\")")
                );
            }
        });
        launcher.getEnvironment().setSourceOutputDirectory(new File("target/test-output"));
        launcher.getEnvironment().setOutputType(OutputType.CLASSES);
        launcher.run();
    }
}
