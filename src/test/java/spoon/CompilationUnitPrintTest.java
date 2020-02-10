package spoon;

import org.junit.Test;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class CompilationUnitPrintTest {

    @Test
    public void test() {

        /* Testing scenario:
            Build a model with a class
            Clone this class
            Modify the clone by adding a method
            Print out the clone
            Build a model using the printed clone
            Compare the number of number between the outputted class and the cloned one
         */

        // build original class
        Launcher launcher = new Launcher();
        launcher.addInputResource("src/test/java/spoon/CompilationUnitPrintTest.java");
        launcher.getEnvironment().setNoClasspath(true);
        launcher.buildModel();
        final Factory factory = launcher.getFactory();

        // clone
        final CtClass<?> compilationUnitPrintTest = factory.Class().get("spoon.CompilationUnitPrintTest");
        final CtClass<?> clone = compilationUnitPrintTest.clone();
        compilationUnitPrintTest.getPackage().addType(clone);
        assertEquals(1 , clone.getMethods().size());
        assertEquals(1 , compilationUnitPrintTest.getMethods().size());

        // modification
        CtMethod<?> cloneMethod = ((CtMethod<?>) clone.getMethodsByName("test").get(0)).clone();
        cloneMethod.setSimpleName("cloneTest");
        clone.addMethod(cloneMethod);
        assertEquals(2 , clone.getMethods().size());
        assertEquals(1 , compilationUnitPrintTest.getMethods().size());

        // print modified class
        Environment env = factory.getEnvironment();
        env.setAutoImports(true);
        env.setNoClasspath(true);
        env.setCommentEnabled(true);
        JavaOutputProcessor processor = new JavaOutputProcessor(new DefaultJavaPrettyPrinter(env));
        processor.setFactory(factory);
        processor.getEnvironment().setSourceOutputDirectory(new File("target/"));
        processor.createJavaFile(clone); // <- here we print out the clone, which have two methods
        assertEquals(2 , clone.getMethods().size());
        assertEquals(1 , compilationUnitPrintTest.getMethods().size());

        // building now a new model from the java file outputted just before
        launcher = new Launcher();
        launcher.addInputResource("target/spoon/CompilationUnitPrintTest.java");
        launcher.getEnvironment().setNoClasspath(true);
        launcher.buildModel();

        // compare the number of methods in the printed class and the clone, should be the same (2)
        assertEquals(
                clone.getMethods().size(),
                launcher.getFactory().Class().get("spoon.CompilationUnitPrintTest").getMethods().size()
        );
    }


}
