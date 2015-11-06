package spoon.test.staticFieldAccess;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;


public class StaticAccessTest {

    Launcher spoon;
    Factory factory;
    SpoonCompiler compiler;

    @Before
    public void setUp()  throws Exception {
          spoon = new Launcher();
          factory = spoon.createFactory();
          compiler = spoon.createCompiler(
                factory,
                SpoonResourceHelper
                        .resources(
                                "./src/test/java/spoon/test/staticFieldAccess/internal/",
                                "./src/test/java/spoon/test/staticFieldAccess/StaticAccessBug.java"
                        ));
        compiler.build();
    }

    @Test
    public void testReferences() throws Exception {

        CtType<?> type = (CtType<?>) factory.Type().get("spoon.test.staticFieldAccess.StaticAccessBug");
        CtBlock<?> block = type.getMethod("references").getBody();
        assertTrue(block.getStatement(0).toString().contains("Extends.MY_STATIC_VALUE"));
        assertTrue(block.getStatement(1).toString().contains("Extends.MY_OTHER_STATIC_VALUE"));
    }


    @Test
    public void testProcessAndCompile() throws Exception{
        compiler.instantiateAndProcess(Arrays.asList(InsertBlockProcessor.class.getName()));

        // generate files
        File tmpdir = new File("target/spooned/staticFieldAccess");
        tmpdir.mkdirs();
        //    tmpdir.deleteOnExit();
        compiler.setSourceOutputDirectory(tmpdir);
        compiler.generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);

        // try to reload generated datas
        spoon = new Launcher();
        compiler = spoon.createCompiler(
                SpoonResourceHelper
                        .resources(tmpdir.getAbsolutePath()));
        assertTrue(compiler.build());
    }

}
