package spoon.support.compiler.jdt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.test.logging.LogTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TreeBuilderCompilerTest {

    @Test
    public void testIgnoreSyntaxErrorsCompilation() {
        // contract: if a file has any syntax errors, it is filtered out, otherwise, it is compiled
        Launcher launcher = setupLauncher();
        launcher.addInputResource("./src/test/resources/compilation/ClassWithStaticFields.java");
        launcher.buildModel();
        CtModel model = launcher.getModel();
        assertEquals(1,model.getAllTypes().size());
    }

    @ExtendWith(LogTest.LogCaptureExtension.class)
    @Test
    public void testIgnoreSyntaxErrorsLogging(LogTest.LogCapture logCapture) {
        // contract: if a file has any syntax errors, the name of the incorrect file is logged
        Launcher launcher = setupLauncher();
        launcher.buildModel();
        assertTrue(logCapture.loggingEvents().get(0).getMessage().endsWith("InvalidClass.java"));
    }

    @Test
    public void testEveryInputHasSyntaxError() {
        // contract: if every input resource has a syntax error, spoon does not crash
        Launcher launcher = setupLauncher();
        launcher.buildModel();
        CtModel model = launcher.getModel();
        assertTrue(model.getAllTypes().isEmpty());
    }

    @Test
    public void testIgnoreSyntaxErrorsCommandLine() {
        // contract: ignore-syntax-errors can be enabled with a command line argument
        Launcher launcher = new Launcher();
        launcher.setArgs(new String[]{"--ignore-syntax-errors", "-i", "./src/test/resources/compilation2/InvalidClass.java"});
        launcher.buildModel();
        CtModel model = launcher.getModel();
        assertTrue(model.getAllTypes().isEmpty());
    }

    private Launcher setupLauncher() {
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setIgnoreSyntaxErrors(true);
        launcher.addInputResource("./src/test/resources/compilation2/InvalidClass.java");
        return launcher;
    }
}
