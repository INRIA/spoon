package spoon;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Assert;

import spoon.reflect.visitor.FragmentDrivenJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoon.support.StandardEnvironment;

public class LauncherTest extends TestCase {

    private Launcher classUnderTest;

    public void setUp() throws Exception {
        classUnderTest = new Launcher();
    }

    public void testInitEnvironment() throws Exception {
        File properties = new File("/");
        int complianceLevel = 5;
        boolean verbose = true;
        boolean debug = true;
        boolean autoImports = true;
        int tabulationSize = 42;
        boolean useTabulations = true;
        boolean useSourceCodeFragments = true;
        boolean preserveLineNumbers = true;

        StandardEnvironment environment = new StandardEnvironment();
        classUnderTest.initEnvironment(environment, complianceLevel, verbose, debug, properties, autoImports, tabulationSize, useTabulations, useSourceCodeFragments, preserveLineNumbers, new File("/home"));

        Assert.assertEquals(properties, environment.getXmlRootFolder());
        Assert.assertTrue(environment.isVerbose());
        Assert.assertTrue(environment.isDebug());
        Assert.assertTrue(environment.isAutoImports());
        Assert.assertTrue(environment.isUsingTabulations());
        Assert.assertTrue(environment.isUsingSourceCodeFragments());
        Assert.assertTrue(environment.isPreserveLineNumbers());
        Assert.assertEquals(tabulationSize, environment.getTabulationSize());

        JavaOutputProcessor processor = (JavaOutputProcessor) environment.getDefaultFileGenerator();
        Assert.assertTrue(processor.getPrinter() instanceof FragmentDrivenJavaPrettyPrinter);
    }
}