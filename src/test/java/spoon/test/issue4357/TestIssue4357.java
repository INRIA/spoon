package spoon.test.issue4357;

import org.junit.jupiter.api.Test;
import spoon.Launcher;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TestIssue4357 {
    
    @Test
    public void testClasspathURLWithSpaces() throws MalformedURLException {
        // contract: launcher can handle spaces in classpath URL
        Launcher launcher = new Launcher();
        URL[] classpath = {
                Paths.get("src/test/resources/path with spaces").toUri().toURL()
        };
        ClassLoader classLoader = new URLClassLoader(classpath);
        launcher.getEnvironment().setInputClassLoader(classLoader);
        launcher.getEnvironment().setNoClasspath(false);
        assertDoesNotThrow(() -> launcher.buildModel());     
    }
}
