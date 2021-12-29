package spoon.test.issue4357;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestIssue4357 {
    
    @Test
    public void testClasspathURLWithSpaces() throws MalformedURLException {
        // contract: launcher can handle spaces in classpath URL
        Launcher launcher = new Launcher();
        URL[] classpath = {
                Paths.get("./src/test/resources/path with spaces/lib/bar.jar").toAbsolutePath().toUri().toURL()
        };
        launcher.getEnvironment().setNoClasspath(false);
        launcher.getEnvironment().setShouldCompile(true);
        ClassLoader classLoader = new URLClassLoader(classpath);
        launcher.getEnvironment().setInputClassLoader(classLoader);
        launcher.addInputResource(Paths.get("./src/test/resources/path with spaces/Foo.java").toAbsolutePath().toString());
        CtModel model = launcher.buildModel();

        assertTrue(model.getAllTypes().stream().anyMatch(ct -> ct.getQualifiedName().equals("Foo")), 
                "CtTxpe 'Foo' not present in model");
    }
}
