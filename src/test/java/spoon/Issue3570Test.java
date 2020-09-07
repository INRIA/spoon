package spoon;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import spoon.compiler.Environment;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.reference.CtTypeReference;

import java.nio.charset.StandardCharsets;

/**
 * Issue3570
 */
public class Issue3570Test {

    @Test
    void test(){
        assertDoesNotThrow(() -> {
            Launcher launcher = new Launcher();
            Environment environment = launcher.getEnvironment();
            environment.setEncoding(StandardCharsets.UTF_8);
            environment.disableConsistencyChecks();
            environment.setComplianceLevel(9);
            environment.setIgnoreDuplicateDeclarations(true);
            environment.setCommentEnabled(false);
            environment.setNoClasspath(true);
            environment.setShouldCompile(false);
            environment.debugMessage("ALL");
            launcher.addInputResource("src/test/resources/issue3570/DefaultTeXHyphenData.java");
            launcher.buildModel();
            System.out.println("Model built"); // Exception is thrown only when we access the model, apparently.
            System.out.println(launcher.getModel().getAllTypes().size());
        });
    }
}
