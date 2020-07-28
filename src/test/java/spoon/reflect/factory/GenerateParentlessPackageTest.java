package spoon.reflect.factory;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * This test generates a parent-less package.
 */
public class GenerateParentlessPackageTest {

    @Test
    public void generateParentlessPackage() {
        final Path tempDir;

        try {
            tempDir = Files.createTempDirectory("spoon");
        } catch (final IOException e) {
            fail("Could not generate temporary directory: " + e.getMessage());
            return;
        }

        final Launcher launcher = new Launcher();
        launcher.addInputResource(tempDir.toFile().getAbsolutePath());
        launcher.setSourceOutputDirectory(tempDir.toFile().getAbsolutePath());
        launcher.getEnvironment().setAutoImports(true);
        launcher.buildModel();

        final Factory factory = launcher.getFactory();

        //contract: a package without a parent can be created
        final CtPackage ctPackage = factory.createPackage(null, "fooBar");
        assertNotNull(ctPackage);
    }

}
