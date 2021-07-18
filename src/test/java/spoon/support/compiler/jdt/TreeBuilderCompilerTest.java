package spoon.support.compiler.jdt;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TreeBuilderCompilerTest {

    @Test
    public void testFilterInvalid() {
        // contract: if a file has any syntax errors, it is filtered out, otherwise, it is compiled
        final TestLogger logger = TestLoggerFactory.getTestLogger(TreeBuilderCompiler.class);
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setFilterInvalid(true);
        launcher.addInputResource("./src/test/resources/compilation2/InvalidClass.java");
        launcher.addInputResource("./src/test/resources/compilation/ClassWithStaticFields.java");
        launcher.buildModel();
        CtModel model = launcher.getModel();
        assertEquals(1,model.getAllTypes().size());

        // contract: if a file has any syntax errors, the incorrect file name is logged
        assertTrue(logger.getLoggingEvents().get(0).getMessage().startsWith("Syntax error detected in"));

        // contract: if every input resource has a syntax error, spoon does not crash
        launcher = new Launcher();
        launcher.getEnvironment().setFilterInvalid(true);
        launcher.addInputResource("./src/test/resources/compilation2/InvalidClass.java");
        launcher.buildModel();
        model = launcher.getModel();
        assertTrue(model.getAllTypes().isEmpty());

        // contract: filter-invalid can be enabled with a command line argument
        launcher = new Launcher();
        launcher.setArgs(new String[]{"--filter-invalid", "-i", "./src/test/resources/compilation2/InvalidClass.java"});
        launcher.buildModel();
        model = launcher.getModel();
        assertTrue(model.getAllTypes().isEmpty());
    }
}
