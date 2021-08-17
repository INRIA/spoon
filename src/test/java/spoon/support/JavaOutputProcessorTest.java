package spoon.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JavaOutputProcessorTest {


    @Test
    void testCreateModuleFileAssertAnnotationFileCreated(@TempDir File tempDir) {
        // contract : createModuleFile creates an annotation file for module and prints it

        // arrange
        Launcher launcher = new Launcher();
        launcher.setSourceOutputDirectory(tempDir.getAbsolutePath());
        launcher.getEnvironment().setComplianceLevel(9);
        Factory factory = launcher.getFactory();

        String moduleName = "emptyModule";
        CtModule module = factory.Module().getOrCreate(moduleName);
        JavaOutputProcessor javaOutputProcessor = new JavaOutputProcessor();
        javaOutputProcessor.setFactory(factory);

        // act
        javaOutputProcessor.process(module);

        // assert
        File expectedFile = tempDir.toPath().resolve("emptyModule/module-info.java").toFile();
        assertTrue(expectedFile.exists());
        assertEquals(1, javaOutputProcessor.printedFiles.size());
    }

    @Test
    void testCreatePackageFileAssertAnnotationFileCreated(@TempDir File tempDir) {
        // contract : createPackageFile creates a package annotation file for rootPackage

        // arrange
        Launcher launcher = new Launcher();
        launcher.setSourceOutputDirectory(tempDir.getAbsolutePath());
        Factory factory = launcher.getFactory();

        CtPackage rootPackage = factory.Package().getOrCreate("spoon.support.JavaOutputProcessor");
        JavaOutputProcessor javaOutputProcessor = new JavaOutputProcessor();
        javaOutputProcessor.setFactory(factory);

        // act
        javaOutputProcessor.process(rootPackage);

        // assert
        File expectedFile =  tempDir.toPath().resolve("spoon/support/JavaOutputProcessor/package-info.java").toFile();
        assertTrue(expectedFile.exists());
        assertEquals(1, javaOutputProcessor.printedFiles.size());
    }
}